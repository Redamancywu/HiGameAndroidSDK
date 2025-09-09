package com.horizon.higame.core.config

import kotlinx.serialization.Serializable

/**
 * 分享配置
 */
@Serializable
data class ShareConfig(
    val enableWechat: Boolean = true,
    val enableWechatMoments: Boolean = true,
    val enableQQ: Boolean = true,
    val enableQQZone: Boolean = true,
    val enableWeibo: Boolean = true,
    val enableSystem: Boolean = true,
    val shareTimeout: Long = 30000L,
    val wechatAppId: String? = null,
    val qqAppId: String? = null,
    val weiboAppKey: String? = null,
    val extraParams: Map<String, String> = emptyMap()
)