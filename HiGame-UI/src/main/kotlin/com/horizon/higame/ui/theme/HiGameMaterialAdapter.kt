package com.horizon.higame.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.horizon.higame.core.ui.HiGameTheme

/**
 * 将 Core 的 HiGameTheme 映射到 Compose Material3
 */
@Composable
fun HiGameMaterialAdapter(
    theme: HiGameTheme,
    content: @Composable () -> Unit
) {
    val isDark = theme.colors.background == 0xFF121212.toInt()
    val colors = if (isDark) {
        darkColorScheme(
            primary = Color(theme.colors.primary),
            secondary = Color(theme.colors.secondary),
            background = Color(theme.colors.background),
            surface = Color(theme.colors.surface),
            error = Color(theme.colors.error),
            onPrimary = Color(theme.colors.onPrimary),
            onSecondary = Color(theme.colors.onSecondary)
        )
    } else {
        lightColorScheme(
            primary = Color(theme.colors.primary),
            secondary = Color(theme.colors.secondary),
            background = Color(theme.colors.background),
            surface = Color(theme.colors.surface),
            error = Color(theme.colors.error),
            onPrimary = Color(theme.colors.onPrimary),
            onSecondary = Color(theme.colors.onSecondary)
        )
    }

    MaterialTheme(
        colorScheme = colors,
        // 可进一步映射 Typography/Shapes，如需更细化可扩展
        content = content
    )
}