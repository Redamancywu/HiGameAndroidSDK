package com.horizon.higame.core.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.util.UUID

/**
 * HiGame 工具类
 */
internal object HiGameUtils {
    
    /**
     * 生成唯一ID
     */
    fun generateUniqueId(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
    
    /**
     * 获取应用版本名
     */
    fun getAppVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "unknown"
        }
    }
    
    /**
     * 获取应用版本号
     */
    fun getAppVersionCode(context: Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            0L
        }
    }
    
    /**
     * 获取设备信息
     */
    fun getDeviceInfo(): Map<String, String> {
        return mapOf(
            "brand" to Build.BRAND,
            "model" to Build.MODEL,
            "manufacturer" to Build.MANUFACTURER,
            "device" to Build.DEVICE,
            "product" to Build.PRODUCT,
            "hardware" to Build.HARDWARE,
            "androidVersion" to Build.VERSION.RELEASE,
            "sdkVersion" to Build.VERSION.SDK_INT.toString()
        )
    }
    
    /**
     * 检查应用是否安装
     */
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    /**
     * 安全地执行代码块
     */
    inline fun <T> safeExecute(block: () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            HiGameLogger.e("Safe execute failed", e)
            null
        }
    }
    
    /**
     * 检查字符串是否为空或空白
     */
    fun String?.isNullOrBlank(): Boolean {
        return this == null || this.isBlank()
    }
    
    /**
     * 检查字符串是否不为空且不为空白
     */
    fun String?.isNotNullOrBlank(): Boolean {
        return !this.isNullOrBlank()
    }
}