package com.horizon.higame.core.model

import java.util.UUID

/**
 * 通用广告信息
 */
data class AdInfo(
    /** 广告唯一标识（可由三方SDK或服务端提供） */
    val adId: String = UUID.randomUUID().toString(),
    /** 广告类型 */
    val type: AdType = AdType.UNKNOWN,
    /** 预计/实际收益，单位分或最小币种单位，具体由业务定义 */
    val revenue: Long? = null,
    /** 货币代码（如 CNY / USD） */
    val currency: String? = null,
    /** 广告位ID */
    val placementId: String? = null,
    /** 媒体/渠道/网络标识（如 admob、pangle 等） */
    val network: String? = null,
    /** 透传业务扩展字段（如traceId、竞价信息等） */
    val extras: Map<String, Any?> = emptyMap()
)