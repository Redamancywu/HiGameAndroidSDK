package com.horizon.higame.core.internal.monitor

import android.content.Context
import com.horizon.higame.core.utils.HiGameLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * HiGame 监控管理器
 * 负责性能监控、错误统计、用户行为分析等
 */
class HiGameMonitorManager private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: HiGameMonitorManager? = null
        
        fun getInstance(): HiGameMonitorManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HiGameMonitorManager().also { INSTANCE = it }
            }
        }
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var context: Context? = null
    
    // 性能监控数据
    private val performanceMetrics = ConcurrentHashMap<String, PerformanceMetric>()
    
    // 错误统计
    private val errorCounts = ConcurrentHashMap<String, AtomicLong>()
    private val errorDetails = mutableListOf<ErrorDetail>()
    
    // 用户行为统计
    private val userActions = mutableListOf<UserAction>()
    
    // API 调用统计
    private val apiCallStats = ConcurrentHashMap<String, ApiCallStat>()
    
    // 监控状态
    private val _isMonitoringEnabled = MutableStateFlow(true)
    val isMonitoringEnabled: StateFlow<Boolean> = _isMonitoringEnabled.asStateFlow()
    
    /**
     * 初始化监控管理器
     */
    suspend fun initialize(context: Context) = withContext(Dispatchers.IO) {
        this@HiGameMonitorManager.context = context.applicationContext
        
        try {
            // 启动定期数据上报任务
            startPeriodicReporting()
            
            HiGameLogger.i("MonitorManager initialized successfully")
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to initialize MonitorManager", e)
            throw e
        }
    }
    
    /**
     * 开始性能监控
     */
    fun startPerformanceMonitoring(operation: String): String {
        if (!_isMonitoringEnabled.value) return ""
        
        val monitorId = generateMonitorId()
        val metric = PerformanceMetric(
            id = monitorId,
            operation = operation,
            startTime = System.currentTimeMillis()
        )
        
        performanceMetrics[monitorId] = metric
        HiGameLogger.d("Started performance monitoring for: $operation")
        
        return monitorId
    }
    
    /**
     * 结束性能监控
     */
    fun endPerformanceMonitoring(monitorId: String, success: Boolean = true, errorMessage: String? = null) {
        if (!_isMonitoringEnabled.value || monitorId.isEmpty()) return
        
        val metric = performanceMetrics[monitorId] ?: return
        
        metric.endTime = System.currentTimeMillis()
        metric.duration = metric.endTime - metric.startTime
        metric.success = success
        metric.errorMessage = errorMessage
        
        HiGameLogger.d("Performance monitoring completed for: ${metric.operation}, duration: ${metric.duration}ms")
        
        // 异步上报数据
        scope.launch {
            reportPerformanceMetric(metric)
        }
    }
    
    /**
     * 记录错误
     */
    fun recordError(errorType: String, errorMessage: String, stackTrace: String? = null) {
        if (!_isMonitoringEnabled.value) return
        
        // 更新错误计数
        errorCounts.getOrPut(errorType) { AtomicLong(0) }.incrementAndGet()
        
        // 记录错误详情
        val errorDetail = ErrorDetail(
            type = errorType,
            message = errorMessage,
            stackTrace = stackTrace,
            timestamp = System.currentTimeMillis()
        )
        
        synchronized(errorDetails) {
            errorDetails.add(errorDetail)
            // 保持最近的 100 条错误记录
            if (errorDetails.size > 100) {
                errorDetails.removeAt(0)
            }
        }
        
        HiGameLogger.w("Error recorded: $errorType - $errorMessage")
        
        // 异步上报错误
        scope.launch {
            reportError(errorDetail)
        }
    }
    
    /**
     * 记录用户行为
     */
    fun recordUserAction(action: String, parameters: Map<String, Any> = emptyMap()) {
        if (!_isMonitoringEnabled.value) return
        
        val userAction = UserAction(
            action = action,
            parameters = parameters,
            timestamp = System.currentTimeMillis()
        )
        
        synchronized(userActions) {
            userActions.add(userAction)
            // 保持最近的 500 条用户行为记录
            if (userActions.size > 500) {
                userActions.removeAt(0)
            }
        }
        
        HiGameLogger.d("User action recorded: $action")
        
        // 异步上报用户行为
        scope.launch {
            reportUserAction(userAction)
        }
    }
    
    /**
     * 记录 API 调用
     */
    fun recordApiCall(apiName: String, success: Boolean, duration: Long, responseCode: Int = 0) {
        if (!_isMonitoringEnabled.value) return
        
        val stat = apiCallStats.getOrPut(apiName) { ApiCallStat(apiName) }
        
        synchronized(stat) {
            stat.totalCalls++
            if (success) {
                stat.successCalls++
            } else {
                stat.failedCalls++
            }
            stat.totalDuration += duration
            stat.averageDuration = stat.totalDuration / stat.totalCalls
            stat.lastCallTime = System.currentTimeMillis()
            
            if (responseCode > 0) {
                stat.responseCodes[responseCode] = (stat.responseCodes[responseCode] ?: 0) + 1
            }
        }
        
        HiGameLogger.d("API call recorded: $apiName, success: $success, duration: ${duration}ms")
    }
    
    /**
     * 获取性能统计
     */
    fun getPerformanceStats(): Map<String, Any> {
        val stats = mutableMapOf<String, Any>()
        
        // 性能指标统计
        val operationStats = mutableMapOf<String, MutableMap<String, Any>>()
        performanceMetrics.values.forEach { metric ->
            val opStats = operationStats.getOrPut(metric.operation) {
                mutableMapOf(
                    "count" to 0,
                    "totalDuration" to 0L,
                    "averageDuration" to 0L,
                    "successCount" to 0,
                    "failedCount" to 0
                )
            }
            
            opStats["count"] = (opStats["count"] as Int) + 1
            opStats["totalDuration"] = (opStats["totalDuration"] as Long) + metric.duration
            opStats["averageDuration"] = (opStats["totalDuration"] as Long) / (opStats["count"] as Int)
            
            if (metric.success) {
                opStats["successCount"] = (opStats["successCount"] as Int) + 1
            } else {
                opStats["failedCount"] = (opStats["failedCount"] as Int) + 1
            }
        }
        
        stats["performanceMetrics"] = operationStats
        stats["errorCounts"] = errorCounts.mapValues { it.value.get() }
        stats["apiCallStats"] = apiCallStats.values.map { it.toMap() }
        
        return stats
    }
    
    /**
     * 获取错误统计
     */
    fun getErrorStats(): Map<String, Long> {
        return errorCounts.mapValues { it.value.get() }
    }
    
    /**
     * 获取最近的错误详情
     */
    fun getRecentErrors(limit: Int = 10): List<ErrorDetail> {
        return synchronized(errorDetails) {
            errorDetails.takeLast(limit)
        }
    }
    
    /**
     * 获取用户行为统计
     */
    fun getUserActionStats(): Map<String, Int> {
        val actionCounts = mutableMapOf<String, Int>()
        
        synchronized(userActions) {
            userActions.forEach { action ->
                actionCounts[action.action] = (actionCounts[action.action] ?: 0) + 1
            }
        }
        
        return actionCounts
    }
    
    /**
     * 启用/禁用监控
     */
    fun setMonitoringEnabled(enabled: Boolean) {
        _isMonitoringEnabled.value = enabled
        HiGameLogger.i("Monitoring ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * 清除所有监控数据
     */
    fun clearAllData() {
        performanceMetrics.clear()
        errorCounts.clear()
        synchronized(errorDetails) {
            errorDetails.clear()
        }
        synchronized(userActions) {
            userActions.clear()
        }
        apiCallStats.clear()
        
        HiGameLogger.i("All monitoring data cleared")
    }
    
    /**
     * 销毁监控管理器
     */
    fun destroy() {
        scope.cancel()
        clearAllData()
        context = null
        HiGameLogger.d("MonitorManager destroyed")
    }
    
    /**
     * 启动定期数据上报
     */
    private fun startPeriodicReporting() {
        scope.launch {
            while (isActive) {
                try {
                    delay(300_000) // 5分钟上报一次
                    
                    if (_isMonitoringEnabled.value) {
                        reportBatchData()
                    }
                    
                } catch (e: Exception) {
                    HiGameLogger.e("Error in periodic reporting", e)
                }
            }
        }
    }
    
    /**
     * 批量上报数据
     */
    private suspend fun reportBatchData() {
        try {
            val stats = getPerformanceStats()
            HiGameLogger.d("Batch reporting monitoring data: ${stats.size} metrics")
            
            // 这里可以实现实际的数据上报逻辑
            // 例如发送到服务器或写入本地文件
            
        } catch (e: Exception) {
            HiGameLogger.e("Failed to report batch data", e)
        }
    }
    
    /**
     * 上报性能指标
     */
    private suspend fun reportPerformanceMetric(metric: PerformanceMetric) {
        try {
            // 实现具体的性能指标上报逻辑
            HiGameLogger.v("Reporting performance metric: ${metric.operation}")
        } catch (e: Exception) {
            HiGameLogger.e("Failed to report performance metric", e)
        }
    }
    
    /**
     * 上报错误
     */
    private suspend fun reportError(error: ErrorDetail) {
        try {
            // 实现具体的错误上报逻辑
            HiGameLogger.v("Reporting error: ${error.type}")
        } catch (e: Exception) {
            HiGameLogger.e("Failed to report error", e)
        }
    }
    
    /**
     * 上报用户行为
     */
    private suspend fun reportUserAction(action: UserAction) {
        try {
            // 实现具体的用户行为上报逻辑
            HiGameLogger.v("Reporting user action: ${action.action}")
        } catch (e: Exception) {
            HiGameLogger.e("Failed to report user action", e)
        }
    }
    
    /**
     * 生成监控ID
     */
    private fun generateMonitorId(): String {
        return "${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}"
    }
    
    /**
     * 性能指标数据类
     */
    data class PerformanceMetric(
        val id: String,
        val operation: String,
        val startTime: Long,
        var endTime: Long = 0,
        var duration: Long = 0,
        var success: Boolean = true,
        var errorMessage: String? = null
    )
    
    /**
     * 错误详情数据类
     */
    data class ErrorDetail(
        val type: String,
        val message: String,
        val stackTrace: String?,
        val timestamp: Long
    )
    
    /**
     * 用户行为数据类
     */
    data class UserAction(
        val action: String,
        val parameters: Map<String, Any>,
        val timestamp: Long
    )
    
    /**
     * API 调用统计数据类
     */
    data class ApiCallStat(
        val apiName: String,
        var totalCalls: Long = 0,
        var successCalls: Long = 0,
        var failedCalls: Long = 0,
        var totalDuration: Long = 0,
        var averageDuration: Long = 0,
        var lastCallTime: Long = 0,
        val responseCodes: MutableMap<Int, Int> = mutableMapOf()
    ) {
        fun toMap(): Map<String, Any> {
            return mapOf(
                "apiName" to apiName,
                "totalCalls" to totalCalls,
                "successCalls" to successCalls,
                "failedCalls" to failedCalls,
                "successRate" to if (totalCalls > 0) (successCalls.toDouble() / totalCalls * 100) else 0.0,
                "averageDuration" to averageDuration,
                "lastCallTime" to lastCallTime,
                "responseCodes" to responseCodes
            )
        }
    }
}