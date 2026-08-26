package com.uansari.coinwise

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("coinwise.android.library")
        pluginManager.apply("coinwise.android.compose")
        pluginManager.apply("coinwise.android.hilt")

        dependencies {
            add("implementation", project(":shared:domain"))
            add("implementation", project(":core:ui"))
            add("implementation", project(":core:navigation"))
            add("implementation", libsCatalog.library("androidx-lifecycle-viewmodel-compose"))
            add("implementation", libsCatalog.library("androidx-lifecycle-runtime-compose"))
            add("implementation", libsCatalog.library("hilt-navigation-compose"))
        }
    }
}