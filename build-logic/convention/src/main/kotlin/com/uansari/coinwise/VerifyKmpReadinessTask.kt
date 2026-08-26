package com.uansari.coinwise

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class VerifyKmpReadinessTask : DefaultTask() {

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val commonMainSources: ConfigurableFileCollection

    @get:Input
    abstract val bannedPrefixes: ListProperty<String>

    @get:Input
    abstract val allowedPrefixes: ListProperty<String>

    @get:Input
    abstract val nonJvmTargetNames: ListProperty<String>

    @get:Input
    abstract val modulePath: Property<String>

    @get:OutputFile
    abstract val report: RegularFileProperty

    @TaskAction
    fun verify() {
        val problems = mutableListOf<String>()

        // ── Check 1: at least one non-JVM target ──────────────────
        // Without one, commonMain may fold into the Android compilation,
        // where the JDK is present, and every other guarantee is void.
        if (nonJvmTargetNames.get().isEmpty()) {
            problems += "${modulePath.get()} declares no non-JVM target. " + "commonMain cannot be trusted to be platform-neutral."
        }

        // ── Check 2: banned imports in commonMain ─────────────────
        val banned = bannedPrefixes.get()
        val allowed = allowedPrefixes.get()
        val importRegex = Regex("""^\s*import\s+([\w.]+)""")

        commonMainSources.asFileTree.matching { include("**/*.kt") }.forEach { file ->
                file.readLines().forEachIndexed { index, line ->
                    val imported =
                        importRegex.find(line)?.groupValues?.get(1) ?: return@forEachIndexed
                    val isBanned = banned.any { imported.startsWith(it) }
                    val isAllowed = allowed.any { imported.startsWith(it) }
                    if (isBanned && !isAllowed) {
                        problems += "${file.path}:${index + 1}  banned import '$imported'"
                    }
                }
            }

        val outFile = report.get().asFile
        outFile.parentFile.mkdirs()

        if (problems.isEmpty()) {
            outFile.writeText("OK — ${modulePath.get()} boundary verified\n")
            return
        }

        outFile.writeText(problems.joinToString("\n"))
        throw GradleException(
            buildString {
                appendLine("KMP boundary violated in ${modulePath.get()}:")
                problems.forEach { appendLine("  $it") }
                appendLine()
                appendLine("Shared modules compile against the common Kotlin stdlib only.")
                appendLine("Platform code belongs in platform/* behind an interface,")
                appendLine("or in this module's androidMain/iosMain source set.")
            })
    }
}