package com.hoperun.smartcar

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.hoperun.smartcar.ui.pages.*
import com.hoperun.smartcar.ui.theme.SmartCarTheme
import com.hoperun.smartcar.utils.PreferencesUtils

/**
 * 主Activity — 应用入口 + 页面导航
 * 与鸿蒙端 EntryAbility.ets / main_pages.json 一致
 *
 * 页面路由:
 *   NetworkSettings → Index → RemoteControl / MecanumWheel
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 保持屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 初始化偏好设置
        PreferencesUtils.init(applicationContext)

        setContent {
            SmartCarTheme {
                AppNavigation()
            }
        }
    }
}

/** 页面枚举 */
enum class Page {
    NetworkSettings,
    Index,
    RemoteControl,
    MecanumWheel
}

/**
 * 应用导航控制器
 * 默认进入首页（跳过登录可改为 NetworkSettings）
 */
@Composable
fun AppNavigation() {
    var currentPage by remember { mutableStateOf(Page.NetworkSettings) }

    when (currentPage) {
        Page.NetworkSettings -> {
            NetworkSettingsPage(
                onConnectSuccess = {
                    currentPage = Page.Index
                }
            )
        }

        Page.Index -> {
            IndexPage(
                onMecanumClick = { currentPage = Page.MecanumWheel },
                onRemoteControlClick = { currentPage = Page.RemoteControl }
            )
        }

        Page.RemoteControl -> {
            RemoteControlPage()
        }

        Page.MecanumWheel -> {
            MecanumWheelPage()
        }
    }

    // 全局返回键处理 — 按返回键回到上一页
    androidx.activity.compose.BackHandler(enabled = currentPage != Page.NetworkSettings) {
        currentPage = when (currentPage) {
            Page.Index -> Page.NetworkSettings
            Page.RemoteControl, Page.MecanumWheel -> Page.Index
            else -> currentPage
        }
    }
}
