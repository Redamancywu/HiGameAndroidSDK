package com.horizon.higame.core.callback

/**
 * 查询回调接口
 */
interface HiGameQueryCallback<T> {
    /**
     * 查询成功
     * @param result 查询结果
     */
    fun onQuerySuccess(result: T)
    
    /**
     * 查询失败
     * @param code 错误码
     * @param message 错误信息
     */
    fun onQueryError(code: Int, message: String)
}