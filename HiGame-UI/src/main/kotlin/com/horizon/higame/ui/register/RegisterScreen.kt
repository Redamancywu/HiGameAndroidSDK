package com.horizon.higame.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.horizon.higame.ui.components.HiGameBottomSheet
import com.horizon.higame.ui.components.HiGameDialog
import com.horizon.higame.ui.components.HiGameLoadingView
import com.horizon.higame.ui.login.LoginConfig
import com.horizon.higame.ui.login.LoginState
import com.horizon.higame.ui.login.LoginUIConfig
import com.horizon.higame.ui.register.RegisterMethod
import com.horizon.higame.ui.register.RegisterMethodSelector
import com.horizon.higame.ui.register.RegisterMethodForm
import com.horizon.higame.ui.register.RegisterAgreementText

/**
 * 注册页面显示模式
 */
enum class RegisterDisplayMode {
    FULLSCREEN,    // 全屏显示
    CARD_MODAL,    // 半屏卡片模态
    BOTTOM_SHEET,  // 底部滑出
    CENTER_DIALOG  // 中心对话框
}

/**
 * 注册页面主入口
 */
@Composable
fun RegisterScreen(
    displayMode: RegisterDisplayMode = RegisterDisplayMode.FULLSCREEN,
    config: LoginConfig,
    onDismiss: (() -> Unit)? = null,
    onNavigateBack: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    var currentMethod by remember { mutableStateOf(RegisterMethod.PHONE) }
    var registerState by remember { mutableStateOf(LoginState.IDLE) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    when (displayMode) {
        RegisterDisplayMode.FULLSCREEN -> {
            FullscreenRegisterContent(
                config = config,
                currentMethod = currentMethod,
                onMethodChange = { currentMethod = it },
                registerState = registerState,
                onRegisterStateChange = { registerState = it },
                errorMessage = errorMessage,
                onErrorChange = { errorMessage = it },
                onDismiss = onDismiss,
                onNavigateBack = onNavigateBack,
                onRegisterSuccess = onRegisterSuccess
            )
        }
        RegisterDisplayMode.CARD_MODAL -> {
            CardModalRegisterContent(
                config = config,
                currentMethod = currentMethod,
                onMethodChange = { currentMethod = it },
                registerState = registerState,
                onRegisterStateChange = { registerState = it },
                errorMessage = errorMessage,
                onErrorChange = { errorMessage = it },
                onDismiss = onDismiss,
                onNavigateBack = onNavigateBack,
                onRegisterSuccess = onRegisterSuccess
            )
        }
        RegisterDisplayMode.BOTTOM_SHEET -> {
            BottomSheetRegisterContent(
                config = config,
                currentMethod = currentMethod,
                onMethodChange = { currentMethod = it },
                registerState = registerState,
                onRegisterStateChange = { registerState = it },
                errorMessage = errorMessage,
                onErrorChange = { errorMessage = it },
                onDismiss = onDismiss,
                onNavigateBack = onNavigateBack,
                onRegisterSuccess = onRegisterSuccess
            )
        }
        RegisterDisplayMode.CENTER_DIALOG -> {
            CenterDialogRegisterContent(
                config = config,
                currentMethod = currentMethod,
                onMethodChange = { currentMethod = it },
                registerState = registerState,
                onRegisterStateChange = { registerState = it },
                errorMessage = errorMessage,
                onErrorChange = { errorMessage = it },
                onDismiss = onDismiss,
                onNavigateBack = onNavigateBack,
                onRegisterSuccess = onRegisterSuccess
            )
        }
    }
}

/**
 * 全屏注册内容
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenRegisterContent(
    config: LoginConfig,
    currentMethod: RegisterMethod,
    onMethodChange: (RegisterMethod) -> Unit,
    registerState: LoginState,
    onRegisterStateChange: (LoginState) -> Unit,
    errorMessage: String?,
    onErrorChange: (String?) -> Unit,
    onDismiss: (() -> Unit)?,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1976D2),
                        Color(0xFF42A5F5)
                    )
                )
            )
    ) {
        // 顶部导航栏
        TopAppBar(
            title = { Text("注册账号", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // 主要内容
        RegisterMainContent(
            config = config,
            currentMethod = currentMethod,
            onMethodChange = onMethodChange,
            registerState = registerState,
            onRegisterStateChange = onRegisterStateChange,
            errorMessage = errorMessage,
            onErrorChange = onErrorChange,
            onRegisterSuccess = onRegisterSuccess,
            isFullscreen = true,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp)
        )
    }
}

/**
 * 卡片模态注册内容
 */
@Composable
fun CardModalRegisterContent(
    config: LoginConfig,
    currentMethod: RegisterMethod,
    onMethodChange: (RegisterMethod) -> Unit,
    registerState: LoginState,
    onRegisterStateChange: (LoginState) -> Unit,
    errorMessage: String?,
    onErrorChange: (String?) -> Unit,
    onDismiss: (() -> Unit)?,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    HiGameDialog(
        onDismiss = { onDismiss?.invoke() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column {
                // 标题栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "注册账号",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { onDismiss?.invoke() }) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                }
                
                Divider()
                
                // 主要内容
                RegisterMainContent(
                    config = config,
                    currentMethod = currentMethod,
                    onMethodChange = onMethodChange,
                    registerState = registerState,
                    onRegisterStateChange = onRegisterStateChange,
                    errorMessage = errorMessage,
                    onErrorChange = onErrorChange,
                    onRegisterSuccess = onRegisterSuccess,
                    isFullscreen = false,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        }
    }
}

/**
 * 底部滑出注册内容
 */
@Composable
fun BottomSheetRegisterContent(
    config: LoginConfig,
    currentMethod: RegisterMethod,
    onMethodChange: (RegisterMethod) -> Unit,
    registerState: LoginState,
    onRegisterStateChange: (LoginState) -> Unit,
    errorMessage: String?,
    onErrorChange: (String?) -> Unit,
    onDismiss: (() -> Unit)?,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    HiGameBottomSheet(
        onDismiss = { onDismiss?.invoke() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "注册账号",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onDismiss?.invoke() }) {
                    Icon(Icons.Default.Close, contentDescription = "关闭")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 主要内容
            RegisterMainContent(
                config = config,
                currentMethod = currentMethod,
                onMethodChange = onMethodChange,
                registerState = registerState,
                onRegisterStateChange = onRegisterStateChange,
                errorMessage = errorMessage,
                onErrorChange = onErrorChange,
                onRegisterSuccess = onRegisterSuccess,
                isFullscreen = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * 中心对话框注册内容
 */
@Composable
fun CenterDialogRegisterContent(
    config: LoginConfig,
    currentMethod: RegisterMethod,
    onMethodChange: (RegisterMethod) -> Unit,
    registerState: LoginState,
    onRegisterStateChange: (LoginState) -> Unit,
    errorMessage: String?,
    onErrorChange: (String?) -> Unit,
    onDismiss: (() -> Unit)?,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss?.invoke() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // 标题
                Text(
                    text = "注册账号",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 主要内容
                RegisterMainContent(
                    config = config,
                    currentMethod = currentMethod,
                    onMethodChange = onMethodChange,
                    registerState = registerState,
                    onRegisterStateChange = onRegisterStateChange,
                    errorMessage = errorMessage,
                    onErrorChange = onErrorChange,
                    onRegisterSuccess = onRegisterSuccess,
                    isFullscreen = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 注册主要内容
 */
@Composable
private fun RegisterMainContent(
    config: LoginConfig,
    currentMethod: RegisterMethod,
    onMethodChange: (RegisterMethod) -> Unit,
    registerState: LoginState,
    onRegisterStateChange: (LoginState) -> Unit,
    errorMessage: String?,
    onErrorChange: (String?) -> Unit,
    onRegisterSuccess: () -> Unit,
    isFullscreen: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 注册方式选择
        RegisterMethodSelector(
            methods = listOf(RegisterMethod.PHONE, RegisterMethod.EMAIL),
            currentMethod = currentMethod,
            onMethodChange = { method ->
                onMethodChange(method)
                onErrorChange(null)
            },
            config = config.uiConfig
        )
        
        // 注册表单
        RegisterMethodForm(
            method = currentMethod,
            config = config,
            registerState = registerState,
            onRegisterStateChange = onRegisterStateChange,
            onErrorChange = onErrorChange,
            onRegisterSuccess = onRegisterSuccess
        )
        
        // 错误信息
        if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2))
            ) {
                Text(
                    text = errorMessage,
                    color = Color(android.graphics.Color.parseColor(config.uiConfig.errorColor)),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        
        // 加载状态
        if (registerState == LoginState.LOADING) {
            HiGameLoadingView(
                modifier = Modifier.size(48.dp)
            )
        }
        
        // 用户协议和隐私政策
        if (config.uiConfig.showUserAgreement) {
            RegisterAgreementText(
                config = config.uiConfig,
                onAgreementClick = { /* TODO: 打开用户协议 */ },
                onPrivacyClick = { /* TODO: 打开隐私政策 */ }
            )
        }
    }
}