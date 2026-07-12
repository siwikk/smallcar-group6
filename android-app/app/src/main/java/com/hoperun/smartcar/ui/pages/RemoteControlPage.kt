package com.hoperun.smartcar.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hoperun.smartcar.car.CarApi
import com.hoperun.smartcar.car.CarEncode
import com.hoperun.smartcar.car.CarState
import com.hoperun.smartcar.ui.components.*
import com.hoperun.smartcar.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 遥控界面 — 竖屏版
 * 控制区在上 + 视频区在下
 */
@Composable
fun RemoteControlPage() {
    var ctrlMode by remember { mutableStateOf(0) }
    val batteryPercent by CarState.batteryPercent.collectAsState()
    val connected by CarState.connected.collectAsState()
    val error by CarState.lastError.collectAsState()
    var speedLevel by remember { mutableStateOf(2) }
    var buzzerOn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            CarApi.getBattery()
            delay(5000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // 状态栏
        StatusBar(
            batteryPercent = batteryPercent,
            speedLevel = speedLevel,
            buzzerOn = buzzerOn,
            onSpeedChange = { speedLevel = it; CarState.setSpeedLevel(it); CarApi.speedCtrl(it) },
            onBuzzerToggle = { buzzerOn = !buzzerOn; CarApi.buzzerCtrl(buzzerOn) },
            connected = connected,
            error = error
        )

        // === 上半部分：控制区 ===
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tab 切换
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Background)
                    .border(1.dp, Border, RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                TabBtn("按钮", ctrlMode == 0) { ctrlMode = 0 }
                TabBtn("遥杆", ctrlMode == 1) { ctrlMode = 1 }
            }

            Spacer(Modifier.height(8.dp))

            // 控制面板
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.dp, Border, RoundedCornerShape(16.dp))
                    .shadow(12.dp, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (ctrlMode == 0) CarBtnComponents() else CarRockerComponents()
            }
        }

        // === 下半部分：视频区 ===
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 视频
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardBackground)
                    .border(1.dp, Border, RoundedCornerShape(16.dp))
                    .shadow(12.dp, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                VideoComponents()
            }

            // 自动循迹
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                    .border(1.dp, Border, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var tracking by remember { mutableStateOf(false) }
                Checkbox(
                    checked = tracking,
                    onCheckedChange = {
                        tracking = it
                        if (it) CarApi.send(CarEncode.trackingOpen())
                        else CarApi.send(CarEncode.trackingClose())
                    },
                    colors = CheckboxDefaults.colors(checkedColor = Accent, uncheckedColor = TextSecondary)
                )
                Text("自动循迹", fontSize = 14.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun TabBtn(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text,
        fontSize = 14.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        color = if (selected) AccentDark else TextSecondary,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) Accent else CardBackground)
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 10.dp)
    )
}
