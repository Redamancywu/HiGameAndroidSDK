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

@Preview(name = "Login - MODAL", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_Login_Modal() {
    LoginPreviewScaffold(style = "MODAL")
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

@Preview(name = "Login - EMBEDDED", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun Preview_Login_Embedded() {
    LoginPreviewScaffold(style = "EMBEDDED")
}

@Composable
private fun LoginPreviewScaffold(style: String) {
    HiGameMaterialAdapter(theme = HiGameTheme()) {
        Surface(color = MaterialTheme.colorScheme.background) {
            val displayStyle = when (style) {
                "FULLSCREEN" -> LoginDisplayStyle.FULLSCREEN
                "MODAL" -> LoginDisplayStyle.MODAL
                "BOTTOM_SHEET" -> LoginDisplayStyle.BOTTOM_SHEET
                "EMBEDDED" -> LoginDisplayStyle.EMBEDDED
                else -> LoginDisplayStyle.FULLSCREEN
            }
            
            val config = LoginConfig(
                displayStyle = displayStyle,
                uiConfig = LoginUIConfig(
                    title = "HiGame登录",
                    subtitle = "欢迎回来",
                    enabledMethods = listOf("WECHAT", "QQ", "PHONE", "GUEST")
                )
            )
            
            LoginScreen(
                config = config,
                onDismiss = {}
            )
        }
    }
}