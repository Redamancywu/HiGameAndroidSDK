package com.horizon.higame.ui.share

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 屏幕截图分享组件
 * 支持自动截取当前屏幕或Composable内容，并提供横向分享按钮
 */
@Composable
fun HiGameShareScreenshot(
    modifier: Modifier = Modifier,
    config: ShareUIConfig = ShareUIConfig(),
    onPlatformClick: (SharePlatform) -> Unit = {},
    onScreenshotReady: (Bitmap?) -> Unit = {}
) {
    val context = LocalContext.current
    val view = LocalView.current
    val density = LocalDensity.current
    
    var screenshotBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    
    // 自动截图
    LaunchedEffect(Unit) {
        isCapturing = true
        val bitmap = captureScreen(context, view)
        screenshotBitmap = bitmap
        onScreenshotReady(bitmap)
        isCapturing = false
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 截图预览区域
        ScreenshotPreview(
            bitmap = screenshotBitmap,
            isCapturing = isCapturing,
            modifier = Modifier.fillMaxWidth(0.75f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 分享平台按钮
        SharePlatformRow(
            platforms = config.enabledPlatforms,
            onPlatformClick = onPlatformClick
        )
    }
}

/**
 * 截图预览组件
 */
@Composable
private fun ScreenshotPreview(
    bitmap: Bitmap?,
    isCapturing: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(9f / 16f), // 手机屏幕比例
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, Color.Gray.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCapturing -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "正在截图...",
                        modifier = Modifier.padding(top = 80.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                bitmap != null -> {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "屏幕截图",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_camera),
                            contentDescription = "截图失败",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "截图失败",
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 横向分享平台按钮行
 */
@Composable
private fun SharePlatformRow(
    platforms: List<SharePlatform>,
    onPlatformClick: (SharePlatform) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "分享到",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(platforms) { platform ->
                SharePlatformIconButton(
                    platform = platform,
                    onClick = { onPlatformClick(platform) }
                )
            }
        }
    }
}

/**
 * 分享平台图标按钮
 */
@Composable
private fun SharePlatformIconButton(
    platform: SharePlatform,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 平台图标
        Card(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = getPlatformColor(platform)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = getPlatformIcon(platform)),
                    contentDescription = getPlatformName(platform),
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 平台名称
        Text(
            text = getPlatformName(platform),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

/**
 * 获取平台颜色
 */
private fun getPlatformColor(platform: SharePlatform): Color {
    return when (platform) {
        SharePlatform.WECHAT -> Color(0xFF07C160)
        SharePlatform.WECHAT_MOMENTS -> Color(0xFF07C160)
        SharePlatform.QQ -> Color(0xFF12B7F5)
        SharePlatform.QQ_ZONE -> Color(0xFFFFCE00)
        SharePlatform.WEIBO -> Color(0xFFE6162D)
        SharePlatform.XIAOHONGSHU -> Color(0xFFFF2442)
        SharePlatform.DOUYIN -> Color(0xFF000000)
        SharePlatform.FACEBOOK -> Color(0xFF1877F2)
        SharePlatform.COPY_LINK -> Color(0xFF6C757D)
        SharePlatform.MORE -> Color(0xFF6C757D)
    }
}

/**
 * 获取平台图标
 */
private fun getPlatformIcon(platform: SharePlatform): Int {
    return when (platform) {
        SharePlatform.WECHAT -> android.R.drawable.ic_dialog_email
        SharePlatform.WECHAT_MOMENTS -> android.R.drawable.ic_dialog_email
        SharePlatform.QQ -> android.R.drawable.ic_dialog_email
        SharePlatform.QQ_ZONE -> android.R.drawable.ic_dialog_email
        SharePlatform.WEIBO -> android.R.drawable.ic_dialog_email
        SharePlatform.XIAOHONGSHU -> android.R.drawable.ic_dialog_email
        SharePlatform.DOUYIN -> android.R.drawable.ic_dialog_email
        SharePlatform.FACEBOOK -> android.R.drawable.ic_dialog_email
        SharePlatform.COPY_LINK -> android.R.drawable.ic_menu_share
        SharePlatform.MORE -> android.R.drawable.ic_menu_more
    }
}

/**
 * 获取平台名称
 */
private fun getPlatformName(platform: SharePlatform): String {
    return when (platform) {
        SharePlatform.WECHAT -> "微信"
        SharePlatform.WECHAT_MOMENTS -> "朋友圈"
        SharePlatform.QQ -> "QQ"
        SharePlatform.QQ_ZONE -> "QQ空间"
        SharePlatform.WEIBO -> "微博"
        SharePlatform.XIAOHONGSHU -> "小红书"
        SharePlatform.DOUYIN -> "抖音"
        SharePlatform.FACEBOOK -> "Facebook"
        SharePlatform.COPY_LINK -> "复制链接"
        SharePlatform.MORE -> "更多"
    }
}

/**
 * 截取屏幕内容
 */
private suspend fun captureScreen(context: Context, view: View): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val activity = context as? Activity ?: return@withContext null
            val window = activity.window
            
            // 获取屏幕尺寸
            val displayMetrics = context.resources.displayMetrics
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            
            // 创建位图
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            
            // 使用PixelCopy进行截图（API 26+）
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val locationOfViewInWindow = IntArray(2)
                view.getLocationInWindow(locationOfViewInWindow)
                
                val rect = Rect(
                    locationOfViewInWindow[0],
                    locationOfViewInWindow[1],
                    locationOfViewInWindow[0] + view.width,
                    locationOfViewInWindow[1] + view.height
                )
                
                var result: Bitmap? = null
                val handler = Handler(Looper.getMainLooper())
                
                PixelCopy.request(
                    window,
                    rect,
                    bitmap,
                    { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) {
                            result = bitmap
                        }
                    },
                    handler
                )
                
                // 等待截图完成
                var attempts = 0
                while (result == null && attempts < 50) {
                    Thread.sleep(100)
                    attempts++
                }
                
                result
            } else {
                // 降级方案：使用View.draw
                val canvas = Canvas(bitmap)
                view.draw(canvas)
                bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}