package com.horizon.higame.core.model

/**
 * 原生广告素材数据
 */
data class HiGameNativeInfo(
    /** 通用广告信息 */
    val base: HiGameAdInfo,
    /** 图标地址 */
    val iconUrl: String? = null,
    /** 主图/大图地址 */
    val imageUrl: String? = null,
    /** 点击跳转链接 */
    val clickUrl: String? = null,
    /** 落地页/应用下载地址 */
    val landingUrl: String? = null,
    /** 标题 */
    val title: String? = null,
    /** 描述 */
    val desc: String? = null,
    /** 行动按钮文案，例如 “下载/打开/查看详情” 等 */
    val callToAction: String? = null,
    /** 透传业务扩展字段 */
    val extras: Map<String, Any?> = emptyMap()
)