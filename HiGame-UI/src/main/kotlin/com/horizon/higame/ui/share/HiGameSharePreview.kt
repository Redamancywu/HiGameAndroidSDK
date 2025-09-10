package com.horizon.higame.ui.share

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.horizon.higame.core.ui.HiGameTheme
import com.horizon.higame.ui.theme.HiGameMaterialAdapter

@Preview(name = "Share - GRID_MODAL", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_Share_GridModal() {
    SharePreviewScaffoldInternal(style = ShareDisplayStyle.GRID_MODAL)
}

@Preview(name = "分享 - 底部弹出", showBackground = true, heightDp = 800)
@Composable
fun Preview_Share_ActionSheet() {
    HiGameMaterialAdapter(theme = HiGameTheme()) {
        ShareActionSheetPreview(
            config = ShareUIConfig(
                displayStyle = ShareDisplayStyle.ACTION_SHEET,
                enabledPlatforms = listOf(
                     SharePlatform.WECHAT,
                     SharePlatform.WECHAT_MOMENTS,
                     SharePlatform.QQ,
                     SharePlatform.QQ_ZONE,
                     SharePlatform.WEIBO,
                     SharePlatform.DOUYIN,
                     SharePlatform.COPY_LINK,
                     SharePlatform.MORE
                 ),
                gridColumns = 4,
                gridRows = 2,
                itemSpacing = 20,
                iconSize = 56,
                cornerRadius = 20
            )
        )
    }
}

@Preview(name = "Share - FULLSCREEN", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_Share_Fullscreen() {
    SharePreviewScaffoldInternal(style = ShareDisplayStyle.FULLSCREEN)
}

@Preview(name = "Share - Platform Grid", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_Share_PlatformGrid() {
    HiGameMaterialAdapter(theme = HiGameTheme()) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "2行4列社交平台分享",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                SharePlatformGrid(
                    platforms = listOf(
                        SharePlatform.QQ,
                        SharePlatform.QQ_ZONE,
                        SharePlatform.WECHAT,
                        SharePlatform.WECHAT_MOMENTS,
                        SharePlatform.WEIBO,
                        SharePlatform.DOUYIN,
                        SharePlatform.COPY_LINK,
                        SharePlatform.MORE
                    ),
                    config = ShareUIConfig(
                        gridColumns = 4,
                        gridRows = 2,
                        iconSize = 48,
                        itemSpacing = 16
                    ),
                    onPlatformClick = { /* 预览模式，无实际操作 */ },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(name = "Share - Container", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_Share_Container() {
    HiGameMaterialAdapter(theme = HiGameTheme()) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.padding(16.dp)) {
                ShareContainer(
                    config = ShareUIConfig(),
                    onPlatformClick = { /* 预览模式，无实际操作 */ },
                    onDismiss = { /* 预览模式，无实际操作 */ }
                )
            }
        }
    }
}



@Composable
fun ShareActionSheetPreview(
    config: ShareUIConfig
) {
    var isVisible by remember { mutableStateOf(true) }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300, easing = EaseOutCubic)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300, easing = EaseInCubic)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { isVisible = false },
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .heightIn(min = 300.dp, max = 500.dp)
                    .clickable(enabled = false) { },
                shape = RoundedCornerShape(
                    topStart = config.cornerRadius.dp,
                    topEnd = config.cornerRadius.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp)
                ) {
                    // 顶部指示条
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                Color.Gray.copy(alpha = 0.3f),
                                RoundedCornerShape(2.dp)
                            )
                            .align(Alignment.CenterHorizontally)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 标题和取消按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "分享到",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        TextButton(
                            onClick = { isVisible = false }
                        ) {
                            Text("取消")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 分享平台网格
                    SharePlatformGrid(
                        platforms = config.enabledPlatforms.take(8),
                        config = config,
                        onPlatformClick = { /* 预览中的点击处理 */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .heightIn(max = 300.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SharePreviewScaffoldInternal(style: ShareDisplayStyle) {
    HiGameMaterialAdapter(theme = HiGameTheme()) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "HiGame Share (${style.name})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                when (style) {
                    ShareDisplayStyle.GRID_MODAL -> {
                        ShareContainer(
                            config = ShareUIConfig(displayStyle = style),
                            onPlatformClick = { /* 预览模式，无实际操作 */ },
                            onDismiss = { /* 预览模式，无实际操作 */ }
                        )
                    }
                    ShareDisplayStyle.ACTION_SHEET -> {
                        ShareContainer(
                            config = ShareUIConfig(
                                displayStyle = style,
                                cornerRadius = 16
                            ),
                            onPlatformClick = { /* 预览模式，无实际操作 */ },
                            onDismiss = { /* 预览模式，无实际操作 */ }
                        )
                    }
                    ShareDisplayStyle.FULLSCREEN -> {
                        SharePlatformGrid(
                            platforms = SharePlatform.values().toList(),
                            config = ShareUIConfig(displayStyle = style),
                            onPlatformClick = { /* 预览模式，无实际操作 */ },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}