pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "CoinWise"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":androidApp")

include(":feature:markets", ":feature:coin", ":feature:portfolio", ":feature:settings")

include(":core:ui", ":core:navigation")

include(":platform:security", ":platform:background", ":platform:widget")

include(
    ":shared:common", ":shared:money", ":shared:domain",
    ":shared:network", ":shared:database", ":shared:data",
    ":shared:testing", ":shared:umbrella",
)