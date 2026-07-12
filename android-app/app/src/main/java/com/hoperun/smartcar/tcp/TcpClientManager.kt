package com.hoperun.smartcar.tcp

import com.hoperun.smartcar.car.CarState
import kotlinx.coroutines.*
import java.io.DataOutputStream
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket

object TcpClientManager {

    private var socket: Socket? = null
    private var address: String = "127.0.0.1"
    private var port: Int = 12345
    private var input: InputStream? = null
    private var output: DataOutputStream? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var receiveJob: Job? = null

    @Volatile
    var isConnected: Boolean = false
        private set

    fun initNetAddress(addr: String, p: Int) {
        address = addr
        port = p
    }

    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            disconnect()
            val sock = Socket()
            sock.keepAlive = true
            sock.connect(InetSocketAddress(address, port), 3000)
            sock.soTimeout = 0

            socket = sock
            output = DataOutputStream(sock.getOutputStream())
            input = sock.getInputStream()
            isConnected = true
            CarState.setConnected(true)
            CarState.clearError()

            receiveJob = scope.launch {
                val buf = ByteArray(1024)
                while (isActive && isConnected) {
                    try {
                        val len = input?.read(buf) ?: -1
                        if (len > 0) {
                            String(buf, 0, len)
                                .split("#")
                                .filter { '$' in it }
                                .forEach { piece ->
                                    val s = piece.lastIndexOf('$')
                                    if (s >= 0) parseOne(piece.substring(s) + "#")
                                }
                        } else break
                    } catch (_: Exception) { break }
                }
                // 接收循环退出 = 连接断开
                isConnected = false
                CarState.setConnected(false)
            }

            true
        } catch (e: Exception) {
            CarState.setError("TCP连接失败: ${e.message}")
            disconnect()
            false
        }
    }

    fun sendMessage(message: String) {
        try {
            output?.writeBytes(message)
            output?.flush()
        } catch (e: Exception) {
            isConnected = false
            CarState.setConnected(false)
            CarState.setError("发送失败: ${e.message}")
        }
    }

    fun disconnect() {
        receiveJob?.cancel()
        try { output?.close() } catch (_: Exception) {}
        try { input?.close() } catch (_: Exception) {}
        try { socket?.close() } catch (_: Exception) {}
        output = null; input = null; socket = null
        isConnected = false
        CarState.setConnected(false)
    }

    private fun parseOne(msg: String) {
        if (msg.length < 8) return
        try {
            val content = msg.removeSurrounding("$", "#")
            if (content.length < 6) return
            val cmd = content.substring(2, 4)
            val size = content.substring(4, 6).toIntOrNull(16) ?: return
            val end = 4 + size
            if (end > content.length - 2) return
            val info = content.substring(6, end)
            when (cmd) {
                "02" -> CarState.parseBatteryResponse(info)
                "01" -> CarState.parseVersionResponse(info)
            }
        } catch (_: Exception) {}
    }
}
