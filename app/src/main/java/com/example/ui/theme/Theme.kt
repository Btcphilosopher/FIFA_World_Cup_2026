package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WorldCupColorScheme = darkColorScheme(
    primary = ArenaPrimary,
    onPrimary = Color(0xFF030D1B),
    primaryContainer = Color(0x2000F5D4),
    onPrimaryContainer = ArenaPrimary,
    secondary = ArenaSecondary,
    onSecondary = Color(0xFF030D1B),
    secondaryContainer = Color(0x201FDF64),
    onSecondaryContainer = ArenaSecondary,
    tertiary = ArenaTertiary,
    onTertiary = Color(0xFF030D1B),
    background = ArenaDarkBg,
    onBackground = ArenaTextPrimary,
    surface = ArenaCardBg,
    onSurface = ArenaTextPrimary,
    surfaceVariant = ArenaBorder,
    onSurfaceVariant = ArenaTextSecondary,
    outline = ArenaBorder,
    error = ArenaAccentRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force premium dark mode for sports-broadcast energy
    dynamicColor: Boolean = false, // Disable dynamic colors to preserve official FIFA brand system
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WorldCupColorScheme,
        typography = Typography,
        content = content
    )
}
