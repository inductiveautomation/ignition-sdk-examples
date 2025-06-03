pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        // add the IA repo to pull in the module-signer artifact.  Can be removed if the module-signer is maven
        // published locally from its source-code and loaded via mavenLocal.
        maven {
            url = uri("https://nexus.inductiveautomation.com/repository/public/")
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        mavenLocal()
        maven {
            url = uri("https://nexus.inductiveautomation.com/repository/public/")
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// this file configures settings for the gradle build tools, as well as the project structure.
// Generally this doesn't need to be altered unless you are adding/removing sub-projects.
rootProject.name = "perspective-component-minimal"


// link up our subprojects as part of this multi-project build.  Add/remove subprojects gradle path notation.
include(":gateway", ":common", "designer")
