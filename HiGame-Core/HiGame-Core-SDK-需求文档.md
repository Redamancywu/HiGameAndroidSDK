# HiGame-Core SDK 需求功能文档

## 新增：UI 基础契约（供 UI AAR 复用）
为实现“模块化、可拔插、统一风格”的 UI 体系，Core 提供三类基础定义，UI AAR（login/share/usercenter）仅依赖这些契约并在 Compose 层做适配。
- 包名建议：`com.horizon.higame.core.ui`
- 组成：主题系统、动画系统、UI 通用状态接口
- 设计目标：对外保持 Kotlin/Java 皆可使用；不绑定 Compose/AndroidX ViewModel；可由 UI 层映射到 Material3/动画

### 1) 主题系统（Kotlin data class）
```kotlin
package com.horizon.higame.core.ui

import androidx.annotation.ColorInt

data class HiGameTheme(
    val colors: Colors = Colors(),
    val typography: Typography = Typography(),
    val shapes: Shapes = Shapes()
) {
    data class Colors(
        @ColorInt val primary: Int = 0xFF4CAF50.toInt(),
        @ColorInt val secondary: Int = 0xFF03A9F4.toInt(),
        @ColorInt val accent: Int = 0xFFFFC107.toInt(),
        @ColorInt val background: Int = 0xFFFFFFFF.toInt(),
        @ColorInt val surface: Int = 0xFFFFFFFF.toInt(),
        @ColorInt val error: Int = 0xFFEF5350.toInt(),
        @ColorInt val onPrimary: Int = 0xFFFFFFFF.toInt(),
        @ColorInt val onSecondary: Int = 0xFF000000.toInt()
    )
    data class Typography(
        val h1Sp: Int = 28, val h2Sp: Int = 24, val h3Sp: Int = 20,
        val h4Sp: Int = 18, val h5Sp: Int = 16, val h6Sp: Int = 14,
        val body1Sp: Int = 16, val body2Sp: Int = 14,
        val captionSp: Int = 12, val overlineSp: Int = 10,
        val fontFamily: String = "sans-serif"
    )
    data class Shapes(
        val smallCornerDp: Int = 6,
        val mediumCornerDp: Int = 10,
        val largeCornerDp: Int = 16
    )
}
```

### 2) 动画系统（描述型 Spec）
```kotlin
package com.horizon.higame.core.ui

object HiGameAnimations {
    const val DURATION_SHORT = 150
    const val DURATION_MEDIUM = 300
    const val DURATION_LONG = 500

    enum class Type { FadeIn, SlideUp, ScaleIn }

    data class Spec(
        val type: Type,
        val durationMs: Int = DURATION_MEDIUM,
        val delayMs: Int = 0
    )

    fun fadeIn(durationMs: Int = DURATION_MEDIUM, delayMs: Int = 0) =
        Spec(Type.FadeIn, durationMs, delayMs)
    fun slideUp(durationMs: Int = DURATION_MEDIUM, delayMs: Int = 0) =
        Spec(Type.SlideUp, durationMs, delayMs)
    fun scaleIn(durationMs: Int = DURATION_MEDIUM, delayMs: Int = 0) =
        Spec(Type.ScaleIn, durationMs, delayMs)
}
```

### 3) UI 通用管理状态接口
```kotlin
package com.horizon.higame.core.ui

interface HiGameUIState<T> {
    fun onLoading()
    fun onSuccess(data: T)
    fun onError(message: String)
    fun onEmpty()
}

// 仅定义生命周期契约；不强依赖 AndroidX ViewModel
abstract class HiGameBaseViewModel(
    protected val eventBus: Any? = null,         // 可替换为 HiGameEventBus
    protected val configProvider: Any? = null    // 可替换为 HiGameConfigProvider
) {
    open fun onCreate() {}
    open fun onResume() {}
    open fun onPause() {}
    open fun onDestroy() {}
}
```

使用建议：
- UI AAR 通过适配器将 HiGameTheme/HiGameAnimations 映射到 Compose Material3 与动画 API
- 引擎项目不需要依赖 Compose，只依赖 Core 的这些轻量契约
- 后续可在 Core 提供全局配置入口：HiGameUIConfig.setTheme(HiGameTheme) 等，供游戏侧统一设置

## 项目概述

### 产品定位
HiGame-Core SDK 是一个**企业级的模块化核心框架**，作为整个 HiGame SDK 生态系统的"中枢神经系统"，为游戏和应用提供统一的服务管理、配置管理、状态管理等核心能力。

### 目标用户
- **游戏开发者**：Cocos Creator、Unity、原生游戏开发团队
- **App 开发者**：Android 原生应用、混合应用开发团队
- **SDK 开发者**：基于 HiGame 生态开发功能模块的开发团队
- **企业客户**：需要统一 SDK 管理的多产品线公司

### 核心价值
- **统一管理**：提供所有功能模块的统一入口和管理机制
- **配置驱动**：通过 JSON 配置文件管理所有模块参数
- **高性能**：编译时优化 + 并发初始化，启动速度快
- **企业级**：完整的安全、监控、版本管理等企业特性

### 技术特性
- **实现技术**：Kotlin + 协程 + StateFlow + KSP
- **架构模式**：门面模式 + 依赖注入 + 事件驱动
- **集成方式**：单例 SDK + JSON 配置 + 自动服务发现
- **性能优化**：编译时代码生成 + 运行时优化

---

## 核心功能需求

## 1. SDK 对外接口设计

### 1.1 统一门面接口
**HiGameSDK** 作为唯一对外入口，所有功能都通过它访问：

```kotlin
class HiGameSDK private constructor() {
    companion object {
        fun getInstance(): HiGameSDK
    }
    
    // 初始化接口
    fun initialize(context: Context, callback: HiGameInitCallback)
    
    // 登录功能
    fun login(showUI: Boolean, callback: HiGameLoginCallback)
    fun logout()
    fun isLoggedIn(): Boolean
    
    // 支付功能
    fun pay(payInfo: HiGamePayInfo, callback: HiGamePayCallback)
    fun queryOrder(orderId: String, callback: HiGameQueryCallback)
    
    // 分享功能
    fun share(shareContent: HiGameShareContent, callback: HiGameShareCallback)
    
    // 用户中心功能
    fun showUserCenter(context: Context)
    
    // 销毁接口
    fun destroy()
}
```

### 1.2 内部委托架构
**三层委托模式**，职责清晰分离：

```kotlin
// 第一层：对外门面层
class HiGameSDK {
    fun login(showUI: Boolean, callback: HiGameLoginCallback) {
        HiGameSDKManager.getInstance().login(showUI, callback)
    }
}

// 第二层：内部管理层
internal class HiGameSDKManager {
    fun login(showUI: Boolean, callback: HiGameLoginCallback) {
        HiGameLoginManager.getInstance().login(showUI, callback)
    }
}

// 第三层：功能实现层
internal class HiGameLoginManager {
    fun login(showUI: Boolean, callback: HiGameLoginCallback) {
        // 调用具体的服务实现
        loginService?.performLogin(showUI, callback)
    }
}
```

### 1.3 设计优势
- **统一入口**：开发者只需要记住 HiGameSDK 一个类
- **职责分离**：每层都有明确的职责和边界
- **易于扩展**：新增功能只需在三层中添加对应方法
- **便于维护**：每层独立，修改不会相互影响
- **符合原则**：遵循单一职责、开闭原则、门面模式

---

## 2. 配置管理系统

### 2.1 JSON 配置解析需求
- **配置文件位置**：assets/higame_config.json
- **配置结构**：全局配置 + 模块配置的层次结构
- **参数支持**：appId、appKey、appSecret、自定义参数等
- **类型安全**：强类型配置类，避免运行时错误

### 2.2 配置示例结构
```json
{
  "globalConfig": {
    "appId": "your_app_id",
    "appSecret": "your_app_secret",
    "environment": "release",
    "logLevel": "info",
    "timeout": 10000
  },
  "moduleConfigs": {
    "login": {
      "enabled": true,
      "appKey": "wechat_app_key",
      "appSecret": "wechat_app_secret",
      "customParams": {
        "autoLogin": true,
        "loginTypes": ["wechat", "qq", "guest"]
      }
    },
    "share": {
      "enabled": true,
      "appKey": "share_app_key",
      "customParams": {
        "defaultChannels": ["wechat", "qq", "weibo"]
      }
    }
  }
}
```

### 2.3 配置优先级需求
- **三层架构**：默认配置 < 本地配置 < 远程配置
- **配置合并**：智能合并不同层级的配置信息
- **热更新**：支持运行时配置更新和通知
- **配置验证**：配置格式和内容的合法性验证

### 2.4 配置隔离需求
- **模块隔离**：每个模块只能访问自己的配置
- **全局共享**：全局配置对所有模块可见
- **权限控制**：敏感配置的访问权限控制
- **配置缓存**：配置信息的本地缓存机制

---

## 3. 内部架构设计

### 3.1 HiGameSDKManager（内部核心管理器）
**职责**：统一管理所有内部逻辑，对外完全透明

```kotlin
internal object HiGameSDKManager {
    // 配置管理
    private val configManager = HiGameConfigManager()
    
    // 服务发现与注册
    private val serviceRegistry = HiGameServiceRegistry()
    
    // 状态管理
    private val stateManager = HiGameStateManager()
    
    // 初始化协调
    suspend fun initialize(context: Context): HiGameInitResult
    
    // 获取功能管理器
    fun <T> getManager(managerClass: Class<T>): T?
}
```

### 3.2 服务自动发现机制
- **编译时生成**：使用 KSP 编译时生成服务注册表
- **运行时发现**：ServiceLoader 作为兜底的动态发现机制
- **自动注册**：各功能模块通过 @AutoService 注解自动注册
- **依赖解析**：自动解析模块间依赖关系并确定初始化顺序

### 3.3 功能管理器架构
每个功能领域都有对应的 Manager，统一管理该领域的所有能力：

```kotlin
// 登录管理器
class HiGameLoginManager private constructor() {
    companion object {
        fun getInstance(): HiGameLoginManager
    }
    
    // 内部持有具体实现
    private var loginService: HiGameLoginService? = null
    
    // 对外提供业务接口
    fun login(showUI: Boolean, callback: HiGameLoginCallback)
    fun logout()
    fun isLoggedIn(): Boolean
}

// 支付管理器
class HiGamePayManager private constructor() {
    companion object {
        fun getInstance(): HiGamePayManager
    }
    
    private var payService: HiGamePayService? = null
    
    fun pay(payInfo: HiGamePayInfo, callback: HiGamePayCallback)
    fun queryOrder(orderId: String, callback: HiGameQueryCallback)
}
```

### 3.4 服务接口定义
```kotlin
// 基础服务接口（内部使用）
internal interface HiGameBaseService {
    val moduleName: String
    val version: String
    val dependencies: List<String>
    
    suspend fun initialize(config: HiGameConfig, callback: HiGameModuleCallback)
    fun onConfigChanged(config: HiGameConfig)
    fun getHealthStatus(): HiGameHealthStatus
}

// 具体功能服务接口（内部使用）
internal interface HiGameLoginService : HiGameBaseService {
    fun performLogin(showUI: Boolean, callback: HiGameLoginCallback)
    fun performLogout()
    fun checkLoginStatus(): Boolean
}

internal interface HiGamePayService : HiGameBaseService {
    fun performPay(payInfo: HiGamePayInfo, callback: HiGamePayCallback)
    fun queryOrderStatus(orderId: String, callback: HiGameQueryCallback)
}
```

---

## 4. 状态管理系统

### 4.1 状态模型设计
- **模块状态枚举**：
  - 已发现（Discovered）
  - 初始化中（Initializing）
  - 就绪（Ready）
  - 活跃（Active）
  - 暂停（Suspended）
  - 错误（Error）
  - 已销毁（Destroyed）
- **整体初始化状态**：
  - 模块状态映射
  - 初始化进度
  - 完成状态
  - 成功状态

### 4.2 响应式状态管理需求
- **StateFlow 驱动**：使用 StateFlow 提供响应式状态流
- **实时更新**：状态变化实时通知所有订阅者
- **UI 绑定**：可直接绑定 UI 组件显示进度和状态
- **状态持久化**：关键状态的本地持久化存储

### 4.3 状态监听需求
- **状态变更监听**：监听单个模块的状态变化
- **进度变更监听**：监听整体初始化进度变化
- **完成状态监听**：监听初始化完成事件
- **错误状态监听**：监听错误发生和恢复事件

### 4.4 状态恢复需求
- **状态保存**：应用退出时保存关键状态
- **状态恢复**：应用重启时恢复之前的状态
- **状态同步**：多进程间的状态同步机制
- **状态清理**：过期状态的自动清理机制

---

## 5. 安全机制

### 5.1 模块验证需求
- **签名验证**：验证模块 AAR 的数字签名
- **完整性检查**：检查模块文件的完整性
- **版本验证**：验证模块版本的合法性
- **来源验证**：验证模块的来源渠道

### 5.2 权限控制需求
- **API 访问控制**：控制模块对敏感 API 的访问
- **数据访问控制**：控制模块对敏感数据的访问
- **权限动态检查**：运行时动态检查权限
- **权限申请流程**：标准化的权限申请流程

### 5.3 数据安全需求
- **敏感数据加密**：对 appKey、appSecret 等敏感数据加密存储
- **传输加密**：网络传输数据的加密保护
- **内存保护**：防止内存中敏感数据被读取
- **日志脱敏**：日志中敏感信息的脱敏处理

### 5.4 安全策略配置
- **安全等级设置**：支持不同的安全等级配置
- **安全策略更新**：支持安全策略的动态更新
- **安全事件上报**：安全异常事件的自动上报
- **安全审计日志**：完整的安全操作审计日志

---

## 6. 监控诊断系统

### 6.1 性能监控需求
- **初始化耗时**：记录各模块的初始化耗时
- **内存使用**：监控 SDK 的内存使用情况
- **CPU 使用**：监控 SDK 的 CPU 使用情况
- **网络请求**：监控网络请求的耗时和成功率

### 6.2 异常监控需求
- **异常捕获**：自动捕获和记录所有异常
- **异常分类**：按类型和严重程度分类异常
- **异常上报**：自动上报异常到监控系统
- **异常恢复**：异常发生后的自动恢复机制

### 6.3 用户行为统计
- **功能使用统计**：统计各功能模块的使用情况
- **用户路径分析**：分析用户的操作路径
- **错误率统计**：统计各功能的错误率
- **性能指标统计**：统计关键性能指标

### 6.4 调试工具需求
- **日志系统**：分级日志系统，支持动态调整日志级别
- **调试面板**：开发阶段的调试信息面板
- **性能分析**：性能瓶颈分析工具
- **网络抓包**：网络请求的调试和分析工具

---

## 7. 版本管理系统

### 7.1 版本兼容性需求
- **依赖版本检查**：检查模块间的版本依赖关系
- **向后兼容性**：确保新版本对旧版本的兼容
- **API 废弃管理**：管理废弃 API 的生命周期
- **版本冲突解决**：自动解决版本冲突问题

### 7.2 升级迁移需求
- **数据迁移**：版本升级时的数据迁移
- **配置迁移**：配置格式变更时的自动迁移
- **API 迁移**：废弃 API 的自动迁移提示
- **回滚机制**：升级失败时的回滚机制

### 7.3 版本信息管理
- **版本号规范**：统一的版本号命名规范
- **版本信息查询**：查询各模块的版本信息
- **版本更新通知**：版本更新的通知机制
- **版本历史记录**：完整的版本变更历史

---

## 8. 数据持久化系统

### 8.1 状态持久化需求
- **关键状态保存**：保存 SDK 和模块的关键状态
- **配置信息缓存**：缓存配置信息到本地
- **用户数据保存**：保存用户相关的数据
- **临时数据管理**：管理临时数据的生命周期

### 8.2 数据存储方案
- **DataStore 使用**：使用 Android DataStore 进行数据存储
- **数据加密**：敏感数据的加密存储
- **数据压缩**：大数据的压缩存储
- **数据清理**：过期数据的自动清理

### 8.3 离线支持需求
- **离线模式**：支持网络不可用时的离线模式
- **数据同步**：网络恢复后的数据同步
- **冲突解决**：数据冲突的解决策略
- **缓存策略**：智能的数据缓存策略

---

## 9. 模块间通信系统

### 9.1 事件总线需求
- **事件发布订阅**：支持事件的发布和订阅机制
- **事件类型管理**：统一的事件类型定义和管理
- **事件优先级**：支持事件的优先级处理
- **事件持久化**：重要事件的持久化存储

### 9.2 数据共享需求
- **跨模块数据共享**：模块间的数据共享机制
- **数据同步**：共享数据的同步更新
- **数据隔离**：模块间数据的安全隔离
- **数据权限**：数据访问的权限控制

### 9.3 生命周期感知通信
- **生命周期绑定**：通信与组件生命周期绑定
- **自动取消**：组件销毁时自动取消通信
- **内存泄漏防护**：防止通信导致的内存泄漏
- **异步处理**：支持异步的通信处理

---

## 10. 回调接口设计

### 10.1 对外回调接口
```kotlin
// SDK 初始化回调
interface HiGameInitCallback {
    fun onInitSuccess()
    fun onInitFailure(error: HiGameError)
}

// 登录回调
interface HiGameLoginCallback {
    fun onLoginSuccess(userInfo: HiGameUserInfo)
    fun onLoginFailure(error: HiGameError)
    fun onLoginCancel()
}

// 支付回调
interface HiGamePayCallback {
    fun onPaySuccess(orderInfo: HiGameOrderInfo)
    fun onPayFailure(error: HiGameError)
    fun onPayCancel()
}

// 分享回调
interface HiGameShareCallback {
    fun onShareSuccess(platform: String)
    fun onShareFailure(error: HiGameError)
    fun onShareCancel()
}

// 统一错误信息
data class HiGameError(
    val code: Int,
    val message: String,
    val cause: Throwable? = null
)
```

### 10.2 内部回调接口
```kotlin
// 模块初始化回调（内部使用）
internal interface HiGameModuleCallback {
    fun onModuleInitSuccess(moduleName: String)
    fun onModuleInitFailure(moduleName: String, error: Throwable)
    fun onModuleInitProgress(moduleName: String, progress: Int)
}

// 配置变更回调（内部使用）
internal interface HiGameConfigCallback {
    fun onConfigUpdated(config: HiGameConfig)
    fun onConfigValidationFailed(errors: List<String>)
}
```

### 10.3 Manager 与 Service 交互
```kotlin
// Manager 调用 Service 的内部交互示例
class HiGameLoginManager {
    fun login(showUI: Boolean, callback: HiGameLoginCallback) {
        loginService?.performLogin(showUI, object : HiGameLoginCallback {
            override fun onLoginSuccess(userInfo: HiGameUserInfo) {
                // 内部状态更新
                updateLoginState(userInfo)
                // 对外回调
                callback.onLoginSuccess(userInfo)
            }
            
            override fun onLoginFailure(error: HiGameError) {
                callback.onLoginFailure(error)
            }
            
            override fun onLoginCancel() {
                callback.onLoginCancel()
            }
        })
    }
}
```

---

## 集成方案设计

### 11.1 游戏应用集成
- **引擎适配**：适配 Cocos Creator、Unity 等游戏引擎
- **生命周期管理**：与游戏主 Activity 的生命周期协调
- **性能优化**：不影响游戏渲染性能
- **资源隔离**：SDK 资源与游戏资源隔离

### 11.2 普通 App 集成
- **Activity 集成**：在 Activity 中集成 SDK
- **Application 集成**：在 Application 中初始化 SDK
- **Fragment 支持**：支持在 Fragment 中使用 SDK
- **多进程支持**：支持多进程应用的集成

### 11.3 技术要求
- **最小 Android 版本**：API 21 (Android 5.0)
- **目标 Android 版本**：API 34 (Android 14)
- **开发语言**：Kotlin + Java 互操作
- **架构组件**：协程、StateFlow、DataStore 等

---

## 性能与质量要求

### 12.1 性能指标
- **初始化速度**：SDK 初始化时间 < 500ms
- **内存占用**：运行时内存占用 < 50MB
- **包体积**：Core 模块 AAR < 3MB
- **CPU 使用**：后台 CPU 使用率 < 1%

### 12.2 稳定性要求
- **崩溃率**：崩溃率 < 0.1%
- **ANR 率**：ANR 率 < 0.05%
- **内存泄漏**：无内存泄漏问题
- **线程安全**：所有公开接口线程安全

### 12.3 兼容性要求
- **系统版本**：支持 Android 5.0 - 14
- **设备适配**：支持主流 Android 设备
- **架构支持**：支持 ARM、ARM64、x86 架构
- **混淆兼容**：支持代码混淆和压缩

---

## 交付物清单

### 13.1 核心文件
- **hi-game-core.aar**：Core 模块 AAR 文件
- **consumer-rules.pro**：混淆规则文件
- **higame_config.json**：配置文件模板
- **README.md**：快速开始指南

### 13.2 文档资料
- **集成指南**：详细的集成步骤和配置说明
- **API 文档**：完整的 API 参考文档
- **配置指南**：配置文件的详细说明
- **最佳实践**：使用建议和最佳实践
- **FAQ 文档**：常见问题和解决方案

### 13.3 示例项目
- **基础集成示例**：最简单的集成示例
- **完整功能示例**：展示所有功能的完整示例
- **游戏集成示例**：游戏项目的集成示例
- **性能测试示例**：性能测试和优化示例

### 13.4 开发工具
- **配置生成器**：可视化配置文件生成工具
- **调试工具**：SDK 调试和诊断工具
- **性能分析工具**：性能分析和优化工具
- **测试工具**：自动化测试工具

---

## 开发计划

### 第一阶段：核心架构（2-3周）
- [ ] HiGameSDK 门面类设计和实现
- [ ] HiGameConfig JSON 配置解析系统
- [ ] KSP 编译时代码生成框架
- [ ] 基础服务接口定义
- [ ] 单元测试框架搭建

### 第二阶段：核心功能（3-4周）
- [ ] 服务发现和管理系统
- [ ] 状态管理和监听系统
- [ ] 配置管理和热更新
- [ ] 安全机制和权限控制
- [ ] 监控诊断系统

### 第三阶段：高级功能（3-4周）
- [ ] 版本管理和兼容性检查
- [ ] 数据持久化系统
- [ ] 事件总线和模块间通信
- [ ] 性能优化和内存管理
- [ ] 错误处理和恢复机制

### 第四阶段：测试优化（2-3周）
- [ ] 完整功能测试
- [ ] 性能压力测试
- [ ] 兼容性测试
- [ ] 安全性测试
- [ ] 文档和示例完善

---

## 14. 使用示例

### 14.1 开发者使用示例
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. 初始化 SDK（只需一次）
        HiGameSDK.getInstance().initialize(this, object : HiGameInitCallback {
            override fun onInitSuccess() {
                Log.d("HiGame", "SDK 初始化成功")
                // 可以开始使用各功能
            }
            
            override fun onInitFailure(error: HiGameError) {
                Log.e("HiGame", "SDK 初始化失败: ${error.message}")
            }
        })
    }
    
    // 2. 使用登录功能
    private fun performLogin() {
        HiGameSDK.getInstance().login(true, object : HiGameLoginCallback {
            override fun onLoginSuccess(userInfo: HiGameUserInfo) {
                Log.d("Login", "登录成功: ${userInfo.nickname}")
            }
            
            override fun onLoginFailure(error: HiGameError) {
                Log.e("Login", "登录失败: ${error.message}")
            }
            
            override fun onLoginCancel() {
                Log.d("Login", "用户取消登录")
            }
        })
    }
    
    // 3. 使用支付功能
    private fun performPay() {
        val payInfo = HiGamePayInfo(
            productId = "product_001",
            amount = 9.99,
            currency = "CNY"
        )
        
        HiGameSDK.getInstance().pay(payInfo, object : HiGamePayCallback {
            override fun onPaySuccess(orderInfo: HiGameOrderInfo) {
                Log.d("Pay", "支付成功: ${orderInfo.orderId}")
            }
            
            override fun onPayFailure(error: HiGameError) {
                Log.e("Pay", "支付失败: ${error.message}")
            }
            
            override fun onPayCancel() {
                Log.d("Pay", "用户取消支付")
            }
        })
    }
    
    // 4. 检查登录状态
    private fun checkLoginStatus() {
        if (HiGameSDK.getInstance().isLoggedIn()) {
            Log.d("Login", "用户已登录")
        } else {
            Log.d("Login", "用户未登录")
        }
    }
}
```

### 14.2 三层委托架构流程
```
开发者调用 -> HiGameSDK.getInstance().initialize()
           -> HiGameSDKManager.getInstance().initialize()
           -> HiGameConfigManager.loadConfig()
           -> HiGameServiceRegistry.discoverServices()
           -> 各功能Manager初始化
           -> 回调 onInitSuccess()

开发者调用 -> HiGameSDK.getInstance().login()
           -> HiGameSDKManager.getInstance().login()
           -> HiGameLoginManager.getInstance().login()
           -> HiGameLoginService.performLogin()
           -> UI模块显示登录界面
           -> 第三方SDK执行登录
           -> 层层回调结果给开发者
```

### 14.3 架构层次说明
```
┌─────────────────────────────────────┐
│          HiGameSDK (门面层)          │  <- 开发者唯一接触点
│  - getInstance()                    │
│  - initialize()                     │
│  - login() / pay() / share()        │
└─────────────────────────────────────┘
                    ↓ 委托调用
┌─────────────────────────────────────┐
│       HiGameSDKManager (管理层)      │  <- 内部协调管理
│  - 配置管理                          │
│  - 服务发现                          │
│  - 状态管理                          │
│  - 委托给具体Manager                  │
└─────────────────────────────────────┘
                    ↓ 委托调用
┌─────────────────────────────────────┐
│    各功能Manager (功能层)            │  <- 具体功能实现
│  - HiGameLoginManager               │
│  - HiGamePayManager                 │
│  - HiGameShareManager               │
│  - 调用Service接口                   │
└─────────────────────────────────────┘
                    ↓ 接口调用
┌─────────────────────────────────────┐
│      各Service实现 (服务层)          │  <- UI模块提供
│  - LoginServiceImpl                 │
│  - PayServiceImpl                   │
│  - ShareServiceImpl                 │
└─────────────────────────────────────┘
```

## 项目总结

### 15.1 核心特性
HiGame-Core SDK 作为**企业级的模块化核心框架**，具有以下核心特性：

🎯 **极简对外接口**：开发者只需调用 initialize() 和各功能 Manager  
🔧 **配置驱动**：JSON 配置文件管理所有模块参数  
🚀 **高性能**：编译时优化 + 并发初始化，启动速度快  
🛡️ **企业级**：完整的安全、监控、版本管理等特性  
📱 **跨平台**：同时支持游戏引擎和原生 App  
🔄 **内部复杂**：服务发现、状态管理、模块协调完全内部化  

### 15.2 架构优势
- **统一门面**：所有功能都通过 HiGameSDK 访问，开发者只需记住一个类
- **三层委托**：门面层→管理层→功能层→服务层，职责清晰分离
- **自动发现**：模块自动注册和发现，无需手动配置
- **统一回调**：所有功能使用一致的回调接口风格
- **企业级特性**：安全、监控、版本管理等完整支持

### 15.3 开发体验
- **学习成本极低**：只需了解 HiGameSDK.getInstance() 一个入口
- **集成超简单**：引入AAR → 初始化 → 直接使用所有功能
- **调试友好**：统一的错误码和日志系统，问题定位容易
- **扩展性强**：新增功能只需在三层中添加对应方法
- **维护性好**：每层职责单一，修改影响范围可控

### 15.4 设计模式应用
- **门面模式**：HiGameSDK 为复杂子系统提供简单统一接口
- **委托模式**：每层将具体实现委托给下一层处理
- **单例模式**：确保全局唯一实例和状态一致性
- **策略模式**：不同功能模块可以有不同的实现策略
- **观察者模式**：状态变化通过回调通知给开发者

### 15.4 应用价值
该 Core SDK 可作为：
- **游戏 SDK 基础**：为游戏 SDK 提供统一的核心能力
- **企业 SDK 平台**：为企业级 SDK 开发提供基础框架
- **模块化解决方案**：为复杂应用提供模块化管理能力
- **开发效率工具**：大幅简化多功能 SDK 的集成和使用

---

*本文档版本：v3.0 - 企业级完整版*  
*最后更新：2025年9月8日*  
*文档类型：功能需求文档*