package com.horizon.higame.ui.login

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

/**
 * 登录方式枚举
 */
enum class LoginMethod {
    WECHAT,      // 微信登录
    QQ,          // QQ登录
    APPLE,       // Apple ID登录
    GOOGLE,      // Google登录
    FACEBOOK,    // Facebook登录
    TWITTER,     // Twitter登录
    PHONE,       // 手机号登录
    EMAIL,       // 邮箱登录
    USERNAME,    // 用户名登录
    GUEST,       // 游客登录
    BIOMETRIC    // 生物识别登录
}

/**
 * 登录界面展示样式
 */
enum class LoginDisplayStyle {
    FULLSCREEN,    // 全屏模式
    MODAL,         // 弹窗模式
    BOTTOM_SHEET,  // 底部面板
    EMBEDDED       // 嵌入模式
}

/**
 * 登录状态枚举
 */
enum class LoginState {
    IDLE,          // 空闲状态
    LOADING,       // 登录中
    SUCCESS,       // 登录成功
    FAILED,        // 登录失败
    CANCELLED      // 登录取消
}

/**
 * 登录UI配置
 */
@Serializable
data class LoginUIConfig(
    // 展示样式
    val displayStyle: String = "MODAL",
    
    // 支持的登录方式
    val enabledMethods: List<String> = listOf("WECHAT", "QQ", "GUEST"),
    
    // UI定制
    val title: String = "登录",
    val subtitle: String? = null,
    val logoUrl: String? = null,
    val logoSize: Float = 80f,
    val backgroundType: String = "COLOR", // COLOR, GRADIENT, IMAGE
    val backgroundColor: String = "#FFFFFF",
    val gradientColors: List<String>? = null,
    val backgroundImageUrl: String? = null,
    
    // 颜色定制
    val primaryColor: String = "#007AFF",
    val secondaryColor: String = "#34C759",
    val textColor: String = "#000000",
    val hintColor: String = "#999999",
    val errorColor: String = "#FF3B30",
    
    // 布局定制
    val cornerRadius: Float = 12f,
    val buttonHeight: Float = 48f,
    val spacing: Float = 16f,
    val padding: Float = 24f,
    
    // 功能配置
    val enableRememberLogin: Boolean = true,
    val enableQuickSwitch: Boolean = true,
    val enableBiometric: Boolean = false,
    val showUserAgreement: Boolean = true,
    val maxLoginAttempts: Int = 5,
    val loginTimeout: Long = 30000L,
    
    // 文案定制
    val loginButtonText: String = "登录",
    val cancelButtonText: String = "取消",
    val forgotPasswordText: String = "忘记密码？",
    val registerText: String = "注册账号",
    val guestLoginText: String = "游客登录",
    val agreeTermsText: String = "我已阅读并同意",
    val termsText: String = "用户协议",
    val privacyText: String = "隐私政策",
    
    // 第三方登录配置
    val wechatAppId: String? = null,
    val qqAppId: String? = null,
    val googleClientId: String? = null,
    val facebookAppId: String? = null,
    val twitterApiKey: String? = null,
    
    // 扩展参数
    val extraParams: Map<String, String> = emptyMap()
) {
    // 将字符串列表转换为LoginMethod枚举列表
    val supportedMethods: List<LoginMethod>
        get() = enabledMethods.mapNotNull { methodString ->
            try {
                LoginMethod.valueOf(methodString)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
}

/**
 * 登录结果数据类
 */
data class LoginResult(
    val success: Boolean,
    val method: LoginMethod?,
    val userId: String?,
    val token: String?,
    val userInfo: Map<String, Any>?,
    val errorCode: String?,
    val errorMessage: String?
)

/**
 * 登录回调接口
 */
interface LoginCallback {
    fun onLoginStart(method: LoginMethod)
    fun onLoginSuccess(result: LoginResult)
    fun onLoginFailure(errorMessage: String)
    fun onLoginCancelled()
    fun onThirdPartyLogin(method: LoginMethod)
    fun onGuestLogin()
    fun onStateChanged(state: LoginState)
}

/**
 * 登录配置数据类
 */
data class LoginConfig(
    val uiConfig: LoginUIConfig = LoginUIConfig(),
    val defaultMethod: LoginMethod = LoginMethod.PHONE,
    val displayStyle: LoginDisplayStyle = LoginDisplayStyle.FULLSCREEN,
    val callback: LoginCallback? = null,
    val validationConfig: FormValidationConfig = FormValidationConfig()
)

/**
 * 登录验证规则
 */
data class ValidationRule(
    val pattern: String,
    val errorMessage: String
)

/**
 * 表单验证配置
 */
data class FormValidationConfig(
    val phoneRule: ValidationRule = ValidationRule(
        "^1[3-9]\\d{9}$",
        "请输入正确的手机号"
    ),
    val emailRule: ValidationRule = ValidationRule(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        "请输入正确的邮箱地址"
    ),
    val passwordRule: ValidationRule = ValidationRule(
        "^.{6,20}$",
        "密码长度应为6-20位"
    ),
    val usernameRule: ValidationRule = ValidationRule(
        "^[A-Za-z0-9_]{3,20}$",
        "用户名应为3-20位字母、数字或下划线"
    ),
    val verificationCodeRule: ValidationRule = ValidationRule(
        "^\\d{4,6}$",
        "请输入4-6位验证码"
    )
)