package com.horizon.higame.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizon.higame.ui.login.FormValidationConfig
import com.horizon.higame.ui.login.LoginUIConfig
import com.horizon.higame.ui.login.LoginConfig
import com.horizon.higame.ui.login.LoginState
import kotlinx.coroutines.launch

/**
 * 注册页面主要内容
 */
@Composable
fun RegisterContent(
    config: LoginConfig,
    onRegister: (RegisterMethod, String, String, String, String) -> Unit, // method, account, password, confirmPassword, code
    onSendCode: (RegisterMethod, String) -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf(RegisterMethod.PHONE) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = config.uiConfig.padding.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部标题栏
        RegisterHeader(
            onBack = onBackToLogin,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 注册方式切换
        RegisterMethodSelector(
            methods = listOf(RegisterMethod.PHONE, RegisterMethod.EMAIL),
            currentMethod = selectedMethod,
            onMethodChange = { selectedMethod = it },
            config = config.uiConfig
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 注册表单
        when (selectedMethod) {
            RegisterMethod.PHONE -> {
                PhoneRegisterForm(
                    config = config.uiConfig,
                    validationConfig = config.validationConfig,
                    onRegister = { phone, password, confirmPassword, code ->
                        onRegister(RegisterMethod.PHONE, phone, password, confirmPassword, code)
                    },
                    onSendCode = { phone ->
                        onSendCode(RegisterMethod.PHONE, phone)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            RegisterMethod.EMAIL -> {
                EmailRegisterForm(
                    config = config.uiConfig,
                    validationConfig = config.validationConfig,
                    onRegister = { email, password, confirmPassword, code ->
                        onRegister(RegisterMethod.EMAIL, email, password, confirmPassword, code)
                    },
                    onSendCode = { email ->
                        onSendCode(RegisterMethod.EMAIL, email)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 用户协议文本
        RegisterAgreementText(
            config = config.uiConfig,
            onAgreementClick = { /* TODO: 处理用户协议点击 */ },
            onPrivacyClick = { /* TODO: 处理隐私政策点击 */ }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 返回登录链接
        Text(
            text = "已有账号？返回登录",
            color = Color(0xFF3B82F6),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable {
                onBackToLogin()
            }
        )
    }
}

/**
 * 注册方式表单
 */
@Composable
fun RegisterMethodForm(
    method: RegisterMethod,
    config: LoginConfig,
    registerState: LoginState,
    onRegisterStateChange: (LoginState) -> Unit,
    onErrorChange: (String?) -> Unit,
    onRegisterSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    
    when (method) {
        RegisterMethod.PHONE -> {
            PhoneRegisterForm(
                config = config.uiConfig,
                validationConfig = config.validationConfig,
                onRegister = { phone, password, confirmPassword, code ->
                    handleRegister(phone, password, confirmPassword, code, config, onRegisterStateChange, onErrorChange, onRegisterSuccess, coroutineScope)
                },
                onSendCode = { phone ->
                    // TODO: 发送验证码
                },
                modifier = modifier
            )
        }
        RegisterMethod.EMAIL -> {
            EmailRegisterForm(
                config = config.uiConfig,
                validationConfig = config.validationConfig,
                onRegister = { email, password, confirmPassword, code ->
                    handleRegister(email, password, confirmPassword, code, config, onRegisterStateChange, onErrorChange, onRegisterSuccess, coroutineScope)
                },
                onSendCode = { email ->
                    // TODO: 发送验证码
                },
                modifier = modifier
            )
        }
    }
}

/**
 * 处理注册逻辑
 */
private fun handleRegister(
    account: String,
    password: String,
    confirmPassword: String,
    code: String,
    config: LoginConfig,
    onRegisterStateChange: (LoginState) -> Unit,
    onErrorChange: (String?) -> Unit,
    onRegisterSuccess: () -> Unit,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    onRegisterStateChange(LoginState.LOADING)
    onErrorChange(null)
    
    // TODO: 实现实际的注册逻辑
    // 这里应该调用实际的注册API
    
    // 模拟注册过程
    coroutineScope.launch {
        try {
            kotlinx.coroutines.delay(2000) // 模拟网络请求
            
            // 模拟注册成功
            onRegisterStateChange(LoginState.SUCCESS)
            onRegisterSuccess()
        } catch (e: Exception) {
            onRegisterStateChange(LoginState.FAILED)
            onErrorChange("注册失败：${e.message}")
        }
    }
}

/**
 * 注册用户协议文本
 */
@Composable
fun RegisterAgreementText(
    config: LoginUIConfig,
    onAgreementClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "注册即表示同意",
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

/**
 * 注册页面标题栏
 */
@Composable
fun RegisterHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "返回",
                tint = Color(0xFF6366F1)
            )
        }
        
        Text(
            text = "注册账号",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(end = 40.dp) // 平衡左侧按钮
        )
    }
}

/**
 * 注册方式选择器
 */
@Composable
fun RegisterMethodSelector(
    methods: List<RegisterMethod>,
    currentMethod: RegisterMethod,
    onMethodChange: (RegisterMethod) -> Unit,
    config: LoginUIConfig
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        methods.forEach { method ->
            FilterChip(
                onClick = { onMethodChange(method) },
                label = {
                    Text(
                        text = when (method) {
                            RegisterMethod.PHONE -> "手机注册"
                            RegisterMethod.EMAIL -> "邮箱注册"
                        },
                        fontSize = 14.sp
                    )
                },
                selected = currentMethod == method,
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(android.graphics.Color.parseColor(config.primaryColor)),
                    selectedLabelColor = Color.White,
                    containerColor = Color.Transparent,
                    labelColor = Color(android.graphics.Color.parseColor(config.primaryColor))
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = currentMethod == method,
                    borderColor = Color(android.graphics.Color.parseColor(config.primaryColor)),
                    selectedBorderColor = Color(android.graphics.Color.parseColor(config.primaryColor))
                )
            )
        }
    }
}

/**
 * 注册方式标签页
 */
@Composable
fun RegisterMethodTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (isSelected) Color.White else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) Color(0xFF6366F1) else Color(0xFF6B7280),
            textAlign = TextAlign.Center
        )
    }
}