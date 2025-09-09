package com.horizon.higame.core.internal.service

import com.horizon.higame.core.callback.HiGamePayCallback
import com.horizon.higame.core.callback.HiGameQueryCallback
import com.horizon.higame.core.model.HiGamePayInfo

/**
 * 支付服务接口
 * 支付模块需要实现的具体接口
 */
internal interface HiGamePayService : HiGameBaseService {
    
    /**
     * 执行支付
     * @param payInfo 支付信息
     * @param callback 支付结果回调
     */
    fun pay(payInfo: HiGamePayInfo, callback: HiGamePayCallback)
    
    /**
     * 查询订单状态
     * @param orderId 订单ID
     * @param callback 查询结果回调
     */
    fun queryOrder(orderId: String, callback: HiGameQueryCallback<String>)
}