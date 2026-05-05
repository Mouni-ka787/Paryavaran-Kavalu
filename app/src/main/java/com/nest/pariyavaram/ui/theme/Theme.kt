package com.nest.pariyavaram.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// ── Typography ──────────────────────────────────────────────────────────────
val AppTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = 26.sp,
        lineHeight = 34.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 22.sp,
        lineHeight = 28.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 18.sp,
        lineHeight = 24.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 17.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 15.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        letterSpacing = 0.1.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        letterSpacing = 0.5.sp
    )
)

// ── Light Color Scheme ────────────────────────────────────────────────────────
private val LightColors = lightColorScheme(
    primary          = MossGreen,
    onPrimary        = CloudWhite,
    primaryContainer = PaleGreen,
    onPrimaryContainer = ForestGreen,
    secondary        = CleanedTeal,
    onSecondary      = CloudWhite,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF004D40),
    tertiary         = EarthAmber,
    onTertiary       = CloudWhite,
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = Color(0xFFBF360C),
    background       = CloudWhite,
    onBackground     = Soil,
    surface          = CloudWhite,
    onSurface        = Soil,
    surfaceVariant   = PaleGreen,
    onSurfaceVariant = Bark,
    outline          = Pebble,
    outlineVariant   = Mist,
    error            = WarnRed,
    onError          = CloudWhite,
    errorContainer   = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C),
    inverseSurface   = DeepNight,
    inverseOnSurface = PaleGreen,
    inversePrimary   = MintGreen,
)

// ── Dark Color Scheme ─────────────────────────────────────────────────────────
private val DarkColors = darkColorScheme(
    primary          = MintGreen,
    onPrimary        = ForestGreen,
    primaryContainer = MossGreen,
    onPrimaryContainer = PaleGreen,
    secondary        = Color(0xFF80CBC4),
    onSecondary      = Color(0xFF004D40),
    secondaryContainer = CleanedTeal,
    onSecondaryContainer = Color(0xFFE0F2F1),
    tertiary         = Color(0xFFFFCC80),
    onTertiary       = Color(0xFF7F3200),
    background       = DeepNight,
    onBackground     = PaleGreen,
    surface          = NightSurface,
    onSurface        = MintGreen,
    surfaceVariant   = NightCard,
    onSurfaceVariant = MintGreen,
    outline          = Color(0xFF4A7A4A),
    error            = Color(0xFFEF9A9A),
    onError          = Color(0xFF7F0000),
    inverseSurface   = PaleGreen,
    inverseOnSurface = ForestGreen,
    inversePrimary   = MossGreen,
)

// ── App Theme ─────────────────────────────────────────────────────────────────
@Composable
fun ParyavaranKavaluTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else      -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = AppTypography,
        content     = content
    )
}