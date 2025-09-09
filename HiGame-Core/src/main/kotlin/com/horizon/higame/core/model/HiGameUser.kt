package com.horizon.higame.core.model

/**
 * HiGame 用户信息模型
 */
data class HiGameUser(
    /** 用户ID */
    val userId: String,
    
    /** 用户名 */
    val username: String,
    
    /** 昵称 */
    val nickname: String? = null,
    
    /** 头像URL */
    val avatar: String? = null,
    
    /** 邮箱 */
    val email: String? = null,
    
    /** 手机号 */
    val phone: String? = null,
    
    /** 性别 0:未知 1:男 2:女 */
    val gender: Int = 0,
    
    /** 年龄 */
    val age: Int? = null,
    
    /** 登录类型 */
    val loginType: LoginType = LoginType.GUEST,
    
    /** 用户等级 */
    val level: Int = 1,
    
    /** 经验值 */
    val experience: Long = 0,
    
    /** 注册时间戳 */
    val registerTime: Long = 0,
    
    /** 最后登录时间戳 */
    val lastLoginTime: Long = 0,
    
    /** 扩展信息 */
    val extra: Map<String, Any> = emptyMap()
) {
    
    /**
     * 登录类型枚举
     */
    enum class LoginType {
        GUEST,      // 游客
        PHONE,      // 手机号
        EMAIL,      // 邮箱
        WECHAT,     // 微信
        QQ,         // QQ
        APPLE,      // Apple ID
        GOOGLE,     // Google
        FACEBOOK,   // Facebook
        CUSTOM      // 自定义
    }
}