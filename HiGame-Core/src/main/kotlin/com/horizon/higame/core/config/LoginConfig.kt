package com.horizon.higame.core.config

import kotlinx.serialization.Serializable

/**
 * 登录配置
 */
@Serializable
data class LoginConfig(
    val enableWechat: Boolean = true,
    val enableQQ: Boolean = true,
    val enableApple: Boolean = true,
    val enableGoogle: Boolean = true,
    val enableGuest: Boolean = true,
    val enablePhone: Boolean = true,
    val autoLogin: Boolean = true,
    val loginTimeout: Long = 30000L,
    val wechatAppId: String? = null,
    val qqAppId: String? = null,
    val extraParams: Map<String, String> = emptyMap()
)