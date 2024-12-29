import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // 如果你的 libs.versions.toml 沒有管理 coroutines，也可以直接用 id "kotlin-android" 即可
}

android {
    namespace = "com.example.chatgf"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chatgf"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // 記得保留你的 proguard 設定
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

//    defaultConfig {
//        // 讀取 local.properties 中的 key，並寫入 BuildConfig
//        val localProperties = gradleLocalProperties(rootDir)
//        val openAiKey = localProperties.getProperty("OPENAI_API_KEY") ?: ""
//
//        // 定義一個字串常數 OPENAI_API_KEY 到 BuildConfig
//        buildConfigField("String", "OPENAI_API_KEY", "\"$openAiKey\"")
//    }
}

dependencies {

    // 你原本的 dependencies (依你提供的 libs.* 為準)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // -----------------------------
    // 以下是新加的部分
    // -----------------------------

    // (1) 協程 (Coroutines)
    // 若你的 libs.versions.toml 沒有管理 coroutines，可以直接加版本號
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // (2) OpenAI Kotlin Client
    implementation("com.aallam.openai:openai-client:3.8.2")

    // (3) Ktor Engine (OkHttp) - 供 openai-client 底層做網路請求
    runtimeOnly("io.ktor:ktor-client-okhttp:3.0.2")

    // 如果你不想指定 Ktor 版本，也可用 BOM，但這樣要調整方式。
    // 這邊用最簡單、直覺的方式即可。
}
