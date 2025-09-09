package com.horizon.higame.core.callback

/**
 * 模块回调接口
 */
interface HiGameModuleCallback {
    /**
     * 模块初始化成功
     */
    fun onModuleInitSuccess()
    
    /**
     * 模块初始化失败
     * @param code 错误码
     * @param message 错误信息
     */
    fun onModuleInitError(code: Int, message: String)
}