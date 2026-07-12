package com.hoperun.smartcar.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hoperun.smartcar.ui.theme.*

/**
 * 首页 — 竖屏版
 */
@Composable
fun IndexPage(
    onMecanumClick: () -> Unit,
    onRemoteControlClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // 标题
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 20.dp)
        ) {
            Text("智慧小车", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("SMART CAR CONTROL", fontSize = 13.sp, color = TextSecondary, letterSpacing = 4.sp)
        }

        // 两张卡片上下排列
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            MenuCard(
                title = "麦科朗姆",
                subtitle = "Mecanum Wheel",
                icon = "◎",
                modifier = Modifier.weight(1f),
                onClick = onMecanumClick
            )
            MenuCard(
                title = "单独控制",
                subtitle = "Individual Control",
                icon = "☰",
                modifier = Modifier.weight(1f),
                onClick = onRemoteControlClick
            )
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    subtitle: String,
    icon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border),
        contentPadding = PaddingValues(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 图标
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Accent.copy(alpha = 0.1f))
                    .border(1.dp, Accent.copy(alpha = 0.2f), RoundedCornerShape(36.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 32.sp, color = Accent)
            }

            // 文字
            Column {
                Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(subtitle, fontSize = 13.sp, color = TextSecondary)
            }
        }
    }
}
