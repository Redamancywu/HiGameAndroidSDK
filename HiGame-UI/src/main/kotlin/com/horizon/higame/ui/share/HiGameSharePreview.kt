package com.horizon.higame.ui.share

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

@Preview(name = "Share - GRID_MODAL", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_Share_GridModal() {
    SharePreviewScaffold(style = "GRID_MODAL")
}

@Preview(name = "Share - ACTION_SHEET", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_Share_ActionSheet() {
    SharePreviewScaffold(style = "ACTION_SHEET")
}

@Preview(name = "Share - FULLSCREEN", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_Share_Fullscreen() {
    SharePreviewScaffold(style = "FULLSCREEN")
}

@Composable
private fun SharePreviewScaffold(style: String) {
    HiGameMaterialAdapter(theme = HiGameTheme()) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                Text("HiGame Share ($style)", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                HiGameButton(text = "微信好友") {}
                Spacer(Modifier.height(8.dp))
                HiGameButton(text = "复制链接") {}
            }
        }
    }
}