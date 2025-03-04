pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.7.3" apply false
        id("org.jetbrains.kotlin.android") version "2.0.0" apply false
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ludopatics"
include(":app")
