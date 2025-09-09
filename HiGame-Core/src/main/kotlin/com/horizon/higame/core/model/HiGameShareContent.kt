package com.horizon.higame.core.model

/**
 * 分享内容
 */
data class HiGameShareContent(
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val linkUrl: String? = null,
    val platform: String,
    val extraData: Map<String, String>? = null
)