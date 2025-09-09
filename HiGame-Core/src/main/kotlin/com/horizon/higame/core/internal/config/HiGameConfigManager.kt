package com.horizon.higame.core.internal.config

import android.content.Context
import com.horizon.higame.core.config.*
import com.horizon.higame.core.utils.HiGameLogger
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.IOException

/**
 * HiGame 配置管理器
 * 负责加载和管理配置信息
 */
internal object HiGameConfigManager {
    
    private const val TAG = "HiGameConfigManager"
    private const val DEFAULT_CONFIG_FILE = "higame_config.json"
    
    private var config: HiGameConfig? = null
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    /**
     * 初始化配置管理器
     * @param context Android上下文
     * @param configFileName 配置文件名，默认为 higame_config.json
     */
    fun initialize(context: Context, configFileName: String = DEFAULT_CONFIG_FILE) {
        try {
            val configJson = loadConfigFromAssets(context, configFileName)
            config = parseConfig(configJson)
            HiGameLogger.d(TAG, "配置初始化成功")
        } catch (e: Exception) {
            HiGameLogger.e("配置初始化失败: ${e.message}", e)
            // 使用默认配置
            config = createDefaultConfig()
        }
    }
    
    /**
     * 从 assets 加载配置文件
     */
    private fun loadConfigFromAssets(context: Context, fileName: String): String {
        return try {
            context.assets.open(fileName).use { inputStream ->
                inputStream.bufferedReader().use { reader ->
                    reader.readText()
                }
            }
        } catch (e: IOException) {
            HiGameLogger.e("无法读取配置文件: $fileName - ${e.message}", e)
            throw e
        }
    }
    
    /**
     * 解析配置 JSON
     */
    private fun parseConfig(configJson: String): HiGameConfig {
        return try {
            json.decodeFromString<HiGameConfig>(configJson)
        } catch (e: Exception) {
            HiGameLogger.e("配置解析失败: ${e.message}", e)
            throw e
        }
    }
    
    /**
     * 创建默认配置
     */
    private fun createDefaultConfig(): HiGameConfig {
        return HiGameConfig(
            globalConfig = HiGameConfig.GlobalConfig(
                appId = "",
                appSecret = "",
                environment = "release"
            ),
            moduleConfigs = mapOf(
                "login" to HiGameConfig.ModuleConfig(),
                "pay" to HiGameConfig.ModuleConfig(),
                "share" to HiGameConfig.ModuleConfig(),
                "userCenter" to HiGameConfig.ModuleConfig()
            )
        )
    }
    
    /**
     * 获取当前配置
     * @return 当前配置实例
     */
    fun getConfig(): HiGameConfig {
        return config ?: throw IllegalStateException("配置管理器未初始化")
    }
    
    /**
     * 获取应用ID
     */
    fun getAppId(): String {
        return config?.globalConfig?.appId ?: ""
    }
    
    /**
     * 获取应用密钥
     */
    fun getAppKey(): String {
        return config?.getModuleConfig("global")?.appKey ?: ""
    }
    
    /**
     * 获取应用秘钥
     */
    fun getAppSecret(): String {
        return config?.globalConfig?.appSecret ?: ""
    }
    
    /**
     * 获取环境配置
     */
    fun getEnvironment(): String {
        return config?.globalConfig?.environment ?: "release"
    }
    
    /**
     * 是否为调试模式
     */
    fun isDebugMode(): Boolean {
        return config?.globalConfig?.environment == "debug"
    }
    
    /**
     * 获取登录配置
     */
    fun getLoginConfig(): LoginConfig {
        return LoginConfig()
    }
    
    /**
     * 获取支付配置
     */
    fun getPayConfig(): PayConfig {
        return PayConfig()
    }
    
    /**
     * 获取分享配置
     */
    fun getShareConfig(): ShareConfig {
        return ShareConfig()
    }
    
    /**
     * 获取用户中心配置
     */
    fun getUserCenterConfig(): UserCenterConfig {
        return UserCenterConfig()
    }
    
    /**
     * 获取安全配置
     */
    fun getSecurityConfig(): SecurityConfig {
        return SecurityConfig()
    }
    
    /**
     * 获取网络配置
     */
    fun getNetworkConfig(): NetworkConfig {
        return NetworkConfig()
    }
    
    /**
     * 验证配置有效性
     */
    fun validateConfig(): Boolean {
        val currentConfig = config ?: return false
        
        // 验证必要字段
        if (currentConfig.globalConfig.appId.isBlank()) {
            HiGameLogger.e(TAG, "AppId 不能为空")
            return false
        }
        
        if (currentConfig.globalConfig.appSecret.isBlank()) {
            HiGameLogger.e(TAG, "AppSecret 不能为空")
            return false
        }
        
        return true
    }
    
    /**
     * 重新加载配置
     */
    fun reloadConfig(context: Context, configFileName: String = DEFAULT_CONFIG_FILE) {
        initialize(context, configFileName)
    }
    
    /**
     * 更新配置
     */
    fun updateConfig(newConfig: HiGameConfig) {
        config = newConfig
        HiGameLogger.d(TAG, "配置更新成功")
    }
    
    /**
     * 清除配置
     */
    fun clearConfig() {
        config = null
        HiGameLogger.d(TAG, "配置已清除")
    }
}