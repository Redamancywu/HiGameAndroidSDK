package com.horizon.higame.core.ui

import androidx.annotation.ColorInt

/**
 * 主题系统（核心契约）
 * - 仅承载“配置值”，UI 层负责将其映射为具体 UI 属性（MaterialTheme、dp/sp 等）
 */
data class HiGameTheme(
    val colors: Colors = Colors(),
    val typography: Typography = Typography(),
    val shapes: Shapes = Shapes()
) {
    data class Colors(
        @ColorInt val primary: Int = 0xFF4CAF50.toInt(),
        @ColorInt val secondary: Int = 0xFF03A9F4.toInt(),
        @ColorInt val accent: Int = 0xFFFFC107.toInt(),
        @ColorInt val background: Int = 0xFFFFFFFF.toInt(),
        @ColorInt val surface: Int = 0xFFFFFFFF.toInt(),
        @ColorInt val error: Int = 0xFFEF5350.toInt(),
        @ColorInt val onPrimary: Int = 0xFFFFFFFF.toInt(),
        @ColorInt val onSecondary: Int = 0xFF000000.toInt(),
        @ColorInt val onBackground: Int = 0xFF000000.toInt(),
        @ColorInt val onSurface: Int = 0xFF000000.toInt()
    )

    /**
     * 字体字号（单位：sp 数值，UI 层再转换）
     */
    data class Typography(
        val h1Sp: Int = 28,
        val h2Sp: Int = 24,
        val h3Sp: Int = 20,
        val h4Sp: Int = 18,
        val h5Sp: Int = 16,
        val h6Sp: Int = 14,
        val body1Sp: Int = 16,
        val body2Sp: Int = 14,
        val captionSp: Int = 12,
        val overlineSp: Int = 10,
        val fontFamily: String = "sans-serif"
    )

    /**
     * 圆角（单位：dp 数值，UI 层再转换）
     */
    data class Shapes(
        val smallCornerDp: Int = 6,
        val mediumCornerDp: Int = 10,
        val largeCornerDp: Int = 16
    )

    companion object {
        /**
         * 提供一个深色主题的预设，便于快速切换
         */
        fun darkDefaults(): HiGameTheme {
            return HiGameTheme(
                colors = Colors(
                    primary = 0xFF80CBC4.toInt(),
                    secondary = 0xFF90CAF9.toInt(),
                    accent = 0xFFFFD54F.toInt(),
                    background = 0xFF121212.toInt(),
                    surface = 0xFF1E1E1E.toInt(),
                    error = 0xFFCF6679.toInt(),
                    onPrimary = 0xFF000000.toInt(),
                    onSecondary = 0xFF000000.toInt(),
                    onBackground = 0xFFFFFFFF.toInt(),
                    onSurface = 0xFFFFFFFF.toInt()
                )
            )
        }
    }
}