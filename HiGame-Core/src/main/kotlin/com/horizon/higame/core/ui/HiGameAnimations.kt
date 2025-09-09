package com.horizon.higame.core.ui

/**
 * 动画系统（描述型 Spec）
 * - 不直接暴露 Android Animation/Animator，避免与 Compose 冲突
 * - UI 层负责将 Spec 解释为具体动画实现（AnimatedVisibility/EnterTransition/animate*AsState 等）
 */
object HiGameAnimations {
    const val DURATION_SHORT = 150
    const val DURATION_MEDIUM = 300
    const val DURATION_LONG = 500

    enum class Type {
        FadeIn,
        SlideUp,
        ScaleIn
    }

    data class Spec(
        val type: Type,
        val durationMs: Int = DURATION_MEDIUM,
        val delayMs: Int = 0
    )

    @JvmStatic
    fun fadeIn(durationMs: Int = DURATION_MEDIUM, delayMs: Int = 0): Spec =
        Spec(Type.FadeIn, durationMs, delayMs)

    @JvmStatic
    fun slideUp(durationMs: Int = DURATION_MEDIUM, delayMs: Int = 0): Spec =
        Spec(Type.SlideUp, durationMs, delayMs)

    @JvmStatic
    fun scaleIn(durationMs: Int = DURATION_MEDIUM, delayMs: Int = 0): Spec =
        Spec(Type.ScaleIn, durationMs, delayMs)
}