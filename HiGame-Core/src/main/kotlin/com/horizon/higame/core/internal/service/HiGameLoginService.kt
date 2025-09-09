package com.horizon.higame.core.internal.service

import com.horizon.higame.core.callback.HiGameCallback
import com.horizon.higame.core.callback.HiGameLoginCallback
import com.horizon.higame.core.model.HiGameUser

/**
 * 登录服务接口
 * 登录模块需要实现的具体接口
 */
internal interface HiGameLoginService : HiGameBaseService {
    
    /**
     * 执行登录
     * @param showUI 是否显示UI界面
     * @param callback 登录结果回调
     */
    fun login(showUI: Boolean, callback: HiGameLoginCallback)
    
    /**
     * 执行登出
     * @param callback 登出结果回调
     */
    fun logout(callback: HiGameLoginCallback)
    
    /**
     * 获取当前用户信息
     * @param callback 获取用户信息回调
     */
    fun getCurrentUser(callback: HiGameCallback<HiGameUser?>)
    
    /**
     * 检查登录状态
     * @param callback 检查结果回调
     */
    fun isLoggedIn(callback: HiGameCallback<Boolean>)
}