package com.horizon.higame.core.config

import kotlinx.serialization.Serializable

/**
 * 安全配置
 */
@Serializable
data class SecurityConfig(
    val enableEncryption: Boolean = true,
    val enableSignature: Boolean = true,
    val enableTokenValidation: Boolean = true,
    val encryptionAlgorithm: String = "AES",
    val signatureAlgorithm: String = "SHA256",
    val keySize: Int = 256,
    val tokenExpireTime: Long = 7200000L, // 2小时
    val maxRetryCount: Int = 3,
    val extraParams: Map<String, String> = emptyMap()
)