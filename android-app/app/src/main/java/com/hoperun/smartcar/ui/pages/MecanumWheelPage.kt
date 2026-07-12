package com.hoperun.smartcar.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hoperun.smartcar.car.CarApi
import com.hoperun.smartcar.car.CarState
import com.hoperun.smartcar.ui.components.*
import com.hoperun.smartcar.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 麦克纳姆轮控制界面 — 竖屏版
 * 四个轮子上下堆叠 + 滚动
 */
@Composable
fun MecanumWheelPage() {
    var l1 by remember { mutableFloatStateOf(0f) }
    var l2 by remember { mutableFloatStateOf(0f) }
    var r1 by remember { mutableFloatStateOf(0f) }
    var r2 by remember { mutableFloatStateOf(0f) }
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

    fun sendSpeed() {
        CarApi.wheelSpeed(l1.toInt(), l2.toInt(), r1.toInt(), r2.toInt())
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

        // 主体 — 可滚动
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 左前
            CardBox("左前轮 L1") { WheelSlider(l1, "L1") { l1 = it } }
            // 左后
            CardBox("左后轮 L2") { WheelSlider(l2, "L2") { l2 = it } }
            // 右前
            CardBox("右前轮 R1") { WheelSlider(r1, "R1") { r1 = it } }
            // 右后
            CardBox("右后轮 R2") { WheelSlider(r2, "R2") { r2 = it } }

            // 摇杆
            CardBox("摇杆控制", centerContent = true, height = 200) {
                CarRockerComponents(Modifier.size(160.dp))
            }

            // 按钮
            CardBox("", height = 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { sendSpeed() },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent)
                    ) {
                        Text("更新速度", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AccentDark)
                    }
                    Button(
                        onClick = {
                            l1 = 0f; l2 = 0f; r1 = 0f; r2 = 0f
                            sendSpeed()
                        },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Danger)
                    ) {
                        Text("全部归零", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun CardBox(title: String, modifier: Modifier = Modifier, centerContent: Boolean = false, height: Int = 0, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (height > 0) Modifier.height(height.dp) else Modifier.height(IntrinsicSize.Min))
            .clip(RoundedCornerShape(14.dp))
            .background(CardBackground)
            .border(1.dp, Border, RoundedCornerShape(14.dp))
            .shadow(10.dp, RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (centerContent) Arrangement.Center else Arrangement.Top
    ) {
        if (title.isNotEmpty()) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Accent)
            Spacer(Modifier.height(6.dp))
        }
        if (centerContent) Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { content() }
        else content()
    }
}

@Composable
fun WheelSlider(value: Float, label: String, onChange: (Float) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Slider(
                value = value,
                onValueChange = onChange,
                valueRange = -100f..100f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Accent,
                    activeTrackColor = Accent,
                    inactiveTrackColor = Background
                )
            )
            Text(
                "${value.toInt()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Accent,
                modifier = Modifier
                    .widthIn(min = 44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Background)
                    .border(1.dp, Border, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                textAlign = TextAlign.Center
            )
        }
        Text(label, fontSize = 11.sp, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}
