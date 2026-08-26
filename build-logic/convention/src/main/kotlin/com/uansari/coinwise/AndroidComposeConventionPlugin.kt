package com.uansari.coinwise

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        pluginManager.withPlugin("com.android.base") {
            extensions.configure(CommonExtension::class.java) {
                buildFeatures.compose = true
            }
        }
        dependencies {
            add("implementation", platform(libsCatalog.library("compose-bom")))
            add("implementation", libsCatalog.bundle("compose-core"))
            add("implementation", libsCatalog.library("compose-ui-tooling-preview"))
            add("debugImplementation", libsCatalog.library("compose-ui-tooling"))
        }
    }
}