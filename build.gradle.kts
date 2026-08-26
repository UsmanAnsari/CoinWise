plugins {
    base
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.multiplatform.library) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
}

tasks.register("verifySharedTier") {
    group = "verification"
    description = "Fails if any shared/* module is not a KMP module."

    val sharedDir = layout.projectDirectory.dir("shared")

    inputs.files(
        sharedDir.asFileTree.matching { include("*/build.gradle.kts") })
        .withPropertyName("sharedModuleBuildFiles")

    val sharedRoot = sharedDir.asFile

    doLast {
        val offenders =
            (sharedRoot.listFiles() ?: emptyArray()).filter { it.isDirectory }.filterNot { dir ->
                    dir.resolve("build.gradle.kts").takeIf { it.exists() }?.readText()
                        ?.contains("coinwise.kmp.library") == true
                }.map { ":shared:${it.name}" }.sorted()

        if (offenders.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("These modules live under shared/ but do not apply coinwise.kmp.library:")
                    offenders.forEach { appendLine("  $it") }
                    appendLine()
                    appendLine("Apply the plugin, or move the module out of shared/.")
                })
        }
    }
}

tasks.named("check") { dependsOn("verifySharedTier") }