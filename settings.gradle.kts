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
        mavenCentral()
        // JitPack রিপোজিটরি এখানে যুক্ত করুন
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "SecureSoftPaySDKProject"
include(":app")
include(":SecureSoftPay-SDK")