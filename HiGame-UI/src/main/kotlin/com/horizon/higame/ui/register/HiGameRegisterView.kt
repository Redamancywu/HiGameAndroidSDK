package com.horizon.higame.ui.register

import android.content.Context
import androidx.compose.runtime.*
import com.horizon.higame.ui.login.LoginConfig
import com.horizon.higame.ui.login.LoginDisplayStyle
import com.horizon.higame.ui.login.LoginState

/**
 * 注册页面管理器
 * 负责根据登录页面的显示模式来展示对应的注册页面
 */
object HiGameRegisterView {
    private var currentConfig: LoginConfig? = null
    
    /**
     * 显示注册页面
     * @param context 上下文
     * @param config 登录配置（继承登录页面的配置）
     * @param onRegisterSuccess 注册成功回调
     * @param onNavigateBack 返回回调
     */
    fun show(
        context: Context,
        config: LoginConfig = LoginConfig(),
        onRegisterSuccess: () -> Unit = {},
        onNavigateBack: () -> Unit = {}
    ) {
        currentConfig = config
        when (config.displayStyle) {
            LoginDisplayStyle.FULLSCREEN -> {
                // 全屏模式直接启动Activity
            }
            LoginDisplayStyle.MODAL,
            LoginDisplayStyle.BOTTOM_SHEET,
            LoginDisplayStyle.EMBEDDED -> {
                // TODO: 启动透明Activity显示注册界面
            }
        }
    }
    
    /**
     * 根据登录显示模式转换为注册显示模式
     */
    fun getRegisterDisplayMode(loginDisplayStyle: LoginDisplayStyle): RegisterDisplayMode {
        return when (loginDisplayStyle) {
            LoginDisplayStyle.FULLSCREEN -> RegisterDisplayMode.FULLSCREEN
            LoginDisplayStyle.MODAL -> RegisterDisplayMode.CARD_MODAL
            LoginDisplayStyle.BOTTOM_SHEET -> RegisterDisplayMode.BOTTOM_SHEET
            LoginDisplayStyle.EMBEDDED -> RegisterDisplayMode.CENTER_DIALOG
        }
    }
    
    internal fun getConfig(): LoginConfig {
        return currentConfig ?: LoginConfig()
    }
}

/**
 * 注册页面组件
 * 根据登录页面的显示模式自动选择对应的注册页面展示方式
 */
@Composable
fun RegisterScreenWithInheritedMode(
    loginDisplayStyle: LoginDisplayStyle,
    config: LoginConfig = HiGameRegisterView.getConfig(),
    onDismiss: (() -> Unit)? = null,
    onNavigateBack: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    val registerDisplayMode = HiGameRegisterView.getRegisterDisplayMode(loginDisplayStyle)
    
    RegisterScreen(
        displayMode = registerDisplayMode,
        config = config,
        onDismiss = onDismiss,
        onNavigateBack = onNavigateBack,
        onRegisterSuccess = onRegisterSuccess
    )
}

/**
 * 独立的注册页面组件
 * 可以直接指定显示模式
 */
@Composable
fun RegisterScreenStandalone(
    displayMode: RegisterDisplayMode = RegisterDisplayMode.FULLSCREEN,
    config: LoginConfig = HiGameRegisterView.getConfig(),
    onDismiss: (() -> Unit)? = null,
    onNavigateBack: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    RegisterScreen(
        displayMode = displayMode,
        config = config,
        onDismiss = onDismiss,
        onNavigateBack = onNavigateBack,
        onRegisterSuccess = onRegisterSuccess
    )
}

/**
 * 从登录页面跳转到注册页面的辅助函数
 * 确保注册页面与登录页面使用相同的展示模式
 */
@Composable
fun NavigateToRegisterFromLogin(
    loginConfig: LoginConfig,
    onDismiss: (() -> Unit)? = null,
    onNavigateBack: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    RegisterScreenWithInheritedMode(
        loginDisplayStyle = loginConfig.displayStyle,
        config = loginConfig,
        onDismiss = onDismiss,
        onNavigateBack = onNavigateBack,
        onRegisterSuccess = onRegisterSuccess
    )
}