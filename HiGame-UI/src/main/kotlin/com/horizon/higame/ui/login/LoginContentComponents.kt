package com.horizon.higame.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.systemBarsPadding
import coil.compose.AsyncImage
import com.horizon.higame.ui.components.HiGameBottomSheet
import com.horizon.higame.ui.components.HiGameDialog
import com.horizon.higame.ui.components.HiGameLoadingView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 全屏登录内容
 */
@Composable
fun FullscreenLoginContent(
    config: LoginConfig,
    currentMethod: LoginMethod,
    onMethodChange: (LoginMethod) -> Unit,
    loginState: LoginState,
    onLoginStateChange: (LoginState) -> Unit,
    errorMessage: String?,
    onErrorChange: (String?) -> Unit,
    onDismiss: (() -> Unit)?,
    onNavigateToRegister: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        // 通用背景渲染（COLOR/GRADIENT/IMAGE）
        LoginBackground(ui = config.uiConfig)
         
         // 关闭按钮
         if (onDismiss != null) {
             IconButton(
                 onClick = onDismiss,
                 modifier = Modifier
                     .align(Alignment.TopEnd)
                     .padding(16.dp)
             ) {
                 Icon(
                     imageVector = Icons.Default.Close,
                     contentDescription = "关闭",
                     tint = Color(android.graphics.Color.parseColor(config.uiConfig.textColor))
                 )
             }
         }
         
         // 主要内容
         Column(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(24.dp)
                 .verticalScroll(rememberScrollState()),
             horizontalAlignment = Alignment.CenterHorizontally,
             verticalArrangement = Arrangement.Center
         ) {
             LoginMainContent(
                 config = config,
                 currentMethod = currentMethod,
                 onMethodChange = onMethodChange,
                 loginState = loginState,
                 onLoginStateChange = onLoginStateChange,
                 errorMessage = errorMessage,
                 onErrorChange = onErrorChange,
                 isFullscreen = true,
                 onNavigateToRegister = onNavigateToRegister
             )
         }
     }
 }

@Composable
private fun LoginBackground(ui: LoginUIConfig) {
    when (ui.backgroundType.uppercase()) {
        "IMAGE" -> {
            // 背景图（使用 Coil AsyncImage）
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = ui.backgroundImageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // 覆盖一层轻微遮罩，保证内容可读性
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
            }
        }
        "GRADIENT" -> {
            val colors = (ui.gradientColors ?: listOf(ui.primaryColor, ui.secondaryColor))
                .mapNotNull { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (colors.isNotEmpty()) colors else listOf(
                                Color(android.graphics.Color.parseColor(ui.primaryColor)),
                                Color(android.graphics.Color.parseColor(ui.secondaryColor))
                            )
                        )
                    )
            )
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(android.graphics.Color.parseColor(ui.backgroundColor)))
            )
        }
    }
}

/**
 * 卡片模态登录内容
 */
@Composable
fun CardModalLoginContent(
    config: LoginConfig,
    currentMethod: LoginMethod,
    onMethodChange: (LoginMethod) -> Unit,
    loginState: LoginState,
    onLoginStateChange: (LoginState) -> Unit,
    errorMessage: String?,
    onErrorChange: (String?) -> Unit,
    onDismiss: (() -> Unit)?,
    onNavigateToRegister: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable { onDismiss?.invoke(); Unit },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .offset(y = (-12).dp)
                .clickable(enabled = false) { }, // 阻止点击穿透
            shape = RoundedCornerShape(config.uiConfig.cornerRadius.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(android.graphics.Color.parseColor(config.uiConfig.backgroundColor))
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 关闭按钮
                if (onDismiss != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭",
                                tint = Color(android.graphics.Color.parseColor(config.uiConfig.textColor))
                            )
                        }
                    }
                }
                
                LoginMainContent(
                    config = config,
                    currentMethod = currentMethod,
                    onMethodChange = onMethodChange,
                    loginState = loginState,
                    onLoginStateChange = onLoginStateChange,
                    errorMessage = errorMessage,
                    onErrorChange = onErrorChange,
                    isFullscreen = false,
                    onNavigateToRegister = onNavigateToRegister
                )
            }
        }
    }
}

/**
 * 底部面板登录内容
 */
@Composable
fun BottomSheetLoginContent(
    config: LoginConfig,
    currentMethod: LoginMethod,
    onMethodChange: (LoginMethod) -> Unit,
    loginState: LoginState,
    onLoginStateChange: (LoginState) -> Unit,
    errorMessage: String?,
    onErrorChange: (String?) -> Unit,
    onDismiss: (() -> Unit)?,
    onNavigateToRegister: () -> Unit = {}
) {
    var showBottomSheet by remember { mutableStateOf(true) }
    
    if (showBottomSheet) {
        HiGameBottomSheet(
            onDismiss = {
                showBottomSheet = false
                onDismiss?.invoke()
                Unit
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 拖拽指示器
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.3f),
                            RoundedCornerShape(2.dp)
                        )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LoginMainContent(
                    config = config,
                    currentMethod = currentMethod,
                    onMethodChange = onMethodChange,
                    loginState = loginState,
                    onLoginStateChange = onLoginStateChange,
                    errorMessage = errorMessage,
                    onErrorChange = onErrorChange,
                    isFullscreen = false,
                    onNavigateToRegister = onNavigateToRegister
                )
            }
        }
    }
}

/**
 * 中心对话框登录内容
 */
@Composable
fun CenterDialogLoginContent(
    config: LoginConfig,
    currentMethod: LoginMethod,
    onMethodChange: (LoginMethod) -> Unit,
    loginState: LoginState,
    onLoginStateChange: (LoginState) -> Unit,
    errorMessage: String?,
    onErrorChange: (String?) -> Unit,
    onDismiss: (() -> Unit)?,
    onNavigateToRegister: () -> Unit = {}
) {
    var showDialog by remember { mutableStateOf(true) }
    
    if (showDialog) {
        HiGameDialog(
            onDismiss = {
                showDialog = false
                onDismiss?.invoke()
                Unit
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginMainContent(
                    config = config,
                    currentMethod = currentMethod,
                    onMethodChange = onMethodChange,
                    loginState = loginState,
                    onLoginStateChange = onLoginStateChange,
                    errorMessage = errorMessage,
                    onErrorChange = onErrorChange,
                    isFullscreen = false,
                    onNavigateToRegister = onNavigateToRegister
                )
            }
        }
    }
}

/**
 * 登录主要内容组件
 */
@Composable
private fun LoginMainContent(
    config: LoginConfig,
    currentMethod: LoginMethod,
    onMethodChange: (LoginMethod) -> Unit,
    loginState: LoginState,
    onLoginStateChange: (LoginState) -> Unit,
    errorMessage: String?,
    onErrorChange: (String?) -> Unit,
    isFullscreen: Boolean,
    onNavigateToRegister: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Logo
        if (!config.uiConfig.logoUrl.isNullOrEmpty()) {
            // TODO: 加载Logo图片
            Box(
                modifier = Modifier
                    .size(config.uiConfig.logoSize.dp)
                    .background(
                        Color.Gray.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LOGO",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // 标题区域
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = config.uiConfig.title,
                fontSize = if (isFullscreen) 32.sp else 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(android.graphics.Color.parseColor(config.uiConfig.textColor)),
                textAlign = TextAlign.Center
            )
            
            if (!config.uiConfig.subtitle.isNullOrEmpty()) {
                Text(
                    text = config.uiConfig.subtitle,
                    fontSize = 16.sp,
                    color = Color(android.graphics.Color.parseColor(config.uiConfig.textColor)).copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // 登录表单卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LoginMethodForm(
                    method = currentMethod,
                    config = config,
                    loginState = loginState,
                    onLoginStateChange = onLoginStateChange,
                    onErrorChange = onErrorChange,
                    onNavigateToRegister = onNavigateToRegister
                )
            }
        }

        // 错误信息
        if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                ),
                border = BorderStroke(1.dp, Color(0xFFFFCDD2))
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
        if (loginState == LoginState.LOADING) {
            HiGameLoadingView(
                modifier = Modifier.size(48.dp)
            )
        }

        // 统一样式的四个选项按钮（放在下方）
        val optionMethods = remember(currentMethod, config.uiConfig.supportedMethods) {
            config.uiConfig.supportedMethods.filter { it != currentMethod }.take(4)
        }
        if (optionMethods.isNotEmpty()) {
            LoginOptionButtons(
                methods = optionMethods,
                onMethodChange = { method ->
                    onMethodChange(method)
                    onErrorChange(null)
                },
                config = config.uiConfig
            )
        }
        
        // 用户协议和隐私政策
        if (config.uiConfig.showUserAgreement) {
            UserAgreementText(
                config = config.uiConfig,
                onAgreementClick = { /* TODO: 打开用户协议 */ },
                onPrivacyClick = { /* TODO: 打开隐私政策 */ }
            )
        }
    }
}

/**
 * 登录方式选择器
 */
@Composable
private fun LoginMethodSelector(
    methods: List<LoginMethod>,
    currentMethod: LoginMethod,
    onMethodChange: (LoginMethod) -> Unit,
    config: LoginUIConfig
) {
    // 已不在顶部使用，保留以兼容旧逻辑。
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        methods.forEach { method ->
            val isSelected = method == currentMethod
            val methodName = when (method) {
                LoginMethod.PHONE -> "手机号"
                LoginMethod.EMAIL -> "邮箱"
                LoginMethod.USERNAME -> "用户名"
                LoginMethod.WECHAT -> "微信"
                LoginMethod.QQ -> "QQ"
                LoginMethod.APPLE -> "Apple"
                LoginMethod.GOOGLE -> "Google"
                LoginMethod.BIOMETRIC -> "生物识别"
                LoginMethod.FACEBOOK -> "Facebook"
                LoginMethod.TWITTER -> "Twitter"
                LoginMethod.GUEST -> "游客"
            }
            
            FilterChip(
                onClick = { onMethodChange(method) },
                label = { Text(methodName) },
                selected = isSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 登录方式表单
 */
@Composable
private fun LoginMethodForm(
    method: LoginMethod,
    config: LoginConfig,
    loginState: LoginState,
    onLoginStateChange: (LoginState) -> Unit,
    onErrorChange: (String?) -> Unit,
    onNavigateToRegister: () -> Unit = {}
) {
    when (method) {
        LoginMethod.PHONE -> {
            PhoneLoginForm(
                config = config.uiConfig,
                validationConfig = config.validationConfig,
                onLogin = { phone, code ->
                    handleLogin(phone, code, config, onLoginStateChange, onErrorChange)
                },
                onSendCode = { phone ->
                    // TODO: 发送验证码
                }
            )
        }
        LoginMethod.EMAIL -> {
            EmailLoginForm(
                config = config.uiConfig,
                validationConfig = config.validationConfig,
                onLogin = { email, password ->
                    handleLogin(email, password, config, onLoginStateChange, onErrorChange)
                },
                onNavigateToRegister = onNavigateToRegister
            )
        }
        LoginMethod.USERNAME -> {
            UsernameLoginForm(
                config = config.uiConfig,
                validationConfig = config.validationConfig,
                onLogin = { username, password ->
                    handleLogin(username, password, config, onLoginStateChange, onErrorChange)
                }
            )
        }
        LoginMethod.GUEST -> {
            GuestLoginButton(
                config = config.uiConfig,
                onGuestLogin = {
                    handleGuestLogin(config, onLoginStateChange, onErrorChange)
                }
            )
        }
        else -> {
            // 第三方登录
            ThirdPartyLoginButton(
                method = method,
                config = config.uiConfig,
                onClick = { loginMethod ->
                    handleThirdPartyLogin(loginMethod, config, onLoginStateChange, onErrorChange)
                }
            )
        }
    }
}

/**
 * 其他登录方式
 */
@Composable
private fun OtherLoginMethods(
    methods: List<LoginMethod>,
    config: LoginConfig,
    onMethodSelect: (LoginMethod) -> Unit
) {
    // 已由统一选项按钮替代，此处保留以兼容旧调用
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "其他登录方式",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            textAlign = TextAlign.Center
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            methods.take(4).forEach { method -> // 最多显示4个
                IconButton(
                    onClick = { onMethodSelect(method) },
                    modifier = Modifier.size(48.dp)
                ) {
                    val icon = when (method) {
                        LoginMethod.WECHAT -> Icons.Filled.Chat
                        LoginMethod.QQ -> Icons.Filled.Forum
                        LoginMethod.APPLE -> Icons.Filled.AccountCircle
                        LoginMethod.GOOGLE -> Icons.Filled.AccountCircle
                        LoginMethod.FACEBOOK -> Icons.Filled.Share
                        LoginMethod.TWITTER -> Icons.Filled.Share
                        LoginMethod.BIOMETRIC -> Icons.Filled.AccountCircle
                        else -> Icons.Filled.Login
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(android.graphics.Color.parseColor(config.uiConfig.primaryColor)),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// 新增：统一风格的选项按钮区域
@Composable
private fun LoginOptionButtons(
    methods: List<LoginMethod>,
    onMethodChange: (LoginMethod) -> Unit,
    config: LoginUIConfig
) {
    // 按照指定顺序排列：QQ、微信、邮箱、游客
    val orderedMethods = listOf(
        LoginMethod.QQ,
        LoginMethod.WECHAT,
        LoginMethod.EMAIL,
        LoginMethod.GUEST
    ).filter { it in methods }
    
    if (orderedMethods.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "其他登录方式",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                 )
                
                // 单行显示所有按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    orderedMethods.forEach { method ->
                        val (icon, label, backgroundColor) = when (method) {
                            LoginMethod.QQ -> Triple(Icons.Filled.Forum, "QQ", Color(0xFF12B7F5))
                            LoginMethod.WECHAT -> Triple(Icons.Filled.Chat, "微信", Color(0xFF07C160))
                            LoginMethod.EMAIL -> Triple(Icons.Filled.Email, "邮箱", Color(0xFF6366F1))
                            LoginMethod.GUEST -> Triple(Icons.Filled.Login, "游客", Color(0xFF6B7280))
                            else -> Triple(Icons.Filled.Login, "登录", Color.Gray)
                        }
                        
                        Button(
                            onClick = { onMethodChange(method) },
                            modifier = Modifier
                                .weight(1f)
                                .height(config.buttonHeight.dp)
                                .heightIn(min = 48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = backgroundColor,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 2.dp,
                                pressedElevation = 4.dp
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 用户协议文本
 */
@Composable
private fun UserAgreementText(
    config: LoginUIConfig,
    onAgreementClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "登录即表示同意",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = "《用户协议》",
            fontSize = 12.sp,
            color = Color(android.graphics.Color.parseColor(config.primaryColor)),
            modifier = Modifier.clickable { onAgreementClick() }
        )
        Text(
            text = "和",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = "《隐私政策》",
            fontSize = 12.sp,
            color = Color(android.graphics.Color.parseColor(config.primaryColor)),
            modifier = Modifier.clickable { onPrivacyClick() }
        )
    }
}

// 登录处理函数
private fun handleLogin(
    credential1: String,
    credential2: String,
    config: LoginConfig,
    onLoginStateChange: (LoginState) -> Unit,
    onErrorChange: (String?) -> Unit
) {
    onLoginStateChange(LoginState.LOADING)
    onErrorChange(null)
    
    // TODO: 实际登录逻辑
    // 这里应该调用实际的登录API
    
    // 模拟登录结果
    CoroutineScope(Dispatchers.Main).launch {
        delay(2000) // 模拟网络请求
        
        val success = true // 模拟登录结果
        if (success) {
            onLoginStateChange(LoginState.SUCCESS)
            config.callback?.onLoginSuccess(LoginResult(
                success = true,
                method = LoginMethod.PHONE, // 默认使用手机登录
                userId = "user123",
                token = "token123",
                userInfo = mapOf("name" to "用户"),
                errorCode = null,
                errorMessage = null
            ))
        } else {
            onLoginStateChange(LoginState.FAILED)
            onErrorChange("登录失败，请重试")
            config.callback?.onLoginFailure("登录失败")
        }
    }
}

private fun handleThirdPartyLogin(
    method: LoginMethod,
    config: LoginConfig,
    onLoginStateChange: (LoginState) -> Unit,
    onErrorChange: (String?) -> Unit
) {
    onLoginStateChange(LoginState.LOADING)
    onErrorChange(null)
    
    // TODO: 第三方登录逻辑
    config.callback?.onThirdPartyLogin(method)
}

private fun handleGuestLogin(
    config: LoginConfig,
    onLoginStateChange: (LoginState) -> Unit,
    onErrorChange: (String?) -> Unit
) {
    onLoginStateChange(LoginState.LOADING)
    onErrorChange(null)
    
    // TODO: 游客登录逻辑
    CoroutineScope(Dispatchers.Main).launch {
        delay(1000)
        onLoginStateChange(LoginState.SUCCESS)
        config.callback?.onGuestLogin()
    }
}