package com.horizon.higame.ui.login

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

@Preview(name = "Login - CARD_MODAL", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_Login_CardModal() {
    LoginPreviewScaffold(style = "CARD_MODAL")
}

@Preview(name = "Login - FULLSCREEN", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_Login_Fullscreen() {
    LoginPreviewScaffold(style = "FULLSCREEN")
}

@Preview(name = "Login - BOTTOM_SHEET", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_Login_BottomSheet() {
    LoginPreviewScaffold(style = "BOTTOM_SHEET")
}

@Composable
private fun LoginPreviewScaffold(style: String) {
    HiGameMaterialAdapter(theme = HiGameTheme()) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {
                Text("HiGame Login ($style)", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                HiGameButton(text = "微信登录") {}
                Spacer(Modifier.height(8.dp))
                HiGameButton(text = "游客登录") {}
            }
        }
    }
}