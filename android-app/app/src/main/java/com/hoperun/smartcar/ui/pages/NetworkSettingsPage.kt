package com.hoperun.smartcar.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hoperun.smartcar.car.CarState
import com.hoperun.smartcar.tcp.TcpClientManager
import com.hoperun.smartcar.ui.components.VideoConfig
import com.hoperun.smartcar.ui.theme.*
import com.hoperun.smartcar.utils.PreferencesUtils
import kotlinx.coroutines.launch
import com.hoperun.smartcar.tcp.TcpReceiveParser

/**
 * 网络设置界面
 * 与鸿蒙端 NetworkSettings.ets 一致
 */
@Composable
fun NetworkSettingsPage(
    onConnectSuccess: () -> Unit
) {
    var ip by remember { mutableStateOf("192.168.1.11") }
    var tcpPort by remember { mutableStateOf("6000") }
    var videoPort by remember { mutableStateOf("6500") }
    var connecting by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // 读取保存的配置
    LaunchedEffect(Unit) {
        ip = PreferencesUtils.getIp()
        tcpPort = PreferencesUtils.getTcpPort()
        videoPort = PreferencesUtils.getVideoPort()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题
        // 标题
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 48.dp, bottom = 24.dp)
        ) {
            Text(
                "网络设置",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "NETWORK CONFIGURATION",
                fontSize = 12.sp,
                color = TextSecondary,
                letterSpacing = 3.sp
            )
        }

        // 登录卡片
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(CardBackground)
                .border(1.dp, Border, RoundedCornerShape(18.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("连接小车", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("输入小车的网络地址信息", fontSize = 12.sp, color = TextSecondary)

            // IP 地址
            InputField("IP 地址", ip, "请输入IP地址") { ip = it }
            // TCP 端口
            InputField("TCP 端口", tcpPort, "请输入TCP端口", isNumber = true) { tcpPort = it }
            // 视频端口
            InputField("视频端口", videoPort, "请输入视频端口", isNumber = true) { videoPort = it }

            // 连接按钮
            Button(
                onClick = {
                    connecting = true
                    message = ""
                    scope.launch {
                        // 保存配置
                        PreferencesUtils.setIp(ip)
                        PreferencesUtils.setTcpPort(tcpPort)
                        PreferencesUtils.setVideoPort(videoPort)

                        // 设置视频配置
                        VideoConfig.ip = ip
                        VideoConfig.port = videoPort.toIntOrNull() ?: 6500

                        // 连接
                        TcpClientManager.initNetAddress(ip, tcpPort.toIntOrNull() ?: 6000)
                        val ok = TcpClientManager.connect()
                        connecting = false
                        if (ok) {
                            message = "连接成功"
                            onConnectSuccess()
                        } else {
                            message = "连接失败\n请检查:\n① 手机和小车在同一网络\n② IP和端口正确\n③ 小车已开机"
                        }
                    }
                },
                enabled = !connecting,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text(
                    if (connecting) "连接中..." else "连 接",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentDark
                )
            }

            if (message.isNotEmpty()) {
                Text(
                    message,
                    fontSize = 13.sp,
                    color = if (message.contains("成功")) Success else Danger,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun InputField(label: String, value: String, placeholder: String, isNumber: Boolean = false, onChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text(placeholder, fontSize = 13.sp, color = TextSecondary.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = Accent,
                unfocusedBorderColor = Border,
                cursorColor = Accent,
                focusedContainerColor = Background,
                unfocusedContainerColor = Background
            ),
            singleLine = true,
            keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions()
        )
    }
}
