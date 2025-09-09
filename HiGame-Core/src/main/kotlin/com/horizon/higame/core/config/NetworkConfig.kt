package com.horizon.higame.core.config

import kotlinx.serialization.Serializable

/**
 * 网络配置
 */
@Serializable
data class NetworkConfig(
    val baseUrl: String = "",
    val connectTimeout: Long = 10000L,
    val readTimeout: Long = 30000L,
    val writeTimeout: Long = 30000L,
    val enableCache: Boolean = true,
    val cacheSize: Long = 10 * 1024 * 1024L, // 10MB
    val enableRetry: Boolean = true,
    val maxRetryCount: Int = 3,
    val retryDelay: Long = 1000L,
    val enableLogging: Boolean = true,
    val extraHeaders: Map<String, String> = emptyMap()
)