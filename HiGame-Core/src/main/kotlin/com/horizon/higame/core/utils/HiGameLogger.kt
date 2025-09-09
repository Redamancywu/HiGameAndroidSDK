package com.horizon.higame.core.utils

import android.util.Log
import timber.log.Timber

/**
 * HiGame 日志工具类
 */
internal object HiGameLogger {
    
    private const val TAG = "HiGame"
    private var isDebugMode = false
    
    /**
     * 设置调试模式
     */
    fun setDebugMode(debug: Boolean) {
        isDebugMode = debug
    }
    
    /**
     * Verbose 日志
     */
    fun v(message: String, tag: String = TAG) {
        if (isDebugMode) {
            Timber.tag(tag).v(message)
        }
    }
    
    /**
     * Debug 日志
     */
    fun d(message: String, tag: String = TAG) {
        if (isDebugMode) {
            Timber.tag(tag).d(message)
        }
    }
    
    /**
     * Info 日志
     */
    fun i(message: String, tag: String = TAG) {
        if (isDebugMode) {
            Timber.tag(tag).i(message)
        }
    }
    
    /**
     * Warning 日志
     */
    fun w(message: String, tag: String = TAG) {
        if (isDebugMode) {
            Timber.tag(tag).w(message)
        }
    }
    
    /**
     * Error 日志
     */
    fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (isDebugMode) {
            if (throwable != null) {
                Timber.tag(tag).e(throwable, message)
            } else {
                Timber.tag(tag).e(message)
            }
        }
    }
    
    /**
     * 格式化日志
     */
    fun format(level: String, className: String, methodName: String, message: String): String {
        return "[$level] $className.$methodName(): $message"
    }
}