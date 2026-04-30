package com.despegue.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.despegue.tv.domain.model.AppFont
import com.despegue.tv.domain.model.AppTheme

data class DespegueExtendedColors(
    val backgroundElevated: Color,
    val backgroundCard: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val focusRing: Color,
    val focusBackground: Color,
    val rating: Color
)

val LocalDespegueColors = staticCompositionLocalOf {
    DespegueColorScheme(ThemeColors.Ocean)
}

val LocalDespegueExtendedColors = staticCompositionLocalOf {
    DespegueExtendedColors(
        backgroundElevated = Color(0xFF1A1A1A),
        backgroundCard = Color(0xFF242424),
        textSecondary = Color(0xFFB3B3B3),
        textTertiary = Color(0xFF808080),
        focusRing = ThemeColors.Ocean.focusRing,
        focusBackground = ThemeColors.Ocean.focusBackground,
        rating = Color(0xFFFFD700)
    )
}

val LocalAppTheme = staticCompositionLocalOf { AppTheme.WHITE }

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DespegueTheme(
    appTheme: AppTheme = AppTheme.WHITE,
    appFont: AppFont = AppFont.INTER,
    amoledMode: Boolean = false,
    amoledSurfacesMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val palette = ThemeColors.getColorPalette(appTheme)
    val colorScheme = DespegueColorScheme(palette, amoledMode, amoledSurfacesMode)

    val materialColorScheme = darkColorScheme(
        primary = colorScheme.Primary,
        onPrimary = colorScheme.OnPrimary,
        secondary = colorScheme.Secondary,
        onSecondary = colorScheme.OnSecondary,
        background = colorScheme.Background,
        surface = colorScheme.Surface,
        surfaceVariant = colorScheme.SurfaceVariant,
        onBackground = colorScheme.TextPrimary,
        onSurface = colorScheme.TextPrimary,
        onSurfaceVariant = colorScheme.TextSecondary,
        error = colorScheme.Error
    )

    val extendedColors = DespegueExtendedColors(
        backgroundElevated = colorScheme.BackgroundElevated,
        backgroundCard = colorScheme.BackgroundCard,
        textSecondary = colorScheme.TextSecondary,
        textTertiary = colorScheme.TextTertiary,
        focusRing = colorScheme.FocusRing,
        focusBackground = colorScheme.FocusBackground,
        rating = colorScheme.Rating
    )

    CompositionLocalProvider(
        LocalDespegueColors provides colorScheme,
        LocalDespegueExtendedColors provides extendedColors,
        LocalAppTheme provides appTheme
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = buildDespegueTypography(getFontFamily(appFont)),
            content = content
        )
    }
}

object DespegueTheme {
    val colors: DespegueColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalDespegueColors.current

    val extendedColors: DespegueExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalDespegueExtendedColors.current

    val currentTheme: AppTheme
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTheme.current
}
