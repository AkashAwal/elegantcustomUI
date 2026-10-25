package com.elegantgalaxy.tvlauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.ui.theme.NavigationStyle
import com.elegantgalaxy.tvlauncher.utils.rememberTvFocusVisuals
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

/**
 * Bar at the top of the content area, above the hero banner (it takes its
 * own row, so content starts below it): live clock plus a profile icon that
 * will open login/account once that screen exists ([onProfileClick] is a
 * caller-supplied no-op until then). The clock re-reads
 * the system time every few seconds so it flips within a few seconds of the
 * minute changing without needing a broadcast receiver.
 */
@Composable
fun TopBar(
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var time by remember { mutableStateOf(LocalTime.now().format(timeFormatter)) }
    LaunchedEffect(Unit) {
        while (true) {
            time = LocalTime.now().format(timeFormatter)
            delay(5_000)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 48.dp, end = 48.dp, top = 20.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = time,
            style = TimeStyle,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(end = 20.dp),
        )
        ProfileButton(onClick = onProfileClick)
    }
}

@Composable
private fun ProfileButton(onClick: () -> Unit) {
    val focusVisuals = rememberTvFocusVisuals(shape = CircleShape)

    Box(
        modifier = Modifier
            .size(44.dp)
            .then(focusVisuals.modifier)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .clickable(
                interactionSource = focusVisuals.interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = "Profile / sign in",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(40.dp),
        )
    }
}
