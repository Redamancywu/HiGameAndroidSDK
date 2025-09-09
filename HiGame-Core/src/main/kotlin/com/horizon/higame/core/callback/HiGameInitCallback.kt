package com.horizon.higame.core.callback

/**
 * HiGame 初始化回调接口
 */
interface HiGameInitCallback {
    fun onSuccess(message: String)
    fun onError(code: Int, message: String)
}