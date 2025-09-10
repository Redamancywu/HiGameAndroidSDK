package com.horizon.higame.core.internal.communication

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.horizon.higame.core.internal.config.HiGameConfig
import com.horizon.higame.core.internal.security.HiGameSecurityManager
import com.horizon.higame.core.utils.HiGameLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * HiGame 网络管理器
 * 负责网络请求、连接状态监控等功能
 */
class HiGameNetworkManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: HiGameNetworkManager? = null
        
        fun getInstance(): HiGameNetworkManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HiGameNetworkManager().also { INSTANCE = it }
            }
        }
        
        private const val DEFAULT_TIMEOUT = 30000 // 30秒
        private const val DEFAULT_READ_TIMEOUT = 30000 // 30秒
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_DELAY = 1000L // 1秒
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var context: Context? = null
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    // 网络状态
    private val _isNetworkAvailable = MutableStateFlow(false)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable.asStateFlow()
    
    private val _networkType = MutableStateFlow<NetworkType>(NetworkType.NONE)
    val networkType: StateFlow<NetworkType> = _networkType.asStateFlow()
    
    // 网络配置
    private var networkConfig: com.horizon.higame.core.config.NetworkConfig? = null
    private var securityManager: HiGameSecurityManager? = null
    
    // 请求缓存
    private val requestCache = ConcurrentHashMap<String, CachedResponse>()
    
    // 请求统计
    private val requestStats = ConcurrentHashMap<String, RequestStat>()
    
    /**
     * 初始化网络管理器
     */
    suspend fun initialize(context: Context) = withContext(Dispatchers.Main) {
        this@HiGameNetworkManager.context = context.applicationContext
        this@HiGameNetworkManager.connectivityManager = 
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        this@HiGameNetworkManager.securityManager = HiGameSecurityManager.getInstance()
        
        try {
            // 注册网络状态监听
            registerNetworkCallback()
            
            // 初始化网络状态
            updateNetworkState()
            
            HiGameLogger.i("NetworkManager initialized successfully")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to initialize NetworkManager", e)
            throw e
        }
    }
    
    /**
     * 设置网络配置
     */
    fun setNetworkConfig(config: com.horizon.higame.core.config.NetworkConfig) {
        this.networkConfig = config
        HiGameLogger.d("Network config updated")
    }
    
    /**
     * 发送 GET 请求
     */
    suspend fun get(
        url: String,
        headers: Map<String, String> = emptyMap(),
        useCache: Boolean = false,
        cacheTimeout: Long = 300000 // 5分钟
    ): NetworkResponse = withContext(Dispatchers.IO) {
        
        // 检查缓存
        if (useCache) {
            val cachedResponse = getCachedResponse(url, cacheTimeout)
            if (cachedResponse != null) {
                HiGameLogger.d("Using cached response for: $url")
                return@withContext cachedResponse
            }
        }
        
        val request = NetworkRequest(
            url = url,
            method = "GET",
            headers = headers
        )
        
        val response = executeRequest(request)
        
        // 缓存成功响应
        if (useCache && response.isSuccess) {
            cacheResponse(url, response)
        }
        
        response
    }
    
    /**
     * 发送 POST 请求
     */
    suspend fun post(
        url: String,
        body: String,
        headers: Map<String, String> = emptyMap(),
        contentType: String = "application/json"
    ): NetworkResponse = withContext(Dispatchers.IO) {
        
        val requestHeaders = headers.toMutableMap()
        requestHeaders["Content-Type"] = contentType
        
        val request = NetworkRequest(
            url = url,
            method = "POST",
            headers = requestHeaders,
            body = body
        )
        
        executeRequest(request)
    }
    
    /**
     * 发送 POST 请求（JSON）
     */
    suspend fun postJson(
        url: String,
        data: Any,
        headers: Map<String, String> = emptyMap()
    ): NetworkResponse = withContext(Dispatchers.IO) {
        
        val jsonBody = when (data) {
            is String -> data
            else -> json.encodeToString(kotlinx.serialization.serializer(), data)
        }
        
        post(url, jsonBody, headers, "application/json")
    }
    
    /**
     * 发送表单请求
     */
    suspend fun postForm(
        url: String,
        formData: Map<String, String>,
        headers: Map<String, String> = emptyMap()
    ): NetworkResponse = withContext(Dispatchers.IO) {
        
        val formBody = formData.entries.joinToString("&") { (key, value) ->
            "${java.net.URLEncoder.encode(key, "UTF-8")}=${java.net.URLEncoder.encode(value, "UTF-8")}"
        }
        
        post(url, formBody, headers, "application/x-www-form-urlencoded")
    }
    
    /**
     * 上传文件
     */
    suspend fun uploadFile(
        url: String,
        filePath: String,
        fieldName: String = "file",
        additionalFields: Map<String, String> = emptyMap(),
        headers: Map<String, String> = emptyMap()
    ): NetworkResponse = withContext(Dispatchers.IO) {
        
        try {
            val file = File(filePath)
            if (!file.exists()) {
                return@withContext NetworkResponse.error("File not found: $filePath")
            }
            
            val boundary = "----HiGameSDKBoundary${System.currentTimeMillis()}"
            val requestHeaders = headers.toMutableMap()
            requestHeaders["Content-Type"] = "multipart/form-data; boundary=$boundary"
            
            val request = NetworkRequest(
                url = url,
                method = "POST",
                headers = requestHeaders
            )
            
            executeFileUpload(request, file, fieldName, additionalFields, boundary)
            
        } catch (e: Exception) {
            HiGameLogger.e("File upload failed", e)
            NetworkResponse.error("File upload failed: ${e.message}")
        }
    }
    
    /**
     * 下载文件
     */
    suspend fun downloadFile(
        url: String,
        savePath: String,
        headers: Map<String, String> = emptyMap(),
        progressCallback: ((progress: Int) -> Unit)? = null
    ): NetworkResponse = withContext(Dispatchers.IO) {
        
        try {
            val connection = createConnection(url, "GET", headers)
            val responseCode = connection.responseCode
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val contentLength = connection.contentLength
                val inputStream = connection.inputStream
                val outputStream = FileOutputStream(savePath)
                
                val buffer = ByteArray(8192)
                var totalBytesRead = 0
                var bytesRead: Int
                
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                    
                    // 更新进度
                    if (contentLength > 0 && progressCallback != null) {
                        val progress = (totalBytesRead * 100 / contentLength)
                        progressCallback(progress)
                    }
                }
                
                inputStream.close()
                outputStream.close()
                
                HiGameLogger.d("File downloaded successfully: $savePath")
                NetworkResponse.success("File downloaded successfully")
                
            } else {
                val errorMessage = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Unknown error"
                NetworkResponse.error("Download failed: $errorMessage", responseCode)
            }
            
        } catch (e: Exception) {
            HiGameLogger.e("File download failed", e)
            NetworkResponse.error("File download failed: ${e.message}")
        }
    }
    
    /**
     * 检查网络连接
     */
    fun isNetworkConnected(): Boolean {
        return _isNetworkAvailable.value
    }
    
    /**
     * 获取网络类型
     */
    fun getCurrentNetworkType(): NetworkType {
        return _networkType.value
    }
    
    /**
     * 清除请求缓存
     */
    fun clearCache() {
        requestCache.clear()
        HiGameLogger.d("Request cache cleared")
    }
    
    /**
     * 获取网络统计信息
     */
    fun getNetworkStats(): Map<String, Any> {
        val stats = mutableMapOf<String, Any>()
        
        stats["isNetworkAvailable"] = _isNetworkAvailable.value
        stats["networkType"] = _networkType.value.name
        stats["cacheSize"] = requestCache.size
        stats["requestStats"] = requestStats.values.map { it.toMap() }
        
        return stats
    }
    
    /**
     * 销毁网络管理器
     */
    fun destroy() {
        scope.cancel()
        
        // 注销网络监听
        networkCallback?.let { callback ->
            connectivityManager?.unregisterNetworkCallback(callback)
        }
        
        clearCache()
        requestStats.clear()
        context = null
        connectivityManager = null
        networkCallback = null
        
        HiGameLogger.d("NetworkManager destroyed")
    }
    
    /**
     * 执行网络请求
     */
    private suspend fun executeRequest(request: NetworkRequest): NetworkResponse {
        var lastException: Exception? = null
        
        repeat(MAX_RETRY_COUNT) { attempt ->
            try {
                val startTime = System.currentTimeMillis()
                
                val connection = createConnection(request.url, request.method, request.headers)
                
                // 写入请求体
                if (request.body != null && (request.method == "POST" || request.method == "PUT")) {
                    connection.doOutput = true
                    connection.outputStream.use { outputStream ->
                        outputStream.write(request.body.toByteArray(Charsets.UTF_8))
                    }
                }
                
                val responseCode = connection.responseCode
                val responseHeaders = connection.headerFields.mapValues { it.value.joinToString(", ") }
                
                val responseBody = if (responseCode >= 200 && responseCode < 300) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                }
                
                val duration = System.currentTimeMillis() - startTime
                
                // 记录请求统计
                recordRequestStat(request.url, responseCode >= 200 && responseCode < 300, duration, responseCode)
                
                val response = NetworkResponse(
                    isSuccess = responseCode >= 200 && responseCode < 300,
                    code = responseCode,
                    message = if (responseCode >= 200 && responseCode < 300) "Success" else "HTTP Error $responseCode",
                    data = responseBody,
                    headers = responseHeaders
                )
                
                HiGameLogger.d("Request completed: ${request.method} ${request.url}, code: $responseCode, duration: ${duration}ms")
                
                return response
                
            } catch (e: Exception) {
                lastException = e
                HiGameLogger.w("Request attempt ${attempt + 1} failed: ${e.message}")
                
                if (attempt < MAX_RETRY_COUNT - 1) {
                    delay(RETRY_DELAY * (attempt + 1)) // 递增延迟
                }
            }
        }
        
        // 所有重试都失败了
        val errorMessage = "Request failed after $MAX_RETRY_COUNT attempts: ${lastException?.message}"
        HiGameLogger.e(errorMessage, lastException)
        
        recordRequestStat(request.url, false, 0, 0)
        
        return NetworkResponse.error(errorMessage)
    }
    
    /**
     * 创建 HTTP 连接
     */
    private fun createConnection(url: String, method: String, headers: Map<String, String>): HttpURLConnection {
        val connection = URL(url).openConnection() as HttpURLConnection
        
        connection.requestMethod = method
        connection.connectTimeout = (networkConfig?.connectTimeout ?: DEFAULT_TIMEOUT).toInt()
        connection.readTimeout = (networkConfig?.readTimeout ?: DEFAULT_READ_TIMEOUT).toInt()
        connection.setRequestProperty("User-Agent", "HiGameSDK/1.0.0")
        
        // 设置请求头
        headers.forEach { (key, value) ->
            connection.setRequestProperty(key, value)
        }
        
        return connection
    }
    
    /**
     * 执行文件上传
     */
    private suspend fun executeFileUpload(
        request: NetworkRequest,
        file: File,
        fieldName: String,
        additionalFields: Map<String, String>,
        boundary: String
    ): NetworkResponse {
        
        try {
            val connection = createConnection(request.url, request.method, request.headers)
            connection.doOutput = true
            
            val outputStream = connection.outputStream
            val writer = PrintWriter(OutputStreamWriter(outputStream, "UTF-8"), true)
            
            // 写入额外字段
            additionalFields.forEach { (key, value) ->
                writer.append("--$boundary\r\n")
                writer.append("Content-Disposition: form-data; name=\"$key\"\r\n\r\n")
                writer.append("$value\r\n")
            }
            
            // 写入文件
            writer.append("--$boundary\r\n")
            writer.append("Content-Disposition: form-data; name=\"$fieldName\"; filename=\"${file.name}\"\r\n")
            writer.append("Content-Type: application/octet-stream\r\n\r\n")
            writer.flush()
            
            file.inputStream().use { fileInputStream ->
                fileInputStream.copyTo(outputStream)
            }
            
            writer.append("\r\n--$boundary--\r\n")
            writer.flush()
            writer.close()
            
            val responseCode = connection.responseCode
            val responseBody = if (responseCode >= 200 && responseCode < 300) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            }
            
            return NetworkResponse(
                isSuccess = responseCode >= 200 && responseCode < 300,
                code = responseCode,
                message = if (responseCode >= 200 && responseCode < 300) "Upload successful" else "Upload failed",
                data = responseBody
            )
            
        } catch (e: Exception) {
            HiGameLogger.e("File upload failed", e)
            return NetworkResponse.error("File upload failed: ${e.message}")
        }
    }
    
    /**
     * 注册网络状态监听
     */
    private fun registerNetworkCallback() {
        val request = android.net.NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                updateNetworkState()
                HiGameLogger.d("Network available")
            }
            
            override fun onLost(network: Network) {
                super.onLost(network)
                updateNetworkState()
                HiGameLogger.d("Network lost")
            }
            
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                updateNetworkState()
            }
        }
        
        connectivityManager?.registerNetworkCallback(request, networkCallback!!)
    }
    
    /**
     * 更新网络状态
     */
    private fun updateNetworkState() {
        val connectivityManager = this.connectivityManager ?: return
        
        val isConnected = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            networkCapabilities != null && 
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
        
        _isNetworkAvailable.value = isConnected
        
        val networkType = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            when {
                networkCapabilities == null -> NetworkType.NONE
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
                else -> NetworkType.OTHER
            }
        } else {
            @Suppress("DEPRECATION")
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            when (activeNetworkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> NetworkType.WIFI
                ConnectivityManager.TYPE_MOBILE -> NetworkType.CELLULAR
                ConnectivityManager.TYPE_ETHERNET -> NetworkType.ETHERNET
                else -> if (activeNetworkInfo != null) NetworkType.OTHER else NetworkType.NONE
            }
        }
        
        _networkType.value = networkType
        
        HiGameLogger.d("Network state updated: connected=$isConnected, type=$networkType")
    }
    
    /**
     * 获取缓存响应
     */
    private fun getCachedResponse(url: String, cacheTimeout: Long): NetworkResponse? {
        val cachedResponse = requestCache[url] ?: return null
        
        val currentTime = System.currentTimeMillis()
        if (currentTime - cachedResponse.timestamp > cacheTimeout) {
            requestCache.remove(url)
            return null
        }
        
        return cachedResponse.response
    }
    
    /**
     * 缓存响应
     */
    private fun cacheResponse(url: String, response: NetworkResponse) {
        requestCache[url] = CachedResponse(response, System.currentTimeMillis())
        
        // 限制缓存大小
        if (requestCache.size > 100) {
            val oldestEntry = requestCache.entries.minByOrNull { it.value.timestamp }
            oldestEntry?.let { requestCache.remove(it.key) }
        }
    }
    
    /**
     * 记录请求统计
     */
    private fun recordRequestStat(url: String, success: Boolean, duration: Long, responseCode: Int) {
        val stat = requestStats.getOrPut(url) { RequestStat(url) }
        
        synchronized(stat) {
            stat.totalRequests++
            if (success) {
                stat.successRequests++
            } else {
                stat.failedRequests++
            }
            stat.totalDuration += duration
            stat.averageDuration = if (stat.totalRequests > 0) stat.totalDuration / stat.totalRequests else 0
            stat.lastRequestTime = System.currentTimeMillis()
            
            if (responseCode > 0) {
                stat.responseCodes[responseCode] = (stat.responseCodes[responseCode] ?: 0) + 1
            }
        }
    }
    
    /**
     * 网络请求数据类
     */
    data class NetworkRequest(
        val url: String,
        val method: String,
        val headers: Map<String, String> = emptyMap(),
        val body: String? = null
    )
    
    /**
     * 网络响应数据类
     */
    data class NetworkResponse(
        val isSuccess: Boolean,
        val code: Int,
        val message: String,
        val data: String = "",
        val headers: Map<String, String> = emptyMap()
    ) {
        companion object {
            fun success(data: String = "", code: Int = 200): NetworkResponse {
                return NetworkResponse(true, code, "Success", data)
            }
            
            fun error(message: String, code: Int = -1): NetworkResponse {
                return NetworkResponse(false, code, message)
            }
        }
        
        inline fun <reified T> parseJson(): T? {
            return try {
                Json.decodeFromString<T>(data)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * 缓存响应数据类
     */
    data class CachedResponse(
        val response: NetworkResponse,
        val timestamp: Long
    )
    
    /**
     * 请求统计数据类
     */
    data class RequestStat(
        val url: String,
        var totalRequests: Long = 0,
        var successRequests: Long = 0,
        var failedRequests: Long = 0,
        var totalDuration: Long = 0,
        var averageDuration: Long = 0,
        var lastRequestTime: Long = 0,
        val responseCodes: MutableMap<Int, Int> = mutableMapOf()
    ) {
        fun toMap(): Map<String, Any> {
            return mapOf(
                "url" to url,
                "totalRequests" to totalRequests,
                "successRequests" to successRequests,
                "failedRequests" to failedRequests,
                "successRate" to if (totalRequests > 0) (successRequests.toDouble() / totalRequests * 100) else 0.0,
                "averageDuration" to averageDuration,
                "lastRequestTime" to lastRequestTime,
                "responseCodes" to responseCodes
            )
        }
    }
    
    /**
     * 网络类型枚举
     */
    enum class NetworkType {
        NONE,
        WIFI,
        CELLULAR,
        ETHERNET,
        OTHER
    }
}