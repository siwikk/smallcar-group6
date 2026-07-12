package com.hoperun.smartcar.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.hoperun.smartcar.car.CarApi
import com.hoperun.smartcar.car.CarEncode
import com.hoperun.smartcar.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 视频组件 — WebView 显示小车摄像头画面
 */
object VideoConfig {
    var ip: String = "192.168.1.11"
    var port: Int = 6500
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoComponents(modifier: Modifier = Modifier) {
    var isRecording by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 视频画面
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0f1923))
                .border(1.dp, Border, RoundedCornerShape(10.dp))
        ) {
            val url = "http://${VideoConfig.ip}:${VideoConfig.port}/index2"
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // 录制指示器
            if (isRecording) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    RedRound()
                }
            }
        }

        // 按钮行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isRecording) {
                ActionButton("拍摄", Accent, AccentDark) {
                    CarApi.send(CarEncode.takePhotos())
                }
                Spacer(Modifier.width(8.dp))
            }

            ActionButton(
                text = if (isRecording) "结束" else "录制",
                bgColor = if (isRecording) Danger else Accent,
                textColor = if (isRecording) TextPrimary else AccentDark
            ) {
                if (isRecording) {
                    CarApi.send(CarEncode.closeRecording())
                } else {
                    CarApi.send(CarEncode.startRecording())
                }
                isRecording = !isRecording
            }
        }
    }
}

@Composable
fun ActionButton(text: String, bgColor: Color, textColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Composable
fun RedRound() {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            visible = !visible
            delay(1000)
        }
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (visible) {
            Text("●", fontSize = 10.sp, color = Danger)
        }
        Text("REC", fontSize = 9.sp, color = Danger, fontWeight = FontWeight.Bold)
    }
}
