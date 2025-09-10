# HiGame Android SDK

## 项目概述

HiGame Android SDK 是一个企业级的模块化游戏和应用开发框架，采用现代化的架构设计，为Android平台提供统一的服务管理、UI组件和功能集成解决方案。

## 版本信息

**当前版本**: v0.0.5  
**发布日期**: 2025年10月  
**版本状态**: 个人中心UI功能完整实现

## 架构设计

### 模块化架构

```
HiGame Android SDK
├── HiGame-Core          # 核心SDK模块
│   ├── 三层委托架构       # API层 → 管理层 → 功能层 → 服务层
│   ├── 服务自动发现       # AutoService + ServiceLoader
│   ├── 配置管理系统       # JSON配置 + 热更新
│   ├── 状态管理          # StateFlow + 响应式编程
│   └── 监控诊断          # 性能监控 + 异常处理
├── HiGame-UI            # UI组件库模块
│   ├── Jetpack Compose  # 现代化UI框架
│   ├── 主题系统          # 可定制的设计系统
│   ├── 通用组件          # 登录、分享、用户中心等
│   └── 响应式设计        # 多屏幕适配
└── Demo App             # 示例应用
    ├── 功能演示          # SDK功能展示
    ├── 集成示例          # 集成方式演示
    └── 最佳实践          # 开发指南
```

### 核心技术栈

| 技术组件 | 版本 | 用途 |
|---------|------|------|
| **Kotlin** | 2.0.21 | 主要开发语言 |
| **Jetpack Compose** | 2024.09.00 | UI框架 |
| **Coroutines** | 1.8.1 | 异步编程 |
| **StateFlow** | - | 响应式状态管理 |
| **AutoService** | 1.1.1 | 服务自动注册 |
| **DataStore** | 1.1.1 | 数据持久化 |
| **OkHttp/Retrofit** | 4.12.0/2.11.0 | 网络请求 |
| **Gson** | 2.11.0 | JSON解析 |

## 版本历史

## v0.0.5 版本实现内容（2025年10月）

### ✅ 新增功能

#### 个人中心UI功能完整实现
- **用户中心组件**: HiGameUserCenterView - 完整的个人信息展示界面
- **用户信息展示**: 支持头像、用户名、等级、经验值等个人信息显示
- **统计数据展示**: 游戏时长、成就数量、好友数量等统计信息
- **功能菜单**: 设置、帮助、关于等常用功能入口
- **响应式设计**: 支持手机和平板的自适应布局
- **主题适配**: 完整的深色/浅色主题支持

#### 头像图片加载优化
- **Coil图片加载**: 集成Coil库实现高效的图片异步加载
- **本地头像资源**: 创建默认头像Vector Drawable资源
- **智能加载策略**: 优先使用本地头像，支持网络头像回退
- **缓存机制**: 内置图片缓存，提升加载性能
- **错误处理**: 完善的加载失败回退机制

#### 用户数据管理
- **UserInfo数据类**: 完整的用户信息数据结构定义
- **参数化组件**: 支持动态数据绑定和展示
- **示例数据**: 提供丰富的预览示例数据
- **多场景预览**: 标准用户、高级用户、深色主题等多种预览场景

#### 代码质量提升
- **编译优化**: 解决所有编译错误和警告
- **资源管理**: 规范的Android资源文件组织
- **模块化设计**: 组件高度可复用，易于维护
- **预览支持**: 完整的Compose预览功能

## v0.0.4 版本实现内容（2025年9月）

### ✅ 新增功能

#### 分享功能完整实现
- **第二种分享样式**: HiGameShareScreenshot - 屏幕截图分享功能
- **自动截图功能**: 支持自动捕获当前屏幕或Composable内容
- **截图容器设计**: 带边框的Card容器，支持75%宽度比例缩放
- **横向分享布局**: 底部横向排列分享平台按钮
- **多平台支持**: 微信、QQ、微博、抖音、小红书、Facebook等10个主流平台
- **分享逻辑优化**: 完善的平台分享函数和错误处理机制
- **预览界面**: HiGameShareScreenshotPreview - 完整的功能展示界面

#### 分享组件优化
- **底部弹出优化**: 改进HiGameSharePreview的布局和显示效果
- **高度约束**: 添加最大高度限制，确保内容完整显示
- **文本显示**: 优化平台名称的换行和显示策略
- **空间分配**: 改进分享平台网格的空间分配和视觉效果

#### 代码质量提升
- **编译错误修复**: 解决ShareUtils中的命名冲突和引用问题
- **函数完整性**: 补充所有平台的分享函数实现
- **模块化设计**: 组件可复用，易于维护和扩展
- **错误处理**: 完善的异常处理和用户反馈机制

## v0.0.3 版本实现内容（2025年8月）

### ✅ 已完成功能

#### 1. HiGame-Core 核心架构
- **三层委托架构**: HiGameSDK → HiGameSDKManager → HiGameXxxManager → HiGameXxxService
- **服务自动发现**: 基于Google AutoService + ServiceLoader的零配置服务注册
- **配置管理系统**: JSON配置文件解析、配置缓存、模块隔离
- **状态管理**: StateFlow驱动的响应式状态管理
- **服务接口定义**: 登录、支付、分享、用户中心等核心服务接口
- **工具类库**: 日志系统、工具类、错误处理
- **网络管理**: HiGameNetworkManager - 网络状态监控和管理
- **API兼容性**: 修复Android API level兼容性问题（minSdk 21）

#### 2. HiGame-UI 组件库
- **基础UI组件**: HiGameButton、HiGameInputField、HiGameDialog等通用组件
- **布局组件**: HiGameBottomSheet、HiGameLoadingView等容器组件
- **主题系统**: HiGameMaterialAdapter - Material3主题适配器
- **登录组件**: HiGameLoginView - 完整的账号密码登录界面
- **注册组件**: HiGameRegisterView - 支持手机和邮箱注册的界面
- **分享组件**: HiGameShareView - 多平台分享功能界面
- **用户中心组件**: HiGameUserCenterView - 个人信息管理界面
- **透明Activity**: HiGameTransparentActivity - 无感知的UI展示容器
- **屏幕规范**: ScreenSpec - 统一的屏幕展示规范
- **预览支持**: 各组件的Compose预览功能

#### 3. Demo应用
- **基础框架**: 使用Jetpack Compose的示例应用
- **登录演示**: 完整的登录界面展示和功能演示
- **注册演示**: 手机和邮箱注册功能展示
- **UI预览**: 各UI组件的展示和预览

#### 4. 项目配置
- **Gradle配置**: 多模块项目配置、依赖管理
- **版本管理**: 统一的版本号管理（libs.versions.toml）
- **构建系统**: 支持Android Gradle Plugin 8.12.2
- **代码规范**: Kotlin代码风格和项目结构规范
- **权限配置**: 网络权限和网络状态权限配置
- **编译兼容**: 修复所有API兼容性问题，支持minSdk 21

### 🔧 技术特性

#### 服务自动发现机制
```kotlin
// 1. 定义服务接口
interface HiGameLoginService : HiGameBaseService {
    fun login(showUI: Boolean, callback: HiGameCallback<HiGameUser>)
}

// 2. 实现服务（使用AutoService注解）
@AutoService(HiGameLoginService::class)
class WeChatLoginService : HiGameLoginService {
    override fun login(showUI: Boolean, callback: HiGameCallback<HiGameUser>) {
        // 微信登录实现
    }
}

// 3. SDK自动发现和注册（零配置）
// AutoService在编译时自动生成META-INF/services/配置文件
// ServiceLoader在运行时自动加载服务实现
```

#### 三层委托架构
```kotlin
// API层 - 对外接口
HiGameSDK.getInstance().login(true) { result -> }

// 管理层 - 内部协调
HiGameSDKManager.getInstance().login(showUI, callback)

// 功能层 - 业务逻辑
HiGameLoginManager.getInstance().login(showUI, callback)

// 服务层 - 具体实现
loginService?.performLogin(showUI, callback)
```

#### 响应式状态管理
```kotlin
// StateFlow驱动的状态管理
class HiGameStateManager {
    private val _initState = MutableStateFlow(InitState.IDLE)
    val initState: StateFlow<InitState> = _initState.asStateFlow()
    
    // 状态变化自动通知所有订阅者
    fun updateState(newState: InitState) {
        _initState.value = newState
    }
}
```

## v0.0.2 版本实现内容（2025年7月）

### ✅ 已完成功能

#### 1. HiGame-UI 模块基础搭建
- **模块创建**: 创建HiGame-UI独立模块，专注于UI组件开发
- **Jetpack Compose集成**: 配置Compose BOM和相关依赖
- **基础组件框架**: 建立UI组件的基础架构和设计规范
- **主题系统**: 初步建立Material3主题适配系统
- **模块依赖**: 配置HiGame-UI对HiGame-Core的依赖关系

#### 2. 核心架构优化
- **服务接口完善**: 补充和优化各服务接口的定义
- **状态管理增强**: 改进StateFlow的使用和状态同步机制
- **配置系统优化**: 增强JSON配置解析和缓存机制
- **错误处理**: 完善异常处理和错误码体系

#### 3. 开发工具和规范
- **代码规范**: 建立Kotlin代码风格和项目结构规范
- **文档完善**: 补充API文档和开发指南
- **构建优化**: 优化Gradle构建配置和依赖管理

## v0.0.1 版本实现内容（2025年6月）

### ✅ 已完成功能

#### 1. 项目基础架构
- **多模块项目搭建**: 创建HiGame-Core、HiGame-UI、Demo App三个模块
- **Gradle配置**: 配置多模块项目的构建系统和依赖管理
- **版本管理**: 建立统一的版本号管理系统（libs.versions.toml）
- **项目结构**: 定义清晰的包结构和代码组织方式

#### 2. HiGame-Core 核心框架
- **SDK入口**: 创建HiGameSDK主入口类，提供统一的API接口
- **服务发现机制**: 基于Google AutoService实现零配置的服务自动注册
- **三层委托架构**: 建立API层 → 管理层 → 服务层的清晰架构
- **基础服务接口**: 定义登录、支付、分享等核心服务的接口规范
- **配置管理**: 实现JSON配置文件的解析和管理机制
- **状态管理**: 建立基于StateFlow的响应式状态管理系统

#### 3. 基础工具和组件
- **日志系统**: 实现统一的日志管理和输出机制
- **工具类库**: 提供常用的工具类和辅助函数
- **数据模型**: 定义核心数据模型和回调接口
- **错误处理**: 建立错误码体系和异常处理机制

#### 4. Demo应用
- **示例应用**: 创建基础的Demo应用展示SDK功能
- **集成示例**: 提供SDK集成和使用的示例代码
- **功能演示**: 展示核心功能的基本使用方法

### 📋 待实现功能（后续版本）

#### v0.1.0 计划
- **默认服务实现**: 提供各功能模块的默认实现示例
- **配置热更新**: 远程配置下载和动态更新
- **性能监控**: 详细的性能指标收集和上报
- **安全加固**: 数据加密、签名验证等安全特性

#### v0.2.0 计划
- **UI组件扩展**: 更多通用UI组件和动画效果
- **国际化支持**: 多语言和本地化支持
- **无障碍优化**: TalkBack等无障碍功能支持
- **测试覆盖**: 完整的单元测试和集成测试

## 快速开始

### 1. 添加依赖

```kotlin
// app/build.gradle.kts
dependencies {
    implementation(project(":HiGame-Core"))
    implementation(project(":HiGame-UI"))  // 可选，如需UI组件
}
```

### 2. 初始化SDK

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化HiGame SDK
        HiGameSDK.getInstance().initialize(this) { success ->
            if (success) {
                Log.d("HiGame", "SDK初始化成功")
            } else {
                Log.e("HiGame", "SDK初始化失败")
            }
        }
    }
}
```

### 3. 使用UI组件

```kotlin
@Composable
fun LoginScreen() {
    HiGameLoginView(
        config = LoginConfig(
            title = "登录游戏",
            enabledMethods = listOf("wechat", "qq", "guest")
        ),
        onLoginResult = { result ->
            // 处理登录结果
        }
    )
}
```

## 项目结构

```
HiGame/
├── HiGame-Core/                    # 核心SDK模块
│   ├── src/main/kotlin/com/horizon/higame/core/
│   │   ├── api/                    # 对外API接口
│   │   ├── callback/               # 回调接口定义
│   │   ├── config/                 # 配置相关
│   │   ├── error/                  # 错误码定义
│   │   ├── model/                  # 数据模型
│   │   ├── utils/                  # 工具类
│   │   └── internal/               # 内部实现（不对外暴露）
│   │       ├── manager/            # 功能管理器
│   │       ├── service/            # 服务接口
│   │       ├── config/             # 配置管理
│   │       ├── state/              # 状态管理
│   │       ├── data/               # 数据存储
│   │       ├── security/           # 安全模块
│   │       ├── monitor/            # 监控模块
│   │       ├── version/            # 版本管理
│   │       └── communication/      # 通信模块
│   └── build.gradle.kts
├── HiGame-UI/                      # UI组件库模块（计划中）
├── app/                            # Demo应用
│   ├── src/main/java/com/horizon/higamesdk/
│   │   ├── MainActivity.kt         # 主Activity
│   │   └── ui/                     # UI相关
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml          # 版本管理
├── build.gradle.kts                # 根项目配置
├── settings.gradle.kts             # 项目设置
└── README.md                       # 项目说明
```

## 开发指南

### 扩展新功能

1. **定义服务接口**（继承HiGameBaseService）
2. **创建功能管理器**（处理业务逻辑）
3. **在SDKManager中添加委托**（内部协调）
4. **在HiGameSDK中暴露API**（对外接口）
5. **实现具体服务**（使用@AutoService注解）

### 自定义UI组件

1. **继承基础组件**（使用Jetpack Compose）
2. **遵循主题系统**（使用统一的设计规范）
3. **支持配置驱动**（通过Config类配置外观和行为）
4. **添加预览功能**（使用@Preview注解）

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目地址: [https://github.com/Redamancywu/HiGameAndroidSDK](https://github.com/Redamancywu/HiGameAndroidSDK)
- 问题反馈: [Issues](https://github.com/Redamancywu/HiGameAndroidSDK/issues)

---

**注意**: 当前版本（v0.0.4）已实现完整的分享功能和UI组件库，后续版本将持续迭代优化，添加更多功能特性和最佳实践示例。