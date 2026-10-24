// file: settings.gradle.kts
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
// file: settings.gradle.kts
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://artifactory.2gis.dev/sdk-maven-release") } // <--- ДОБАВИТЬ ЭТУ СТРОКУ
    }
}

rootProject.name = "beyedono"
include(":app")