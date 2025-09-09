package com.horizon.higame.core.callback

/**
 * HiGame 通用回调接口
 */
interface HiGameCallback<T> {
    /**
     * 成功回调
     * @param result 结果数据
     */
    fun onSuccess(result: T)
    
    /**
     * 失败回调
     * @param code 错误码
     * @param message 错误信息
     */
    fun onError(code: Int, message: String)
    
    /**
     * 取消回调（可选实现）
     */
    fun onCancel() {}
}