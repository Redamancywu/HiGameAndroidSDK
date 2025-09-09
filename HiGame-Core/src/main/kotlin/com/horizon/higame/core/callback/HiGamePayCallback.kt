package com.horizon.higame.core.callback

/**
 * 支付回调接口
 */
interface HiGamePayCallback {
    /**
     * 支付成功
     * @param orderId 订单ID
     * @param transactionId 交易ID
     */
    fun onPaySuccess(orderId: String, transactionId: String)
    
    /**
     * 支付失败
     * @param code 错误码
     * @param message 错误信息
     */
    fun onPayError(code: Int, message: String)
    
    /**
     * 支付取消
     */
    fun onPayCancel()
}