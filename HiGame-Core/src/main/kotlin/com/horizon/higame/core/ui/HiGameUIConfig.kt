package com.horizon.higame.core.ui

import java.util.concurrent.atomic.AtomicReference

/**
 * UI 全局配置入口（线程安全）
 * - 宿主/游戏可在初始化阶段统一设置主题与动画偏好
 * - UI 模块在渲染时读取当前配置，保持一致风格
 */
object HiGameUIConfig {

    private val themeRef = AtomicReference(HiGameTheme())
    private val defaultEnterAnimationRef = AtomicReference(HiGameAnimations.fadeIn())
    private val defaultExitAnimationRef = AtomicReference(HiGameAnimations.fadeIn())

    @JvmStatic
    fun getTheme(): HiGameTheme = themeRef.get()

    @JvmStatic
    fun setTheme(theme: HiGameTheme) {
        themeRef.set(theme)
    }

    @JvmStatic
    fun setDarkThemeEnabled(enabled: Boolean) {
        if (enabled) {
            themeRef.set(HiGameTheme.darkDefaults())
        }
    }

    @JvmStatic
    fun getDefaultEnterAnimation(): HiGameAnimations.Spec = defaultEnterAnimationRef.get()

    @JvmStatic
    fun setDefaultEnterAnimation(spec: HiGameAnimations.Spec) {
        defaultEnterAnimationRef.set(spec)
    }

    @JvmStatic
    fun getDefaultExitAnimation(): HiGameAnimations.Spec = defaultExitAnimationRef.get()

    @JvmStatic
    fun setDefaultExitAnimation(spec: HiGameAnimations.Spec) {
        defaultExitAnimationRef.set(spec)
    }
}