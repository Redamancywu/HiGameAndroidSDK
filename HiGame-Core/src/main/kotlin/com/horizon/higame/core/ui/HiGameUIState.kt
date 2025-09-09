package com.horizon.higame.core.ui

/**
 * 通用 UI 状态协议（轻量，无框架依赖）
 * - 供 UI 层/引擎侧理解状态流转（加载/成功/错误/空）
 */
interface HiGameUIState<T> {
    fun onLoading()
    fun onSuccess(data: T)
    fun onError(message: String)
    fun onEmpty()
}