package com.horizon.higame.core.model

/**
 * 支付信息
 */
data class HiGamePayInfo(
    val orderId: String,
    val productId: String,
    val productName: String,
    val amount: Double,
    val currency: String = "CNY",
    val description: String? = null,
    val extraData: Map<String, String>? = null
)