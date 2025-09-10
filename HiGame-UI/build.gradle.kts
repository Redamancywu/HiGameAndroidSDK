plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    // Kotlin 2.0+ 必须：Compose 编译器插件
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.horizon.higame.ui"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34
        consumerProguardFiles("consumer-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // 统一 Java 编译目标到 11，避免与 Kotlin 不一致
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    // 使用 Kotlin Compose 插件后无需手动指定 compilerExtensionVersion
    // composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf(
            "-Xjvm-default=all",
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":HiGame-Core"))

    // Compose BOM（使用版本库）
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.foundation)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Compose 基础
    implementation(libs.androidx.ui)
    implementation("androidx.compose.foundation:foundation")
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    // Material3
    implementation(libs.androidx.material3)
    
    // Material Icons Extended - 包含完整的Material图标集
    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    
    // Coil - 图片加载库
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Activity Compose
    implementation(libs.androidx.activity.compose)

    // Kotlin 扩展
    implementation(libs.androidx.core.ktx)

    // 测试（可选）
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}