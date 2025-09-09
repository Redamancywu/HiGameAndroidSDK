package com.horizon.higame.core.internal.version

import android.content.Context
import com.horizon.higame.core.utils.HiGameLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * HiGame 版本管理器
 * 负责版本检查、更新提醒等功能
 */
class HiGameVersionManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: HiGameVersionManager? = null
        
        fun getInstance(): HiGameVersionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HiGameVersionManager().also { INSTANCE = it }
            }
        }
        
        private const val CURRENT_VERSION = "1.0.0"
        private const val VERSION_CHECK_INTERVAL = 24 * 60 * 60 * 1000L // 24小时
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var context: Context? = null
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    // 版本信息状态
    private val _currentVersion = MutableStateFlow(CURRENT_VERSION)
    val currentVersion: StateFlow<String> = _currentVersion.asStateFlow()
    
    private val _latestVersion = MutableStateFlow<VersionInfo?>(null)
    val latestVersion: StateFlow<VersionInfo?> = _latestVersion.asStateFlow()
    
    private val _updateAvailable = MutableStateFlow(false)
    val updateAvailable: StateFlow<Boolean> = _updateAvailable.asStateFlow()
    
    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> = _isChecking.asStateFlow()
    
    private var lastCheckTime = 0L
    private var versionCheckUrl: String? = null
    
    /**
     * 初始化版本管理器
     */
    suspend fun initialize(context: Context) = withContext(Dispatchers.IO) {
        this@HiGameVersionManager.context = context.applicationContext
        
        try {
            // 从配置中获取版本检查URL
            loadVersionCheckConfig()
            
            // 启动定期版本检查
            startPeriodicVersionCheck()
            
            HiGameLogger.i("VersionManager initialized successfully")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to initialize VersionManager", e)
            throw e
        }
    }
    
    /**
     * 检查版本更新
     */
    suspend fun checkForUpdate(forceCheck: Boolean = false): VersionCheckResult = withContext(Dispatchers.IO) {
        try {
            val currentTime = System.currentTimeMillis()
            
            // 检查是否需要进行版本检查
            if (!forceCheck && (currentTime - lastCheckTime) < VERSION_CHECK_INTERVAL) {
                HiGameLogger.d("Version check skipped, too soon since last check")
                return@withContext VersionCheckResult.SkippedTooSoon
            }
            
            _isChecking.value = true
            
            val url = versionCheckUrl
            if (url.isNullOrEmpty()) {
                HiGameLogger.w("Version check URL not configured")
                return@withContext VersionCheckResult.ConfigurationError
            }
            
            HiGameLogger.d("Checking for version update from: $url")
            
            val versionInfo = fetchVersionInfo(url)
            if (versionInfo != null) {
                _latestVersion.value = versionInfo
                lastCheckTime = currentTime
                
                val hasUpdate = compareVersions(CURRENT_VERSION, versionInfo.version) < 0
                _updateAvailable.value = hasUpdate
                
                HiGameLogger.i("Version check completed. Current: $CURRENT_VERSION, Latest: ${versionInfo.version}, Update available: $hasUpdate")
                
                return@withContext if (hasUpdate) {
                    VersionCheckResult.UpdateAvailable(versionInfo)
                } else {
                    VersionCheckResult.NoUpdateNeeded
                }
            } else {
                HiGameLogger.w("Failed to fetch version information")
                return@withContext VersionCheckResult.NetworkError
            }
            
        } catch (e: Exception) {
            HiGameLogger.e("Error during version check", e)
            return@withContext VersionCheckResult.Error(e.message ?: "Unknown error")
        } finally {
            _isChecking.value = false
        }
    }
    
    /**
     * 获取当前版本
     */
    fun getCurrentVersion(): String {
        return CURRENT_VERSION
    }
    
    /**
     * 获取最新版本信息
     */
    fun getLatestVersionInfo(): VersionInfo? {
        return _latestVersion.value
    }
    
    /**
     * 是否有可用更新
     */
    fun hasUpdateAvailable(): Boolean {
        return _updateAvailable.value
    }
    
    /**
     * 是否正在检查版本
     */
    fun isCheckingVersion(): Boolean {
        return _isChecking.value
    }
    
    /**
     * 设置版本检查URL
     */
    fun setVersionCheckUrl(url: String) {
        versionCheckUrl = url
        HiGameLogger.d("Version check URL updated: $url")
    }
    
    /**
     * 获取版本历史
     */
    fun getVersionHistory(): List<String> {
        // 这里可以返回版本历史记录
        return listOf(
            "1.0.0 - Initial release",
            "0.9.0 - Beta version",
            "0.8.0 - Alpha version"
        )
    }
    
    /**
     * 获取版本特性
     */
    fun getVersionFeatures(version: String): List<String> {
        return when (version) {
            "1.0.0" -> listOf(
                "完整的登录功能",
                "支付功能集成",
                "分享功能",
                "用户中心",
                "安全加密",
                "性能监控"
            )
            else -> emptyList()
        }
    }
    
    /**
     * 比较版本号
     * @return 负数表示 version1 < version2，0表示相等，正数表示 version1 > version2
     */
    fun compareVersions(version1: String, version2: String): Int {
        val v1Parts = version1.split(".").map { it.toIntOrNull() ?: 0 }
        val v2Parts = version2.split(".").map { it.toIntOrNull() ?: 0 }
        
        val maxLength = maxOf(v1Parts.size, v2Parts.size)
        
        for (i in 0 until maxLength) {
            val v1Part = v1Parts.getOrNull(i) ?: 0
            val v2Part = v2Parts.getOrNull(i) ?: 0
            
            when {
                v1Part < v2Part -> return -1
                v1Part > v2Part -> return 1
            }
        }
        
        return 0
    }
    
    /**
     * 检查版本是否兼容
     */
    fun isVersionCompatible(version: String, minVersion: String): Boolean {
        return compareVersions(version, minVersion) >= 0
    }
    
    /**
     * 销毁版本管理器
     */
    fun destroy() {
        scope.cancel()
        context = null
        versionCheckUrl = null
        HiGameLogger.d("VersionManager destroyed")
    }
    
    /**
     * 加载版本检查配置
     */
    private suspend fun loadVersionCheckConfig() {
        try {
            val context = this.context ?: return
            
            // 尝试从 assets 加载配置
            val inputStream = context.assets.open("higame_version_config.json")
            val configJson = inputStream.bufferedReader().use { it.readText() }
            
            val config = json.decodeFromString<VersionConfig>(configJson)
            versionCheckUrl = config.checkUrl
            
            HiGameLogger.d("Version check config loaded: $versionCheckUrl")
            
        } catch (e: IOException) {
            HiGameLogger.w("Version config file not found, using default settings")
            // 使用默认配置
            versionCheckUrl = "https://api.higame.com/version/check"
        } catch (e: Exception) {
            HiGameLogger.e("Failed to load version config", e)
        }
    }
    
    /**
     * 启动定期版本检查
     */
    private fun startPeriodicVersionCheck() {
        scope.launch {
            while (isActive) {
                try {
                    delay(VERSION_CHECK_INTERVAL)
                    checkForUpdate(forceCheck = false)
                } catch (e: Exception) {
                    HiGameLogger.e("Error in periodic version check", e)
                }
            }
        }
    }
    
    /**
     * 从服务器获取版本信息
     */
    private suspend fun fetchVersionInfo(url: String): VersionInfo? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("User-Agent", "HiGameSDK/$CURRENT_VERSION")
            
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                json.decodeFromString<VersionInfo>(response)
            } else {
                HiGameLogger.w("Version check failed with response code: $responseCode")
                null
            }
        } catch (e: Exception) {
            HiGameLogger.e("Failed to fetch version info", e)
            null
        }
    }
    
    /**
     * 版本配置数据类
     */
    @Serializable
    data class VersionConfig(
        val checkUrl: String,
        val checkInterval: Long = VERSION_CHECK_INTERVAL
    )
    
    /**
     * 版本信息数据类
     */
    @Serializable
    data class VersionInfo(
        val version: String,
        val versionCode: Int,
        val releaseDate: String,
        val description: String,
        val features: List<String> = emptyList(),
        val bugFixes: List<String> = emptyList(),
        val downloadUrl: String? = null,
        val forceUpdate: Boolean = false,
        val minCompatibleVersion: String? = null,
        val releaseNotes: String? = null
    )
    
    /**
     * 版本检查结果
     */
    sealed class VersionCheckResult {
        object NoUpdateNeeded : VersionCheckResult()
        data class UpdateAvailable(val versionInfo: VersionInfo) : VersionCheckResult()
        object NetworkError : VersionCheckResult()
        object ConfigurationError : VersionCheckResult()
        object SkippedTooSoon : VersionCheckResult()
        data class Error(val message: String) : VersionCheckResult()
    }
}