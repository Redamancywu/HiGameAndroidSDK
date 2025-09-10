package com.horizon.higame.ui.share

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.horizon.higame.core.ui.HiGameTheme
import com.horizon.higame.ui.internal.HiGameTransparentActivity
import com.horizon.higame.ui.screens.ScreenSpec
import com.horizon.higame.ui.theme.HiGameMaterialAdapter

object HiGameShareView {

    @JvmStatic
    fun show(
        activity: Activity, 
        style: String = "GRID_MODAL",
        content: ShareContent = ShareContent(),
        callback: ShareCallback? = null
    ) {
        val config = ShareUIConfig(
            displayStyle = when (style) {
                "ACTION_SHEET" -> ShareDisplayStyle.ACTION_SHEET
                "FULLSCREEN" -> ShareDisplayStyle.FULLSCREEN
                else -> ShareDisplayStyle.GRID_MODAL
            }
        )
        HiGameTransparentActivity.start(activity, ScreenSpec.Share { 
            ShareScreen(content, config, callback) 
        })
    }

    @Composable
    private fun ShareScreen(
        content: ShareContent,
        config: ShareUIConfig,
        callback: ShareCallback?
    ) {
        when (config.displayStyle) {
            ShareDisplayStyle.GRID_MODAL -> {
                ShareModalDialog(content, config, callback)
            }
            ShareDisplayStyle.ACTION_SHEET -> {
                ShareActionSheet(content, config, callback)
            }
            ShareDisplayStyle.FULLSCREEN -> {
                ShareFullscreen(content, config, callback)
            }
        }
    }

    @Composable
    private fun ShareModalDialog(
        content: ShareContent,
        config: ShareUIConfig,
        callback: ShareCallback?
    ) {
        var showDialog by remember { mutableStateOf(true) }
        
        if (showDialog) {
            Dialog(
                onDismissRequest = {
                    showDialog = false
                    callback?.onShareCancelled()
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                SharePlatformGrid(
                    platforms = config.enabledPlatforms,
                    config = config,
                    onPlatformClick = { platform ->
                        callback?.onPlatformSelected(platform)
                        ShareUtils.shareToPlatform(platform, content, callback)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Composable
    private fun ShareActionSheet(
        content: ShareContent,
        config: ShareUIConfig,
        callback: ShareCallback?
    ) {
        var showSheet by remember { mutableStateOf(true) }
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        val screenHeight = with(density) { configuration.screenHeightDp.dp }
        val sheetHeight = screenHeight * 0.4f
        
        if (showSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { 
                        showSheet = false
                        callback?.onShareCancelled()
                    },
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visible = showSheet,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = FastOutSlowInEasing
                        )
                    ) + fadeIn(
                        animationSpec = tween(
                            durationMillis = 200,
                            delayMillis = 100
                        )
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(
                            durationMillis = 250,
                            easing = FastOutLinearInEasing
                        )
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 150)
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(sheetHeight)
                            .clickable(enabled = false) { /* 阻止点击穿透 */ },
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                        ) {
                            // 顶部指示条
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.Gray.copy(alpha = 0.3f))
                                    .align(Alignment.CenterHorizontally)
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            // 标题
                            Text(
                                text = "分享到",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )
                            
                            // 分享平台网格
                            SharePlatformGrid(
                                platforms = config.enabledPlatforms.take(8),
                                config = config,
                                onPlatformClick = { platform ->
                                    showSheet = false
                                    callback?.onPlatformSelected(platform)
                                    ShareUtils.shareToPlatform(platform, content, callback)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // 取消按钮
                            OutlinedButton(
                                onClick = {
                                    showSheet = false
                                    callback?.onShareCancelled()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "取消",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ShareFullscreen(
        content: ShareContent,
        config: ShareUIConfig,
        callback: ShareCallback?
    ) {
        var showFullscreen by remember { mutableStateOf(true) }
        
        if (showFullscreen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(config.backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    if (content.title.isNotEmpty()) {
                        Text(
                            text = content.title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    if (content.description.isNotEmpty()) {
                        Text(
                            text = content.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                    }
                    
                    SharePlatformGrid(
                        platforms = config.enabledPlatforms,
                        config = config,
                        onPlatformClick = { platform ->
                            showFullscreen = false
                            callback?.onPlatformSelected(platform)
                            ShareUtils.shareToPlatform(platform, content, callback)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = {
                            showFullscreen = false
                            callback?.onShareCancelled()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("关闭")
                    }
                }
            }
        }
    }
}