# Immutable Project Module Example

As of 8.3.0, modules can dynamically import, update, delete projects that cannot be edited in a designer. This is a simple example of importing a project using one of the template Perspective projects.

## Getting Started

This is a quick-start set of requirements/commands to get this project built.

Strictly speaking, this module should be buildable without downloading or installing any additional tools.  If
build commands are executed through the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html),
it will handle downloading the appropriate versions of all tools, and then use those tools to execute the build.


> Note: the module related task are defined by the module plugin.  Check the documentation at the [Ignition Module Tool](https://github.com/inductiveautomation/ignition-module-tools) repository for more information about the tasks and configuration options.

To run the build, clone this repo and open a command line in the `immutable-project` directory, and run the `build` gradle task:

```
// on Windows
gradlew.bat build

// on linux/osx
./gradlew build
```

### Example Structure & Layout

The entire example consists of only two major parts. ImmutableProjectGatewayHook.java and the sampleimmutableproject.zip

```
└── gateway
       └── src/main/java/
               ├── io.ia.example.immutableproject
               │        └── ImmutableProjectGatewayHook.java
               │
               └── resources
                        └── sampleimmutableproject.zip
```

The ImmutableProjectGatewayHook.java extends off AbstractGatewayModuleHook and on startup imports the project in 3 basic steps:
1. Create an input stream to read the projext export sampleimmutableproject.zip from the resources root
2. Create a ProjectImport object using ProjectFileUtil.importFromZip()
3. Import the project object into the gateway as an immutable project via gatewayContext.getProjectManager().addImmutableProject()