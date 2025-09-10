package com.horizon.higame.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 分享平台图标按钮
 */
@Composable
fun SharePlatformButton(
    platform: SharePlatform,
    config: ShareUIConfig,
    onClick: (SharePlatform) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = if (config.enableRippleEffect) androidx.compose.material3.ripple() else null
            ) { onClick(platform) }
            .padding(8.dp)
    ) {
        // 平台图标
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(config.iconSize.dp)
                .clip(CircleShape)
                .background(platform.brandColor)
        ) {
            // 这里使用文字代替图标，实际项目中应该使用真实的平台图标
            Text(
                text = when (platform) {
                    SharePlatform.QQ -> "Q"
                    SharePlatform.QQ_ZONE -> "空"
                    SharePlatform.WECHAT -> "微"
                    SharePlatform.WECHAT_MOMENTS -> "圈"
                    SharePlatform.WEIBO -> "博"
                    SharePlatform.XIAOHONGSHU -> "红"
                    SharePlatform.DOUYIN -> "抖"
                    SharePlatform.FACEBOOK -> "F"
                    SharePlatform.COPY_LINK -> "链"
                    SharePlatform.MORE -> "更"
                },
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // 平台名称
        if (config.showPlatformName) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = platform.displayName,
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp,
                modifier = Modifier.widthIn(max = 80.dp)
            )
        }
    }
}

/**
 * 分享平台网格布局
 */
@Composable
fun SharePlatformGrid(
    platforms: List<SharePlatform>,
    config: ShareUIConfig,
    onPlatformClick: (SharePlatform) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(config.gridColumns),
        contentPadding = PaddingValues(config.itemSpacing.dp),
        horizontalArrangement = Arrangement.spacedBy(config.itemSpacing.dp),
        verticalArrangement = Arrangement.spacedBy(config.itemSpacing.dp),
        modifier = modifier
    ) {
        items(platforms) { platform ->
            SharePlatformButton(
                platform = platform,
                config = config,
                onClick = onPlatformClick
            )
        }
    }
}

/**
 * 分享容器组件
 */
@Composable
fun ShareContainer(
    config: ShareUIConfig,
    onPlatformClick: (SharePlatform) -> Unit,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val platforms = config.enabledPlatforms.take(config.gridColumns * config.gridRows)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(config.cornerRadius.dp),
        colors = CardDefaults.cardColors(containerColor = config.backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题
            Text(
                text = "分享到",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 平台网格
            SharePlatformGrid(
                platforms = platforms,
                config = config,
                onPlatformClick = onPlatformClick,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 取消按钮
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "取消",
                    color = Color.Gray
                )
            }
        }
    }
}