import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins { id("coinwise.kmp.library") }

kotlin {
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.framework {
            baseName = "SharedLogic"
            isStatic = true

            export(projects.shared.common)
            export(projects.shared.domain)
            export(projects.shared.money)
        }
    }

    sourceSets.commonMain.dependencies {
        api(projects.shared.common)
        api(projects.shared.domain)
        api(projects.shared.money)
        implementation(projects.shared.data)
    }
}