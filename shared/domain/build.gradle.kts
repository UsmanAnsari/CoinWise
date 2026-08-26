plugins { id("coinwise.kmp.library") }

kotlin {
    sourceSets.commonMain.dependencies {
        api(projects.shared.money)
    }
}