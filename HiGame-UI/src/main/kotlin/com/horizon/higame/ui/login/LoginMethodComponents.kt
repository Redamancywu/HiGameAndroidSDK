package com.horizon.higame.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizon.higame.ui.components.HiGameButton
import com.horizon.higame.ui.components.HiGameInputField
import com.horizon.higame.ui.components.HiGameLoadingView
import kotlinx.coroutines.delay

/**
 * 第三方登录按钮组件
 */
@Composable
fun ThirdPartyLoginButton(
    method: LoginMethod,
    config: LoginUIConfig,
    onClick: (LoginMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, text, backgroundColor) = when (method) {
        LoginMethod.WECHAT -> Triple(Icons.Filled.Chat, "微信登录", Color(0xFF07C160))
        LoginMethod.QQ -> Triple(Icons.Filled.Forum, "QQ登录", Color(0xFF12B7F5))
        LoginMethod.APPLE -> Triple(Icons.Filled.AccountCircle, "Apple登录", Color(0xFF000000))
        LoginMethod.GOOGLE -> Triple(Icons.Filled.AccountCircle, "Google登录", Color(0xFF4285F4))
        LoginMethod.FACEBOOK -> Triple(Icons.Filled.Share, "Facebook登录", Color(0xFF1877F2))
        LoginMethod.TWITTER -> Triple(Icons.Filled.Share, "Twitter登录", Color(0xFF1DA1F2))
        else -> Triple(Icons.Filled.Login, "登录", Color.Gray)
    }
    
    Button(
        onClick = { onClick(method) },
        modifier = modifier
            .fillMaxWidth()
            .height(config.buttonHeight.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(config.cornerRadius.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 手机号登录组件
 */
@Composable
fun PhoneLoginForm(
    config: LoginUIConfig,
    validationConfig: FormValidationConfig,
    onLogin: (String, String) -> Unit,
    onSendCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var phone by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var codeError by remember { mutableStateOf<String?>(null) }
    var countdown by remember { mutableStateOf(0) }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // 倒计时效果
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            kotlinx.coroutines.delay(1000)
            countdown--
        }
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(config.spacing.dp)
    ) {
        // 手机号输入框
        HiGameInputField(
            value = phone,
            onValueChange = { 
                phone = it
                phoneError = null
            },
            placeholder = "请输入手机号",
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next,
            leading = {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = null,
                    tint = Color.Gray
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (phoneError != null) {
            Text(
                text = phoneError!!,
                color = Color(android.graphics.Color.parseColor(config.errorColor)),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        
        // 验证码输入框和发送按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HiGameInputField(
                value = verificationCode,
                onValueChange = { 
                    verificationCode = it
                    codeError = null
                },
                placeholder = "请输入验证码",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                onImeAction = {
                    keyboardController?.hide()
                    if (validateInputs(phone, verificationCode, validationConfig)) {
                        onLogin(phone, verificationCode)
                    }
                },
                leading = {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                modifier = Modifier.weight(1f)
            )
            
            Button(
                onClick = {
                    if (validatePhone(phone, validationConfig.phoneRule)) {
                        onSendCode(phone)
                        countdown = 60
                    } else {
                        phoneError = validationConfig.phoneRule.errorMessage
                    }
                },
                enabled = countdown == 0 && phone.isNotEmpty(),
                modifier = Modifier.height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(android.graphics.Color.parseColor(config.primaryColor))
                )
            ) {
                Text(
                    text = if (countdown > 0) "${countdown}s" else "发送",
                    fontSize = 14.sp
                )
            }
        }
        
        if (codeError != null) {
            Text(
                text = codeError!!,
                color = Color(android.graphics.Color.parseColor(config.errorColor)),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        
        // 登录按钮
        HiGameButton(
            text = config.loginButtonText,
            onClick = {
                if (validateInputs(phone, verificationCode, validationConfig)) {
                    onLogin(phone, verificationCode)
                } else {
                    if (!validatePhone(phone, validationConfig.phoneRule)) {
                        phoneError = validationConfig.phoneRule.errorMessage
                    }
                    if (!validateCode(verificationCode, validationConfig.verificationCodeRule)) {
                        codeError = validationConfig.verificationCodeRule.errorMessage
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(config.buttonHeight.dp)
        )
    }
}

/**
 * 邮箱登录组件（验证码方式）
 */
@Composable
fun EmailLoginForm(
    config: LoginUIConfig,
    validationConfig: FormValidationConfig,
    onLogin: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(config.spacing.dp)
    ) {
        // 邮箱输入框
        HiGameInputField(
            value = email,
            onValueChange = { 
                email = it
                emailError = null
            },
            placeholder = "请输入您的邮箱地址",
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            leading = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null,
                    tint = Color(0xFF6366F1)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (emailError != null) {
            Text(
                text = emailError!!,
                color = Color(android.graphics.Color.parseColor(config.errorColor)),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        
        // 密码输入框
        HiGameInputField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = null
            },
            placeholder = "请输入您的密码",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            isPassword = !passwordVisible,
            onImeAction = {
                keyboardController?.hide()
                if (validateEmailLogin(email, password, validationConfig)) {
                    onLogin(email, password)
                }
            },
            leading = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Color(0xFF6366F1)
                )
            },
            trailing = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "隐藏密码" else "显示密码",
                        tint = Color(0xFF6366F1)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (passwordError != null) {
            Text(
                text = passwordError!!,
                color = Color(android.graphics.Color.parseColor(config.errorColor)),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        
        // 注册账号链接
        Text(
            text = "注册账号",
            color = Color(0xFF3B82F6),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable {
                    onNavigateToRegister()
                }
        )
        
        // 登录按钮
        Button(
            onClick = {
                if (validateEmailLogin(email, password, validationConfig)) {
                    onLogin(email, password)
                } else {
                    if (!validateEmail(email, validationConfig.emailRule)) {
                        emailError = validationConfig.emailRule.errorMessage
                    }
                    if (!validatePassword(password, validationConfig.passwordRule)) {
                        passwordError = validationConfig.passwordRule.errorMessage
                    }
                }
            },
            enabled = email.isNotEmpty() && password.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(config.buttonHeight.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6366F1),
                contentColor = Color.White,
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 3.dp,
                pressedElevation = 6.dp
            )
        ) {
            Text(
                text = config.loginButtonText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 用户名登录组件
 */
@Composable
fun UsernameLoginForm(
    config: LoginUIConfig,
    validationConfig: FormValidationConfig,
    onLogin: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(config.spacing.dp)
    ) {
        // 用户名输入框
        HiGameInputField(
            value = username,
            onValueChange = { 
                username = it
                usernameError = null
            },
            placeholder = "请输入用户名",
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
            leading = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color.Gray
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (usernameError != null) {
            Text(
                text = usernameError!!,
                color = Color(android.graphics.Color.parseColor(config.errorColor)),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        
        // 密码输入框
        HiGameInputField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = null
            },
            placeholder = "请输入密码",
            isPassword = !passwordVisible,
            imeAction = ImeAction.Done,
            onImeAction = {
                keyboardController?.hide()
                if (validateUsernameLogin(username, password, validationConfig)) {
                    onLogin(username, password)
                }
            },
            leading = {
                     Icon(
                         imageVector = Icons.Filled.Lock,
                         contentDescription = null,
                         tint = Color.Gray
                     )
                 },
                 trailing = {
                     IconButton(
                         onClick = { passwordVisible = !passwordVisible }
                     ) {
                         Icon(
                             imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                             contentDescription = if (passwordVisible) "隐藏密码" else "显示密码",
                             tint = Color.Gray
                         )
                     }
                 },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (passwordError != null) {
            Text(
                text = passwordError!!,
                color = Color(android.graphics.Color.parseColor(config.errorColor)),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        
        // 登录按钮
        HiGameButton(
            text = config.loginButtonText,
            onClick = {
                if (validateUsernameLogin(username, password, validationConfig)) {
                    onLogin(username, password)
                } else {
                    if (!validateUsername(username, validationConfig.usernameRule)) {
                        usernameError = validationConfig.usernameRule.errorMessage
                    }
                    if (!validatePassword(password, validationConfig.passwordRule)) {
                        passwordError = validationConfig.passwordRule.errorMessage
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(config.buttonHeight.dp)
        )
    }
}

/**
 * 游客登录组件
 */
@Composable
fun GuestLoginButton(
    config: LoginUIConfig,
    onGuestLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onGuestLogin,
        modifier = modifier
            .fillMaxWidth()
            .height(config.buttonHeight.dp),
        border = BorderStroke(
            1.dp, 
            Color(android.graphics.Color.parseColor(config.primaryColor))
        ),
        shape = RoundedCornerShape(config.cornerRadius.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = null,
            tint = Color(android.graphics.Color.parseColor(config.primaryColor)),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = config.guestLoginText,
            color = Color(android.graphics.Color.parseColor(config.primaryColor)),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// 验证函数
internal fun validatePhone(phone: String, rule: ValidationRule): Boolean {
    return phone.matches(Regex(rule.pattern))
}

internal fun validateEmail(email: String, rule: ValidationRule): Boolean {
    return email.matches(Regex(rule.pattern))
}

internal fun validatePassword(password: String, rule: ValidationRule): Boolean {
    return password.matches(Regex(rule.pattern))
}

internal fun validateUsername(username: String, rule: ValidationRule): Boolean {
    return username.matches(Regex(rule.pattern))
}

internal fun validateCode(code: String, rule: ValidationRule): Boolean {
    return code.matches(Regex(rule.pattern))
}

private fun validateInputs(phone: String, code: String, config: FormValidationConfig): Boolean {
    return validatePhone(phone, config.phoneRule) && validateCode(code, config.verificationCodeRule)
}

private fun validateEmailLogin(email: String, password: String, config: FormValidationConfig): Boolean {
    return validateEmail(email, config.emailRule) && validatePassword(password, config.passwordRule)
}

private fun validateUsernameLogin(username: String, password: String, config: FormValidationConfig): Boolean {
    return validateUsername(username, config.usernameRule) && validatePassword(password, config.passwordRule)
}

private fun validateEmailCode(email: String, code: String, config: FormValidationConfig): Boolean {
    return validateEmail(email, config.emailRule) && validateCode(code, config.verificationCodeRule)
}