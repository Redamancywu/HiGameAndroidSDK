package com.horizon.higamesdk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.horizon.higamesdk.ui.theme.HiGameSDKTheme
import com.horizon.higame.ui.login.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiGameSDKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PreviewLauncher(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
private fun PreviewLauncher(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            HiGameLoginView.show(
                context = context,
                config = LoginConfig(
                    displayStyle = LoginDisplayStyle.MODAL,
                    uiConfig = LoginUIConfig(
                        title = "HiGame登录",
                        subtitle = "欢迎回来",
                        enabledMethods = listOf("WECHAT", "QQ", "PHONE", "GUEST")
                    )
                )
            )
        }) {
            Text("打开登录界面（弹窗）")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLauncherPreview() {
    HiGameSDKTheme {
        PreviewLauncher()
    }
}