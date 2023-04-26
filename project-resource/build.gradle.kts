import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

version = "0.0.1"

ignitionModule {
    id.set("io.ia.examples.resource")
    name.set("Project Resource Example")
    moduleDescription.set("Example module for project resources")
    val buildNumber = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyDDDHHmm"))
    moduleVersion.set("${project.version}.$buildNumber")
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
            "io.ia.examples.resource.GatewayHook" to "G",
            "io.ia.examples.resource.DesignerHook" to "D",
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
