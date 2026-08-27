package com.uansari.coinwise

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {

        require(path.startsWith(":shared:")) {
            "coinwise.kmp.library is for shared/* modules only. $path is Android-tier — use coinwise.android.library."
        }

        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("com.android.kotlin.multiplatform.library")

        val ns = coinwiseNamespace
        val compileSdkVersion = libsCatalog.version("android-compile-sdk").toInt()
        val minSdkVersion = libsCatalog.version("android-min-sdk").toInt()

        val coroutinesCore = libsCatalog.library("kotlinx-coroutines-core")
        val kotlinTest = libsCatalog.library("kotlin-test")
        val coroutinesTest = libsCatalog.library("kotlinx-coroutines-test")
        val turbineLib = libsCatalog.library("turbine")

        extensions.configure<KotlinMultiplatformExtension> {

            val androidExt =
                (this as ExtensionAware).extensions.getByType<KotlinMultiplatformAndroidLibraryExtension>()

            androidExt.namespace = ns
            androidExt.compileSdk = compileSdkVersion
            androidExt.minSdk = minSdkVersion
            androidExt.withHostTest { }

            jvmToolchain(21)

            iosArm64()
            iosSimulatorArm64()

            sourceSets.getByName("commonMain").dependencies {
                implementation(coroutinesCore)
            }
            sourceSets.getByName("commonTest").dependencies {
                implementation(kotlinTest)
                implementation(coroutinesTest)
                implementation(turbineLib)
            }
        }

        val kmpExtension = extensions.getByType<KotlinMultiplatformExtension>()

        val verify = tasks.register<VerifyKmpReadinessTask>("verifyKmpReadiness") {
            group = "verification"
            description = "Fails if shared code references platform-only APIs."

            modulePath.set(path)
            commonMainSources.from(layout.projectDirectory.dir("src/commonMain/kotlin"))
            bannedPrefixes.set(BANNED_IMPORT_PREFIXES)
            allowedPrefixes.set(ALLOWED_IMPORT_PREFIXES)
            report.set(layout.buildDirectory.file("reports/kmp-boundary.txt"))

            nonJvmTargetNames.set(
                provider {
                    kmpExtension.targets.filter {
                        it.platformType.name !in setOf(
                            "jvm", "androidJvm", "common"
                        )
                    }.map { it.name }
                })
        }

        tasks.matching { it.name == "check" }.configureEach { dependsOn(verify) }
    }
}