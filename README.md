# Ignition SDK

We have restructured the Ignition SDK to let you create Ignition modules with minimal startup time.  This new format reduces configuration, eases dependency management and makes it easier to set up the development environment of your choosing.  This project is still in early-preview and we plan for it to become the standard for module development in a future Ignition 7.7.x release.  

We are in the process of updating documentation and will post updates here and on the [Inductive Automation](http://www.inductiveautomation.com) website.  Until then, the existing module SDK programming guide is available with the 7.7.1 SDK and provides some good material to get started with.


## How to get started

1. You will need the Java JDK 1.8+ installed, downloadable from the [Java SDK Downloads](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html) page.

2. You will need Maven 3.+ installed.  Linux users can use their package manager to install at the command line (example for apt package management`sudo apt-get install maven`), and similarly OSX users using brew can `brew install maven`.  Windows users can install via [Chocolatey](https://chocolatey.org/) or by downloading the installer at the [Maven downloads](http://maven.apache.org/download.cgi_) page.

1. You will want a running version of Ignition to test your module in.  If you don't already have Ignition installed head to the Inductive Automation [downloads](https://www.inductiveautomation.com/downloads/) page, download the correct package for your system and follow the installation instructions to get a gateway up and running.  

2. You will also need a developer account, which you can get started at the Ignition [Module Development](https://marketplace.inductiveautomation.com/developer) page.  *Note:* The dev registration process is longer if you are interested in selling your modules through the Inductive Automation [Module Marketplace](http://marketplace.inductiveautomation.com/).  

3. Once you have configured your developer gateway, clone this repo to a directory of your choice :

    ```git clone https://github.com/inductiveautomation/ignition-sdk-examples.git```

4. Using your IDE of choice, you should be able to create or open any of these included Example Modules through the parent pom.xml file located in the root of each example.  Upon importing this project into your IDE, it should download (if auto-import is on) Maven dependencies from the Inductive Automation artifact repository. Dependencies are managed through Maven and are cached to your local environment after they are downloaded.

5. Once all dependencies are cached, you should be able to run `mvn package` in any of the examples to generate the *.modl* file (which will be created in the `build\target\` directory of the example).  The modl file is the Ignition module file you install to the Dev Mode Ignition in `Config > Modules` in your browser's Gateway page (generally found at `http://localhost:8088/main`).

6. You should now be able to see your module installed and running!

## The Module Build System

These examples utilize Maven and our Maven Plugin.  The ignition-maven-plugin is available through our [Nexus Repository](http://nexus.inductiveautomation.com:8081/nexus/content/repositories/inductiveautomation-releases) (see examples for how to add to depenency sources).  

The pom files in these examples should prove useful tools to understanding how the new SDK works while we update the documentation in preparation for the full release of this new SDK.  
