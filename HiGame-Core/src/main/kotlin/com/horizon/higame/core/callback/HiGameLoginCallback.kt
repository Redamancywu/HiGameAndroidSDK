package com.horizon.higame.core.callback

import com.horizon.higame.core.model.HiGameUser

/**
 * 登录回调接口
 */
interface HiGameLoginCallback {
    /**
     * 登录成功
     * @param user 用户信息
     */
    fun onLoginSuccess(user: HiGameUser)
    
    /**
     * 登录失败
     * @param code 错误码
     * @param message 错误信息
     */
    fun onLoginError(code: Int, message: String)
    
    /**
     * 登录取消
     */
    fun onLoginCancel()
}