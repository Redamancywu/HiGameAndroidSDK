# HiGame-Core SDK

## 概述

HiGame-Core 是一个企业级的多模块 SDK 核心库，采用三层委托架构设计，使用 Google AutoService 实现服务的自动发现和注册机制。

## 架构设计

### 三层委托架构

```
HiGameSDK (API层)
    ↓
HiGameSDKManager (管理层)
    ↓
HiGameXxxManager (功能层)
    ↓
HiGameXxxService (服务层)
```

### 服务自动发现机制

使用 Google AutoService 的 `@AutoService` 注解 + ServiceLoader 实现服务的自动注册和发现：

```kotlin
@AutoService(HiGameLoginService::class)
class CustomLoginService : HiGameLoginService {
    // 实现登录服务接口
}
```

**优势：**
- ✅ **零配置**：无需手动创建 META-INF/services/ 文件
- ✅ **编译时生成**：AutoService 在编译时自动生成配置文件
- ✅ **类型安全**：编译时检查服务接口匹配
- ✅ **简洁易用**：只需一个注解即可完成注册

## 使用方式

### 1. 初始化 SDK

```kotlin
HiGameSDK.getInstance().initialize(context) { success ->
    if (success) {
        // 初始化成功
    } else {
        // 初始化失败
    }
}
```

### 2. 使用功能模块

```kotlin
// 登录
HiGameSDK.getInstance().login(true) { result ->
    when {
        result.isSuccess -> {
            val user = result.getDataOrNull()
            // 登录成功
        }
        result.isError -> {
            // 登录失败
        }
    }
}

// 支付
val orderInfo = mapOf("orderId" to "123", "amount" to 100)
HiGameSDK.getInstance().pay(orderInfo) { result ->
    // 处理支付结果
}
```

### 3. 自定义服务实现

#### 步骤 1：实现服务接口
```kotlin
class WeChatLoginService : HiGameLoginService {
    override fun initialize(config: Map<String, Any>, callback: HiGameCallback<Boolean>) {
        // 初始化微信登录 SDK
    }
    
    override fun login(showUI: Boolean, callback: HiGameCallback<HiGameUser>) {
        // 实现微信登录逻辑
    }
    
    // ... 其他方法实现
}
```

#### 步骤 2：添加 AutoService 注解
```kotlin
@AutoService(HiGameLoginService::class)
class WeChatLoginService : HiGameLoginService {
    // 服务实现
}
```

#### 步骤 3：编译项目
AutoService 会在编译时自动生成 `META-INF/services/com.horizon.higame.core.internal.service.HiGameLoginService` 文件，内容为：
```
com.example.WeChatLoginService
```

#### 步骤 4：运行时自动发现
SDK 运行时会自动发现和加载服务，无需任何额外配置！

## 模块结构

- **api**: 对外 API 层
- **callback**: 回调接口定义
- **error**: 错误码定义
- **model**: 数据模型
- **utils**: 工具类
- **internal**: 内部实现（不对外暴露）
  - **manager**: 功能管理器
  - **service**: 服务接口
  - **config**: 配置管理
  - **state**: 状态管理
  - **data**: 数据存储
  - **security**: 安全模块
  - **monitor**: 监控模块
  - **version**: 版本管理
  - **communication**: 通信模块
- **example**: 默认服务实现示例

## 技术栈

- **Kotlin 2.0.21**
- **Google AutoService 1.1.1** (服务自动注册)
- **Coroutines + StateFlow** (响应式编程)
- **DataStore** (数据持久化)
- **ServiceLoader** (服务发现)

## AutoService vs 手动配置对比

### 使用 AutoService（推荐）
```kotlin
@AutoService(HiGameLoginService::class)
class WeChatLoginService : HiGameLoginService {
    // 实现
}
```
- ✅ 一行注解搞定
- ✅ 编译时自动生成配置
- ✅ 类型安全检查
- ✅ 支持多模块项目

### 手动配置（繁琐）
```
1. 创建 META-INF/services/ 目录
2. 创建服务接口全限定名文件
3. 在文件中写入实现类全限定名
4. 手动维护多个配置文件
5. 容易出错，难以维护
```

## 扩展开发

要扩展 SDK 功能，只需：

1. **定义新的服务接口**继承 `HiGameBaseService`
2. **创建对应的功能管理器**
3. **在 HiGameSDKManager 中添加委托方法**
4. **在 HiGameSDK 中暴露对外 API**
5. **实现具体的服务并使用 `@AutoService` 注解**

```kotlin
// 1. 定义服务接口
interface HiGameCustomService : HiGameBaseService {
    fun customMethod(callback: HiGameCallback<String>)
}

// 2. 实现服务
@AutoService(HiGameCustomService::class)
class MyCustomService : HiGameCustomService {
    override fun customMethod(callback: HiGameCallback<String>) {
        // 实现逻辑
    }
}
```

SDK 会自动处理服务的注册、发现和生命周期管理，开发者只需专注业务逻辑实现！

## 服务实现说明

当前版本（v0.0.1）提供了完整的服务接口定义和架构框架，但尚未包含具体的服务实现。开发者需要根据实际需求实现相应的服务：

### 需要实现的服务接口
- `HiGameLoginService` - 登录服务接口
- `HiGamePayService` - 支付服务接口  
- `HiGameShareService` - 分享服务接口
- `HiGameUserCenterService` - 用户中心服务接口

### 实现方式
使用 `@AutoService` 注解实现具体的服务类，SDK 会自动发现和注册这些服务实现。

**注意**：当前版本专注于核心架构搭建，后续版本将提供默认的服务实现示例。