# Web UI Webpage (Hello Ignition) Example

This is an example module which adds a React built webpage and a corresponding nav link to the gateway application.

## Getting Started

This is a quick-start set of requirements/commands to get this project built.

Strictly speaking, this module should be buildable without downloading or installing any additional tools.  If
build commands are executed through the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html),
it will handle downloading the appropriate versions of all tools, and then use those tools to execute the build.


> Note: the module related task are defined by the module plugin.  Check the documentation at the [Ignition Module Tool](https://github.com/inductiveautomation/ignition-module-tools) repository for more information about the tasks and configuration options.

To run the build, clone this repo and open a command line in the `webui-webpage` directory, and run the `build` gradle task:

```
// on Windows
gradlew build

// on linux/osx
./gradlew build
```

This will produce a `.modl` file in the `build` directory, which can be installed on an 8.3+ gateway to add the `Web UI Webpage` category to the `Home` section of the gateway.

## Quick Tool Overview

This project uses a number of build tools in order to complete the various parts of its assembly.  It's important to note that these tools are just some example options.  You may use any tool you want (or no tool at all).  These examples use:

* [Gradle](https://gradle.org/) - the primary build tool. Most tasks executed in a typical workflow are gradle tasks.
  and 'packages' in the same git/hg repository without having to do a lot of complicated symlinking/publishing to pull in changes from one project to another.  It's mostly useful from the commandline, outside of gradle.
* [yarn](https://yarnpkg.com/) - is a javascript dependency (package) manager that provides a number of improvements
  over npm, though it shares much of the same commands and api.  Much like Ivy or Maven, yarn is used to resolve and download dependencies hosted on remotely hosted repositories.  Inductive Automation publishes our own dependencies through the
  same nexus repository system we use for other sdk artifacts.  To correctly resolve the Inductive Automation node packages,
  an `.npmrc` file needs to be added to the front end projects to tell yarn/npm where to find packages in the `@inductiveautomation` namespace.  You will find examples of these in the `web-ui/` directory.
* [Typescript](https://www.typescriptlang.org/) - the language used to write the front end parts.  Typescript is not required, but is strongly recommended.  Typescript can be thought of as modern javascript with types added (though this is a simplification). The addition of types to JS results in a far better developer experience through much better tooling
  support.  This can improve maintainability, refactoring, code navigation, bug discovery, etc.  Typescript has its own compiler which emits javascript.  This compiler is frequently paired with other build tools in a way that it emits the javascript, but
  other tools handle the actual bundling of assets, css, and other supporting dependencies.  Think of typescript as the
  java compiler without jars or resources.  It just takes typescript files in, and emits the javascript files.
* [Webpack](https://webpack.js.org/) - the 'bundler' that we use to take the javascript emitted by the typescript compiler and turn it into an actual package that includes necessary assets, dependencies, generates sourcemaps, etc.


## How it works
1. The React component that represents your page will need to be bundled into a single UMD JS file.
    - We use webpack in this example.
    - This example uses gradle to download tools necessary to build the project (node, yarn and NPM) and then runs a yarn install to install all of the dependencies listed in the package.json. After this is complete, it will use the webpack.config.js file to build the JS bundle and place it in proper location.
   

2. The file will need to be served on the gateway using the Module Resource API.
    - Files should be available at `<host gateway>/res/<module id>/<mounted folder>/<target file>`, provided they are identifiable on the module's classpath - i.e., they need to be bundled into a jar that is part of you module.  The module id is used in the url by default, but a shorter alternative may be specified by overriding `GatewayHook.getMountPathAlias()`. Similarly, the 'mounted folder' may also be specified, by overriding `GatewayHook.getMountedResourceFolder()`. See the `GatewayHook.java` file for implementation details.
   

3. The gateway hook (WebuiWebpageGatewayHook.java in this example) will need to be altered to include information about the JS module.
    - The setup method, in the Gateway hook, should create the JS module (SystemJsModule) and then define the navigation using the module (see example in WebuiWebpageGatewayHook.java)
    - The name of your exported component is important as it will be required in this step to let the application know what component in your module to look for.


> Note: the module related task are defined by the module plugin.  Check the documentation at the [Ignition Module Tool](https://github.com/inductiveautomation/ignition-module-tools) repository for more information about the tasks and configuration options.

## Additional Information
- React is required in order to inject your page / component into the gateway.
- We use webpack in this example, but feel free to use whatever bundling tool you're comfortable with.
- Bundles need to be in the UMD ([Universal Module Definition](https://github.com/umdjs/umd)) format.
- We provide a handful of libraries that are currently listed as "externals" in the webpack config. You should install them while developing locally, but they should not be bundled with your JS file as they will be provided by the gateway.
- We do have a shared component library built in React (documentation / storybook will eventually be available). You will need a .yarnrc file to direct requests for @inductiveatutomation libraries when using yarn or NPM (.yarnrc included in example)
- We also have a shared Icon library (used in example, documentation also coming)
- Included in the `web-ui` package.json is a script that will run your FE code locally for development purposes `run:dev`. This does not need to be used but is just an example of how it can be done.
  - This can be executed while in the `web-ui` folder by running `yarn run:dev`
  - This script runs a local server on port 9999 using webpack-dev-server. Instead of bundling your project, it will serve an HTML file and your bundled file so that you can develop your page.