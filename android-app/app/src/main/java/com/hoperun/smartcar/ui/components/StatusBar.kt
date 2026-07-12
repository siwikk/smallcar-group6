package com.hoperun.smartcar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hoperun.smartcar.ui.theme.*

/**
 * 顶部状态栏组件
 * 显示：错误信息、连接状态、速度档位、蜂鸣器、电池电量
 */
@Composable
fun StatusBar(
    batteryPercent: Int,
    speedLevel: Int,
    buzzerOn: Boolean,
    onSpeedChange: (Int) -> Unit,
    onBuzzerToggle: () -> Unit,
    connected: Boolean = true,
    error: String = ""
) {
    Column {
        // 错误提示条
        if (error.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Danger.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⚠", fontSize = 14.sp, color = Danger)
                Spacer(Modifier.width(6.dp))
                Text(
                    error,
                    fontSize = 12.sp,
                    color = Danger,
                    maxLines = 2
                )
            }
        }

        // 主状态栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 连接状态 + 速度档位 — 左侧
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 连接指示灯
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (connected) Success else Danger)
                )
                Spacer(Modifier.width(8.dp))
                SpeedGearSelector(speedLevel, onSpeedChange)
            }

            Spacer(modifier = Modifier.weight(1f))

            // 蜂鸣器 — 中间
            BuzzerButton(buzzerOn, onBuzzerToggle)

            Spacer(modifier = Modifier.width(8.dp))

            // 电池电量 — 右上角
            BatteryIndicator(batteryPercent)
        }
    }
}

@Composable
fun BatteryIndicator(percent: Int) {
    val color = when {
        percent <= 0 -> TextSecondary
        percent <= 20 -> Danger
        percent <= 50 -> Warning
        else -> Success
    }
    val icon = when {
        percent <= 0 -> "⊘"
        percent <= 20 -> "▁"
        percent <= 40 -> "▃"
        percent <= 60 -> "▅"
        percent <= 80 -> "▇"
        else -> "█"
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CardBackground)
            .border(1.dp, Border, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // 电池图标外框
        Box(
            modifier = Modifier
                .size(20.dp, 12.dp)
                .border(1.5.dp, color, RoundedCornerShape(2.dp))
                .padding(1.5.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // 电池内部填充
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = (percent.coerceIn(0, 100) / 100f))
                    .clip(RoundedCornerShape(1.dp))
                    .background(color)
            )
        }
        Text("$percent%", fontSize = 13.sp, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SpeedGearSelector(level: Int, onChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CardBackground)
            .border(1.dp, Border, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("速", fontSize = 11.sp, color = TextSecondary)
        SpeedBtn("低", 1, level, onChange)
        SpeedBtn("中", 2, level, onChange)
        SpeedBtn("高", 3, level, onChange)
    }
}

@Composable
fun SpeedBtn(label: String, value: Int, current: Int, onClick: (Int) -> Unit) {
    val selected = current == value
    Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        color = if (selected) AccentDark else TextSecondary,
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(if (selected) Accent else CardBackground)
            .clickable { onClick(value) }
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
fun BuzzerButton(on: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(32.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (on) Accent.copy(alpha = 0.2f) else CardBackground
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, if (on) Accent.copy(alpha = 0.4f) else Border
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = if (on) "♪" else "×",
            fontSize = 13.sp,
            color = if (on) Accent else TextSecondary
        )
    }
}
