package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.SettingsInputHdmi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated2
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated3
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals

private data class FeaturedMovie(val title: String, val accentColor: Color)

/**
 * Placeholder catalogue, same pattern as [heroBannerItems] in HeroBanner.kt —
 * solid gradient blocks standing in for real poster art. Swap [accentColor]
 * for an `Image`/`AsyncImage` per title once real artwork exists.
 */
private val featuredMovies = listOf(
    FeaturedMovie("Stranger Things", Color(0xFF7A1F1F)),
    FeaturedMovie("The Crown", Color(0xFF3D4A2E)),
    FeaturedMovie("Money Heist", Color(0xFFB8004A)),
    FeaturedMovie("Dune", Color(0xFF5C4A1F)),
    FeaturedMovie("The Bear", Color(0xFF1F4A5C)),
)

/**
 * Row beneath [HeroBanner]: quick-access square tile for Android TV (the
 * launcher's own home surface), a rectangular tile for HDMI/other inputs,
 * then rectangular featured show/movie tiles — each with its own small
 * caption above it ("OS", "Recent Input", "Featured") instead of one
 * shared row title, since the three groups mean different things.
 * [onAndroidTvClick] and [onInputsClick] are left as caller-supplied
 * no-ops for now — neither screen exists yet, same "wire it up when the
 * destination exists" approach as the RailDestination entries that
 * already point at Home. Tiles are square-cornered (no rounding), unlike
 * the app tiles below.
 */
@Composable
fun FeaturedContentRow(
    onAndroidTvClick: () -> Unit,
    onInputsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item { CaptionedTile(caption = "OS") { AndroidTvTile(onClick = onAndroidTvClick) } }
        item { CaptionedTile(caption = "Recent Input") { InputsTile(onClick = onInputsClick) } }
        items(featuredMovies, key = { it.title }) { movie ->
            CaptionedTile(caption = "Featured") { MovieTile(movie = movie) }
        }
    }
}

@Composable
private fun CaptionedTile(caption: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = caption.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        content()
    }
}

@Composable
private fun AndroidTvTile(onClick: () -> Unit) {
    val focusVisuals = rememberTvFocusVisuals()

    Box(
        modifier = Modifier
            .size(160.dp)
            .then(focusVisuals.modifier)
            .background(SurfaceElevated2)
            .clickable(
                interactionSource = focusVisuals.interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Android,
            contentDescription = "Android TV",
            tint = Color(0xFF3DDC84),
            modifier = Modifier.size(64.dp),
        )
    }
}

@Composable
private fun InputsTile(onClick: () -> Unit) {
    val focusVisuals = rememberTvFocusVisuals()

    Box(
        modifier = Modifier
            .width(240.dp)
            .height(160.dp)
            .then(focusVisuals.modifier)
            .background(
                Brush.horizontalGradient(listOf(SurfaceElevated2, SurfaceElevated3)),
            )
            .clickable(
                interactionSource = focusVisuals.interactionSource,
                indication = null,
                onClick = onClick,
            ),
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.SettingsInputHdmi,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = "HDMI & Inputs",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun MovieTile(movie: FeaturedMovie) {
    val focusVisuals = rememberTvFocusVisuals()

    Box(
        modifier = Modifier
            .width(240.dp)
            .height(160.dp)
            .then(focusVisuals.modifier)
            .clickable(
                interactionSource = focusVisuals.interactionSource,
                indication = null,
                onClick = {},
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(movie.accentColor.copy(alpha = 0.85f), Color.Black.copy(alpha = 0.75f)),
                    ),
                ),
        )
        Text(
            text = movie.title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
        )
    }
}
