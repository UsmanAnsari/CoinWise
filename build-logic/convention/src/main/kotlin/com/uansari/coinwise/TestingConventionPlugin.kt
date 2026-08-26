package com.uansari.coinwise

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class TestingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        dependencies {
            add("testImplementation", libsCatalog.library("junit"))
            add("testImplementation", libsCatalog.library("assertk"))
            add("testImplementation", libsCatalog.library("kotlinx-coroutines-test"))
            add("testImplementation", libsCatalog.library("robolectric"))
        }
    }
}