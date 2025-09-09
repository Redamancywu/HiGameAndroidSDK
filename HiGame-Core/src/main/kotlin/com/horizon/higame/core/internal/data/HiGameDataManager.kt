package com.horizon.higame.core.internal.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.horizon.higame.core.model.HiGameUser
import com.horizon.higame.core.utils.HiGameLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.util.concurrent.ConcurrentHashMap

/**
 * HiGame 数据管理器
 * 负责数据持久化存储，使用 DataStore 替代 SharedPreferences
 */
class HiGameDataManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: HiGameDataManager? = null
        
        fun getInstance(): HiGameDataManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HiGameDataManager().also { INSTANCE = it }
            }
        }
        
        // DataStore 扩展
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "higame_preferences")
        
        // 预定义的键
        private val KEY_USER_INFO = stringPreferencesKey("user_info")
        private val KEY_LOGIN_TOKEN = stringPreferencesKey("login_token")
        private val KEY_APP_CONFIG = stringPreferencesKey("app_config")
        private val KEY_LAST_LOGIN_TIME = longPreferencesKey("last_login_time")
        private val KEY_LOGIN_COUNT = intPreferencesKey("login_count")
        private val KEY_FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        private val KEY_SDK_VERSION = stringPreferencesKey("sdk_version")
        private val KEY_DEVICE_ID = stringPreferencesKey("device_id")
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var context: Context? = null
    private var dataStore: DataStore<Preferences>? = null
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    // 内存缓存
    private val memoryCache = ConcurrentHashMap<String, Any>()
    
    /**
     * 初始化数据管理器
     */
    suspend fun initialize(context: Context) = withContext(Dispatchers.IO) {
        this@HiGameDataManager.context = context.applicationContext
        this@HiGameDataManager.dataStore = context.dataStore
        
        try {
            // 预加载常用数据到内存缓存
            preloadCache()
            
            HiGameLogger.i("DataManager initialized successfully")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to initialize DataManager", e)
            throw e
        }
    }
    
    /**
     * 保存用户信息
     */
    suspend fun saveUserInfo(user: HiGameUser) {
        try {
            val userJson = json.encodeToString(user)
            dataStore?.edit { preferences ->
                preferences[KEY_USER_INFO] = userJson
            }
            
            // 更新内存缓存
            memoryCache["user_info"] = user
            
            HiGameLogger.d("User info saved: ${user.userId}")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to save user info", e)
            throw e
        }
    }
    
    /**
     * 获取用户信息
     */
    suspend fun getUserInfo(): HiGameUser? {
        return try {
            // 先从内存缓存获取
            val cachedUser = memoryCache["user_info"] as? HiGameUser
            if (cachedUser != null) {
                return cachedUser
            }
            
            // 从 DataStore 获取
            val userJson = dataStore?.data?.first()?.get(KEY_USER_INFO)
            if (userJson != null) {
                val user = json.decodeFromString<HiGameUser>(userJson)
                memoryCache["user_info"] = user
                user
            } else {
                null
            }
        } catch (e: Exception) {
            HiGameLogger.e("Failed to get user info", e)
            null
        }
    }
    
    /**
     * 获取用户信息流
     */
    fun getUserInfoFlow(): Flow<HiGameUser?> {
        return dataStore?.data?.map { preferences ->
            try {
                val userJson = preferences[KEY_USER_INFO]
                if (userJson != null) {
                    json.decodeFromString<HiGameUser>(userJson)
                } else {
                    null
                }
            } catch (e: Exception) {
                HiGameLogger.e("Failed to decode user info from flow", e)
                null
            }
        } ?: flowOf(null)
    }
    
    /**
     * 保存登录令牌
     */
    suspend fun saveLoginToken(token: String) {
        try {
            dataStore?.edit { preferences ->
                preferences[KEY_LOGIN_TOKEN] = token
            }
            
            memoryCache["login_token"] = token
            HiGameLogger.d("Login token saved")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to save login token", e)
            throw e
        }
    }
    
    /**
     * 获取登录令牌
     */
    suspend fun getLoginToken(): String? {
        return try {
            // 先从内存缓存获取
            val cachedToken = memoryCache["login_token"] as? String
            if (cachedToken != null) {
                return cachedToken
            }
            
            // 从 DataStore 获取
            val token = dataStore?.data?.first()?.get(KEY_LOGIN_TOKEN)
            if (token != null) {
                memoryCache["login_token"] = token
            }
            token
        } catch (e: Exception) {
            HiGameLogger.e("Failed to get login token", e)
            null
        }
    }
    
    /**
     * 清除登录令牌
     */
    suspend fun clearLoginToken() {
        try {
            dataStore?.edit { preferences ->
                preferences.remove(KEY_LOGIN_TOKEN)
            }
            
            memoryCache.remove("login_token")
            HiGameLogger.d("Login token cleared")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to clear login token", e)
        }
    }
    
    /**
     * 保存应用配置
     */
    suspend fun saveAppConfig(config: Map<String, Any>) {
        try {
            val configJson = json.encodeToString(config)
            dataStore?.edit { preferences ->
                preferences[KEY_APP_CONFIG] = configJson
            }
            
            memoryCache["app_config"] = config
            HiGameLogger.d("App config saved")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to save app config", e)
            throw e
        }
    }
    
    /**
     * 获取应用配置
     */
    suspend fun getAppConfig(): Map<String, Any>? {
        return try {
            // 先从内存缓存获取
            val cachedConfig = memoryCache["app_config"] as? Map<String, Any>
            if (cachedConfig != null) {
                return cachedConfig
            }
            
            // 从 DataStore 获取
            val configJson = dataStore?.data?.first()?.get(KEY_APP_CONFIG)
            if (configJson != null) {
                val config = json.decodeFromString<Map<String, Any>>(configJson)
                memoryCache["app_config"] = config
                config
            } else {
                null
            }
        } catch (e: Exception) {
            HiGameLogger.e("Failed to get app config", e)
            null
        }
    }
    
    /**
     * 更新最后登录时间
     */
    suspend fun updateLastLoginTime() {
        try {
            val currentTime = System.currentTimeMillis()
            dataStore?.edit { preferences ->
                preferences[KEY_LAST_LOGIN_TIME] = currentTime
            }
            
            memoryCache["last_login_time"] = currentTime
            HiGameLogger.d("Last login time updated")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to update last login time", e)
        }
    }
    
    /**
     * 获取最后登录时间
     */
    suspend fun getLastLoginTime(): Long {
        return try {
            // 先从内存缓存获取
            val cachedTime = memoryCache["last_login_time"] as? Long
            if (cachedTime != null) {
                return cachedTime
            }
            
            // 从 DataStore 获取
            val time = dataStore?.data?.first()?.get(KEY_LAST_LOGIN_TIME) ?: 0L
            if (time > 0) {
                memoryCache["last_login_time"] = time
            }
            time
        } catch (e: Exception) {
            HiGameLogger.e("Failed to get last login time", e)
            0L
        }
    }
    
    /**
     * 增加登录次数
     */
    suspend fun incrementLoginCount() {
        try {
            val currentCount = getLoginCount()
            val newCount = currentCount + 1
            
            dataStore?.edit { preferences ->
                preferences[KEY_LOGIN_COUNT] = newCount
            }
            
            memoryCache["login_count"] = newCount
            HiGameLogger.d("Login count incremented to: $newCount")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to increment login count", e)
        }
    }
    
    /**
     * 获取登录次数
     */
    suspend fun getLoginCount(): Int {
        return try {
            // 先从内存缓存获取
            val cachedCount = memoryCache["login_count"] as? Int
            if (cachedCount != null) {
                return cachedCount
            }
            
            // 从 DataStore 获取
            val count = dataStore?.data?.first()?.get(KEY_LOGIN_COUNT) ?: 0
            if (count > 0) {
                memoryCache["login_count"] = count
            }
            count
        } catch (e: Exception) {
            HiGameLogger.e("Failed to get login count", e)
            0
        }
    }
    
    /**
     * 检查是否首次启动
     */
    suspend fun isFirstLaunch(): Boolean {
        return try {
            val isFirst = dataStore?.data?.first()?.get(KEY_FIRST_LAUNCH) ?: true
            if (isFirst) {
                // 标记为非首次启动
                dataStore?.edit { preferences ->
                    preferences[KEY_FIRST_LAUNCH] = false
                }
            }
            isFirst
        } catch (e: Exception) {
            HiGameLogger.e("Failed to check first launch", e)
            true
        }
    }
    
    /**
     * 保存设备ID
     */
    suspend fun saveDeviceId(deviceId: String) {
        try {
            dataStore?.edit { preferences ->
                preferences[KEY_DEVICE_ID] = deviceId
            }
            
            memoryCache["device_id"] = deviceId
            HiGameLogger.d("Device ID saved")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to save device ID", e)
        }
    }
    
    /**
     * 获取设备ID
     */
    suspend fun getDeviceId(): String? {
        return try {
            // 先从内存缓存获取
            val cachedId = memoryCache["device_id"] as? String
            if (cachedId != null) {
                return cachedId
            }
            
            // 从 DataStore 获取
            val deviceId = dataStore?.data?.first()?.get(KEY_DEVICE_ID)
            if (deviceId != null) {
                memoryCache["device_id"] = deviceId
            }
            deviceId
        } catch (e: Exception) {
            HiGameLogger.e("Failed to get device ID", e)
            null
        }
    }
    
    /**
     * 保存 SDK 版本
     */
    suspend fun saveSDKVersion(version: String) {
        try {
            dataStore?.edit { preferences ->
                preferences[KEY_SDK_VERSION] = version
            }
            
            memoryCache["sdk_version"] = version
            HiGameLogger.d("SDK version saved: $version")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to save SDK version", e)
        }
    }
    
    /**
     * 获取 SDK 版本
     */
    suspend fun getSDKVersion(): String? {
        return try {
            // 先从内存缓存获取
            val cachedVersion = memoryCache["sdk_version"] as? String
            if (cachedVersion != null) {
                return cachedVersion
            }
            
            // 从 DataStore 获取
            val version = dataStore?.data?.first()?.get(KEY_SDK_VERSION)
            if (version != null) {
                memoryCache["sdk_version"] = version
            }
            version
        } catch (e: Exception) {
            HiGameLogger.e("Failed to get SDK version", e)
            null
        }
    }
    
    /**
     * 清除所有用户数据
     */
    suspend fun clearUserData() {
        try {
            dataStore?.edit { preferences ->
                preferences.remove(KEY_USER_INFO)
                preferences.remove(KEY_LOGIN_TOKEN)
                preferences.remove(KEY_LAST_LOGIN_TIME)
                preferences.remove(KEY_LOGIN_COUNT)
            }
            
            // 清除内存缓存中的用户数据
            memoryCache.remove("user_info")
            memoryCache.remove("login_token")
            memoryCache.remove("last_login_time")
            memoryCache.remove("login_count")
            
            HiGameLogger.d("User data cleared")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to clear user data", e)
        }
    }
    
    /**
     * 清除所有数据
     */
    suspend fun clearAllData() {
        try {
            dataStore?.edit { preferences ->
                preferences.clear()
            }
            
            memoryCache.clear()
            HiGameLogger.d("All data cleared")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to clear all data", e)
        }
    }
    
    /**
     * 获取数据存储统计信息
     */
    suspend fun getStorageStats(): Map<String, Any> {
        return try {
            val preferences = dataStore?.data?.first() ?: return emptyMap()
            
            mapOf(
                "totalKeys" to preferences.asMap().size,
                "hasUserInfo" to preferences.contains(KEY_USER_INFO),
                "hasLoginToken" to preferences.contains(KEY_LOGIN_TOKEN),
                "hasAppConfig" to preferences.contains(KEY_APP_CONFIG),
                "loginCount" to (preferences[KEY_LOGIN_COUNT] ?: 0),
                "lastLoginTime" to (preferences[KEY_LAST_LOGIN_TIME] ?: 0L),
                "sdkVersion" to (preferences[KEY_SDK_VERSION] ?: "unknown"),
                "cacheSize" to memoryCache.size
            )
        } catch (e: Exception) {
            HiGameLogger.e("Failed to get storage stats", e)
            emptyMap()
        }
    }
    
    /**
     * 销毁数据管理器
     */
    fun destroy() {
        scope.cancel()
        memoryCache.clear()
        context = null
        dataStore = null
        HiGameLogger.d("DataManager destroyed")
    }
    
    /**
     * 预加载缓存
     */
    private suspend fun preloadCache() {
        try {
            // 预加载常用数据到内存缓存
            getUserInfo()
            getLoginToken()
            getLastLoginTime()
            getLoginCount()
            getDeviceId()
            getSDKVersion()
            
            HiGameLogger.d("Cache preloaded successfully")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to preload cache", e)
        }
    }
}