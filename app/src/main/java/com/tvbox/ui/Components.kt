package com.tvbox.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val palettes = listOf(
    listOf(Color(0xFF0C1C3C), Color(0xFF2B5FA8), Color(0xFF69A8FF)),
    listOf(Color(0xFF3A1A08), Color(0xFF8A4514), Color(0xFFFFAB48)),
    listOf(Color(0xFF04211D), Color(0xFF0B4A40), Color(0xFF0AFFC3)),
    listOf(Color(0xFF2A1030), Color(0xFF5A2350), Color(0xFFFF6FA8)),
    listOf(Color(0xFF3F86A8), Color(0xFFB98BC9), Color(0xFFFFB27A)),
    listOf(Color(0xFF12442F), Color(0xFF1E6B4A), Color(0xFF9BE8B8))
)

@Composable
fun PosterArt(seed: Int, title: String = "", modifier: Modifier = Modifier, dim: Boolean = false) {
    val p = palettes[Math.abs(seed) % palettes.size]
    Box(modifier.clip(RoundedCornerShape(14.dp)).background(Brush.verticalGradient(listOf(p[0], p[1])))) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            drawCircle(Color.White.copy(alpha = .85f), radius = w * .012f, center = Offset(w * .22f, h * .18f))
            drawCircle(Color.White.copy(alpha = .55f), radius = w * .008f, center = Offset(w * .42f, h * .3f))
            drawCircle(Color.White.copy(alpha = .7f), radius = w * .01f, center = Offset(w * .72f, h * .12f))
            drawCircle(p[2].copy(alpha = .55f), radius = w * .38f, center = Offset(w * .78f, h * .42f))
            drawCircle(Color.Black.copy(alpha = .30f), radius = w * .34f, center = Offset(w * .55f, h * .95f))
        }
        if (dim) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .18f)))
        if (title.isNotEmpty()) {
            Text(
                title,
                color = Color.White.copy(alpha = .92f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
            )
        }
    }
}

@Composable
fun Pill(
    text: String,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier
            .clip(RoundedCornerShape(999.dp))
            .then(if (selected) Modifier.background(AccentBrush) else Modifier.background(Color.White.copy(alpha = .055f)))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 15.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            color = if (selected) Color.White else Text2,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

@Composable
fun SectionHead(title: String, action: String = "", modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(title, color = Text1, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        if (action.isNotEmpty()) Text(action, color = Text3, fontSize = 12.sp)
    }
}

@Composable
fun StatusDot(ms: Int, size: Int = 6) {
    val c = when {
        ms > 120 -> Color(0xFF666666)
        ms > 80 -> WarnColor
        else -> OkColor
    }
    Box(Modifier.size(size.dp).clip(CircleShape).background(c))
}

@Composable
fun PosterCard(title: String, sub: String, seed: Int, progress: Float? = null, modifier: Modifier = Modifier) {
    Column(modifier.width(118.dp)) {
        Box {
            PosterArt(seed, modifier = Modifier.fillMaxWidth().height(168.dp))
            if (progress != null) {
                Box(
                    Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(8.dp)
                        .height(3.dp).clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = .22f))
                ) {
                    Box(Modifier.fillMaxWidth(progress).fillMaxHeight().background(AccentBrush))
                }
            }
        }
        Spacer(Modifier.height(9.dp))
        Text(title, color = Text1, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(3.dp))
        Text(sub, color = Text3, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun BottomBar(current: Int, onPick: (Int) -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(76.dp)
            .background(Color(0xF014161D))
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val items = listOf("首页", "搜索", "数据源")
        items.forEachIndexed { i, label ->
            val on = i == current
            Column(
                Modifier.weight(1f).clickable { onPick(i) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier
                        .width(56.dp).height(28.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (on) Accent.copy(alpha = .16f) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier.size(18.dp).clip(RoundedCornerShape(5.dp))
                            .background(if (on) Accent else Text3)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(label, color = if (on) Text1 else Text3, fontSize = 10.5.sp)
            }
        }
    }
}
