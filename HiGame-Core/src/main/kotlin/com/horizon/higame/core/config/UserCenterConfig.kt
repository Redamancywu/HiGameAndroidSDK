package com.horizon.higame.core.config

import kotlinx.serialization.Serializable

/**
 * 用户中心配置
 */
@Serializable
data class UserCenterConfig(
    val enableUserInfo: Boolean = true,
    val enableAccountBinding: Boolean = true,
    val enableSettings: Boolean = true,
    val enableCustomerService: Boolean = true,
    val enableFeedback: Boolean = true,
    val enableLogout: Boolean = true,
    val autoSyncUserInfo: Boolean = true,
    val syncInterval: Long = 300000L, // 5分钟
    val extraParams: Map<String, String> = emptyMap()
)