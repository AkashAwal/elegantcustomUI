package com.elegantgalaxy.tvlauncher.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elegantgalaxy.tvlauncher.ui.theme.AccentLavender
import com.elegantgalaxy.tvlauncher.ui.theme.NavigationStyle
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals

private val CARD_SIZE = DpSize(240.dp, 135.dp)
private val CARD_RADIUS = 12.dp

/**
 * 16:9 poster card for the home screen's content rows (Figma "Media card"):
 * 240x135, 12 radius. Focused (D-Pad) state per Figma: 1.08x scale, 2px
 * #9F7FDB inside stroke, a 0/4/12 black-25% drop shadow, and — over the bottom
 * of the artwork — the [title] on a fade-to-black scrim plus a thin progress
 * bar when [progress] is above 0 (Continue Watching).
 */
@Composable
fun MediaCard(
    title: String,
    @DrawableRes poster: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    progress: Float = 0f,
) {
    val shape = RoundedCornerShape(CARD_RADIUS)
    val focusVisuals = rememberTvFocusVisuals(
        shape = shape,
        focusedScale = 1.08f,
        focusedBorderColor = AccentLavender,
        borderWidthDp = 2,
    )
    val focused by focusVisuals.interactionSource.collectIsFocusedAsState()

    Box(
        modifier = modifier
            .size(CARD_SIZE)
            .then(focusVisuals.modifier)
            // Native shadow layer for the same reason as the focused app tile: Modifier.shadow
            // can't offset/blur independently.
            .drawBehind {
                if (focused) {
                    drawIntoCanvas { canvas ->
                        val paint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            color = Color.Black.toArgb()
                            setShadowLayer(6.dp.toPx(), 0f, 4.dp.toPx(), Color.Black.copy(alpha = 0.25f).toArgb())
                        }
                        val radius = CARD_RADIUS.toPx()
                        canvas.nativeCanvas.drawRoundRect(0f, 0f, size.width, size.height, radius, radius, paint)
                    }
                }
            }
            .clip(shape)
            .clickable(
                interactionSource = focusVisuals.interactionSource,
                indication = null,
                onClick = onClick,
            ),
    ) {
        Image(
            painter = painterResource(poster),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        if (focused) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(63.dp)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))))
                    .padding(8.dp),
            ) {
                Text(
                    text = title,
                    style = NavigationStyle.copy(fontSize = 14.sp),
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.BottomStart),
                )
            }
            if (progress > 0f) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 22.dp)
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color(0xFFD9D9D9).copy(alpha = 0.2f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .height(2.dp)
                            .background(AccentLavender),
                    )
                }
            }
        }
    }
}
