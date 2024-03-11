import java.util.concurrent.TimeUnit
import io.ia.sdk.gradle.modl.task.Deploy

plugins {
    base
    // the ignition module plugin: https://github.com/inductiveautomation/ignition-module-tools
    id("io.ia.sdk.modl") version("0.1.1")
}

allprojects {
    version = "0.0.1-SNAPSHOT"
    group = "io.ia.example.perspective.min"
}

ignitionModule {
    // name of the .modl file to build
    fileName.set("OneComponent")

    // module xml configuration
    name.set("OneComponent")
    id.set("io.ia.example.perspective.min")
    moduleVersion.set("${project.version}")
    moduleDescription.set("A module that adds components to the Perspective module.")
    requiredIgnitionVersion.set("8.1.8")
    requiredFrameworkVersion.set("8")
    // says 'this module is free, does not require licensing'.  Defaults to false, delete for commercial modules.
    freeModule.set(true)
    license.set("license.html")

    // If we depend on other module being loaded/available, then we specify IDs of the module we depend on,
    // and specify the Ignition Scope(s) that apply. "G" for gateway, "D" for designer, "C" for VISION client
    // (this module does not run in the scope of a Vision client, so we don't need a "C" entry here)
    moduleDependencies.putAll(
        mapOf(
            "com.inductiveautomation.perspective" to "GD"
        )
    )

    // map of 'Gradle Project Path' to Ignition Scope in which the project is relevant.  This is is combined with
    // the dependency declarations within the subproject's build.gradle.kts in order to determine which
    // dependencies need to be bundled with the module and added to the module.xml.
    projectScopes.putAll(
        mapOf(
            ":gateway" to "G",
            ":common" to "DG",
            ":designer" to "D"
        )
    )

    // 'hook classes' are the things that Ignition loads and runs when your module is installed.  This map tells
    // Ignition which classes should be loaded in a given scope.
    hooks.putAll(
        mapOf(
            "io.ia.example.perspective.min.gateway.OneComponentGatewayHook" to "G",
            "io.ia.example.perspective.min.designer.OneComponentDesignerHook" to "D"
        )
    )
}


val deepClean by tasks.registering {
    dependsOn(allprojects.map { "${it.path}:clean" })
    description = "Executes clean tasks and remove node plugin caches."
    doLast {
        delete(file(".gradle"))
    }
}
