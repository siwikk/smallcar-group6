package com.hoperun.smartcar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.hoperun.smartcar.car.CarApi
import com.hoperun.smartcar.ui.theme.Accent

/**
 * 摇杆控制组件 — Canvas 绘制
 * 与鸿蒙端 RockerComponent 功能一致
 */
@Composable
fun CarRockerComponents(modifier: Modifier = Modifier) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var fingerOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    val bgColor = Accent.copy(alpha = 0.15f)
    val ringColor = Accent.copy(alpha = 0.4f)
    val fingerColor = Accent.copy(alpha = 0.7f)

    // 居中手指位置
    LaunchedEffect(size) {
        if (size.width > 0 && size.height > 0 && !isDragging) {
            fingerOffset = Offset(size.width / 2f, size.height / 2f)
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        fingerOffset = clampFinger(offset, size)
                        sendTilt(fingerOffset, size)
                    },
                    onDrag = { _, dragAmount ->
                        fingerOffset = clampFinger(fingerOffset + dragAmount, size)
                        sendTilt(fingerOffset, size)
                    },
                    onDragEnd = {
                        isDragging = false
                        fingerOffset = Offset(size.width / 2f, size.height / 2f)
                        sendTilt(fingerOffset, size)
                        // 发送停止
                        CarApi.rockerCtrl(0, 0)
                    },
                    onDragCancel = {
                        isDragging = false
                        fingerOffset = Offset(size.width / 2f, size.height / 2f)
                        CarApi.rockerCtrl(0, 0)
                    }
                )
            }
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = minOf(cx, cy) * 0.9f
        val fingerR = radius * 0.25f

        // 背景圆
        drawCircle(
            color = bgColor,
            radius = radius,
            center = Offset(cx, cy)
        )
        // 背景圆环
        drawCircle(
            color = ringColor,
            radius = radius,
            center = Offset(cx, cy),
            style = Stroke(width = 2f)
        )
        // 手指
        drawCircle(
            color = fingerColor,
            radius = fingerR,
            center = fingerOffset
        )
    }
}

private fun clampFinger(offset: Offset, size: IntSize): Offset {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val maxR = minOf(cx, cy) * 0.9f * 0.75f // 手指中心不能超出背景圆 * 0.75
    val dx = offset.x - cx
    val dy = offset.y - cy
    val dist = kotlin.math.sqrt(dx * dx + dy * dy)
    if (dist > maxR && dist > 0) {
        return Offset(cx + dx / dist * maxR, cy + dy / dist * maxR)
    }
    return offset
}

private fun sendTilt(finger: Offset, size: IntSize) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val maxR = minOf(cx, cy) * 0.9f * 0.75f
    // 映射 [-100, 100]
    val tiltX = ((finger.x - cx) / maxR * 100).toInt().coerceIn(-100, 100)
    val tiltY = (-(finger.y - cy) / maxR * 100).toInt().coerceIn(-100, 100)
    CarApi.rockerCtrl(tiltX, tiltY)
}
