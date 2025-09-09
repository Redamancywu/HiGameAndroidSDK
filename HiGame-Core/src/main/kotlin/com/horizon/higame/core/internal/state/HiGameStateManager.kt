package com.horizon.higame.core.internal.state

import com.horizon.higame.core.model.HiGameUser
import com.horizon.higame.core.utils.HiGameLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * HiGame 状态管理器
 * 使用 StateFlow 管理 SDK 的各种状态
 */
class HiGameStateManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: HiGameStateManager? = null
        
        fun getInstance(): HiGameStateManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HiGameStateManager().also { INSTANCE = it }
            }
        }
    }
    
    // SDK 初始化状态
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()
    
    // 登录状态
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    // 当前用户
    private val _currentUser = MutableStateFlow<HiGameUser?>(null)
    val currentUser: StateFlow<HiGameUser?> = _currentUser.asStateFlow()
    
    // 网络状态
    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()
    
    // 支付状态
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()
    
    // 分享状态
    private val _shareState = MutableStateFlow<ShareState>(ShareState.Idle)
    val shareState: StateFlow<ShareState> = _shareState.asStateFlow()
    
    // 用户中心状态
    private val _userCenterState = MutableStateFlow<UserCenterState>(UserCenterState.Idle)
    val userCenterState: StateFlow<UserCenterState> = _userCenterState.asStateFlow()
    
    // 应用生命周期状态
    private val _appLifecycleState = MutableStateFlow<AppLifecycleState>(AppLifecycleState.Created)
    val appLifecycleState: StateFlow<AppLifecycleState> = _appLifecycleState.asStateFlow()
    
    // 自定义状态存储
    private val customStates = ConcurrentHashMap<String, MutableStateFlow<Any>>()
    
    /**
     * 初始化状态管理器
     */
    fun initialize() {
        HiGameLogger.d("StateManager initialized")
        _isInitialized.value = true
    }
    
    /**
     * 设置登录状态
     */
    fun setLoginState(isLoggedIn: Boolean, user: HiGameUser? = null) {
        _isLoggedIn.value = isLoggedIn
        _currentUser.value = user
        HiGameLogger.d("Login state updated: $isLoggedIn, user: ${user?.userId}")
    }
    
    /**
     * 更新当前用户信息
     */
    fun updateCurrentUser(user: HiGameUser?) {
        _currentUser.value = user
        HiGameLogger.d("Current user updated: ${user?.userId}")
    }
    
    /**
     * 设置网络状态
     */
    fun setNetworkState(isAvailable: Boolean) {
        _isNetworkAvailable.value = isAvailable
        HiGameLogger.d("Network state updated: $isAvailable")
    }
    
    /**
     * 设置支付状态
     */
    fun setPaymentState(state: PaymentState) {
        _paymentState.value = state
        HiGameLogger.d("Payment state updated: $state")
    }
    
    /**
     * 设置分享状态
     */
    fun setShareState(state: ShareState) {
        _shareState.value = state
        HiGameLogger.d("Share state updated: $state")
    }
    
    /**
     * 设置用户中心状态
     */
    fun setUserCenterState(state: UserCenterState) {
        _userCenterState.value = state
        HiGameLogger.d("UserCenter state updated: $state")
    }
    
    /**
     * 设置应用生命周期状态
     */
    fun setAppLifecycleState(state: AppLifecycleState) {
        _appLifecycleState.value = state
        HiGameLogger.d("App lifecycle state updated: $state")
    }
    
    /**
     * 获取初始化状态
     */
    fun getInitializationState(): StateFlow<Boolean> {
        return isInitialized
    }
    
    /**
     * 获取登录状态
     */
    fun getLoginState(): StateFlow<Boolean> {
        return isLoggedIn
    }
    
    /**
     * 获取当前用户
     */
    fun getCurrentUser(): StateFlow<HiGameUser?> {
        return currentUser
    }
    
    /**
     * 获取网络状态
     */
    fun getNetworkState(): StateFlow<Boolean> {
        return isNetworkAvailable
    }
    
    /**
     * 获取支付状态
     */
    fun getPaymentState(): StateFlow<PaymentState> {
        return paymentState
    }
    
    /**
     * 获取分享状态
     */
    fun getShareState(): StateFlow<ShareState> {
        return shareState
    }
    
    /**
     * 获取用户中心状态
     */
    fun getUserCenterState(): StateFlow<UserCenterState> {
        return userCenterState
    }
    
    /**
     * 获取应用生命周期状态
     */
    fun getAppLifecycleState(): StateFlow<AppLifecycleState> {
        return appLifecycleState
    }
    
    /**
     * 设置自定义状态
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> setCustomState(key: String, value: T) {
        val stateFlow = customStates.getOrPut(key) {
            MutableStateFlow(value)
        } as MutableStateFlow<T>
        stateFlow.value = value
        HiGameLogger.d("Custom state updated: $key = $value")
    }
    
    /**
     * 获取自定义状态
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getCustomState(key: String): StateFlow<T>? {
        return customStates[key] as? StateFlow<T>
    }
    
    /**
     * 移除自定义状态
     */
    fun removeCustomState(key: String) {
        customStates.remove(key)
        HiGameLogger.d("Custom state removed: $key")
    }
    
    /**
     * 清除所有自定义状态
     */
    fun clearCustomStates() {
        customStates.clear()
        HiGameLogger.d("All custom states cleared")
    }
    
    /**
     * 重置所有状态到初始值
     */
    fun reset() {
        _isInitialized.value = false
        _isLoggedIn.value = false
        _currentUser.value = null
        _isNetworkAvailable.value = true
        _paymentState.value = PaymentState.Idle
        _shareState.value = ShareState.Idle
        _userCenterState.value = UserCenterState.Idle
        _appLifecycleState.value = AppLifecycleState.Created
        clearCustomStates()
        HiGameLogger.d("All states reset")
    }
    
    /**
     * 销毁状态管理器
     */
    fun destroy() {
        reset()
        HiGameLogger.d("StateManager destroyed")
    }
    
    /**
     * 支付状态枚举
     */
    sealed class PaymentState {
        object Idle : PaymentState()
        object Processing : PaymentState()
        data class Success(val orderId: String) : PaymentState()
        data class Failed(val error: String) : PaymentState()
        object Cancelled : PaymentState()
    }
    
    /**
     * 分享状态枚举
     */
    sealed class ShareState {
        object Idle : ShareState()
        object Preparing : ShareState()
        object Sharing : ShareState()
        data class Success(val platform: String) : ShareState()
        data class Failed(val error: String) : ShareState()
        object Cancelled : ShareState()
    }
    
    /**
     * 用户中心状态枚举
     */
    sealed class UserCenterState {
        object Idle : UserCenterState()
        object Loading : UserCenterState()
        object Showing : UserCenterState()
        object Hidden : UserCenterState()
        data class Error(val message: String) : UserCenterState()
    }
    
    /**
     * 应用生命周期状态枚举
     */
    sealed class AppLifecycleState {
        object Created : AppLifecycleState()
        object Started : AppLifecycleState()
        object Resumed : AppLifecycleState()
        object Paused : AppLifecycleState()
        object Stopped : AppLifecycleState()
        object Destroyed : AppLifecycleState()
    }
}