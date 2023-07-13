enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        // add the IA repo to pull in the module-signer artifact.
        maven {
            url = uri("https://nexus.inductiveautomation.com/repository/public/")
        }
    }
}

rootProject.name = "project-resource"

include(
    "common",
    "designer",
    "gateway",
)
