package com.horizon.higame.core.api

import android.app.Activity
import android.content.Context
import com.horizon.higame.core.callback.HiGameCallback
import com.horizon.higame.core.callback.HiGameInitCallback
import com.horizon.higame.core.internal.manager.HiGameSDKManager
import com.horizon.higame.core.model.HiGameResult
import com.horizon.higame.core.model.HiGameUser
import com.horizon.higame.core.utils.HiGameLogger

/**
 * HiGame SDK 对外统一入口
 * 门面模式：所有功能都通过此类访问
 */
class HiGameSDK private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: HiGameSDK? = null
        
        fun getInstance(): HiGameSDK {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HiGameSDK().also { INSTANCE = it }
            }
        }
    }
    
    private val sdkManager = HiGameSDKManager.getInstance()
    
    /**
     * 初始化 SDK
     * @param context 应用上下文
     * @param callback 初始化回调
     */
    fun initialize(context: Context, callback: HiGameInitCallback) {
        HiGameLogger.d("HiGameSDK initialize called")
        sdkManager.initialize(context, callback)
    }
    
    /**
     * 初始化 SDK（Activity 版本）
     * @param activity Activity 实例
     * @param callback 初始化回调
     */
    fun initialize(activity: Activity, callback: HiGameInitCallback) {
        HiGameLogger.d("HiGameSDK initialize with activity called")
        sdkManager.initialize(activity.applicationContext, callback)
    }
    
    /**
     * 登录
     * @param showUI 是否显示登录界面
     * @param callback 登录回调
     */
    fun login(showUI: Boolean, callback: HiGameCallback<HiGameUser>) {
        HiGameLogger.d("HiGameSDK login called, showUI: $showUI")
        sdkManager.login(showUI, callback)
    }
    
    /**
     * 登出
     * @param callback 登出回调
     */
    fun logout(callback: HiGameCallback<Boolean>) {
        HiGameLogger.d("HiGameSDK logout called")
        sdkManager.logout(callback)
    }
    
    /**
     * 获取当前登录用户
     * @return 用户信息，未登录返回 null
     */
    fun getCurrentUser(): HiGameUser? {
        return sdkManager.getCurrentUser()
    }
    
    /**
     * 检查是否已登录
     * @return 是否已登录
     */
    fun isLoggedIn(): Boolean {
        return sdkManager.isLoggedIn()
    }
    
    /**
     * 发起支付
     * @param orderInfo 订单信息
     * @param callback 支付回调
     */
    fun pay(orderInfo: Map<String, Any>, callback: HiGameCallback<String>) {
        HiGameLogger.d("HiGameSDK pay called")
        sdkManager.pay(orderInfo, callback)
    }
    
    /**
     * 查询订单状态
     * @param orderId 订单ID
     * @param callback 查询回调
     */
    fun queryOrder(orderId: String, callback: HiGameCallback<Map<String, Any>>) {
        HiGameLogger.d("HiGameSDK queryOrder called, orderId: $orderId")
        sdkManager.queryOrder(orderId, callback)
    }
    
    /**
     * 分享内容
     * @param shareInfo 分享信息
     * @param callback 分享回调
     */
    fun share(shareInfo: Map<String, Any>, callback: HiGameCallback<String>) {
        HiGameLogger.d("HiGameSDK share called")
        sdkManager.share(shareInfo, callback)
    }
    
    /**
     * 检查分享平台是否可用
     * @param platform 分享平台
     * @return 是否可用
     */
    fun isPlatformAvailable(platform: String): Boolean {
        return sdkManager.isPlatformAvailable(platform)
    }
    
    /**
     * 显示用户中心
     * @param context 上下文（Activity 或 Application Context，建议 Activity）
     * @param callback 操作回调
     */
    fun showUserCenter(context: Context, callback: HiGameCallback<String>) {
        HiGameLogger.d("HiGameSDK showUserCenter called")
        sdkManager.showUserCenter(context, callback)
    }
    
    /**
     * 更新用户信息
     * @param userInfo 用户信息
     * @param callback 更新回调
     */
    fun updateUserInfo(userInfo: HiGameUser, callback: HiGameCallback<Boolean>) {
        HiGameLogger.d("HiGameSDK updateUserInfo called")
        sdkManager.updateUserInfo(userInfo, callback)
    }
    
    /**
     * 绑定第三方账号
     * @param platform 平台类型
     * @param callback 绑定回调
     */
    fun bindThirdParty(platform: String, callback: HiGameCallback<Boolean>) {
        HiGameLogger.d("HiGameSDK bindThirdParty called, platform: $platform")
        sdkManager.bindThirdParty(platform, callback)
    }
    
    /**
     * 应用恢复时调用
     */
    fun onResume() {
        HiGameLogger.d("HiGameSDK onResume called")
        sdkManager.onResume()
    }
    
    /**
     * 应用暂停时调用
     */
    fun onPause() {
        HiGameLogger.d("HiGameSDK onPause called")
        sdkManager.onPause()
    }
    
    /**
     * 销毁 SDK，释放资源
     */
    fun destroy() {
        HiGameLogger.d("HiGameSDK destroy called")
        sdkManager.destroy()
    }
    
    /**
     * 获取 SDK 版本
     * @return SDK 版本号
     */
    fun getSDKVersion(): String {
        return sdkManager.getSDKVersion()
    }
}