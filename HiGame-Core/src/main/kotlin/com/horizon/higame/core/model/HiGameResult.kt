package com.horizon.higame.core.model

/**
 * HiGame 通用结果封装类
 */
sealed class HiGameResult<out T> {
    
    /**
     * 成功结果
     */
    data class Success<T>(val data: T) : HiGameResult<T>()
    
    /**
     * 失败结果
     */
    data class Error(val code: Int, val message: String, val throwable: Throwable? = null) : HiGameResult<Nothing>()
    
    /**
     * 加载中状态
     */
    object Loading : HiGameResult<Nothing>()
    
    /**
     * 是否成功
     */
    val isSuccess: Boolean get() = this is Success
    
    /**
     * 是否失败
     */
    val isError: Boolean get() = this is Error
    
    /**
     * 是否加载中
     */
    val isLoading: Boolean get() = this is Loading
    
    /**
     * 获取数据（仅成功时有效）
     */
    fun getDataOrNull(): T? = if (this is Success) data else null
    
    /**
     * 获取错误信息（仅失败时有效）
     */
    fun getErrorOrNull(): Error? = if (this is Error) this else null
    
    companion object {
        /**
         * 创建成功结果
         */
        fun <T> success(data: T): HiGameResult<T> = Success(data)
        
        /**
         * 创建失败结果
         */
        fun error(code: Int, message: String, throwable: Throwable? = null): HiGameResult<Nothing> = 
            Error(code, message, throwable)
        
        /**
         * 创建加载中状态
         */
        fun loading(): HiGameResult<Nothing> = Loading
    }
}