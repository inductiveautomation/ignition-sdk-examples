# Web subproject #

This folder contains all the Typescript source files for the custom component set.  This readme contains information
about the tools/requirements used to build the final JS that can be used for perspective.  It's important to note that
there are many options for assembling frontend (javascript, css) files.  These are just examples, and it's up to the
developer to learn them, or choose your own alternatives.  The important part is that you generate js that is consistent
with the APIs provided by the browser-side perspective runtime, and that this js is available to the gateway at
runtime, which is accomplished through the component registry and descriptors demonstrated in the gateway and common
projects.

## Requirements ##

There are two different ways to build these component resources.  The easiest is to simply allow Gradle to handle the build by
running `./gradlew :web:build` (macOs/linux) or `gradlew.bat :web:build` (windows) from the root of the project.  Doing this will
download Node, Npm, Yarn, with versions set to those specified in the `web/build.gradle` configuration file.

It will then execute the typescript compilation using these downloaded binaries.

Alternatively, the packages can be built at the commandline, but require locally (user) installed versions of the
following dependencies:

* Node JS
* Npm (the Node Package Manager)
* Typescript
* Webpack
* Lerna
* Yarn

Versions of these dependencies are defined in the `package.json` files found in the web/ directory, generally as  _devDependencies_ section.  The root package.json defines shared dev dependency configurations used by the subprojects in 'web/packages/'

The suggested install route for these is to install the latest LTS version of NPM (which will also install Node) as per
the typical npm install route, and then use npm itself to install the other dependencies.

With NPM installed, the following command will install the remaining dependents (should not require sudo/admin privs to
succeed):

`npm i -g typescript tslint webpack lerna`

Building using these locally installed tools is described below in the 'Usage' section.

## How the Web Package are Included in the Module ##

The web files we create (js/css/etc) need to be resolvable as _resources_ by the Java Classloader.
Prior to updating the module plugin to the new open-source version (`io.ia.sdk.modl`), we used a simple copy mechanism
to get our js and css files into the _gateway_ scoped classpath.  In doing so, we simply copied the files into the
gateway folder's _src/main/resources/mounted/_ folder.  This is a functional solution, but is not a great practice for
a number of reasons: the risk of ending up with multiple/stale copies in your resource folder, the need to .gitignore,
and the inability to properly support Gradle's [incremental assembly](https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:up_to_date_checks)
(the gateway jar needs to get rebuilt if our web assets change, because its resources have changed), and more.

In this current project, the _web_ project itself is built into a jar file (artifact) whose contents are simply the web
resources. This _web_ artifact is a dependency of the _gateway_ subproject, which gives the gateway classloader the
same ability to resolve the resources (js/css files), without needing to mutate the gateway jar at all.


### About These Dependencies ###

Briefly - these tools serve the following purposes:

* *Lerna* - used to orchestrate the building of multiple inter-dependent node packages
* *Yarn* - used as a dependency manager, replacing `npm` as the package manager in the context of the `web/` packages being
built.
* *Webpack* - a 'bundler', which is ultimately a build tool that combines or _bundles_ the necessary files/source into something
that may be used as a `<script>` file on a web page.  Through plugins/configuration, it can strip excess/unused/unreachable
code, minify, uglify, create source maps for browser-enabled debugging, etc.  The configuration provided in this example
is a bare-minimum 'simple use' case which includes the use of the _Typescript Loader_ to manager the typescript
compilation prior to bundling.
* *Typescript* - A superset of Javascript, which allows for strong typing, fuller OOP support.  Ultimately compiles
 (sometimes called _transpiles_ ) to javascript.  Javascript version compatibility depends on configuration of
 `tsconfig.json`, and webpack.


## Directory Structure Information ##

Perspective has different 'scopes', much like Vision.  The 'client' scope refers to a perspective project running in a
web browser.  The 'designer' scope refers to a perspective project executing in the Ignition Designer's Perspective View
Workspace (perspective resource editor).  Similar to the OOP principles used in Vision, perspective's designer scope
builds on top of the client scope, adding designer-specific functionality.

These scopes are distinct in that the designer may have UI/runtime elements that are not present in the client, such as design-specific layout guides/ui,'rulers', and 'interaction delegates' implemented by advanced components and containers in order to provide additional functionality in the designer.

As a result, we have two different folders under the 'packages' directory - one for client which is loaded when a
project loads and executes once published.  The other for the designer, which depends on and may extend the client
scoped components, and exists only within the designer.

Using Lerna and Yarn allows us to resolve these dependencies locally, and allows for a more sane development experience.

Each subpackage withing `packages` has a number of files.  Here is a brief description of the file and what it does:

* *.npmrc* - Contains configuration used by npm (as well as in our case - yarn), to determine where it resolves
packages.  Notably, this is required to tell our dependency manager that dependencies containing the
`@inductiveautomation` scope prefix should resolve against the Inductive Automation node package repository, similar to
 how we provide maven artifacts.
 * *webpack.config.js* - configuration file used for webpack, bearing the default webpack name.
 * *package.json* - Where package names, versions, and dependencies are defined, including dev dependencies.  May
 optionally contain configuration for additional tools.
 * *tsconfig.json* - Contains configuration specific to the typescript compiler as well as (optionally) configuration
 for typescript related build plugins/tools
 * *yarn.lock* - a dependency 'lock file' - which is used to 'lock' the dependency structure into specific versions and
 a statically-defined dependency graph.  By default, npm packages don't have this consistency.  This file should be
 committed and saved any time a dependency is changed/added.
 * *typescript/* - folder containing all the typescript source files.  The root `<scopename>.ts` is the 'index' or
 'entry' point of the package.  All components intended to be usable at runtime must be exported from this root index,
 otherwise it may get 'pruned' from the final javascript file.
 * *dist/* - the `distribution` or `build output` directory.  It's created and populated with the result of your
 typescript --> webpack bundling.  May be safely deleted, will be recreated on next build.

## Usage ##

If not using the gradle build (which is likely overkill if only seeking to rebuild the typescript packages), you can use
the following procedure to install dependencies and compile the files.

1. Execute `yarn` through the commandline in the `./web/` directory.  This will download and install node dependencies,
described in the package.jsons of all packages, including the perspective-client and perspective-designer dependencies.
This command only needs to be run one time initially, and then once follow any changes to dependencies in package.json,
or if node_modules folders (the local dependency cache folder) are deleted.  It only needs to be run once, in the root
of the `web` folder, and will establish the dependencies for child packages.

2. Execute `lerna run build` - which will execute the build scripts for each child package.

## Notes ##

* Typescript, webpack, etc, is not required to build a module with components for Perspective. We recommend it as best
practice, but you are free to create your javascript any way you would prefer.
* dist/ contains the final output of a build
* The webpack build finalizes with the copying of the webpacked resources from each packages `dist/` folder into the
gateway scoped `resources/mounted/js/` folder, which is where perspective will look to retrieve them.  This location is
registered as part of the GatewayHook.


## Terms ##

`package` - in NPM/Node parlance, a 'package' is a dependency that may be used by other packages, either as a global name on the page, or as a Universal Module (UMD).
