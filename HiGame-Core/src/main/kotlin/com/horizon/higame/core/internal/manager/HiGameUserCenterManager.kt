package com.horizon.higame.core.internal.manager

import com.horizon.higame.core.callback.HiGameCallback
import com.horizon.higame.core.internal.service.HiGameServiceRegistry
import com.horizon.higame.core.model.HiGameUser
import com.horizon.higame.core.utils.HiGameLogger

/**
 * 用户中心功能管理器
 * 第三层：功能层 - 负责用户中心相关的所有功能
 */
internal class HiGameUserCenterManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: HiGameUserCenterManager? = null
        
        fun getInstance(): HiGameUserCenterManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HiGameUserCenterManager().also { INSTANCE = it }
            }
        }
    }
    
    /**
     * 显示用户中心
     * @param callback 操作回调
     */
    fun showUserCenter(context: android.content.Context, callback: HiGameCallback<String>) {
        val userCenterService = HiGameServiceRegistry.getUserCenterService()
        if (userCenterService != null) {
            HiGameLogger.d("Delegating showUserCenter to service: ${userCenterService::class.simpleName}")
            userCenterService.showUserCenter(context, callback)
        } else {
            HiGameLogger.e("UserCenter service not found")
            callback.onError(-1, "UserCenter service not available")
        }
    }
    
    /**
     * 更新用户信息
     * @param userInfo 用户信息
     * @param callback 更新回调
     */
    fun updateUserInfo(userInfo: HiGameUser, callback: HiGameCallback<Boolean>) {
        val userCenterService = HiGameServiceRegistry.getUserCenterService()
        if (userCenterService != null) {
            HiGameLogger.d("Delegating updateUserInfo to service: ${userCenterService::class.simpleName}")
            userCenterService.updateUserInfo(userInfo, callback)
        } else {
            HiGameLogger.e("UserCenter service not found")
            callback.onError(-1, "UserCenter service not available")
        }
    }
    
    /**
     * 绑定第三方账号
     * @param platform 平台类型
     * @param callback 绑定回调
     */
    fun bindThirdParty(platform: String, callback: HiGameCallback<Boolean>) {
        val userCenterService = HiGameServiceRegistry.getUserCenterService()
        if (userCenterService != null) {
            HiGameLogger.d("Delegating bindThirdParty to service: ${userCenterService::class.simpleName}")
            userCenterService.bindThirdParty(platform, callback)
        } else {
            HiGameLogger.e("UserCenter service not found")
            callback.onError(-1, "UserCenter service not available")
        }
    }
}