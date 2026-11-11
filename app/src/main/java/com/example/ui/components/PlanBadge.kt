package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SuperAiPlan

@Composable
fun PlanBadge(
    plan: SuperAiPlan,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val brush = when (plan) {
        SuperAiPlan.FREE -> Brush.horizontalGradient(listOf(Color(0xFF475569), Color(0xFF334155)))
        SuperAiPlan.PRO -> Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFFA855F7)))
        SuperAiPlan.ADMIN -> Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444)))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(brush)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag("plan_badge")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (plan == SuperAiPlan.PRO || plan == SuperAiPlan.ADMIN) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(13.dp)
                )
            }
            Text(
                text = plan.title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
