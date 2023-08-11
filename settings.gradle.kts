pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        jcenter()
        maven(
            "https://jitpack.io"
        )
        mavenCentral()
    }
}

rootProject.name = "DatingApp"
include(":app")
 