# Minimal Perspective Component Module Example

This is an example module which demonstrates the bare minimum required to add a single component to the perspective
component palette.  This module project is not 'production ready', and is not intended to be the starting point for
a production-capable component module.  Instead, this module's structure is merely to demonstrate the minimal APIs
required to:

1. Define a component and its required assets
2. Register the component with the Perspective component registries, so that it can be added to a View from the designer's component palette and be usable in the designer and at runtime

To minimize risk of confusion, we use a single static javascript file as our web-scoped component implementation

## Anatomy of a Component

To build components for Perspective, it's helpful to understand what a Component is.  Fundamentally, a  _Component_ in Perspective is a React Component that meets a required property signature (specified by the Perspective component API, explained below), and has been 'registered' with the Perspective module's 'Component Registries'.  To add a component to Perspective requires two programmatic elements, and one fundamental action:

1. Element: An implementation of a React component which exists in a javascript file that is hosted by the gateway.  The component is required to have perspective-runtime-provided properties for layout and event handling.
2. Element: Meta-information about the component that identifies and describes the component type. Examples of such metainfo includes:
   * Unique component ID
   * Human-friendly component name
   * The default size of the component when added to a View
   * Default properties and/or property schema
3. Action: The registration of the component and metadata with the appropriate _Component Registries_


We'll briefly describe each of these items.

## The React Component

> Demonstrative example implementation in `gateway/src/main/resources/mounted/js/onecomponent.js`

To function in Perspective's unique layout and event handling systems, the React component requires a few properties
(*'React Props'*) provided.  This is generally accomplished by extending the `Component` class from `PerspectiveClient` object in the
browser window.  When using a front end build tool, you would import this from the
`@inductiveautomation/perspective-client` package.  **Note: The Component from PerspectiveClient is NOT the same as
a React.Component**

A `PerspectiveClient.Component` features a unique property signature to provide the functions and properties that allow the Perspective Client runtime to place the component in the appropriate location inside it's parent Layout.  For convenience, the necessary layout and event handling properties are provided by the `emit()` function that is passed into each PerspectiveClient.Component as a react property.  So applying the appropriate handling is generally as simple as making sure the the 'top-level' dom element that is rendered by your React component implementation includes the properties that result from the output of calling this function.

In this minimal and tool-less example, this is accomplished in the render function of our simple Image component:

```javascript
// create the element, and make sure the properties include the results of this.props.emit()
class Image extends PerspectiveClient.Component {
    render() {
        const { props: { url }, emit } = this.props;

        return (React.createElement("img", Object.assign({}, emit(), { src: url, alt: `image-src-${url}` })));
    }
}
```

In a 'production' project, you will likely use JSX syntax to accomplish the same result.  That might look something like this, written in our suggested production language Typescript:

```tsx
import {Component, ComponentProps} from '@inductiveautomation/perspective-client';

export class Image extends Component<ComponentProps<{ url: string }>, any> {
    render() {
        const { props: { url }, emit } = this.props;

        return (<img {...emit()} src={url} alt={`image-src-${url}`} />);
    }
}
```

## The Meta-Information

> Note: This section discusses the two minimal required meta-info types, but there are more than two types of _possible_ meta-information, generally used by more complex component types, or for special designer-handling.  To see examples of additional APIs, look at the _perspective-component_ example in the repo, which demonstrates additional APIs to provide things like special configuration UI in the designer, or special data-handling endpoints in the gateway.

Item number two for developing a Perspective Component is the creation of meta-information that the system relies on to identify the component type across the system, as well as provide default values for things like component properties and layout sizing.

There are two required types of meta-information, and three different areas we need to tell the system about one of these meta-information types.

#### Type 1 - Browser-Side `ComponentMeta`

The ComponentMeta object is something you create in the browser context once per component type, and then register with the `PerspectiveClient.ComponentRegistry`.  A minimal implementation and example of registration exists in the [onecomponent.js file](gateway/src/main/resources/mounted/js/onecomponent.js).

#### Type 2 - Designer/Gateway (Java)-Side `ComponentDescriptor`

Each component type needs to be known not just to the browser, but also for each of the Gateway and Designer scopes.  To do that, we build an instance of a `ComponentDescriptor` for each component type, and register it with the appropriately-scoped `ComponentRegistry`.

The `ComponentDescriptor` shares a small amount of information with the Browser-side ComponentMeta, most importantly the unique ID of the component, which should be *exactly* the same in each meta-info object.  In addition, the ComponentDescriptor is where we define component property schemas, placeholder icons (for the designer), and similar information used not by the web-based runtime, but instead by the gateway (server) or designer.

## The Registration of the Component and Meta-Info

There are three fundamental registries required to have a component that is available in the designer's component palette and in a project runtime:

1. Gateway scope `ComponentRegistry` - where you [register an instance of a ComponentDescriptor](gateway/src/main/java/io/ia/example/perspective/min/gateway/OneComponentGatewayHook.java).  This descriptor is primarily used to establish the property model that provides the data that is sent to the browser at project runtime.
2. Designer scope `DesignerComponentRegistry` - where you [register an instance of ComponentDescriptor](designer/src/main/java/io/ia/example/perspective/min/designer/OneComponentDesignerHook.java).  This descriptor is primarily used to add your component to the Designer's component palette for use when designing views.
3. Browser (perspective-client runtime) ComponentRegistry - where you [register an instance of `ComponentMeta`](gateway/src/main/resources/mounted/js/onecomponent.js).



## Getting Started

This is a quick-start set of requirements/commands to get this project built.

Strictly speaking, this module should be buildable without downloading or installing any additional tools.  If
build commands are executed through the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html),
it will handle downloading the appropriate versions of all tools, and then use those tools to execute the build.


> Note: the module related task are defined by the module plugin.  Check the documentation at the [Ignition Module Tool](https://github.com/inductiveautomation/ignition-module-tools) repository for more information about the tasks and configuration options.

To run the build, clone this repo and open a command line in the `perspective-component-minimal` directory, and run the `build` gradle task:

```
// on Windows
gradlew build

// on linux/osx
./gradlew build
```

### Project structure & Layout

This section provides a high-level overview of the different parts of this project.

This example module has a traditional Ignition Module project layout targeting the gateway and designer scopes, with a `common` subproject in which we can place logic that is shared by both scopes.  Currently this project is only depended on by the `gateway` project.which is shared between gateway and designer scopes.

The primary use of the `common` scope is to define `ComponentDescriptor`s for registration in the gateway and designer's respective ComponentRegistry.


```


  ├── build.gradle.kts                     // root build configuration, like a root pom.xml file
  ├── common
  │   ├── build.gradle                     // configuration for common scoped build
  │   └── src
  │       └── main/java                    // where source files live
  ├── designer
  │   ├── build.gradle
  │   └── src
  │       └── main/java
  ├── gateway
  │   ├── build.gradle
  │   └── src
  │       └── main/java
  ├── gradle                              // gradle wrapper assets to allow wrapper functionality, should be commited
  │   └── wrapper
  │       ├── gradle-wrapper.jar
  │       └── gradle-wrapper.properties
  ├── gradlew                             // gradle build script for linux/osx
  ├── gradlew.bat                         // gradle build script for windows
  ├── settings.gradle.kts                 // Gradle project structure/global configuration.

```

### Building

Building this module through the gradle wrapper is easy!

 In a bash (or any similar posix) terminal execute `./gradlew buildModule` (linux/osx).  If on windows, run
`gradle.bat buildModule`.  This will result in the appropriate gradle binaries being downloaded (match the version
 and info provided by our `wrapper` task and committed `gradle/` directory).  This will compile and assemble all jars,
 as well as execute the webpack.

 All three steps above are typically executed as part of the command `./gradlew buildSignedModule`, which is the
 main task that creates a .modl file and signs it.  To sign your module, you'll need appropriate signing certificates,
 and a configured `sign.props` file that points to those certificates'.


 ### Configuring/Customizing

How to configure and customize the build is outside the scope of this example.  We encourage you to read the docs of
the various tools used and linked above to determine the appropriate build configurations for your project.

### SDK Tips

Perspective is a fairly complex system that is seeing regular changes/additions.  While we consider the APIs 'mostly stable', there will likely be additions and/or breaking changes as it matures.  As a result, we encourage testing modules against each version of Ignition/Perspective you intend to support.

#### Standards

As of the 8.1.4 release, a `ref` is passed down to the component via `emit` (emit props).  This is required to
provide back a reference to the root element of each component, which is used internally by the Perspective module.
For this reason, the root element of any authored components cannot contain a `ref` property.  Doing so will
override the emitted ref and will not allow your component to properly display any changes to the state of its
qualities and may cause the component to throw an error.  The ref can still be accessed from the component's store,
if needed.  In addition, it is highly recommended that the root element does not change throughout the lifecycle
of the component.  For more information and an example usage, see the `MessengerComponent` from the example
components.
