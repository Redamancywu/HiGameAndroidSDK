package com.horizon.higame.core.callback

/**
 * 分享回调接口
 */
interface HiGameShareCallback {
    /**
     * 分享成功
     * @param platform 分享平台
     */
    fun onShareSuccess(platform: String)
    
    /**
     * 分享失败
     * @param code 错误码
     * @param message 错误信息
     */
    fun onShareError(code: Int, message: String)
    
    /**
     * 分享取消
     */
    fun onShareCancel()
}