package com.horizon.higame.ui.share

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 截图分享样式预览
 */
@Composable
fun HiGameShareScreenshotPreview(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onPlatformShare: (SharePlatform, Bitmap?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // 分享配置
    val shareConfig = ShareUIConfig(
        enabledPlatforms = listOf(
            SharePlatform.WECHAT,
            SharePlatform.WECHAT_MOMENTS,
            SharePlatform.QQ,
            SharePlatform.QQ_ZONE,
            SharePlatform.WEIBO,
            SharePlatform.XIAOHONGSHU,
            SharePlatform.DOUYIN,
            SharePlatform.COPY_LINK,
            SharePlatform.MORE
        )
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题
        Text(
            text = "游戏截图分享",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "自动截取当前屏幕内容并分享到各大平台",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 截图分享组件
        HiGameShareScreenshot(
            config = shareConfig,
            onPlatformClick = { platform ->
                // 处理平台分享点击
                handlePlatformShare(platform, capturedBitmap, context)
                onPlatformShare(platform, capturedBitmap)
            },
            onScreenshotReady = { bitmap ->
                capturedBitmap = bitmap
            }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 操作按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 重新截图按钮
            OutlinedButton(
                onClick = {
                    // 触发重新截图
                    capturedBitmap = null
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("重新截图")
            }
            
            // 关闭按钮
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text("关闭")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 功能说明
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "功能特点",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val features = listOf(
                    "✓ 自动截取当前屏幕内容",
                    "✓ 截图按比例缩放至75%宽度",
                    "✓ 带边框的精美容器展示",
                    "✓ 横向排列的分享平台按钮",
                    "✓ 支持微信、QQ、微博等主流平台",
                    "✓ 一键复制链接和系统分享"
                )
                
                features.forEach { feature ->
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * 处理平台分享逻辑
 */
private fun handlePlatformShare(
    platform: SharePlatform,
    bitmap: Bitmap?,
    context: android.content.Context
) {
    when (platform) {
        SharePlatform.WECHAT -> {
            // 微信分享逻辑
            ScreenshotShareUtils.shareToWechat(context, bitmap)
        }
        SharePlatform.WECHAT_MOMENTS -> {
            // 微信朋友圈分享逻辑
            ScreenshotShareUtils.shareToWechatMoments(context, bitmap)
        }
        SharePlatform.QQ -> {
            // QQ分享逻辑
            ScreenshotShareUtils.shareToQQ(context, bitmap)
        }
        SharePlatform.QQ_ZONE -> {
            // QQ空间分享逻辑
            ScreenshotShareUtils.shareToQQZone(context, bitmap)
        }
        SharePlatform.WEIBO -> {
            // 微博分享逻辑
            ScreenshotShareUtils.shareToWeibo(context, bitmap)
        }
        SharePlatform.XIAOHONGSHU -> {
            // 小红书分享逻辑
            ScreenshotShareUtils.shareToXiaohongshu(context, bitmap)
        }
        SharePlatform.DOUYIN -> {
            // 抖音分享逻辑
            ScreenshotShareUtils.shareToDouyin(context, bitmap)
        }
        SharePlatform.FACEBOOK -> {
            // Facebook分享逻辑
            ScreenshotShareUtils.shareToFacebook(context, bitmap)
        }
        SharePlatform.COPY_LINK -> {
            // 复制链接
            ScreenshotShareUtils.shareCopyLink(context)
        }
        SharePlatform.MORE -> {
            // 系统分享
            ScreenshotShareUtils.shareMore(context, bitmap)
        }
    }
}

/**
 * 扩展ShareUtils以支持图片分享
 */
private object ScreenshotShareUtils {
    fun shareToWechat(context: android.content.Context, bitmap: Bitmap?) {
        // 实现微信图片分享
        shareImage(context, bitmap, "微信")
    }
    
    fun shareToWechatMoments(context: android.content.Context, bitmap: Bitmap?) {
        // 实现微信朋友圈图片分享
        shareImage(context, bitmap, "微信朋友圈")
    }
    
    fun shareToQQ(context: android.content.Context, bitmap: Bitmap?) {
        // 实现QQ图片分享
        shareImage(context, bitmap, "QQ")
    }
    
    fun shareToQQZone(context: android.content.Context, bitmap: Bitmap?) {
        // 实现QQ空间图片分享
        shareImage(context, bitmap, "QQ空间")
    }
    
    fun shareToWeibo(context: android.content.Context, bitmap: Bitmap?) {
        // 实现微博图片分享
        shareImage(context, bitmap, "微博")
    }
    
    fun shareToXiaohongshu(context: android.content.Context, bitmap: Bitmap?) {
        // 实现小红书图片分享
        shareImage(context, bitmap, "小红书")
    }
    
    fun shareToDouyin(context: android.content.Context, bitmap: Bitmap?) {
        // 实现抖音图片分享
        shareImage(context, bitmap, "抖音")
    }
    
    fun shareToFacebook(context: android.content.Context, bitmap: Bitmap?) {
        // 实现Facebook图片分享
        shareImage(context, bitmap, "Facebook")
    }
    
    fun shareCopyLink(context: android.content.Context) {
        // 复制链接到剪贴板
        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("游戏链接", "https://higame.example.com/share")
        clipboard.setPrimaryClip(clip)
        
        // 显示提示
        android.widget.Toast.makeText(context, "链接已复制到剪贴板", android.widget.Toast.LENGTH_SHORT).show()
    }
    
    fun shareMore(context: android.content.Context, bitmap: Bitmap?) {
        // 系统分享
        shareImage(context, bitmap, "系统分享")
    }
    
    private fun shareImage(context: android.content.Context, bitmap: Bitmap?, platformName: String) {
        try {
            if (bitmap != null) {
                // 保存图片到临时文件
                val file = java.io.File(context.cacheDir, "share_screenshot.png")
                val outputStream = java.io.FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
                
                // 创建分享Intent
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                
                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    putExtra(android.content.Intent.EXTRA_TEXT, "来自HiGame的精彩游戏截图")
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                val chooser = android.content.Intent.createChooser(intent, "分享到$platformName")
                context.startActivity(chooser)
            } else {
                android.widget.Toast.makeText(context, "截图失败，无法分享", android.widget.Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.widget.Toast.makeText(context, "分享失败：${e.message}", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HiGameShareScreenshotPreviewDemo() {
    MaterialTheme {
        HiGameShareScreenshotPreview()
    }
}