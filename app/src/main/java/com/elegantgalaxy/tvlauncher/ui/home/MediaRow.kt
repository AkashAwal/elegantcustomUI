package com.elegantgalaxy.tvlauncher.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.R
import com.elegantgalaxy.tvlauncher.ui.components.MediaCard
import com.elegantgalaxy.tvlauncher.ui.theme.AccentLavender
import com.elegantgalaxy.tvlauncher.ui.theme.HomeContentEnd
import com.elegantgalaxy.tvlauncher.ui.theme.HomeContentStart
import com.elegantgalaxy.tvlauncher.ui.theme.HomeSectionTitleStyle
import com.elegantgalaxy.tvlauncher.ui.theme.SeeAllStyle

data class MediaItem(
    val title: String,
    @DrawableRes val poster: Int,
    /** 0f = not started; above 0 shows a progress bar on the focused card. */
    val progress: Float = 0f,
)

/**
 * Placeholder catalogue taken from the Figma home frame — the launcher has no
 * content provider yet, so these stand in for real "continue watching" /
 * "recommended" data, same approach as the placeholder tiles in
 * [FeaturedContentRow]. Posters are 480x270 WebP in drawable-nodpi (2x the card).
 */
val continueWatchingItems = listOf(
    MediaItem("Daredevil", R.drawable.poster_cw_1),
    MediaItem("Interstellar", R.drawable.poster_cw_2),
    MediaItem("The Devil Wears Prada", R.drawable.poster_cw_3),
    MediaItem("La La Land", R.drawable.poster_cw_4, progress = 0.5f),
    MediaItem("Run", R.drawable.poster_cw_5),
    MediaItem("The Punisher", R.drawable.poster_cw_6),
    MediaItem("Breaking Bad", R.drawable.poster_cw_7),
    MediaItem("Shutter Island", R.drawable.poster_cw_8),
)

val recommendedItems = listOf(
    MediaItem("Parasite", R.drawable.poster_rec_1),
    MediaItem("House of the Dragon", R.drawable.poster_rec_2),
    MediaItem("The Godfather", R.drawable.poster_rec_3),
    MediaItem("Recommended 4", R.drawable.poster_rec_4),
    MediaItem("Recommended 5", R.drawable.poster_rec_5),
    MediaItem("Recommended 6", R.drawable.poster_rec_6),
    MediaItem("Recommended 7", R.drawable.poster_rec_7),
    MediaItem("Recommended 8", R.drawable.poster_rec_8),
)

/**
 * Titled, horizontally-scrolling row of [MediaCard]s (Figma "Section Header" +
 * card strip): 32sp title on the left, optional lavender "See All ›" on the
 * right when [onSeeAll] is given, 16dp between cards. Same side gutters as
 * [AppCarousel]; the row restores focus to the last-focused card on
 * re-entry, like the app rows.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MediaRow(
    title: String,
    items: List<MediaItem>,
    onItemClick: (MediaItem) -> Unit,
    modifier: Modifier = Modifier,
    onSeeAll: (() -> Unit)? = null,
    /** Lets a caller (e.g. the row above) send D-Pad focus into this row; it restores the last-focused card. */
    rowFocusRequester: FocusRequester? = null,
) {
    val (ownRowRequester, firstItem) = remember { FocusRequester.createRefs() }
    val lazyRow = rowFocusRequester ?: ownRowRequester

    Column(modifier = modifier.focusGroup()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Header 40 + 20 + 12 of focus-scale room = Figma's 72 to the cards' top edge.
                // End is 12 short of the gutter: SeeAllLink pads itself by 12 so its text still
                // lines up with the gutter while leaving room for the focus outline.
                .padding(start = HomeContentStart, end = HomeContentEnd - SEE_ALL_PADDING, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = HomeSectionTitleStyle,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
            )
            if (onSeeAll != null) {
                SeeAllLink(onClick = onSeeAll)
            }
        }

        LazyRow(
            // Vertical padding leaves room for the focused card's 1.08x scale and shadow.
            contentPadding = PaddingValues(start = HomeContentStart, end = HomeContentEnd, top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .focusRequester(lazyRow)
                .focusRestorer(firstItem),
        ) {
            itemsIndexed(items, key = { _, item -> item.poster }) { index, item ->
                MediaCard(
                    title = item.title,
                    poster = item.poster,
                    progress = item.progress,
                    onClick = { onItemClick(item) },
                    modifier = if (index == 0) Modifier.focusRequester(firstItem) else Modifier,
                )
            }
        }
    }
}

private val SEE_ALL_PADDING = 12.dp

/** "See All ›" — lavender text that gets the same 2px lavender outline as other focused controls. */
@Composable
private fun SeeAllLink(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(10.dp)

    Text(
        text = "See All ›",
        style = SeeAllStyle,
        color = AccentLavender,
        modifier = Modifier
            .clip(shape)
            .background(if (focused) AccentLavender.copy(alpha = 0.16f) else Color.Transparent)
            .then(if (focused) Modifier.border(2.dp, AccentLavender, shape) else Modifier)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = SEE_ALL_PADDING, vertical = 4.dp),
    )
}
