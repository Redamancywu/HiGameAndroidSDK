plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.horizon.higame_core"
    compileSdk = 36

    defaultConfig {
        minSdk = 21  // 根据需求文档要求支持 Android 5.0+
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        
        // 添加构建配置字段
        buildConfigField("String", "SDK_VERSION", "\"1.0.0\"")
        buildConfigField("boolean", "DEBUG_MODE", "false")
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "DEBUG_MODE", "true")
            isMinifyEnabled = false
        }
        release {
            buildConfigField("boolean", "DEBUG_MODE", "false")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    buildFeatures {
        buildConfig = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview"
        )
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Android 核心库
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    
    // 协程支持 (StateFlow, 并发初始化)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    
    // 生命周期组件 (StateFlow, ViewModel)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    
    // 数据持久化 (DataStore)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    
    // JSON 解析 (配置文件解析)
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)
    
    // 服务发现 (ServiceLoader + AutoService)
    implementation(libs.auto.service.annotations)
    kapt(libs.auto.service)
    
    // 加密支持 (敏感数据保护)
    implementation(libs.androidx.security.crypto)
    
    // 网络请求 (配置热更新, 监控上报)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    
    // 日志系统
    implementation(libs.timber)
    
    // 事件总线 (模块间通信)
    implementation(libs.eventbus)
    
    // 内存泄漏检测 (Debug 版本)
    debugImplementation(libs.leakcanary.android)
    
    // 单元测试
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    
    // Android 测试
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}