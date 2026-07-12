package com.hoperun.smartcar.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hoperun.smartcar.car.CarApi
import com.hoperun.smartcar.car.CarDirection
import com.hoperun.smartcar.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun CarBtnComponents(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            HoldBtn("↺", "左转", "normal", CarDirection.LeftRotate, Modifier.weight(1f))
            HoldBtn("▲", "前", "primary", CarDirection.Front, Modifier.weight(1f))
            HoldBtn("↻", "右转", "normal", CarDirection.RightRotate, Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            HoldBtn("◀", "左", "primary", CarDirection.Left, Modifier.weight(1f))
            StopBtn("■", "停", Modifier.weight(1f))
            HoldBtn("▶", "右", "primary", CarDirection.Right, Modifier.weight(1f))
        }
        Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Spacer(Modifier.weight(1f))
            HoldBtn("▼", "后", "primary", CarDirection.After, Modifier.weight(1f))
            Spacer(Modifier.weight(1f))
        }
    }
}

// ====== 方向键：按下持续发指令，松开停止 ======
@Composable
fun HoldBtn(
    symbol: String, label: String, type: String,
    direction: CarDirection, modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val bg = animateColorAsState(if (pressed) pressColor(type) else normalColor(type), tween(80), label = "bg")
    val border = if (pressed) pressBorder(type) else normalBorder(type)
    val text = if (type == "danger") TextPrimary else Accent
    val sub = if (type == "danger") TextPrimary.copy(alpha = 0.8f) else TextSecondary

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(bg.value)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .pointerInput(direction) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: continue
                        if (change.changedToDown()) {
                            pressed = true
                            val job = scope.launch {
                                while (isActive) {
                                    CarApi.carBtnCtrl(direction)
                                    delay(80)
                                }
                            }
                            do {
                                val up = awaitPointerEvent()
                            } while (up.changes.any { it.pressed })
                            job.cancel()
                            pressed = false
                            CarApi.carBtnCtrl(CarDirection.Stop)
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(symbol, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = text)
            Text(label, fontSize = 10.sp, color = sub)
        }
    }
}

// ====== 暂停键：点一下就停 ======
@Composable
fun StopBtn(symbol: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(Danger)
            .border(1.dp, Danger.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable { CarApi.carBtnCtrl(CarDirection.Stop) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(symbol, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(label, fontSize = 10.sp, color = TextPrimary.copy(alpha = 0.8f))
        }
    }
}

// ====== 颜色工具 ======
fun normalColor(type: String) = when (type) {
    "danger" -> Danger; "primary" -> Color(0xFF1a3347); else -> CardBackground
}
fun pressColor(type: String) = when (type) {
    "danger" -> Danger.copy(alpha = 0.7f); "primary" -> Color(0xFF2a5577); else -> Color(0xFF2a3340)
}
fun normalBorder(type: String) = when (type) {
    "danger" -> Danger.copy(alpha = 0.4f); "primary" -> Accent.copy(alpha = 0.25f); else -> Border
}
fun pressBorder(type: String) = when (type) {
    "danger" -> Danger; "primary" -> Accent.copy(alpha = 0.6f); else -> Accent.copy(alpha = 0.4f)
}
