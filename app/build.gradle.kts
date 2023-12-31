@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.anago.spviewer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.anago.spviewer"
        minSdk = 23
        targetSdk = 34
        versionCode = 10_10_10
        versionName = "1.1.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.activity.ktx)
    implementation(libs.swiperefreshlayout)
    implementation(libs.preference)

    implementation(libs.glide)
    ksp(libs.glide.ksp)

    implementation(libs.appiconloader.glide)

    implementation(libs.fastscroll)

    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
}