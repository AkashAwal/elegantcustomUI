package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private data class HeroBannerItem(
    val title: String,
    val subtitle: String,
    val accentColor: Color,
)

/**
 * Placeholder banner data — solid gradient blocks standing in for real
 * artwork/thumbnails per title. Swap [accentColor] for an `Image`/`AsyncImage`
 * once real banner art exists per item; the rotation/indicator/overlay
 * plumbing here doesn't need to change either way.
 */
private val heroBannerItems = listOf(
    HeroBannerItem("YouTube", "Endless entertainment, ready to stream", Color(0xFFFF3B30)),
    HeroBannerItem("Netflix", "New episodes added weekly", Color(0xFFB8004A)),
    HeroBannerItem("Prime Video", "Included with your subscription", Color(0xFF1F5C99)),
    HeroBannerItem("Spotify", "Your daily mix, refreshed", Color(0xFF1DB954)),
)

/**
 * Full-width rotating hero banner, edge-to-edge (no side padding, unlike
 * the 48dp-padded carousels below it). Auto-advances every 6s; the
 * background is a placeholder gradient per item — see [heroBannerItems].
 */
@Composable
fun HeroBanner(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { heroBannerItems.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(6000)
            val nextPage = (pagerState.currentPage + 1) % heroBannerItems.size
            pagerState.animateScrollToPage(nextPage)
        }
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

        PageIndicator(
            pageCount = heroBannerItems.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 48.dp, bottom = 24.dp),
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
private fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            Box(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(if (isActive) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (isActive) Color.White else Color.White.copy(alpha = 0.4f)),
            )
        }
    }
}
