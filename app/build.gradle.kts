plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.ksp)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.quick.liveswitcher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.quick.liveswitcher"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    signingConfigs {
        create("packJKS"){
            storeFile = file("./keystore-3588-n")
            storePassword = "jd123456"
            keyAlias = "jd123456"
            keyPassword = "jd123456"
        }
    }


    buildTypes {
        val signJKS = signingConfigs.getByName("packJKS")
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signJKS
        }
        debug {
            signingConfig = signJKS
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    room {
        schemaDirectory ("$projectDir/schemas")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar","*.aar"))))
    //mmkv
    implementation(libs.tencent.mmkv)
    //http(okhttp&retrofit)
    implementation(libs.bundles.http)
    //easypermission
    implementation(libs.easypermissions)
    //room
    ksp(libs.room.compiler)
    implementation(libs.bundles.room)
    //flex布局
    implementation(libs.flexbox)
    //glide
    implementation(libs.glide)
}