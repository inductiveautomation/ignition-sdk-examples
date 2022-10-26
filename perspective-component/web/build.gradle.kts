import com.github.gradle.node.yarn.task.YarnTask
import com.github.gradle.node.npm.task.NpmTask

plugins {
    java
    id("com.github.node-gradle.node") version("3.2.1")
}
// define a variable that describes the path to the mounted gateway folder, where we want to put things eventually
val projectOutput: String by extra("$buildDir/generated-resources/")

// configurations on which versions of Node, Npm, and Yarn the gradle build should use.  Configuration provided by/to
// the gradle node plugin that"s applied above (com.moowork.node)
node {
    version.set("16.15.0")
    yarnVersion.set("1.22.18")
    npmVersion.set("8.5.5")
    download.set(true)
    nodeProjectDir.set(file(project.projectDir))

}

// define a gradle task that will install our npm dependencies, extends the YarnTask provided by the node gradle plugin
val yarnPackages by tasks.registering(YarnTask::class) {

    description = "Executes 'yarn' at the root of the web/ directory to install npm dependencies for the yarn workspace."
    // which yarn command to execute
    args.set(listOf("install", "--verbose"))

    // set this tasks "inputs" to be any package.json files or yarn.lock files that are found in or below our current
    // folder (project.projectDir).  This lets the build system avoid repeatedly trying to reinstall dependencies
    // which have already been installed.  If changes to package.json or yarn.lock are detected, then it will execute
    // the install task again.
    inputs.files(
        fileTree(project.projectDir).matching {
            include("**/package.json", "**/yarn.lock")
        }
    )

    // outputs of running 'yarn install'
    outputs.dirs(
        file("node_modules"),
        file("packages/client/node_modules"),
        file("packages/designer/node_modules")
    )

    dependsOn("${project.path}:yarn", ":web:npmSetup")
}

// define a gradle task that executes an npm script (defined in the package.json).
val webpack by tasks.registering(NpmTask::class) {
    group = "Ignition Module"
    description = "Runs 'npm run build', executing the build script of the web project's root package.json"

    // same as running "npm run build" in the ./web/ directory.
    args.set(listOf("run", "build"))

    // we require the installPackages to be done before the npm build (which calls webpack) can run, as we need our dependencies!
    dependsOn(yarnPackages)

    // we should re-run this task on consecutive builds if we detect changes to any non-generated files, so here we
    // define that we wish to have all files -- except those excluded -- as input dependencies for this task.
    inputs.files(project.fileTree("packages").matching {
        exclude("**/node_modules/**", "**/dist/**", "**/.awcache/**", "**/yarn-error.log")
    }.toList())

    // the outputs of this task include where we place the final files for use in the module, as well as the local
    // temporary "dist" folders.  Defining these outputs gives the build enough awareness to avoid running this
    // task if it"s already been executed, the outputs are where they are expected, and there have been no changes to
    // inputs.
    outputs.files(fileTree(projectOutput))
}

// task to delete the dist folders
val deleteDistFolders by tasks.registering(Delete::class) {
    delete(file("packages/designer/dist/"))
    delete(file("packages/client/dist/"))
}

tasks {
    processResources {
        dependsOn(webpack, yarnPackages)
    }

    clean {
        // makes the "built in" clean task execute the deletion tasks
        dependsOn(deleteDistFolders)
    }
}


val deepClean by tasks.registering {
    doLast {
        delete(file("packages/designer/node_modules"))
        delete(file("packages/designer/.gradle"))
        delete(file("packages/client/node_modules"))
        delete(file("packages/client/.gradle"))
        delete(file(".gradle"))
        delete(file("node_modules"))
    }

    dependsOn(project.tasks.named("clean"))
}

// make sure the gateway project doesn't process resources until the webpack task is done.
project(":gateway")?.tasks?.named("processResources")?.configure {
    dependsOn(webpack)
}


sourceSets {
    main {
        output.dir(projectOutput, "builtBy" to listOf(webpack))
    }
}
