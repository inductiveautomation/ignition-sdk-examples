plugins {
    base
    // the ignition module plugin: https://github.com/inductiveautomation/ignition-module-tools
    id("io.ia.sdk.modl") version("0.5.0")
}

allprojects {
    version = "0.0.1-SNAPSHOT"
    group = "io.ia.example.immutableproject"
}

ignitionModule {
    // name of the .modl file to build
    fileName.set("ImmutableProject")

    // module xml configuration
    name.set("Immutable Project")
    id.set("io.ia.example.immutableproject")
    moduleVersion.set("${project.version}")
    moduleDescription.set("A module that adds a sample immutable project.")
    requiredIgnitionVersion.set("8.3.0")
    requiredFrameworkVersion.set("8")

    // If we depend on other module being loaded/available, then we specify IDs of the module we depend on,
    // and specify the Ignition Scope(s) that apply. "G" for gateway, "D" for designer, "C" for VISION client
    moduleDependencySpecs {
        register("com.inductiveautomation.perspective") {
            scope = "G"
            required = true
        }
    }

    // map of 'Gradle Project Path' to Ignition Scope in which the project is relevant.  This is combined with
    // the dependency declarations within the subproject's build.gradle.kts in order to determine which
    // dependencies need to be bundled with the module and added to the module.xml.
    projectScopes.putAll(
        mapOf(
            ":gateway" to "G"
        )
    )

    // 'hook classes' are the things that Ignition loads and runs when your module is installed.  This map tells
    // Ignition which classes should be loaded in a given scope.
    hooks.putAll(
        mapOf(
            "io.ia.example.immutableproject.ImmutableProjectGatewayHook" to "G"
        )
    )

    skipModlSigning.set(true)
}
