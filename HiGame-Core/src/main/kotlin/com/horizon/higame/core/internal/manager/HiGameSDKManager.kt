package com.horizon.higame.core.internal.manager

import android.R
import android.content.Context
import com.horizon.higame.core.callback.HiGameCallback
import com.horizon.higame.core.callback.HiGameInitCallback
import com.horizon.higame.core.internal.config.HiGameConfigManager
import com.horizon.higame.core.internal.state.HiGameStateManager
import com.horizon.higame.core.internal.security.HiGameSecurityManager
import com.horizon.higame.core.internal.monitor.HiGameMonitorManager
import com.horizon.higame.core.internal.version.HiGameVersionManager
import com.horizon.higame.core.internal.data.HiGameDataManager
import com.horizon.higame.core.internal.communication.HiGameEventBus
import com.horizon.higame.core.model.HiGameUser
import com.horizon.higame.core.utils.HiGameLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow

/**
 * HiGame SDK 核心管理器
 * 负责协调各个功能模块的工作
 */
class HiGameSDKManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: HiGameSDKManager? = null
        
        fun getInstance(): HiGameSDKManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HiGameSDKManager().also { INSTANCE = it }
            }
        }
    }
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // 各功能管理器
    private val configManager = HiGameConfigManager
    private val stateManager = HiGameStateManager.getInstance()
    private val securityManager = HiGameSecurityManager.getInstance()
    private val monitorManager = HiGameMonitorManager.getInstance()
    private val versionManager = HiGameVersionManager.getInstance()
    private val dataManager = HiGameDataManager.getInstance()
    private val eventBus = HiGameEventBus.getInstance()
    
    private val loginManager = HiGameLoginManager.getInstance()
    @Volatile private var cachedUser: HiGameUser? = null
    @Volatile private var cachedLoggedIn: Boolean = false
    private val payManager = HiGamePayManager.getInstance()
    private val shareManager = HiGameShareManager.getInstance()
    private val userCenterManager = HiGameUserCenterManager.getInstance()
    
    @Volatile
    private var isInitialized = false
    private var applicationContext: Context? = null
    
    /**
     * 初始化 SDK
     */
    fun initialize(context: Context, callback: HiGameInitCallback) {
        if (isInitialized) {
            HiGameLogger.w("SDK already initialized")
            callback.onSuccess("SDK already initialized")
            return
        }
        
        applicationContext = context.applicationContext
        
        scope.launch {
            try {
                HiGameLogger.i("Starting SDK initialization...")
                
                // 1. 初始化配置管理器
                configManager.initialize(context)
                
                // 2. 初始化安全管理器
                securityManager.initialize(context)
                
                // 3. 初始化数据管理器
                dataManager.initialize(context)
                
                // 4. 初始化状态管理器
                stateManager.initialize()
                
                // 5. 初始化监控管理器
                monitorManager.initialize(context)
                
                // 6. 初始化版本管理器
                versionManager.initialize(context)
                
                // 7. 初始化事件总线
                eventBus.initialize()
                
                // 8. 初始化各功能管理器
                // 功能管理器不需要单独初始化，它们通过服务注册表获取服务
                HiGameLogger.d("Functional managers initialized")
                
                isInitialized = true
                
                HiGameLogger.i("SDK initialization completed successfully")
                callback.onSuccess("SDK initialization completed")
                
                // 发送初始化完成事件
                eventBus.post("sdk_initialized", mapOf("version" to getSDKVersion()))
                
            } catch (e: Exception) {
                HiGameLogger.e("SDK initialization failed", e)
                callback.onError(-1, "SDK initialization failed: ${e.message}")
            }
        }
    }
    
    /**
     * 登录
     */
    fun login(showUI: Boolean, callback: HiGameCallback<HiGameUser>) {
        checkInitialized {
            loginManager.login(showUI, object : HiGameCallback<HiGameUser> {
                override fun onSuccess(data: HiGameUser) {
                    cachedUser = data
                    cachedLoggedIn = true
                    callback.onSuccess(data)
                }
                override fun onError(code: Int, message: String) {
                    cachedLoggedIn = false
                    callback.onError(code, message)
                }
            })
        }
    }
    
    /**
     * 登出
     */
    fun logout(callback: HiGameCallback<Boolean>) {
        checkInitialized {
            loginManager.logout(object : HiGameCallback<Boolean> {
                override fun onSuccess(data: Boolean) {
                    cachedLoggedIn = false
                    cachedUser = null
                    callback.onSuccess(data)
                }
                override fun onError(code: Int, message: String) {
                    callback.onError(code, message)
                }
            })
        }
    }
    
    /**
     * 获取当前登录用户
     */
    fun getCurrentUser(): HiGameUser? {
        return if (isInitialized) cachedUser else null
    }
    
    /**
     * 检查是否已登录
     */
    fun isLoggedIn(): Boolean {
        return if (isInitialized) cachedLoggedIn else false
    }
    
    /**
     * 发起支付
     */
    fun pay(orderInfo: Map<String, Any>, callback: HiGameCallback<String>) {
        checkInitialized {
            payManager.pay(orderInfo, callback)
        }
    }
    
    /**
     * 查询订单状态
     */
    fun queryOrder(orderId: String, callback: HiGameCallback<Map<String, Any>>) {
        checkInitialized {
            payManager.queryOrder(orderId, callback)
        }
    }
    
    /**
     * 分享内容
     */
    fun share(shareInfo: Map<String, Any>, callback: HiGameCallback<String>) {
        checkInitialized {
            shareManager.share(shareInfo, callback)
        }
    }
    
    /**
     * 检查分享平台是否可用
     */
    fun isPlatformAvailable(platform: String): Boolean {
       return true
    }
    
    /**
     * 显示用户中心
     */
    fun showUserCenter(context: Context, callback: HiGameCallback<String>) {
        checkInitialized {
            userCenterManager.showUserCenter(context, callback)
        }
    }
    
    /**
     * 更新用户信息
     */
    fun updateUserInfo(userInfo: HiGameUser, callback: HiGameCallback<Boolean>) {
        checkInitialized {
            userCenterManager.updateUserInfo(userInfo, callback)
        }
    }
    
    /**
     * 绑定第三方账号
     * 注意：避免使用 also/apply/let 等导致返回 Any 的链式写法，保持纯透传
     */
    fun bindThirdParty(platform: String, callback: HiGameCallback<Boolean>) {
        checkInitialized {
            // 显式透传，避免返回值推断为 Any
            userCenterManager.bindThirdParty(platform, callback)
        }
    }
    
    /**
     * 应用恢复时调用
     */
    fun onResume() {
        if (!isInitialized) return
        
        // 功能管理器不需要生命周期管理
        HiGameLogger.d("Functional managers resumed")
        
        eventBus.post("app_resumed", emptyMap<String, Any>())
    }
    
    /**
     * 应用暂停时调用
     */
    fun onPause() {
        if (!isInitialized) return
        
        // 功能管理器不需要生命周期管理
        HiGameLogger.d("Functional managers paused")
        
        eventBus.post("app_paused", emptyMap<String, Any>())
    }
    
    /**
     * 销毁 SDK，释放资源
     */
    fun destroy() {
        if (!isInitialized) return
        
        scope.launch {
            try {
                HiGameLogger.i("Destroying SDK...")
                
                // 销毁各功能管理器
                // 功能管理器无显式销毁逻辑
                HiGameLogger.d("Functional managers cleanup done")
                
                // 销毁核心组件
                eventBus.destroy()
                // 清理核心组件（存在 destroy 的才调用）
                eventBus.destroy()
                monitorManager.destroy()
                dataManager.destroy()
                stateManager.destroy()
                securityManager.destroy()
                // configManager 无显式 destroy
                
                isInitialized = false
                applicationContext = null
                
                HiGameLogger.i("SDK destroyed successfully")
                
            } catch (e: Exception) {
                HiGameLogger.e("Error during SDK destruction", e)
            } finally {
                scope.cancel()
            }
        }
    }
    
    /**
     * 获取 SDK 版本
     */
    fun getSDKVersion(): String {
        return "1.0.0"
    }
    
    /**
     * 获取初始化状态
     */
    fun getInitializationState(): StateFlow<Boolean> {
        return stateManager.stateInitialization()
    }
    
    /**
     * 检查是否已初始化，未初始化则不执行操作
     */
    private inline fun checkInitialized(action: () -> Unit) {
        if (isInitialized) {
            action()
        } else {
            HiGameLogger.w("SDK not initialized, operation ignored")
        }
    }
}