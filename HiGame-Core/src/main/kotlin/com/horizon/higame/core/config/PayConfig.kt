package com.horizon.higame.core.config

import kotlinx.serialization.Serializable

/**
 * 支付配置
 */
@Serializable
data class PayConfig(
    val enableAlipay: Boolean = true,
    val enableWechatPay: Boolean = true,
    val enableGooglePay: Boolean = true,
    val enableApplePay: Boolean = true,
    val payTimeout: Long = 60000L,
    val alipayAppId: String? = null,
    val wechatPayAppId: String? = null,
    val merchantId: String? = null,
    val extraParams: Map<String, String> = emptyMap()
)