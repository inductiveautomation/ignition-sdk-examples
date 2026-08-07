import java.util.concurrent.TimeUnit


plugins {
    base
    // the ignition module plugin: https://github.com/inductiveautomation/ignition-module-tools
    id("io.ia.sdk.modl") version("0.5.0")
    id("org.barfuin.gradle.taskinfo") version "2.1.0"
}

allprojects {
    version = "1.0.0"
    group = "org.fakester"
}

ignitionModule {
    // name of the .modl file to build
    fileName.set("RadComponents")

    // module xml configuration
    name.set("RadComponents")
    id.set("org.fakester.radcomponent")
    moduleVersion.set("${project.version}")
    moduleDescription.set("A module that adds components to the Perspective module.")
    requiredIgnitionVersion.set("8.3.0")
    requiredFrameworkVersion.set("8")
    license.set("license.html")

    // Module dependencies for Ignition 8.3+: use moduleDependencySpecs (not the deprecated
    // moduleDependencies map). "G" = gateway, "D" = designer, "C" = Vision client.
    // (this module does not run in Vision client scope, so no "C" entry)
    moduleDependencySpecs {
        register("com.inductiveautomation.perspective") {
            scope = "DG"
            required = true
        }
    }

    // map of 'Gradle Project Path' to Ignition Scope in which the project is relevant.  This is is combined with
    // the dependency declarations within the subproject's build.gradle.kts in order to determine which
    // dependencies need to be bundled with the module and added to the module.xml.
    projectScopes.putAll(
        mapOf(
            ":gateway" to "G",
            ":web" to "G",
            ":designer" to "D",
            ":common" to "GD"
        )
    )

    // 'hook classes' are the things that Ignition loads and runs when your module is installed.  This map tells
    // Ignition which classes should be loaded in a given scope.
    hooks.putAll(
        mapOf(
            "org.fakester.gateway.RadGatewayHook" to "G",
            "org.fakester.designer.RadDesignerHook" to "D"
        )
    )
    skipModlSigning.set(true)
}


val deepClean by tasks.registering {
    dependsOn(allprojects.map { "${it.path}:clean" })
    description = "Executes clean tasks and remove node plugin caches."
    doLast {
        delete(file(".gradle"))
    }
}
