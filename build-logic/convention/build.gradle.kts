plugins {
    `kotlin-dsl`
}

group = "com.uansari.coinwise.buildlogic"

kotlin {
    jvmToolchain(21)
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "coinwise.android.library"
            implementationClass = "com.uansari.coinwise.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "coinwise.android.compose"
            implementationClass = "com.uansari.coinwise.AndroidComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "coinwise.android.feature"
            implementationClass = "com.uansari.coinwise.AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "coinwise.android.hilt"
            implementationClass = "com.uansari.coinwise.AndroidHiltConventionPlugin"
        }
        register("kmpLibrary") {
            id = "coinwise.kmp.library"
            implementationClass = "com.uansari.coinwise.KmpLibraryConventionPlugin"
        }
        register("testing") {
            id = "coinwise.testing"
            implementationClass = "com.uansari.coinwise.TestingConventionPlugin"
        }
    }
}