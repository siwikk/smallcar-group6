package com.hoperun.smartcar.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * 偏好设置工具
 * 与鸿蒙端 PreferencesUtils.ets 功能一致
 */
object PreferencesUtils {

    private const val PREFS_NAME = "smart_car_prefs"
    private const val KEY_IP = "ip"
    private const val KEY_TCP_PORT = "tcp_port"
    private const val KEY_VIDEO_PORT = "video_port"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getIp(): String = prefs?.getString(KEY_IP, "192.168.1.11") ?: "192.168.1.11"
    fun getTcpPort(): String = prefs?.getString(KEY_TCP_PORT, "6000") ?: "6000"
    fun getVideoPort(): String = prefs?.getString(KEY_VIDEO_PORT, "6500") ?: "6500"

    fun setIp(ip: String) { prefs?.edit()?.putString(KEY_IP, ip)?.apply() }
    fun setTcpPort(port: String) { prefs?.edit()?.putString(KEY_TCP_PORT, port)?.apply() }
    fun setVideoPort(port: String) { prefs?.edit()?.putString(KEY_VIDEO_PORT, port)?.apply() }
}
