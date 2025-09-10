package com.horizon.higame.ui.share

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * 分享工具类
 */
object ShareUtils {
    
    /**
     * 执行平台分享
     */
    fun shareToPlatform(
        platform: SharePlatform,
        content: ShareContent,
        callback: ShareCallback?
    ) {
        try {
            when (platform) {
                SharePlatform.QQ -> shareToQQ(content, callback)
                SharePlatform.QQ_ZONE -> shareToQZone(content, callback)
                SharePlatform.WECHAT -> shareToWechat(content, callback)
                SharePlatform.WECHAT_MOMENTS -> shareToMoments(content, callback)
                SharePlatform.WEIBO -> shareToWeibo(content, callback)
                SharePlatform.DOUYIN -> shareToDouyin(content, callback)
                SharePlatform.COPY_LINK -> shareCopyLink(content, callback)
                SharePlatform.MORE -> shareMore(content, callback)
                SharePlatform.XIAOHONGSHU -> shareToXiaohongshu(content, callback)
                SharePlatform.FACEBOOK -> shareToFacebook(content, callback)
            }
        } catch (e: Exception) {
            callback?.onShareCompleted(platform, false)
        }
    }
    
    /**
     * 通用系统分享
     */
    fun shareWithSystem(
        context: Context,
        content: ShareContent,
        platform: SharePlatform? = null
    ) {
        try {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                
                val shareText = buildString {
                    if (content.title.isNotEmpty()) {
                        append(content.title)
                        append("\n")
                    }
                    if (content.description.isNotEmpty()) {
                        append(content.description)
                        append("\n")
                    }
                    if (content.linkUrl.isNotEmpty()) {
                        append(content.linkUrl)
                    }
                }
                
                putExtra(Intent.EXTRA_TEXT, shareText)
                if (content.title.isNotEmpty()) {
                    putExtra(Intent.EXTRA_SUBJECT, content.title)
                }
            }
            
            val chooser = Intent.createChooser(shareIntent, "分享到")
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 模拟分享操作（用于演示）
     */
    private fun simulateShare(platform: SharePlatform, callback: ShareCallback?) {
        // 模拟分享成功
        callback?.onShareCompleted(platform, true)
    }
    
    private fun shareToQQ(content: ShareContent, callback: ShareCallback?) {
        // QQ分享实现
        // 这里应该集成QQ SDK
        simulateShare(SharePlatform.QQ, callback)
    }
    
    private fun shareToQZone(content: ShareContent, callback: ShareCallback?) {
        // QQ空间分享实现
        // 这里应该集成QQ SDK
        simulateShare(SharePlatform.QQ_ZONE, callback)
    }
    
    private fun shareToWechat(content: ShareContent, callback: ShareCallback?) {
        // 微信分享实现
        // 这里应该集成微信SDK
        simulateShare(SharePlatform.WECHAT, callback)
    }
    
    private fun shareToMoments(content: ShareContent, callback: ShareCallback?) {
        // 朋友圈分享实现
        // 这里应该集成微信SDK
        simulateShare(SharePlatform.WECHAT_MOMENTS, callback)
    }
    
    private fun shareToWeibo(content: ShareContent, callback: ShareCallback?) {
        // 微博分享实现
        // 这里应该集成微博SDK
        simulateShare(SharePlatform.WEIBO, callback)
    }
    
    private fun shareToDouyin(content: ShareContent, callback: ShareCallback?) {
        // 抖音分享实现
        // 这里应该集成抖音SDK
        simulateShare(SharePlatform.DOUYIN, callback)
    }
    
    private fun shareToXiaohongshu(content: ShareContent, callback: ShareCallback?) {
        // 小红书分享实现
        // 这里应该集成小红书SDK
        simulateShare(SharePlatform.XIAOHONGSHU, callback)
    }
    
    private fun shareToFacebook(content: ShareContent, callback: ShareCallback?) {
        // Facebook分享实现
        // 这里应该集成Facebook SDK
        simulateShare(SharePlatform.FACEBOOK, callback)
    }
    
    private fun shareCopyLink(content: ShareContent, callback: ShareCallback?) {
        // 复制链接实现
        simulateShare(SharePlatform.COPY_LINK, callback)
    }
    
    private fun shareMore(content: ShareContent, callback: ShareCallback?) {
        // 更多分享选项实现
        simulateShare(SharePlatform.MORE, callback)
    }
    
    /**
     * 检查应用是否已安装
     */
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取平台包名
     */
    fun getPlatformPackageName(platform: SharePlatform): String {
        return when (platform) {
            SharePlatform.QQ -> "com.tencent.mobileqq"
            SharePlatform.QQ_ZONE -> "com.tencent.mobileqq"
            SharePlatform.WECHAT -> "com.tencent.mm"
            SharePlatform.WECHAT_MOMENTS -> "com.tencent.mm"
            SharePlatform.WEIBO -> "com.sina.weibo"
            SharePlatform.DOUYIN -> "com.ss.android.ugc.aweme"
            SharePlatform.COPY_LINK -> ""
            SharePlatform.MORE -> ""
            SharePlatform.XIAOHONGSHU -> "com.xingin.xhs"
            SharePlatform.FACEBOOK -> "com.facebook.katana"
        }
    }
}