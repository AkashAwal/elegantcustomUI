package com.elegantgalaxy.tvlauncher.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.elegantgalaxy.tvlauncher.ui.components.PrimaryButton
import com.elegantgalaxy.tvlauncher.ui.components.SecondaryButton
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated2
import com.elegantgalaxy.tvlauncher.ui.theme.SurfaceElevated3

/**
 * In-launcher "install this app" card for a [FamousApp] that isn't on the device. A launcher can't
 * install apps itself, so [onInstall] hands off to the Play Store listing; this just frames the
 * step in the launcher's own UI first. Focus starts on Install; Back or "Not now" dismisses.
 */
@Composable
fun AppInstallPanel(
    app: FamousApp,
    onInstall: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val installFocus = remember { FocusRequester() }
    val brandLogo = remember(app.key) { context.resources.getIdentifier("ic_brand_${app.key}", "drawable", context.packageName) }
    BackHandler(onBack = onDismiss)
    LaunchedEffect(app) { installFocus.requestFocus() }

    Box(
        modifier = modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 880.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(SurfaceElevated2)
                .padding(40.dp)
                .focusGroup()
                // Keep D-Pad focus on the panel while it is open.
                .focusProperties { onExit = { cancelFocusChange() } },
            horizontalArrangement = Arrangement.spacedBy(36.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 200.dp, height = 200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (brandLogo != 0) SurfaceElevated3 else app.color),
                contentAlignment = Alignment.Center,
            ) {
                if (brandLogo != 0) {
                    Image(painter = painterResource(brandLogo), contentDescription = app.label, modifier = Modifier.size(150.dp))
                } else {
                    Text(
                        text = app.label,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = app.label,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = "Not installed on this TV",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = app.description,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    PrimaryButton(
                        text = "Install from Play Store",
                        onClick = onInstall,
                        modifier = Modifier.focusRequester(installFocus),
                        width = Dp.Unspecified,
                        height = 52.dp,
                        cornerRadius = 16.dp,
                        horizontalPadding = 24.dp,
                    )
                    SecondaryButton(
                        text = "Not now",
                        onClick = onDismiss,
                        width = Dp.Unspecified,
                        height = 52.dp,
                        cornerRadius = 16.dp,
                        horizontalPadding = 24.dp,
                    )
                }
            }
        }
    }
}
