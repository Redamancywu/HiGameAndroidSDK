package com.horizon.higame.ui.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizon.higame.ui.components.HiGameInputField
import com.horizon.higame.ui.login.FormValidationConfig
import com.horizon.higame.ui.login.LoginUIConfig
import com.horizon.higame.ui.login.validateEmail
import com.horizon.higame.ui.login.validatePassword
import com.horizon.higame.ui.login.validatePhone
import com.horizon.higame.ui.login.validateCode
import kotlinx.coroutines.delay

/**
 * 注册方式枚举
 */
enum class RegisterMethod {
    PHONE,
    EMAIL
}

/**
 * 手机注册表单
 */
@Composable
fun PhoneRegisterForm(
    config: LoginUIConfig,
    validationConfig: FormValidationConfig,
    onRegister: (String, String, String, String) -> Unit, // phone, password, confirmPassword, code
    onSendCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var codeError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
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
            placeholder = "请输入您的手机号",
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Next,
            leading = {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = null,
                    tint = Color(0xFF6366F1)
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
        
        // 密码输入框
        HiGameInputField(
            value = password,
            onValueChange = { 
                password = it
                passwordError = null
            },
            placeholder = "请输入密码",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
            isPassword = !passwordVisible,
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
        
        // 确认密码输入框
        HiGameInputField(
            value = confirmPassword,
            onValueChange = { 
                confirmPassword = it
                confirmPasswordError = null
            },
            placeholder = "请再次输入密码",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
            isPassword = !confirmPasswordVisible,
            leading = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Color(0xFF6366F1)
                )
            },
            trailing = {
                IconButton(
                    onClick = { confirmPasswordVisible = !confirmPasswordVisible }
                ) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "隐藏密码" else "显示密码",
                        tint = Color(0xFF6366F1)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (confirmPasswordError != null) {
            Text(
                text = confirmPasswordError!!,
                color = Color(android.graphics.Color.parseColor(config.errorColor)),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        
        // 验证码输入框和发送按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HiGameInputField(
                value = verificationCode,
                onValueChange = { 
                    verificationCode = it
                    codeError = null
                },
                placeholder = "请输入6位验证码",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                onImeAction = {
                    keyboardController?.hide()
                    if (validatePhoneRegister(phone, password, confirmPassword, verificationCode, validationConfig)) {
                        onRegister(phone, password, confirmPassword, verificationCode)
                    }
                },
                leading = {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = Color(0xFF6366F1)
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
                modifier = Modifier
                    .height(56.dp)
                    .width(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (countdown == 0 && phone.isNotEmpty()) Color(0xFF6366F1) else Color.Gray,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text(
                    text = if (countdown > 0) "${countdown}s" else "发送",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
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
        
        // 注册按钮
        Button(
            onClick = {
                if (validatePhoneRegister(phone, password, confirmPassword, verificationCode, validationConfig)) {
                    onRegister(phone, password, confirmPassword, verificationCode)
                } else {
                    if (!validatePhone(phone, validationConfig.phoneRule)) {
                        phoneError = validationConfig.phoneRule.errorMessage
                    }
                    if (!validatePassword(password, validationConfig.passwordRule)) {
                        passwordError = validationConfig.passwordRule.errorMessage
                    }
                    if (password != confirmPassword) {
                        confirmPasswordError = "两次输入的密码不一致"
                    }
                    if (!validateCode(verificationCode, validationConfig.verificationCodeRule)) {
                        codeError = validationConfig.verificationCodeRule.errorMessage
                    }
                }
            },
            enabled = phone.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && verificationCode.isNotEmpty(),
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
                text = "注册",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 邮箱注册表单
 */
@Composable
fun EmailRegisterForm(
    config: LoginUIConfig,
    validationConfig: FormValidationConfig,
    onRegister: (String, String, String, String) -> Unit, // email, password, confirmPassword, code
    onSendCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var codeError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
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
            placeholder = "请输入密码",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
            isPassword = !passwordVisible,
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
        
        // 确认密码输入框
        HiGameInputField(
            value = confirmPassword,
            onValueChange = { 
                confirmPassword = it
                confirmPasswordError = null
            },
            placeholder = "请再次输入密码",
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
            isPassword = !confirmPasswordVisible,
            leading = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Color(0xFF6366F1)
                )
            },
            trailing = {
                IconButton(
                    onClick = { confirmPasswordVisible = !confirmPasswordVisible }
                ) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "隐藏密码" else "显示密码",
                        tint = Color(0xFF6366F1)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (confirmPasswordError != null) {
            Text(
                text = confirmPasswordError!!,
                color = Color(android.graphics.Color.parseColor(config.errorColor)),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        
        // 验证码输入框和发送按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HiGameInputField(
                value = verificationCode,
                onValueChange = { 
                    verificationCode = it
                    codeError = null
                },
                placeholder = "请输入6位验证码",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                onImeAction = {
                    keyboardController?.hide()
                    if (validateEmailRegister(email, password, confirmPassword, verificationCode, validationConfig)) {
                        onRegister(email, password, confirmPassword, verificationCode)
                    }
                },
                leading = {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = Color(0xFF6366F1)
                    )
                },
                modifier = Modifier.weight(1f)
            )
            
            Button(
                onClick = {
                    if (validateEmail(email, validationConfig.emailRule)) {
                        onSendCode(email)
                        countdown = 60
                    } else {
                        emailError = validationConfig.emailRule.errorMessage
                    }
                },
                enabled = countdown == 0 && email.isNotEmpty(),
                modifier = Modifier
                    .height(56.dp)
                    .width(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (countdown == 0 && email.isNotEmpty()) Color(0xFF6366F1) else Color.Gray,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text(
                    text = if (countdown > 0) "${countdown}s" else "发送",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
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
        
        // 注册按钮
        Button(
            onClick = {
                if (validateEmailRegister(email, password, confirmPassword, verificationCode, validationConfig)) {
                    onRegister(email, password, confirmPassword, verificationCode)
                } else {
                    if (!validateEmail(email, validationConfig.emailRule)) {
                        emailError = validationConfig.emailRule.errorMessage
                    }
                    if (!validatePassword(password, validationConfig.passwordRule)) {
                        passwordError = validationConfig.passwordRule.errorMessage
                    }
                    if (password != confirmPassword) {
                        confirmPasswordError = "两次输入的密码不一致"
                    }
                    if (!validateCode(verificationCode, validationConfig.verificationCodeRule)) {
                        codeError = validationConfig.verificationCodeRule.errorMessage
                    }
                }
            },
            enabled = email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && verificationCode.isNotEmpty(),
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
                text = "注册",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// 验证函数
private fun validatePhoneRegister(
    phone: String,
    password: String,
    confirmPassword: String,
    code: String,
    config: FormValidationConfig
): Boolean {
    return validatePhone(phone, config.phoneRule) &&
            validatePassword(password, config.passwordRule) &&
            password == confirmPassword &&
            validateCode(code, config.verificationCodeRule)
}

private fun validateEmailRegister(
    email: String,
    password: String,
    confirmPassword: String,
    code: String,
    config: FormValidationConfig
): Boolean {
    return validateEmail(email, config.emailRule) &&
            validatePassword(password, config.passwordRule) &&
            password == confirmPassword &&
            validateCode(code, config.verificationCodeRule)
}