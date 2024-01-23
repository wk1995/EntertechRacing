pluginManagement {
    repositories {
        mavenCentral()
        
        maven (url="https://jitpack.io" ) //必须添加这行
        google()
        mavenLocal()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()

        maven (url="https://jitpack.io" ) //必须添加这行
        google()
        mavenLocal()
    }
}

rootProject.name = "EntertechRacing"
include(":app")
 