package com.horizon.higame.core.internal.service

import com.horizon.higame.core.utils.HiGameLogger
import java.util.ServiceLoader

/**
 * HiGame 服务注册表
 * 使用 ServiceLoader 实现服务的自动发现和注册
 */
internal object HiGameServiceRegistry {
    
    private val serviceCache = mutableMapOf<Class<*>, Any>()
    
    /**
     * 获取服务实例
     * @param serviceClass 服务接口类
     * @return 服务实例，如果未找到则返回 null
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : HiGameBaseService> getService(serviceClass: Class<T>): T? {
        // 先从缓存中查找
        serviceCache[serviceClass]?.let {
            return it as T
        }
        
        return try {
            // 使用 ServiceLoader 加载服务
            val serviceLoader = ServiceLoader.load(serviceClass)
            val service = serviceLoader.iterator().let { iterator ->
                if (iterator.hasNext()) {
                    iterator.next()
                } else {
                    null
                }
            }
            
            service?.let {
                // 缓存服务实例
                serviceCache[serviceClass] = it
                HiGameLogger.d("Service loaded: ${serviceClass.simpleName}")
            } ?: run {
                HiGameLogger.w("Service not found: ${serviceClass.simpleName}")
            }
            
            service
        } catch (e: Exception) {
            HiGameLogger.e("Failed to load service: ${serviceClass.simpleName}", e)
            null
        }
    }
    
    /**
     * 获取登录服务
     */
    fun getLoginService(): HiGameLoginService? {
        return getService(HiGameLoginService::class.java)
    }
    
    /**
     * 获取支付服务
     */
    fun getPayService(): HiGamePayService? {
        return getService(HiGamePayService::class.java)
    }
    
    /**
     * 获取分享服务
     */
    fun getShareService(): HiGameShareService? {
        return getService(HiGameShareService::class.java)
    }
    
    /**
     * 获取用户中心服务
     */
    fun getUserCenterService(): HiGameUserCenterService? {
        return getService(HiGameUserCenterService::class.java)
    }
    
    /**
     * 注册服务实例（用于测试或手动注册）
     */
    fun <T : HiGameBaseService> registerService(serviceClass: Class<T>, service: T) {
        serviceCache[serviceClass] = service
        HiGameLogger.d("Service registered manually: ${serviceClass.simpleName}")
    }
    
    /**
     * 清除所有缓存的服务
     */
    fun clearCache() {
        serviceCache.clear()
        HiGameLogger.d("Service cache cleared")
    }
    
    /**
     * 获取所有已注册的服务
     */
    fun getAllServices(): Map<Class<*>, Any> {
        return serviceCache.toMap()
    }
    
    /**
     * 检查服务是否可用
     */
    fun <T : HiGameBaseService> isServiceAvailable(serviceClass: Class<T>): Boolean {
        return getService(serviceClass) != null
    }
}