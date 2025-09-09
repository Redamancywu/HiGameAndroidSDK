package com.horizon.higame.core.internal.manager

import com.horizon.higame.core.callback.HiGameCallback
import com.horizon.higame.core.internal.service.HiGameServiceRegistry
import com.horizon.higame.core.utils.HiGameLogger

/**
 * 支付功能管理器
 * 第三层：功能层 - 负责支付相关的所有功能
 */
internal class HiGamePayManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: HiGamePayManager? = null
        
        fun getInstance(): HiGamePayManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HiGamePayManager().also { INSTANCE = it }
            }
        }
    }
    
    /**
     * 发起支付
     * @param orderInfo 订单信息
     * @param callback 支付回调
     */
    fun pay(orderInfo: Map<String, Any>, callback: HiGameCallback<String>) {
        val payService = HiGameServiceRegistry.getPayService()
        if (payService != null) {
            HiGameLogger.d("Delegating pay to service: ${payService::class.simpleName}")
            // 转换为HiGamePayInfo
            val payInfo = com.horizon.higame.core.model.HiGamePayInfo(
                orderId = orderInfo["orderId"] as? String ?: "",
                productId = orderInfo["productId"] as? String ?: "",
                productName = orderInfo["productName"] as? String ?: "",
                amount = (orderInfo["amount"] as? Number)?.toDouble() ?: 0.0,
                currency = orderInfo["currency"] as? String ?: "CNY",
                description = orderInfo["description"] as? String,
                extraData = orderInfo["extraData"] as? Map<String, String>
            )
            payService.pay(payInfo, object : com.horizon.higame.core.callback.HiGamePayCallback {
                override fun onPaySuccess(orderId: String, transactionId: String) {
                    callback.onSuccess("Payment successful: $orderId")
                }
                override fun onPayError(code: Int, message: String) {
                    callback.onError(code, message)
                }
                override fun onPayCancel() {
                    callback.onError(-2, "Payment cancelled")
                }
            })
        } else {
            HiGameLogger.e("Pay service not found")
            callback.onError(-1, "Pay service not available")
        }
    }
    
    /**
     * 查询订单状态
     * @param orderId 订单ID
     * @param callback 查询回调
     */
    fun queryOrder(orderId: String, callback: HiGameCallback<Map<String, Any>>) {
        val payService = HiGameServiceRegistry.getPayService()
        if (payService != null) {
            HiGameLogger.d("Delegating queryOrder to service: ${payService::class.simpleName}")
            payService.queryOrder(orderId, object : com.horizon.higame.core.callback.HiGameQueryCallback<String> {
                override fun onQuerySuccess(result: String) {
                    // 将字符串结果转换为Map
                    val resultMap = mapOf("result" to result, "orderId" to orderId)
                    callback.onSuccess(resultMap)
                }
                override fun onQueryError(code: Int, message: String) {
                    callback.onError(code, message)
                }
            })
        } else {
            HiGameLogger.e("Pay service not found")
            callback.onError(-1, "Pay service not available")
        }
    }
}