package com.horizon.higame.ui.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.horizon.higame.ui.components.HiGameButton
import com.horizon.higame.ui.components.HiGameLoadingView

object HiGameLoginView {
    private var currentConfig: LoginConfig? = null
    
    fun show(
        context: Context,
        config: LoginConfig = LoginConfig(),
        callback: LoginCallback? = null
    ) {
        currentConfig = config.copy(callback = callback)
        when (config.displayStyle) {
            LoginDisplayStyle.FULLSCREEN -> {
                // 全屏模式直接启动Activity
            }
            LoginDisplayStyle.MODAL,
             LoginDisplayStyle.BOTTOM_SHEET,
             LoginDisplayStyle.EMBEDDED -> {
                 // TODO: 启动透明Activity显示登录界面
             }
         }
    }
    
    internal fun getConfig(): LoginConfig {
        return currentConfig ?: LoginConfig()
    }
}

@Composable
fun LoginScreen(
    config: LoginConfig = HiGameLoginView.getConfig(),
    onDismiss: (() -> Unit)? = null
) {
    var currentMethod by remember { mutableStateOf(LoginMethod.USERNAME) }
    var loginState by remember { mutableStateOf(LoginState.IDLE) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val onNavigateToRegister = {
        // 根据当前登录显示模式跳转到对应的注册页面
        com.horizon.higame.ui.register.HiGameRegisterView.show(
            context = context,
            config = config
        )
        // 关闭当前登录页面
        onDismiss?.invoke()
        Unit
    }
    
    when (config.displayStyle) {
        LoginDisplayStyle.FULLSCREEN -> {
            FullscreenLoginContent(
                config = config,
                currentMethod = currentMethod,
                onMethodChange = { currentMethod = it },
                loginState = loginState,
                onLoginStateChange = { loginState = it },
                errorMessage = errorMessage,
                onErrorChange = { errorMessage = it },
                onDismiss = onDismiss,
                onNavigateToRegister = onNavigateToRegister
            )
        }
        LoginDisplayStyle.MODAL -> {
            CardModalLoginContent(
                config = config,
                currentMethod = currentMethod,
                onMethodChange = { currentMethod = it },
                loginState = loginState,
                onLoginStateChange = { loginState = it },
                errorMessage = errorMessage,
                onErrorChange = { errorMessage = it },
                onDismiss = onDismiss,
                onNavigateToRegister = onNavigateToRegister
            )
        }
        LoginDisplayStyle.BOTTOM_SHEET -> {
            BottomSheetLoginContent(
                config = config,
                currentMethod = currentMethod,
                onMethodChange = { currentMethod = it },
                loginState = loginState,
                onLoginStateChange = { loginState = it },
                errorMessage = errorMessage,
                onErrorChange = { errorMessage = it },
                onDismiss = onDismiss,
                onNavigateToRegister = onNavigateToRegister
            )
        }
        LoginDisplayStyle.EMBEDDED -> {
            CenterDialogLoginContent(
                config = config,
                currentMethod = currentMethod,
                onMethodChange = { currentMethod = it },
                loginState = loginState,
                onLoginStateChange = { loginState = it },
                errorMessage = errorMessage,
                onErrorChange = { errorMessage = it },
                onDismiss = onDismiss,
                onNavigateToRegister = onNavigateToRegister
            )
        }
    }
}