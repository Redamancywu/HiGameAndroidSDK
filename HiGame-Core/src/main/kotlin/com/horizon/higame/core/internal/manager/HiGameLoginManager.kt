package com.horizon.higame.core.internal.manager

import com.horizon.higame.core.callback.HiGameCallback
import com.horizon.higame.core.internal.service.HiGameServiceRegistry
import com.horizon.higame.core.model.HiGameUser
import com.horizon.higame.core.utils.HiGameLogger

/**
 * 登录功能管理器
 * 第三层：功能层 - 负责登录相关的所有功能
 */
internal class HiGameLoginManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: HiGameLoginManager? = null
        
        fun getInstance(): HiGameLoginManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HiGameLoginManager().also { INSTANCE = it }
            }
        }
    }
    
    /**
     * 登录
     * @param showUI 是否显示登录界面
     * @param callback 登录回调
     */
    fun login(showUI: Boolean, callback: HiGameCallback<HiGameUser>) {
        val loginService = HiGameServiceRegistry.getLoginService()
        if (loginService != null) {
            HiGameLogger.d("Delegating login to service: ${loginService::class.simpleName}")
            loginService.login(showUI, object : com.horizon.higame.core.callback.HiGameLoginCallback {
                override fun onLoginSuccess(user: com.horizon.higame.core.model.HiGameUser) {
                    callback.onSuccess(user)
                }
                override fun onLoginError(code: Int, message: String) {
                    callback.onError(code, message)
                }
                override fun onLoginCancel() {
                    callback.onError(-2, "Login cancelled")
                }
            })
        } else {
            HiGameLogger.e("Login service not found")
            callback.onError(-1, "Login service not available")
        }
    }
    
    /**
     * 登出
     * @param callback 登出回调
     */
    fun logout(callback: HiGameCallback<Boolean>) {
        val loginService = HiGameServiceRegistry.getLoginService()
        if (loginService != null) {
            HiGameLogger.d("Delegating logout to service: ${loginService::class.simpleName}")
            loginService.logout(object : com.horizon.higame.core.callback.HiGameLoginCallback {
                override fun onLoginSuccess(user: com.horizon.higame.core.model.HiGameUser) {
                    callback.onSuccess(true)
                }
                override fun onLoginError(code: Int, message: String) {
                    callback.onError(code, message)
                }
                override fun onLoginCancel() {
                    callback.onError(-2, "Logout cancelled")
                }
            })
        } else {
            HiGameLogger.e("Login service not found")
            callback.onError(-1, "Login service not available")
        }
    }
    
    /**
     * 获取当前登录用户信息
     * @return 用户信息，未登录返回 null
     */
    fun getCurrentUser(callback: HiGameCallback<HiGameUser?>) {
        val loginService = HiGameServiceRegistry.getLoginService()
        if (loginService != null) {
            loginService.getCurrentUser(callback)
        } else {
            callback.onError(-1, "Login service not available")
        }
    }
    
    /**
     * 检查是否已登录
     * @return 是否已登录
     */
    fun isLoggedIn(callback: HiGameCallback<Boolean>) {
        val loginService = HiGameServiceRegistry.getLoginService()
        if (loginService != null) {
            loginService.isLoggedIn(callback)
        } else {
            callback.onError(-1, "Login service not available")
        }
    }
}