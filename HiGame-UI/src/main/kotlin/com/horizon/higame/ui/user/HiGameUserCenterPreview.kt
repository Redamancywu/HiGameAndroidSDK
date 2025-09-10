package com.horizon.higame.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.horizon.higame.core.ui.HiGameTheme
import com.horizon.higame.ui.theme.HiGameMaterialAdapter

/**
 * UserCenter 独立预览
 * - 不依赖 Activity/透明容器
 * - 可在 AS 预览面板选择不同样式
 * - 展示完整的用户中心界面设计
 */
@Preview(
    name = "UserCenter - LIST_MODAL", 
    showBackground = true, 
    backgroundColor = 0xFFFFFFFF,
    heightDp = 800,
    widthDp = 400
)
@Composable
fun Preview_UserCenter_ListModal() {
    UserCenterPreviewScaffold(style = "LIST_MODAL")
}

@Preview(
    name = "UserCenter - FULLSCREEN", 
    showBackground = true, 
    backgroundColor = 0xFFFFFFFF,
    heightDp = 800,
    widthDp = 400
)
@Composable
fun Preview_UserCenter_Fullscreen() {
    UserCenterPreviewScaffold(style = "FULLSCREEN")
}

@Preview(
    name = "UserCenter - SLIDE_PANEL", 
    showBackground = true, 
    backgroundColor = 0xFFFFFFFF,
    heightDp = 800,
    widthDp = 400
)
@Composable
fun Preview_UserCenter_SlidePanel() {
    UserCenterPreviewScaffold(style = "SLIDE_PANEL")
}

@Preview(
    name = "UserCenter - Dark Theme", 
    showBackground = true, 
    backgroundColor = 0xFF121212,
    heightDp = 800,
    widthDp = 400
)
@Composable
fun Preview_UserCenter_DarkTheme() {
    UserCenterPreviewScaffold(style = "LIST_MODAL", isDark = true)
}

@Composable
private fun UserCenterPreviewScaffold(style: String, isDark: Boolean = false) {
    // 使用 Core 的主题配置适配到 Material3，便于预览一致风格
    val theme = if (isDark) {
        HiGameTheme().copy(
            colors = HiGameTheme().colors.copy(
                background = 0xFF121212.toInt(),
                surface = 0xFF1E1E1E.toInt()
            )
        )
    } else {
        HiGameTheme()
    }
    
    // 示例用户数据
    val sampleUserInfo = HiGameUserCenterView.UserInfo(
        username = "超级玩家",
        level = 25,
        experience = "1580/2000",
        avatarUrl = null, // 使用本地头像
        gameTime = "256小时",
        points = "8,520",
        achievements = "42"
    )
    
    HiGameMaterialAdapter(theme = theme) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            // 直接使用 HiGameUserCenterView 的 UserCenterScreen
            // 现在它是 internal 可见性，可以在预览中使用
            HiGameUserCenterView.UserCenterScreen(
                style = style,
                userInfo = sampleUserInfo
            )
        }
    }
}

/**
 * 紧凑版预览 - 用于快速查看布局
 */
@Preview(
    name = "UserCenter - Compact", 
    showBackground = true,
    heightDp = 600,
    widthDp = 320
)
@Composable
fun Preview_UserCenter_Compact() {
    UserCenterPreviewScaffold(style = "LIST_MODAL")
}

/**
 * 高级用户预览 - 展示丰富数据
 */
@Preview(
    name = "UserCenter - Advanced User", 
    showBackground = true,
    heightDp = 800,
    widthDp = 400
)
@Composable
fun Preview_UserCenter_AdvancedUser() {
    val advancedUserInfo = HiGameUserCenterView.UserInfo(
        username = "传奇大师",
        level = 99,
        experience = "9999/10000",
        avatarUrl = null, // 使用本地头像
        gameTime = "1,024小时",
        points = "99,999",
        achievements = "128"
    )
    
    HiGameMaterialAdapter(theme = HiGameTheme()) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            HiGameUserCenterView.UserCenterScreen(
                style = "LIST_MODAL",
                userInfo = advancedUserInfo
            )
        }
    }
}

/**
 * 平板预览 - 展示响应式布局
 */
@Preview(
    name = "UserCenter - Tablet", 
    showBackground = true,
    heightDp = 800,
    widthDp = 600,
    device = "spec:width=600dp,height=800dp,dpi=240"
)
@Composable
fun Preview_UserCenter_Tablet() {
    UserCenterPreviewScaffold(style = "FULLSCREEN")
}