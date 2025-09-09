package com.horizon.higame.core.error

/**
 * HiGame 错误定义
 */
object HiGameError {
    
    // 通用错误码
    const val ERROR_UNKNOWN = -1
    const val ERROR_NETWORK = 1001
    const val ERROR_TIMEOUT = 1002
    const val ERROR_PARSE = 1003
    const val ERROR_PERMISSION = 1004
    
    // 初始化错误码
    const val ERROR_INIT_FAILED = 2001
    const val ERROR_CONFIG_INVALID = 2002
    const val ERROR_SDK_NOT_INIT = 2003
    
    // 登录错误码
    const val ERROR_LOGIN_FAILED = 3001
    const val ERROR_LOGIN_CANCELLED = 3002
    const val ERROR_LOGIN_INVALID_PARAMS = 3003
    const val ERROR_LOGIN_THIRD_PARTY_FAILED = 3004
    
    // 支付错误码
    const val ERROR_PAY_FAILED = 4001
    const val ERROR_PAY_CANCELLED = 4002
    const val ERROR_PAY_INVALID_ORDER = 4003
    const val ERROR_PAY_INSUFFICIENT_BALANCE = 4004
    
    // 分享错误码
    const val ERROR_SHARE_FAILED = 5001
    const val ERROR_SHARE_CANCELLED = 5002
    const val ERROR_SHARE_PLATFORM_NOT_INSTALLED = 5003
    
    // 用户中心错误码
    const val ERROR_USER_CENTER_FAILED = 6001
    const val ERROR_USER_INFO_NOT_FOUND = 6002
    const val ERROR_USER_NOT_LOGIN = 6003
    
    /**
     * 获取错误信息
     * @param code 错误码
     * @return 错误信息
     */
    fun getErrorMessage(code: Int): String {
        return when (code) {
            ERROR_UNKNOWN -> "未知错误"
            ERROR_NETWORK -> "网络错误"
            ERROR_TIMEOUT -> "请求超时"
            ERROR_PARSE -> "数据解析错误"
            ERROR_PERMISSION -> "权限不足"
            
            ERROR_INIT_FAILED -> "初始化失败"
            ERROR_CONFIG_INVALID -> "配置无效"
            ERROR_SDK_NOT_INIT -> "SDK 未初始化"
            
            ERROR_LOGIN_FAILED -> "登录失败"
            ERROR_LOGIN_CANCELLED -> "登录已取消"
            ERROR_LOGIN_INVALID_PARAMS -> "登录参数无效"
            ERROR_LOGIN_THIRD_PARTY_FAILED -> "第三方登录失败"
            
            ERROR_PAY_FAILED -> "支付失败"
            ERROR_PAY_CANCELLED -> "支付已取消"
            ERROR_PAY_INVALID_ORDER -> "订单无效"
            ERROR_PAY_INSUFFICIENT_BALANCE -> "余额不足"
            
            ERROR_SHARE_FAILED -> "分享失败"
            ERROR_SHARE_CANCELLED -> "分享已取消"
            ERROR_SHARE_PLATFORM_NOT_INSTALLED -> "分享平台未安装"
            
            ERROR_USER_CENTER_FAILED -> "用户中心操作失败"
            ERROR_USER_INFO_NOT_FOUND -> "用户信息未找到"
            ERROR_USER_NOT_LOGIN -> "用户未登录"
            
            else -> "未知错误($code)"
        }
    }
}