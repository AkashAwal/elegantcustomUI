package com.elegantgalaxy.tvlauncher.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.model.AppCategory
import com.elegantgalaxy.tvlauncher.model.AppInfo
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated2
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals

private val TILE_SIZE = 96.dp
private val RISE_FOCUSED = 16.dp
private val RISE_NEIGHBOR = 6.dp

/**
 * One horizontally-scrolling row of apps for a single [AppCategory].
 * The home screen stacks several of these; LazyRow only composes tiles
 * near the viewport, which matters on 1-2GB RAM devices with large
 * app catalogues.
 *
 * Tiles "rise" (shift up) based on distance from whichever one currently
 * has D-Pad focus — the focused tile rises the most, its immediate left/
 * right neighbors rise a little, everything farther stays flat. Distance
 * is tracked as a plain index rather than derived from focus state per
 * tile so neighbors 1 index away can react even though they aren't
 * focused themselves.
 *
 * The row restores focus to whichever tile was last focused when D-Pad
 * navigation re-enters it (falling back to the first tile the first time),
 * instead of always resetting to the start — same pattern Google's
 * JetStreamCompose sample uses for its content rows, since without it a
 * TV remote "loses its place" every time focus leaves and returns to a row.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppCarousel(
    title: String,
    apps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
    onAppFocusChange: (AppInfo?) -> Unit = {},
) {
    var focusedIndex by remember { mutableStateOf(-1) }
    val (lazyRow, firstItem) = remember { FocusRequester.createRefs() }

    Column(modifier = modifier.focusGroup()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 48.dp, bottom = 12.dp),
        )

        LazyRow(
            contentPadding = PaddingValues(start = 48.dp, end = 48.dp, top = RISE_FOCUSED, bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .focusRequester(lazyRow)
                .focusRestorer { firstItem },
        ) {
            itemsIndexed(apps, key = { _, app -> app.packageName }) { index, app ->
                val distance = if (focusedIndex == -1) Int.MAX_VALUE else kotlin.math.abs(index - focusedIndex)
                val rise by animateDpAsState(
                    targetValue = when (distance) {
                        0 -> RISE_FOCUSED
                        1 -> RISE_NEIGHBOR
                        else -> 0.dp
                    },
                    animationSpec = tween(150),
                    label = "tileRise",
                )

                val itemFocusRequester = if (index == 0) Modifier.focusRequester(firstItem) else Modifier

                CompactAppTile(
                    app = app,
                    onClick = { onAppClick(app) },
                    modifier = itemFocusRequester.size(TILE_SIZE).offset(y = -rise),
                    onFocusChange = { focusedAppOrNull ->
                        onAppFocusChange(focusedAppOrNull)
                        focusedIndex = when {
                            focusedAppOrNull != null -> index
                            focusedIndex == index -> -1
                            else -> focusedIndex
                        }
                    },
                )
            }
        }
    }
}

/** Icon-only square tile — no label, unlike the larger [AppTile] used in [AppGrid]. */
@Composable
private fun CompactAppTile(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onFocusChange: (AppInfo?) -> Unit = {},
) {
    val shape = RoundedCornerShape(10.dp)
    val focusVisuals = rememberTvFocusVisuals(shape = shape)

    Box(
        modifier = modifier
            .then(focusVisuals.modifier)
            .clip(shape)
            .background(SurfaceElevated2)
            .onFocusChanged { onFocusChange(if (it.isFocused) app else null) }
            .clickable(
                interactionSource = focusVisuals.interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(14.dp),
        contentAlignment = Alignment.Center,
    ) {
        val icon = app.icon
        if (icon != null) {
            Image(
                painter = icon,
                contentDescription = app.label,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text = app.label.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
