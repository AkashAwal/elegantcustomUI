package com.elegantgalaxy.tvlauncher.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
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
    MediaItem("Continue 1", R.drawable.poster_cw_1),
    MediaItem("Interstellar", R.drawable.poster_cw_2),
    MediaItem("The Devil Wears Prada", R.drawable.poster_cw_3),
    MediaItem("Daredevil", R.drawable.poster_cw_4, progress = 0.5f),
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
 * right when [onSeeAll] is given, 16dp between cards. Same 48dp side padding
 * as [AppCarousel]; the row restores focus to the last-focused card on
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
) {
    Column(modifier = modifier.focusGroup()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Header 40 + 20 + 12 of focus-scale room = Figma's 72 to the cards' top edge.
                .padding(start = HomeContentStart, end = HomeContentEnd, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = HomeSectionTitleStyle,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f),
            )
            if (onSeeAll != null) {
                Text(
                    text = "See All ›",
                    style = SeeAllStyle,
                    color = AccentLavender,
                    modifier = Modifier.clickable(onClick = onSeeAll),
                )
            }
        }

        LazyRow(
            // Vertical padding leaves room for the focused card's 1.08x scale and shadow.
            contentPadding = PaddingValues(start = HomeContentStart, end = HomeContentEnd, top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.focusRestorer(),
        ) {
            items(items, key = { it.poster }) { item ->
                MediaCard(
                    title = item.title,
                    poster = item.poster,
                    progress = item.progress,
                    onClick = { onItemClick(item) },
                )
            }
        }
    }
}
