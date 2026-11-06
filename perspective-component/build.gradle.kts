import java.util.concurrent.TimeUnit


plugins {
    base
    // the ignition module plugin: https://github.com/inductiveautomation/ignition-module-tools
    id("io.ia.sdk.modl") version("0.1.1")
    id("com.diffplug.spotless") version("8.11.0")
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
    requiredIgnitionVersion.set("2027.2.0")
    license.set("license.html")

    // If we depend on other module being loaded/available, then we specify IDs of the module we depend on,
    // and specify the Ignition Scope that applies. "G" for gateway, "D" for designer, "C" for VISION client
    // (this module does not run in the scope of a Vision client, so we don't need a "C" entry here)
    moduleDependencies.put("com.inductiveautomation.perspective", "DG")

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

// Formats Java sources with google-java-format before they compile.
spotless {
    // The build formats the sources, so it doesn't need to check them too.
    isEnforceCheck = false

    java {
        target("*/src/**/*.java")
        googleJavaFormat("2.0.0")
    }
}

val spotlessApply = tasks.spotlessApply

subprojects {
    tasks.withType<JavaCompile>().configureEach {
        dependsOn(spotlessApply)
    }
}


tasks.register("deepClean") {
    dependsOn(allprojects.map { "${it.path}:clean" })
    description = "Executes clean tasks and remove node plugin caches."
    doLast {
        delete(file(".gradle"))
    }
}
