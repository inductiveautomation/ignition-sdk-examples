# Ignition SDK

## Summary of Example Modules
**AbstractTagDriverExample**
Creates an example device in the Gateway. The device will create tags that are visible under the local OPC-UA server.

**ComponentExample**
Creates a Hello World component that can be dragged onto a window in the Designer.

**expression-example**
Creates an exampleMultiply expression that can be used by other components, such as expression tags. The example expression is located under the Extended expression category.

**gateway-network-example**
Requires two Gateways connected via the gateway network. The module must also be installed on both Gateways. This module adds a system.example.getRemoteLogEntries script function that can retrieve console log entries from a remote Gateway over the gateway network. Also adds a Gateway Task type that can retrieve a remote gateway’s wrapper log and save as a local file.

**home-connect-example**
Demonstrates how to implement Gateway Status and Config pages. HomeConnect pages are added to the Gateway that configure an imaginary HomeConnect device. 

**ModbusDriverExample**
A module that provides a fully functional Modbus TCP driver.

**NotificationExample**
Adds a Console Alarm Notification type that handles alarm notifications via log messages in the console.

**report-component**
Adds a Smiley shaped component to the Report Designer.

**report-datasource-example**
Adds a datasource to the report designer that can retrieve JSON data via a REST call to a website.

**scripting-rpc-example**
Adds a system.example.multiply script that can be executed from both a client and a Gateway. Also demonstrates how the client can call a method in the Gateway via RPC.

**SimpleTagProviderExample**
Adds a DynamicTags tag provider, which includes tags that are updated every second by the module.


## Getting Started

1. You will need the Java JDK 1.8+ installed, downloadable from the [Java SDK Downloads](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html) page.

2. You will need Maven 3.+ installed.  Linux users can use their package manager to install at the command line (example for apt package management`sudo apt-get install maven`), and similarly OSX users using brew can `brew install maven`.  Windows users can install via [Chocolatey](https://chocolatey.org/) (`choco install maven`) or by downloading the installer at the [Maven downloads](http://maven.apache.org/download.cgi_) page.

1. You will want a running version of Ignition to test your module in.  If you don't already have Ignition installed head to the Inductive Automation [downloads](https://www.inductiveautomation.com/downloads/) page, download the correct package for your system and follow the installation instructions to get a gateway up and running.  

3. Once you have configured your developer gateway, make sure [git](https://git-scm.com/downloads) is installed and clone this repo to a directory of your choice :

    ```git clone https://github.com/inductiveautomation/ignition-sdk-examples.git```

4. Using your IDE of choice, you should be able to create or open any of these included Example Modules through the parent pom.xml file located in the root of each example.  Upon importing this project into your IDE, it should download (if auto-import is on) Maven dependencies from the Inductive Automation artifact repository. Dependencies are managed through Maven and are cached to your local environment after they are downloaded.

5. Once all dependencies are cached, you should be able to run `mvn package` in any of the examples to generate the *.modl* file (which will be created in the `build\target\` directory of the example).  The modl file is the Ignition module file you install to the Dev Mode Ignition in `Config > Modules` in your browser's Gateway page (generally found at `http://localhost:8088/main`).

6. You should now be able to see your module installed and running!

## The Module Build System

These examples utilize Maven and our Maven Plugin.  The ignition-maven-plugin is available through our [Nexus Repository](https://nexus.inductiveautomation.com/repository/inductiveautomation-releases/) (see examples for how to add to depenency sources).  

The pom files in these examples should prove useful tools to understanding how the new SDK works while we update the documentation in preparation for the full release of this new SDK.  
