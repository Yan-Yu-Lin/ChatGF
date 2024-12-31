    import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
    import java.util.Properties
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

            val keystoreFile = project.rootProject.file("local.properties")
            val properties = Properties()
            properties.load(keystoreFile.inputStream())

            //return empty key in case something goes wrong
            val api_key = properties.getProperty("API_KEY") ?: ""

            buildFeatures {
                buildConfig = true
            }

            buildConfigField(
                "String",
                "API_KEY",
                "\"$api_key\""
            )
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

    }


    dependencies {

        // 你原本的 dependencies (依你提供的 libs.* 為準)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        implementation("com.airbnb.android:lottie:5.2.0")


        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)

        // 1. 引入 openai-client-bom
        implementation(platform("com.aallam.openai:openai-client-bom:3.8.2"))

        // 2. 加入 openai-client (不指定版本，讓 BOM 幫你管理)
        implementation("com.aallam.openai:openai-client")

        // 3. 指定 ktor engine (OkHttp)
        runtimeOnly("io.ktor:ktor-client-okhttp")

        // (可選) 如果需要協程
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

        implementation("com.google.code.gson:gson:2.10.1")
        implementation("androidx.recyclerview:recyclerview:1.3.2")
    }
