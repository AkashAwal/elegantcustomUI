package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elegantgalaxy.tvlauncher.model.Banner
import com.elegantgalaxy.tvlauncher.ui.components.PrimaryButton
import com.elegantgalaxy.tvlauncher.ui.components.SecondaryButton
import com.elegantgalaxy.tvlauncher.ui.theme.BodyStyle
import com.elegantgalaxy.tvlauncher.ui.theme.HeroBannerTitleStyle
import com.elegantgalaxy.tvlauncher.ui.theme.NavigationStyle
import com.elegantgalaxy.tvlauncher.utils.BannerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val SLIDE_DURATION_MS = 6000
private val BANNER_HEIGHT = 562.dp // Figma home frame; dp here is Figma px, see ProvideFigmaDensity

/**
 * Full-width rotating hero banner, edge-to-edge (no side padding, unlike
 * the 48dp-padded carousels below it). Content comes from [banners] (at most
 * 4, see [BannerRepository]); selecting the banner calls [onBannerClick] with
 * the slide currently showing.
 *
 * A single [Animatable] drives both the auto-advance timing and the active
 * dot's fill, and it's only read in the draw phase so the animation doesn't
 * recompose the banner every frame. The effect is keyed on [activePage] — a
 * plain local `Int` we own — rather than `pagerState.currentPage`, which
 * updates mid-scroll before a scroll animation actually finishes settling.
 * Only the visible page is composed, so only one image bitmap is decoded at a
 * time. [key] on [banners] restarts from slide 0 when the feed is swapped.
 */
@Composable
fun HeroBanner(
    banners: List<Banner>,
    onBannerClick: (Banner) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (banners.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth().height(BANNER_HEIGHT))
        return
    }
    key(banners) { HeroBannerContent(banners, onBannerClick, modifier) }
}

@Composable
private fun HeroBannerContent(
    banners: List<Banner>,
    onBannerClick: (Banner) -> Unit,
    modifier: Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    var activePage by remember { mutableStateOf(0) }
    // True while Play / More Info (or anything inside the hero) holds D-Pad focus. Only the
    // visible page is composed, so advancing would dispose the focused button and drop focus
    // to nothing — the rotation waits until focus leaves instead.
    var heroHasFocus by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(activePage, heroHasFocus) {
        if (pagerState.currentPage != activePage) {
            pagerState.animateScrollToPage(activePage)
        }
        if (banners.size < 2 || heroHasFocus) return@LaunchedEffect
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(SLIDE_DURATION_MS, easing = LinearEasing))
        // Only reached if the animation played out its full natural duration.
        activePage = (activePage + 1) % banners.size
    }

    Box(modifier = modifier.fillMaxWidth().onFocusChanged { heroHasFocus = it.hasFocus }) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(BANNER_HEIGHT),
        ) { page ->
            HeroBannerPage(banner = banners[page], onOpen = { onBannerClick(banners[page]) })
        }

        if (banners.size > 1) {
            PageIndicator(
                pageCount = banners.size,
                currentPage = activePage,
                progress = progress,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
            )
        }
    }
}

@Composable
private fun HeroBannerPage(banner: Banner, onOpen: () -> Unit) {
    val accent = Color(banner.accentArgb)
    val image by produceState<ImageBitmap?>(null, banner.imagePath) {
        value = banner.imagePath?.let { path ->
            withContext(Dispatchers.IO) { BannerRepository.decode(path)?.asImageBitmap() }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.85f), Color.Black.copy(alpha = 0.75f)))),
    ) {
        image?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            // Figma blends the artwork into the page with background-colour fades on the
            // left, right and bottom edges. Alphas are measured from the design export
            // (see HeroFade* below) rather than eyeballed, and double as the title's scrim.
            Box(Modifier.fillMaxSize().background(HeroFadeLeft))
            Box(Modifier.fillMaxSize().background(HeroFadeRight))
            Box(Modifier.fillMaxSize().background(HeroFadeBottom))
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .widthIn(max = 600.dp)
                .padding(start = 77.dp, bottom = 85.dp, end = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = banner.title,
                style = HeroBannerTitleStyle,
                color = Color.White,
            )
            if (banner.meta.isNotEmpty()) {
                HeroMetaRow(banner.meta)
            }
            Text(
                text = banner.subtitle,
                style = BodyStyle.copy(fontSize = 18.sp, lineHeight = 25.sp),
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 3,
            )
            // Neither a title-details screen nor a separate "play" action exists yet,
            // so both buttons open the banner's app/deep link.
            Row(horizontalArrangement = Arrangement.spacedBy(23.dp)) {
                PrimaryButton(
                    text = "Play",
                    onClick = onOpen,
                    width = Dp.Unspecified,
                    height = 52.dp,
                    cornerRadius = 16.dp,
                    horizontalPadding = 24.dp,
                    textStyle = HeroButtonTextStyle,
                )
                SecondaryButton(
                    text = "More Info.",
                    onClick = onOpen,
                    // Figma: white @ 4% glass over the artwork, not the solid #171A2C secondary fill.
                    fill = Color.White.copy(alpha = 0.04f),
                    width = Dp.Unspecified,
                    height = 52.dp,
                    cornerRadius = 16.dp,
                    horizontalPadding = 24.dp,
                    textStyle = HeroButtonTextStyle,
                )
            }
        }
    }
}

// Figma hero buttons: Plus Jakarta Sans Medium 18 / 24, +1px tracking.
private val HeroButtonTextStyle = NavigationStyle.copy(letterSpacing = 1.sp)

// Alpha stops of the #0D1020 fades over the artwork, sampled from the design export
// (fraction of the hero's width/height -> opacity).
private val HeroFadeColor = HomeBackground
private val HeroFadeLeft = Brush.horizontalGradient(
    0f to HeroFadeColor.copy(alpha = 0.82f),
    0.035f to HeroFadeColor.copy(alpha = 0.74f),
    0.059f to HeroFadeColor.copy(alpha = 0.50f),
    0.088f to HeroFadeColor.copy(alpha = 0.46f),
    0.117f to HeroFadeColor.copy(alpha = 0.33f),
    0.176f to HeroFadeColor.copy(alpha = 0.14f),
    0.235f to HeroFadeColor.copy(alpha = 0f),
)
private val HeroFadeRight = Brush.horizontalGradient(
    0.765f to HeroFadeColor.copy(alpha = 0f),
    0.824f to HeroFadeColor.copy(alpha = 0.09f),
    0.883f to HeroFadeColor.copy(alpha = 0.14f),
    0.912f to HeroFadeColor.copy(alpha = 0.22f),
    0.941f to HeroFadeColor.copy(alpha = 0.38f),
    0.965f to HeroFadeColor.copy(alpha = 0.57f),
    0.982f to HeroFadeColor.copy(alpha = 0.63f),
    1f to HeroFadeColor.copy(alpha = 0.66f),
)
private val HeroFadeBottom = Brush.verticalGradient(
    0.67f to HeroFadeColor.copy(alpha = 0f),
    0.765f to HeroFadeColor.copy(alpha = 0.20f),
    0.82f to HeroFadeColor.copy(alpha = 0.30f),
    0.872f to HeroFadeColor.copy(alpha = 0.47f),
    0.907f to HeroFadeColor.copy(alpha = 0.60f),
    0.943f to HeroFadeColor.copy(alpha = 0.73f),
    1f to HeroFadeColor.copy(alpha = 0.70f),
)

/**
 * "IMDb 8.8 • 2018 • [16+] • 4 Seasons • Black Comedy, Satire": entries are
 * `|`-separated in [meta]; an entry in square brackets is drawn as the small
 * outlined age-rating badge, the rest as plain 16sp text, with 3dp dots between.
 */
@Composable
private fun HeroMetaRow(meta: String) {
    val entries = meta.split('|').map { it.trim() }.filter { it.isNotEmpty() }
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        entries.forEachIndexed { index, entry ->
            if (index > 0) {
                Box(Modifier.size(3.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.6f)))
            }
            if (entry.startsWith("[") && entry.endsWith("]")) {
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .border(1.dp, Color(0x33FFE8E8), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = entry.removePrefix("[").removeSuffix("]"),
                        style = BodyStyle.copy(fontSize = 12.sp, lineHeight = 16.sp),
                        color = Color.White,
                    )
                }
            } else {
                Text(
                    text = entry,
                    style = BodyStyle.copy(fontSize = 16.sp, lineHeight = 20.sp),
                    color = if (index == 0) Color.White.copy(alpha = 0.85f) else Color.White,
                )
            }
        }
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
    progress: Animatable<Float, *>,
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
                        .background(Color.White.copy(alpha = 0.3f))
                        .drawBehind {
                            drawRect(Color.White, size = Size(size.width * progress.value, size.height))
                        },
                )
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
