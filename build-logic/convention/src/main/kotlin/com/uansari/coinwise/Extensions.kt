package com.uansari.coinwise

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libsCatalog: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun VersionCatalog.library(alias: String) = findLibrary(alias).get()

fun VersionCatalog.version(alias: String) = findVersion(alias).get().requiredVersion

fun VersionCatalog.bundle(alias: String) = findBundle(alias).get()

internal val Project.coinwiseNamespace: String
    get() {
        val modulePath = path.split(":").drop(1).joinToString(".") { it.replace("-", "") }
        return if (modulePath.isNotEmpty()) "com.uansari.coinwise.$modulePath" else "com.uansari.coinwise"
    }

internal val BANNED_IMPORT_PREFIXES = listOf(
    "java.",
    "javax.",
    "android.",
    "androidx.",
    "dagger.",
    "kotlinx.coroutines.android",
    "okhttp3.",
    "retrofit2.",
    "com.google.android",
)

internal val ALLOWED_IMPORT_PREFIXES = listOf(
    "androidx.room",
    "androidx.room3",
    "androidx.sqlite",
    "androidx.datastore",
    "androidx.paging",
    "androidx.annotation",
)