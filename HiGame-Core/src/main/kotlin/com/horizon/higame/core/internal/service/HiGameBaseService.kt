package com.horizon.higame.core.internal.service

import com.horizon.higame.core.internal.config.HiGameConfig
import com.horizon.higame.core.callback.HiGameModuleCallback
import com.horizon.higame.core.model.HiGameHealthStatus

/**
 * 基础服务接口
 * 所有功能模块必须实现的基础接口
 */
internal interface HiGameBaseService {
    
    /** 模块唯一标识 */
    val moduleName: String
    
    /** 模块版本 */
    val version: String
    
    /** 模块依赖关系 */
    val dependencies: List<String>
    
    /**
     * 初始化模块
     * @param config 配置信息
     * @param callback 初始化回调
     */
    suspend fun initialize(config: HiGameConfig, callback: HiGameModuleCallback)
    
    /**
     * 配置变更通知
     * @param config 新的配置信息
     */
    suspend fun onConfigChanged(config: HiGameConfig)
    
    /**
     * 获取健康状态
     * @return 健康状态信息
     */
    fun getHealthStatus(): HiGameHealthStatus
}