plugins {
    alias(libs.plugins.android.application)
    id("coinwise.android.compose")
    id("coinwise.android.hilt")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.feature.markets)
    implementation(projects.feature.coin)
    implementation(projects.feature.portfolio)
    implementation(projects.feature.settings)

    implementation(projects.platform.security)
    implementation(projects.platform.background)
    implementation(projects.platform.widget)

    implementation(projects.shared.data)

    implementation(libs.androidx.activity.compose)
}

android {
    namespace = "com.uansari.coinwise"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "com.uansari.coinwise"
        minSdk = libs.versions.android.min.sdk.get().toInt()
        targetSdk = libs.versions.android.target.sdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}