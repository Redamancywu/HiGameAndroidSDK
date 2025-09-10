package com.horizon.higame.ui.share

import androidx.compose.ui.graphics.Color

/**
 * 分享平台枚举
 */
enum class SharePlatform(val displayName: String, val iconRes: String, val brandColor: Color) {
    QQ("QQ", "qq", Color(0xFF12B7F5)),
    QQ_ZONE("QQ空间", "qzone", Color(0xFFFFCE00)),
    WECHAT("微信", "wechat", Color(0xFF07C160)),
    WECHAT_MOMENTS("朋友圈", "moments", Color(0xFF07C160)),
    WEIBO("微博", "weibo", Color(0xFFE6162D)),
    XIAOHONGSHU("小红书", "xiaohongshu", Color(0xFFFF2442)),
    DOUYIN("抖音", "douyin", Color(0xFF000000)),
    FACEBOOK("Facebook", "facebook", Color(0xFF1877F2)),
    COPY_LINK("复制链接", "copy_link", Color(0xFF666666)),
    MORE("更多", "more", Color(0xFF999999))
}

/**
 * 分享界面展示样式
 */
enum class ShareDisplayStyle {
    GRID_MODAL,     // 网格模态框
    ACTION_SHEET,   // 底部操作表
    FULLSCREEN      // 全屏显示
}

/**
 * 分享UI配置
 */
data class ShareUIConfig(
    val displayStyle: ShareDisplayStyle = ShareDisplayStyle.GRID_MODAL,
    val enabledPlatforms: List<SharePlatform> = SharePlatform.values().toList(),
    val gridColumns: Int = 4,
    val gridRows: Int = 2,
    val iconSize: Int = 48, // dp
    val itemSpacing: Int = 16, // dp
    val showPlatformName: Boolean = true,
    val enableRippleEffect: Boolean = true,
    val backgroundColor: Color = Color.White,
    val cornerRadius: Int = 12 // dp
)

/**
 * 分享回调接口
 */
interface ShareCallback {
    fun onPlatformSelected(platform: SharePlatform)
    fun onShareCancelled()
    fun onShareCompleted(platform: SharePlatform, success: Boolean)
    fun onShareSuccess(platform: SharePlatform)
    fun onShareError(platform: SharePlatform, error: String)
}

/**
 * 分享内容数据类
 */
data class ShareContent(
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val linkUrl: String = "",
    val extraData: Map<String, Any> = emptyMap()
)