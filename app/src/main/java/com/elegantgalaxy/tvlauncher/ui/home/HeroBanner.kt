package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private data class HeroBannerItem(
    val title: String,
    val subtitle: String,
    val accentColor: Color,
)

/**
 * Placeholder banner data — solid gradient blocks standing in for real
 * artwork/thumbnails per title. Swap [accentColor] for an `Image`/`AsyncImage`
 * once real banner art exists per item; the rotation/indicator/overlay
 * plumbing here doesn't need to change either way. Two slides for now —
 * add more here whenever there's real content to back them.
 */
private val heroBannerItems = listOf(
    HeroBannerItem("YouTube", "Endless entertainment, ready to stream", Color(0xFFFF3B30)),
    HeroBannerItem("Netflix", "New episodes added weekly", Color(0xFFB8004A)),
)

private const val SLIDE_DURATION_MS = 6000

/**
 * Full-width rotating hero banner, edge-to-edge (no side padding, unlike
 * the 48dp-padded carousels below it). A single [Animatable] drives both
 * the auto-advance timing and the active dot's fill animation so they stay
 * perfectly in sync; manual chevron taps change the current page, which
 * restarts that same animation from zero.
 *
 * The effect is keyed on [activePage] — a plain local `Int` we own — rather
 * than `pagerState.currentPage`. `currentPage` updates mid-scroll, before
 * `animateScrollToPage` actually finishes settling; keying off it caused
 * this effect to cancel its own in-flight scroll animation and restart,
 * leaving the pager stuck straddling two pages instead of settling on one.
 */
@Composable
fun HeroBanner(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { heroBannerItems.size })
    var activePage by remember { mutableStateOf(0) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(activePage) {
        if (pagerState.currentPage != activePage) {
            pagerState.animateScrollToPage(activePage)
        }
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(SLIDE_DURATION_MS, easing = LinearEasing))
        // Only reached if the animation wasn't cancelled by a manual page
        // change above — i.e. it played out its full natural duration.
        activePage = (activePage + 1) % heroBannerItems.size
    }

    fun goToPage(page: Int) {
        activePage = (page + heroBannerItems.size) % heroBannerItems.size
    }

    Box(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp),
        ) { page ->
            HeroBannerPage(item = heroBannerItems[page])
        }

        ChevronButton(
            icon = Icons.Filled.ChevronLeft,
            contentDescription = "Previous slide",
            onClick = { goToPage(pagerState.currentPage - 1) },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
        )
        ChevronButton(
            icon = Icons.Filled.ChevronRight,
            contentDescription = "Next slide",
            onClick = { goToPage(pagerState.currentPage + 1) },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
        )

        PageIndicator(
            pageCount = heroBannerItems.size,
            currentPage = pagerState.currentPage,
            progress = progress.value,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
        )
    }
}

@Composable
private fun HeroBannerPage(item: HeroBannerItem) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    listOf(item.accentColor.copy(alpha = 0.85f), Color.Black.copy(alpha = 0.75f)),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 48.dp, bottom = 40.dp, end = 48.dp),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun ChevronButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = Color.White)
    }
}

/**
 * Bottom-center dots; the active one is a wider capsule whose fill grows
 * left-to-right in step with [progress] (0f–1f), so it visibly empties and
 * refills over exactly [SLIDE_DURATION_MS] — the same duration the slide
 * itself is on screen for.
 */
@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(pageCount) { index ->
            if (index == currentPage) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .clip(CircleShape)
                            .background(Color.White),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.4f)),
                )
            }
        }
    }
}
