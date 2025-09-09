package com.horizon.higame.core.internal.communication

import com.horizon.higame.core.utils.HiGameLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong

/**
 * HiGame 事件总线
 * 负责组件间的事件通信和消息传递
 */
class HiGameEventBus private constructor() {
    
    companion object {
        @Volatile
        private var INSTANCE: HiGameEventBus? = null
        
        fun getInstance(): HiGameEventBus {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HiGameEventBus().also { INSTANCE = it }
            }
        }
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // 事件流
    private val eventChannel = Channel<Event>(Channel.UNLIMITED)
    private val eventFlow = eventChannel.receiveAsFlow().shareIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        replay = 0
    )
    
    // 订阅者管理
    private val subscribers = ConcurrentHashMap<String, CopyOnWriteArrayList<EventSubscriber>>()
    private val globalSubscribers = CopyOnWriteArrayList<EventSubscriber>()
    
    // 事件统计
    private val eventCounter = AtomicLong(0)
    private val eventStats = ConcurrentHashMap<String, EventStat>()
    
    // 事件历史（用于调试）
    private val eventHistory = CopyOnWriteArrayList<Event>()
    private val maxHistorySize = 1000
    
    @Volatile
    private var isInitialized = false
    
    /**
     * 初始化事件总线
     */
    fun initialize() {
        if (isInitialized) {
            HiGameLogger.w("EventBus already initialized")
            return
        }
        
        // 启动事件处理协程
        scope.launch {
            eventFlow.collect { event ->
                processEvent(event)
            }
        }
        
        isInitialized = true
        HiGameLogger.i("EventBus initialized successfully")
    }
    
    /**
     * 发布事件
     */
    fun post(eventType: String, data: Map<String, Any> = emptyMap()) {
        if (!isInitialized) {
            HiGameLogger.w("EventBus not initialized, event ignored: $eventType")
            return
        }
        
        val event = Event(
            id = eventCounter.incrementAndGet(),
            type = eventType,
            data = data,
            timestamp = System.currentTimeMillis(),
            threadName = Thread.currentThread().name
        )
        
        // 异步发送事件
        scope.launch {
            try {
                eventChannel.send(event)
                HiGameLogger.d("Event posted: $eventType")
            } catch (e: Exception) {
                HiGameLogger.e("Failed to post event: $eventType", e)
            }
        }
    }
    
    /**
     * 发布事件（同步）
     */
    suspend fun postSync(eventType: String, data: Map<String, Any> = emptyMap()) {
        if (!isInitialized) {
            HiGameLogger.w("EventBus not initialized, event ignored: $eventType")
            return
        }
        
        val event = Event(
            id = eventCounter.incrementAndGet(),
            type = eventType,
            data = data,
            timestamp = System.currentTimeMillis(),
            threadName = Thread.currentThread().name
        )
        
        try {
            eventChannel.send(event)
            HiGameLogger.d("Event posted sync: $eventType")
        } catch (e: Exception) {
            HiGameLogger.e("Failed to post sync event: $eventType", e)
        }
    }
    
    /**
     * 订阅特定类型的事件
     */
    fun subscribe(eventType: String, subscriber: EventSubscriber) {
        val subscriberList = subscribers.getOrPut(eventType) { CopyOnWriteArrayList() }
        subscriberList.add(subscriber)
        
        HiGameLogger.d("Subscribed to event: $eventType, subscriber: ${subscriber.name}")
    }
    
    /**
     * 订阅所有事件
     */
    fun subscribeAll(subscriber: EventSubscriber) {
        globalSubscribers.add(subscriber)
        HiGameLogger.d("Subscribed to all events, subscriber: ${subscriber.name}")
    }
    
    /**
     * 取消订阅特定类型的事件
     */
    fun unsubscribe(eventType: String, subscriber: EventSubscriber) {
        subscribers[eventType]?.remove(subscriber)
        HiGameLogger.d("Unsubscribed from event: $eventType, subscriber: ${subscriber.name}")
    }
    
    /**
     * 取消订阅所有事件
     */
    fun unsubscribeAll(subscriber: EventSubscriber) {
        globalSubscribers.remove(subscriber)
        subscribers.values.forEach { it.remove(subscriber) }
        HiGameLogger.d("Unsubscribed from all events, subscriber: ${subscriber.name}")
    }
    
    /**
     * 创建事件流（用于响应式编程）
     */
    fun createEventFlow(eventType: String): Flow<Event> {
        return eventFlow.filter { it.type == eventType }
    }
    
    /**
     * 创建全局事件流
     */
    fun createGlobalEventFlow(): Flow<Event> {
        return eventFlow
    }
    
    /**
     * 获取事件统计信息
     */
    fun getEventStats(): Map<String, Any> {
        val stats = mutableMapOf<String, Any>()
        
        stats["totalEvents"] = eventCounter.get()
        stats["subscriberCount"] = subscribers.values.sumOf { it.size } + globalSubscribers.size
        stats["eventTypeCount"] = subscribers.size
        stats["historySize"] = eventHistory.size
        stats["eventTypeStats"] = eventStats.values.map { it.toMap() }
        
        return stats
    }
    
    /**
     * 获取事件历史
     */
    fun getEventHistory(limit: Int = 50): List<Event> {
        return eventHistory.takeLast(limit)
    }
    
    /**
     * 获取特定类型的事件历史
     */
    fun getEventHistory(eventType: String, limit: Int = 50): List<Event> {
        return eventHistory.filter { it.type == eventType }.takeLast(limit)
    }
    
    /**
     * 清除事件历史
     */
    fun clearEventHistory() {
        eventHistory.clear()
        HiGameLogger.d("Event history cleared")
    }
    
    /**
     * 清除事件统计
     */
    fun clearEventStats() {
        eventStats.clear()
        HiGameLogger.d("Event stats cleared")
    }
    
    /**
     * 获取订阅者信息
     */
    fun getSubscriberInfo(): Map<String, Any> {
        val info = mutableMapOf<String, Any>()
        
        info["globalSubscribers"] = globalSubscribers.map { it.name }
        info["eventSubscribers"] = subscribers.mapValues { (_, subscribers) ->
            subscribers.map { it.name }
        }
        
        return info
    }
    
    /**
     * 检查是否有订阅者
     */
    fun hasSubscribers(eventType: String): Boolean {
        return globalSubscribers.isNotEmpty() || subscribers[eventType]?.isNotEmpty() == true
    }
    
    /**
     * 销毁事件总线
     */
    fun destroy() {
        scope.cancel()
        
        subscribers.clear()
        globalSubscribers.clear()
        eventStats.clear()
        eventHistory.clear()
        
        eventChannel.close()
        
        isInitialized = false
        HiGameLogger.d("EventBus destroyed")
    }
    
    /**
     * 处理事件
     */
    private suspend fun processEvent(event: Event) {
        try {
            // 记录事件历史
            addToHistory(event)
            
            // 更新事件统计
            updateEventStats(event)
            
            // 通知全局订阅者
            globalSubscribers.forEach { subscriber ->
                notifySubscriber(subscriber, event)
            }
            
            // 通知特定类型的订阅者
            subscribers[event.type]?.forEach { subscriber ->
                notifySubscriber(subscriber, event)
            }
            
            HiGameLogger.v("Event processed: ${event.type}, subscribers notified")
            
        } catch (e: Exception) {
            HiGameLogger.e("Error processing event: ${event.type}", e)
        }
    }
    
    /**
     * 通知订阅者
     */
    private suspend fun notifySubscriber(subscriber: EventSubscriber, event: Event) {
        try {
            when (subscriber.deliveryMode) {
                DeliveryMode.IMMEDIATE -> {
                    // 立即在当前协程中执行
                    subscriber.onEvent(event)
                }
                DeliveryMode.ASYNC -> {
                    // 在新的协程中异步执行
                    scope.launch {
                        subscriber.onEvent(event)
                    }
                }
                DeliveryMode.MAIN_THREAD -> {
                    // 在主线程中执行
                    withContext(Dispatchers.Main) {
                        subscriber.onEvent(event)
                    }
                }
            }
        } catch (e: Exception) {
            HiGameLogger.e("Error notifying subscriber: ${subscriber.name}", e)
        }
    }
    
    /**
     * 添加到历史记录
     */
    private fun addToHistory(event: Event) {
        eventHistory.add(event)
        
        // 限制历史记录大小
        if (eventHistory.size > maxHistorySize) {
            eventHistory.removeAt(0)
        }
    }
    
    /**
     * 更新事件统计
     */
    private fun updateEventStats(event: Event) {
        val stat = eventStats.getOrPut(event.type) { EventStat(event.type) }
        
        synchronized(stat) {
            stat.count++
            stat.lastEventTime = event.timestamp
            
            // 计算平均间隔
            if (stat.firstEventTime == 0L) {
                stat.firstEventTime = event.timestamp
            } else {
                val totalTime = event.timestamp - stat.firstEventTime
                stat.averageInterval = if (stat.count > 1) totalTime / (stat.count - 1) else 0
            }
        }
    }
    
    /**
     * 事件数据类
     */
    data class Event(
        val id: Long,
        val type: String,
        val data: Map<String, Any>,
        val timestamp: Long,
        val threadName: String
    ) {
        fun <T> getDataAs(key: String): T? {
            @Suppress("UNCHECKED_CAST")
            return data[key] as? T
        }
        
        fun hasData(key: String): Boolean {
            return data.containsKey(key)
        }
    }
    
    /**
     * 事件订阅者接口
     */
    interface EventSubscriber {
        val name: String
        val deliveryMode: DeliveryMode get() = DeliveryMode.ASYNC
        
        suspend fun onEvent(event: Event)
    }
    
    /**
     * 简单的事件订阅者实现
     */
    class SimpleEventSubscriber(
        override val name: String,
        override val deliveryMode: DeliveryMode = DeliveryMode.ASYNC,
        private val handler: suspend (Event) -> Unit
    ) : EventSubscriber {
        
        override suspend fun onEvent(event: Event) {
            handler(event)
        }
    }
    
    /**
     * 事件传递模式
     */
    enum class DeliveryMode {
        IMMEDIATE,    // 立即执行（同步）
        ASYNC,        // 异步执行
        MAIN_THREAD   // 主线程执行
    }
    
    /**
     * 事件统计数据类
     */
    data class EventStat(
        val eventType: String,
        var count: Long = 0,
        var firstEventTime: Long = 0,
        var lastEventTime: Long = 0,
        var averageInterval: Long = 0
    ) {
        fun toMap(): Map<String, Any> {
            return mapOf(
                "eventType" to eventType,
                "count" to count,
                "firstEventTime" to firstEventTime,
                "lastEventTime" to lastEventTime,
                "averageInterval" to averageInterval
            )
        }
    }
}