package com.horizon.higame.core.internal.security

import android.content.Context
import android.util.Base64
import com.horizon.higame.core.internal.config.HiGameConfig
import com.horizon.higame.core.utils.HiGameLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

/**
 * HiGame 安全管理器
 * 负责加密、解密、签名验证等安全相关功能
 */
class HiGameSecurityManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: HiGameSecurityManager? = null
        
        fun getInstance(): HiGameSecurityManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HiGameSecurityManager().also { INSTANCE = it }
            }
        }
        
        private const val AES_TRANSFORMATION = "AES/GCM/NoPadding"
        private const val AES_KEY_LENGTH = 256
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 16
    }
    
    private var context: Context? = null
    private var securityConfig: com.horizon.higame.core.config.SecurityConfig? = null
    private var secretKey: SecretKey? = null
    private val secureRandom = SecureRandom()
    
    /**
     * 初始化安全管理器
     */
    suspend fun initialize(context: Context) = withContext(Dispatchers.IO) {
        this@HiGameSecurityManager.context = context.applicationContext
        
        try {
            // 生成或加载密钥
            secretKey = generateOrLoadSecretKey()
            
            HiGameLogger.i("SecurityManager initialized successfully")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to initialize SecurityManager", e)
            throw e
        }
    }
    
    /**
     * 设置安全配置
     */
    fun setSecurityConfig(config: com.horizon.higame.core.config.SecurityConfig) {
        this.securityConfig = config
        HiGameLogger.d("Security config updated")
    }
    
    /**
     * 加密数据
     */
    suspend fun encrypt(data: String): String = withContext(Dispatchers.IO) {
        try {
            val key = secretKey ?: throw IllegalStateException("SecretKey not initialized")
            
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            val iv = ByteArray(GCM_IV_LENGTH)
            secureRandom.nextBytes(iv)
            
            val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec)
            
            val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            
            // 将 IV 和加密数据组合
            val result = ByteArray(iv.size + encryptedData.size)
            System.arraycopy(iv, 0, result, 0, iv.size)
            System.arraycopy(encryptedData, 0, result, iv.size, encryptedData.size)
            
            Base64.encodeToString(result, Base64.NO_WRAP)
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to encrypt data", e)
            throw SecurityException("Encryption failed", e)
        }
    }
    
    /**
     * 解密数据
     */
    suspend fun decrypt(encryptedData: String): String = withContext(Dispatchers.IO) {
        try {
            val key = secretKey ?: throw IllegalStateException("SecretKey not initialized")
            
            val data = Base64.decode(encryptedData, Base64.NO_WRAP)
            
            // 分离 IV 和加密数据
            val iv = ByteArray(GCM_IV_LENGTH)
            val encrypted = ByteArray(data.size - GCM_IV_LENGTH)
            System.arraycopy(data, 0, iv, 0, iv.size)
            System.arraycopy(data, iv.size, encrypted, 0, encrypted.size)
            
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec)
            
            val decryptedData = cipher.doFinal(encrypted)
            String(decryptedData, Charsets.UTF_8)
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to decrypt data", e)
            throw SecurityException("Decryption failed", e)
        }
    }
    
    /**
     * 生成 MD5 哈希
     */
    fun generateMD5(input: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(input.toByteArray())
            digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            HiGameLogger.e("Failed to generate MD5", e)
            ""
        }
    }
    
    /**
     * 生成 SHA256 哈希
     */
    fun generateSHA256(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(input.toByteArray())
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            HiGameLogger.e("Failed to generate SHA256", e)
            ""
        }
    }
    
    /**
     * 生成签名
     */
    fun generateSignature(data: Map<String, Any>, appSecret: String): String {
        return try {
            // 按键名排序
            val sortedData = data.toSortedMap()
            
            // 构建签名字符串
            val signString = sortedData.entries.joinToString("&") { "${it.key}=${it.value}" }
            val finalString = "$signString&key=$appSecret"
            
            // 生成 MD5 签名
            generateMD5(finalString).uppercase()
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to generate signature", e)
            ""
        }
    }
    
    /**
     * 验证签名
     */
    fun verifySignature(data: Map<String, Any>, signature: String, appSecret: String): Boolean {
        return try {
            val expectedSignature = generateSignature(data, appSecret)
            expectedSignature.equals(signature, ignoreCase = true)
        } catch (e: Exception) {
            HiGameLogger.e("Failed to verify signature", e)
            false
        }
    }
    
    /**
     * 生成随机字符串
     */
    fun generateRandomString(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }
    
    /**
     * 生成时间戳
     */
    fun generateTimestamp(): Long {
        return System.currentTimeMillis() / 1000
    }
    
    /**
     * 生成 Nonce
     */
    fun generateNonce(): String {
        return generateRandomString(16)
    }
    
    /**
     * 验证时间戳是否有效（防重放攻击）
     */
    fun isTimestampValid(timestamp: Long, toleranceSeconds: Long = 300): Boolean {
        val currentTime = System.currentTimeMillis() / 1000
        val diff = kotlin.math.abs(currentTime - timestamp)
        return diff <= toleranceSeconds
    }
    
    /**
     * 对敏感数据进行脱敏处理
     */
    fun maskSensitiveData(data: String, maskChar: Char = '*'): String {
        return when {
            data.length <= 4 -> data
            data.length <= 8 -> {
                val start = data.substring(0, 2)
                val end = data.substring(data.length - 2)
                val middle = maskChar.toString().repeat(data.length - 4)
                "$start$middle$end"
            }
            else -> {
                val start = data.substring(0, 4)
                val end = data.substring(data.length - 4)
                val middle = maskChar.toString().repeat(data.length - 8)
                "$start$middle$end"
            }
        }
    }
    
    /**
     * 检查字符串是否包含敏感信息
     */
    fun containsSensitiveInfo(data: String): Boolean {
        val sensitivePatterns = listOf(
            "password", "pwd", "secret", "key", "token",
            "appSecret", "privateKey", "accessToken"
        )
        
        return sensitivePatterns.any { pattern ->
            data.contains(pattern, ignoreCase = true)
        }
    }
    
    /**
     * 安全地比较两个字符串（防时序攻击）
     */
    fun secureEquals(a: String, b: String): Boolean {
        if (a.length != b.length) {
            return false
        }
        
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        
        return result == 0
    }
    
    /**
     * 销毁安全管理器
     */
    fun destroy() {
        secretKey = null
        securityConfig = null
        context = null
        HiGameLogger.d("SecurityManager destroyed")
    }
    
    /**
     * 生成或加载密钥
     */
    private fun generateOrLoadSecretKey(): SecretKey {
        return try {
            // 这里可以从 Android Keystore 或其他安全存储中加载密钥
            // 为了简化，这里直接生成新密钥
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(AES_KEY_LENGTH)
            keyGenerator.generateKey()
        } catch (e: Exception) {
            HiGameLogger.e("Failed to generate secret key", e)
            // 使用固定密钥作为后备方案（生产环境不推荐）
            val keyBytes = "HiGameSDKSecretKey123456789012345".toByteArray().copyOf(32)
            SecretKeySpec(keyBytes, "AES")
        }
    }
}