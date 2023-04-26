plugins {
    alias(libs.plugins.modl)
}

subprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://nexus.inductiveautomation.com/repository/public/")
        }
    }
}

ignitionModule {
    id.set("io.ia.examples.resource")
    name.set("Project Resource Example")
    moduleDescription.set("Example module for project resources")
    moduleVersion.set("1.0.0")
    fileName.set("ProjectResourceExample.modl")
    requiredIgnitionVersion.set(libs.versions.ignition)

    projectScopes.set(
        mapOf(
            projects.gateway.dependencyProject.path to "G",
            projects.designer.dependencyProject.path to "D",
            projects.common.dependencyProject.path to "GD",
        ),
    )
    hooks.set(
        mapOf(
            "G" to "io.ia.examples.resource.GatewayHook",
            "D" to "io.ia.examples.resource.DesignerHook",
        ),
    )
    skipModlSigning.set(true)
}

tasks {
    // set the deployModl task to post to the local gateway running in the Docker container
    // see docker-compose.yml for details
    deployModl {
        hostGateway.set("http://localhost:18088")
    }
}
