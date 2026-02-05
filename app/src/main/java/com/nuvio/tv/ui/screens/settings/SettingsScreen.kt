@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.nuvio.tv.ui.screens.settings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.nuvio.tv.ui.screens.plugin.PluginScreenContent
import com.nuvio.tv.ui.screens.plugin.PluginViewModel
import com.nuvio.tv.ui.theme.NuvioColors

private enum class SettingsCategory(val displayName: String, val icon: ImageVector) {
    APPEARANCE("Appearance", Icons.Default.Palette),
    PLUGINS("Plugins", Icons.Default.Build),
    TMDB("TMDB Enrichment", Icons.Default.Tune),
    PLAYBACK("Playback", Icons.Default.Settings),
    ABOUT("About", Icons.Default.Info)
}

@Composable
fun SettingsScreen(
    onNavigateToPlugins: () -> Unit = {},
) {
    var selectedCategory by remember { mutableStateOf(SettingsCategory.APPEARANCE) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(NuvioColors.Background)
    ) {
        // Left sidebar
        Column(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
                .padding(start = 40.dp, top = 32.dp, bottom = 24.dp, end = 16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                color = NuvioColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            SettingsCategory.entries.forEach { category ->
                SidebarCategoryItem(
                    category = category,
                    isSelected = selectedCategory == category,
                    onClick = {
                        selectedCategory = category
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Right content panel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, end = 32.dp, bottom = 16.dp)
                .background(
                    color = NuvioColors.BackgroundCard,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(32.dp)
        ) {
            Crossfade(targetState = selectedCategory, label = "settings_content") { category ->
                when (category) {
                    SettingsCategory.APPEARANCE -> ThemeSettingsContent()
                    SettingsCategory.PLAYBACK -> PlaybackSettingsContent()
                    SettingsCategory.TMDB -> TmdbSettingsContent()
                    SettingsCategory.ABOUT -> AboutSettingsContent()
                    SettingsCategory.PLUGINS -> PluginScreenContent()
                }
            }
        }
    }
}

@Composable
private fun SidebarCategoryItem(
    category: SettingsCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        colors = CardDefaults.colors(
            containerColor = if (isSelected) NuvioColors.FocusBackground else Color.Transparent,
            focusedContainerColor = NuvioColors.FocusBackground
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(2.dp, NuvioColors.FocusRing),
                shape = RoundedCornerShape(12.dp)
            )
        ),
        shape = CardDefaults.shape(RoundedCornerShape(12.dp)),
        scale = CardDefaults.scale(focusedScale = 1.0f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected || isFocused) NuvioColors.Secondary else NuvioColors.TextSecondary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = category.displayName,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected || isFocused) NuvioColors.TextPrimary else NuvioColors.TextSecondary
            )
        }
    }
}
