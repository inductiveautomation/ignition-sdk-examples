# Ignition SDK

## Example Modules

### Gradle Examples

##### [Perspective Component](perspective-component)
Adds component to the Perspective module's set of components, demonstrating use of various APIs in a build automation which represents one possible solution for 'production' toolchains.

##### [Perspective Minimal Component](perspective-component-minimal)
Adds a single simple component to the Perspective component palette.  This minimal example demonstrates the minimal APIs required to register a single simple component.

##### [Project Resource](project-resource)
A basic example/tour of APIs involved in the project resource system as of Ignition 8.3.

##### [WebUI Webpage](webui-webpage)
Adds a React built webpage and a corresponding nav link to the gateway application.

### Maven Examples

##### [Event Stream Source](event-stream-source)
Creates a source that for a given list of comma separated items, will emit an event for each item in the list every second.

##### [Event Stream Handler](event-stream-handler)
Creates a handler that will listen for events from the Event Stream and write the payload to file.

##### [Expression Function](expression-function)
Creates an exampleMultiply expression that can be used by other components, such as expression tags. The example expression is located under the Extended expression category.

##### [Gateway Network/Get Remote Logs](gateway-network-function)
Requires two Gateways connected via the gateway network. The module must also be installed on both Gateways. This module adds a system.example.getRemoteLogEntries script function that can retrieve console log entries from a remote Gateway over the gateway network. Also adds a Gateway Task type that can retrieve a remote gateway’s wrapper log and save as a local file.

##### [Managed Tag Provider](managed-tag-provider)
Shows how to implement a Managed Tag Provider, to allow easy control of Ignition tags from an external program or data.

##### [OPC UA Device](opc-ua-device)
Creates an example device in the Gateway. The device will create tags that are visible under the local OPC-UA server.

##### [Report Component](report-component)
Adds a Smiley shaped component to the Report Designer.

##### [Report Datasource](report-datasource)
> [!WARNING]
> This example is pending an update to the SDK. Will be **FAULTED** when installed on the Gateway

Adds a datasource to the report designer that can retrieve JSON data via a REST call to a website.

##### [Scripting Function (RPC)](scripting-function)
Adds a system.example.multiply script that can be executed from both a client and a Gateway. Also demonstrates how the client can call a method in the Gateway via RPC.

##### [Secret Provider](secret-provider)
Adds a Secret Provider that allows you to store and retrieve secrets in the Gateway. The secrets are stored in a Mongo DB backend.

##### [Slack Alarm Notification](slack-alarm-notification)
Adds a Slack Alarm Notification type that handles alarm notifications through Slack's outgoing webhooks.

##### [User Source Profile](user-source-profile)
Adds a User Source Profile that allows you to manage users and roles in the Gateway. Users and roles stored in a Mongo DB backend.

##### [Vision Component](vision-component)
Creates a Hello World component that can be dragged onto a window in the Designer.

## General Requirements

These requirements generally apply to both Gradle and Maven build tools.

* Java Development Kit (JDK) 17 installed. You can download it on the [Java SDK Downloads](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html) page.  Licensed/TCK tested JDK vendors such as Adoptium, Azul Zulu, etc., are generally suitable JDKs as well.
* A running, 8.3.0+ version of Ignition to test your module in. If you don't already have Ignition installed head to the Inductive Automation [downloads](https://www.inductiveautomation.com/downloads/) page, download the correct package for your system and follow the installation instructions to get a gateway up and running.
* For development convenience, you may want to allow unsigned modules. Open the `ignition.conf` file in the `data/` directory, then in the `wrapper.java.additional` section add a line like: `wrapper.java.additional.7=-Dignition.allowunsignedmodules=true` (the index does not matter).

## The Module Build System

These examples use either Maven and our Maven Plugin, or Gradle and our [Gradle Plugin](https://github.com/inductiveautomation/ignition-module-tools).  Both tools are mature and capable, offering different tradeoffs in terms of performance, ease of customization, language support, etc.  If you prefer XML configuration, take a look at Maven.  If you prefer declarative programming-language-based configuration, check out Gradle.  Inductive Automation uses Gradle to build Ignition and our own modules with the same open source plugin linked above.

The ignition-maven-plugin is available through our [Nexus Repository](https://nexus.inductiveautomation.com/repository/inductiveautomation-releases/) (see examples for how to add to dependency sources).

The Gradle Plugin is published to the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/io.ia.sdk.modl), and may be used simply by applying the plugin to your Gradle project.

## Getting Started

* Once you have configured your developer gateway, make sure [git](https://git-scm.com/downloads) is installed and clone this repo to a directory of your choice:
  `git clone https://github.com/inductiveautomation/ignition-sdk-examples.git`

* Using your IDE of choice, you should be able to create or open any of these included Example Modules through the _pom.xml_ or _settings.gradle.kts_, file located in the root of each example.  Upon importing this project into your IDE, it should download (if auto-import is on) necessary dependencies from the Inductive Automation artifact repository. Dependencies are managed through Maven and are cached to your local environment after they are downloaded.

## Running Maven Examples

* First, make sure Maven 3.+ installed. Linux users can use their package manager to install at the command line (ex: `sudo apt-get install maven`), and similarly OSX users using brew can `brew install maven`. Windows users can install via [Chocolatey](https://chocolatey.org/) (`choco install maven`) or by downloading the installer at the [Maven downloads](http://maven.apache.org/download.cgi_) page.

* Once all dependencies are cached, you should be able to run `mvn package` in any of the examples to generate the *.modl* file (which will be created in the `build\target\` directory of the example).  The modl file is the Ignition module file you install to the Dev Mode Ignition in `Config > Modules` in your browser's Gateway page (generally found at `http://localhost:8088/main`). Alternately, if on a Unix system, you can use the `buildall.sh` file in the base directory to build all modules.

* Then, from the Ignition gateway web interface, head to Configure -> Modules, and scroll down to install any of your built modules from the `/module/module-build/` directory.

## Running Gradle Examples

For instructions on how to build with the Gradle plugin, take a look at the documentation on the [Gradle Plugin](https://github.com/inductiveautomation/ignition-module-tools/tree/master/gradle-module-plugin) repository.

## Javadocs
Head over to our [wiki page](https://github.com/inductiveautomation/ignition-sdk-examples/wiki/Javadocs-&-Notable-Api-Changes) for a listing of 8.0+ Javadocs.
