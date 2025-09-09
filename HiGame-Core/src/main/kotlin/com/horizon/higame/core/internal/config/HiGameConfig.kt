package com.horizon.higame.core.internal.config

/**
 * HiGame 配置类
 * 用于解析和管理 JSON 配置文件
 */
internal data class HiGameConfig(
    /** 全局配置 */
    val globalConfig: GlobalConfig,
    
    /** 模块配置映射 */
    val moduleConfigs: Map<String, ModuleConfig>
) {
    
    /**
     * 全局配置
     */
    data class GlobalConfig(
        val appId: String,
        val appSecret: String,
        val environment: String = "release",
        val logLevel: String = "info",
        val timeout: Long = 10000L
    )
    
    /**
     * 模块配置
     */
    data class ModuleConfig(
        val enabled: Boolean = true,
        val appKey: String? = null,
        val appSecret: String? = null,
        val customParams: Map<String, Any> = emptyMap()
    )
    
    /**
     * 获取指定模块的配置
     * @param moduleName 模块名称
     * @return 模块配置，如果不存在则返回默认配置
     */
    fun getModuleConfig(moduleName: String): ModuleConfig {
        return moduleConfigs[moduleName] ?: ModuleConfig()
    }
}