package com.hoperun.smartcar.car

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 小车状态共享数据
 * 用于TCP接收和UI之间的数据传递
 */
object CarState {

    private val _batteryPercent = MutableStateFlow(0)
    val batteryPercent: StateFlow<Int> = _batteryPercent

    private val _batteryVoltage = MutableStateFlow(0.0)
    val batteryVoltage: StateFlow<Double> = _batteryVoltage

    private val _hardwareVersion = MutableStateFlow("")
    val hardwareVersion: StateFlow<String> = _hardwareVersion

    private val _currentSpeedLevel = MutableStateFlow(2)
    val currentSpeedLevel: StateFlow<Int> = _currentSpeedLevel

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected

    private val _lastError = MutableStateFlow("")
    val lastError: StateFlow<String> = _lastError

    fun setConnected(c: Boolean) { _connected.value = c }
    fun setError(msg: String) { _lastError.value = msg }
    fun clearError() { _lastError.value = "" }

    fun setSpeedLevel(level: Int) {
        _currentSpeedLevel.value = level.coerceIn(1, 3)
    }

    fun parseBatteryResponse(dataHex: String) {
        val raw = dataHex.toIntOrNull(16) ?: return
        if (raw <= 0) return
        val voltage = raw / 100.0
        val minVoltage = 9.0
        val maxVoltage = 12.6
        var percent = ((voltage - minVoltage) / (maxVoltage - minVoltage) * 100)
        percent = percent.coerceIn(0.0, 100.0)
        _batteryVoltage.value = voltage
        _batteryPercent.value = percent.toInt()
    }

    fun parseVersionResponse(dataHex: String) {
        val sb = StringBuilder()
        var i = 0
        while (i < dataHex.length) {
            val charCode = dataHex.substring(i, i + 2).toIntOrNull(16) ?: 0
            if (charCode in 32..126) sb.append(charCode.toChar())
            i += 2
        }
        _hardwareVersion.value = sb.toString()
    }
}
