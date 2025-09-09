package com.horizon.higame.ui.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.horizon.higame.core.ui.HiGameTheme
import com.horizon.higame.ui.components.HiGameButton
import com.horizon.higame.ui.theme.HiGameMaterialAdapter

/**
 * UserCenter 独立预览
 * - 不依赖 Activity/透明容器
 * - 可在 AS 预览面板选择不同样式
 */
@Preview(name = "UserCenter - LIST_MODAL", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_UserCenter_ListModal() {
    UserCenterPreviewScaffold(style = "LIST_MODAL")
}

@Preview(name = "UserCenter - FULLSCREEN", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_UserCenter_Fullscreen() {
    UserCenterPreviewScaffold(style = "FULLSCREEN")
}

@Preview(name = "UserCenter - SLIDE_PANEL", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_UserCenter_SlidePanel() {
    UserCenterPreviewScaffold(style = "SLIDE_PANEL")
}

@Composable
private fun UserCenterPreviewScaffold(style: String) {
    // 使用 Core 的主题配置适配到 Material3，便于预览一致风格
    HiGameMaterialAdapter(theme = HiGameTheme()) {
        Surface(color = MaterialTheme.colorScheme.background) {
            // 直接复用 HiGameUserCenterView 内部的屏幕内容
            // 该 Composable 目前定义为 private，故这里拷贝一份轻量展示，或将其改为 internal/public 以便复用
            UserCenterScreenPreviewContent(style)
        }
    }
}

@Composable
private fun UserCenterScreenPreviewContent(style: String) {
    Column {
        Text("HiGame User Center ($style)", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        HiGameButton(text = "设置") {}
        Spacer(Modifier.height(8.dp))
        HiGameButton(text = "登出") {}
    }
}