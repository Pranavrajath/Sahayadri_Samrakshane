package com.sahyadri.samrakshane.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary          = ForestGreen400,
    onPrimary        = ForestGreen900,
    primaryContainer = ForestGreen700,
    onPrimaryContainer = ForestGreen100,

    secondary        = SageGreen,
    onSecondary      = ForestGreen900,
    secondaryContainer = MossGreen,
    onSecondaryContainer = MintDew,

    tertiary         = EarthSand,
    onTertiary       = EarthBrown700,
    tertiaryContainer = EarthBrown500,
    onTertiaryContainer = EarthSand,

    background       = DarkForest,
    onBackground     = ForestGreen100,

    surface          = SurfaceDark,
    onSurface        = ForestGreen100,
    surfaceVariant   = CardDark,
    onSurfaceVariant = ForestGreen300,

    outline          = FernGreen,
    outlineVariant   = MossGreen,

    error            = FireOrange,
    onError          = Color.White,
    errorContainer   = Color(0xFF4A1200),
    onErrorContainer = Color(0xFFFFB4AB),

    inverseSurface   = ForestGreen100,
    inverseOnSurface = ForestGreen800,
    inversePrimary   = ForestGreen600,

    scrim            = Color(0xFF000000)
)

private val LightColorScheme = lightColorScheme(
    primary          = ForestGreen600,
    onPrimary        = Color.White,
    primaryContainer = ForestGreen100,
    onPrimaryContainer = ForestGreen800,

    secondary        = MossGreen,
    onSecondary      = Color.White,
    secondaryContainer = ForestGreen100,
    onSecondaryContainer = MossGreen,

    tertiary         = EarthBrown500,
    onTertiary       = Color.White,
    tertiaryContainer = Color(0xFFFFDDBC),
    onTertiaryContainer = EarthBrown700,

    background       = LeafWhite,
    onBackground     = ForestGreen900,

    surface          = ParchmentWhite,
    onSurface        = ForestGreen900,
    surfaceVariant   = ForestGreen50,
    onSurfaceVariant = ForestGreen700,

    outline          = SageGreen,
    outlineVariant   = ForestGreen200,

    error            = FireRed,
    onError          = Color.White,
)

@Composable
fun SahyadriSamrakshaneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SahyadriTypography,
        content = content
    )
}
