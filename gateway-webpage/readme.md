# Gateway Webpage (Home Connect) Example

This simple module gives an example of how you add top-level navigational items to the Configuration and Status pages of the 
Ignition Gateway.  This is accomplished through an imaginary 'Home Connect' device the module might support though configurability and status reporting in the Gateway web interface.

## Module Structure

The two primary source code components can be found in the gateway-webpage-gateway's src/main/, where you'll find both java and javascript components.  Ultimately, the javascript component is assembled via [webpack](https://v4.webpack.js.org/) and placed into `src/main/resources/mounted/js`.  The `gateway-webpage-build` subproject contains configuration used to assembly the module.  It's _target_ directory will also contain the assembled module, after the module has been packaged (note, target won't exist till you've run `mvn package` or another output-generating command).

# How It Works

This module demonstrates a few different key APIs for modules that wish to contribute status and/or configuration pages in the Ignition Gateway web interface.  Configuration pages demonstrate use of the Record/Persistence apis that are involved, while the Status Page example demonstrates one method of creating a custom status page for display in the Ignition Gateway's web ui by making use of the 'Module Resource' API added in Ignition 7.9.


In that version, we added the ability to easily mount and serve 'resource' files through your module.  These resources may be any file, but they are often js, css, or html.  These resources are served by default at `<host gateway>/main/res/<module id>/<mounted folder>/<target file>`, provided they are identifiable on the module's classpath - i.e., they need to be bundled into a jar that is part of you module (note, in 8.0+ you can and should omit the _/main_ portion).  The module id is used in the url by default, but a shorter alternative may be specified by overriding `GatewayHook.getMountPathAlias()`. Similarly, the 'mounted folder' may also be specified, by overriding `GatewayHook.getMountedResourceFolder()`. See the `GatewayHook.java` file for implementation details. 

For this example, our status page is served as an example of such a _resource_.  The status page itself is assembled using the popular [React](https://reactjs.org/) javascript library, and the [webpack](https://v4.webpack.js.org/) bundling tool.  The webpack process outputs a `homeconnectstatus.js` file that is referenced in the gateway hook.  By placing it in the appropriate resource directory, it is included in the module when the mvn command is executed.

If you want to alter `homeconnectstatus.js`, you need to change the source in `src/main/javascript`, then run webpack before building your module. To do so, you will need Node and the Node Package Manager (npm) installed. See the [instructions for installing node and npm](https://docs.npmjs.com/getting-started/installing-node) if you don't have them installed on your development machine.  We suggest using the current LTS version, though any LTS from version 12 up should work.

Once installed, follow these instructions at the command line, in the `javascript` folder:

0. (Optional) Install the yarn package management tool to make use of its faster resolution and dependency locking features:
    ```
       npm i -g yarn@1.22.11
    ```
1. Navigate to the source javascript directory:
    ```
   gateway-webpage-gateway/src/main/javascript
   ```
2. Install the local dependencies using yarn or npm:
    ```
       npm install
       # or alternatively, if using yarn
       yarn install
    ```
3. Use the provided node scripts to build the javascript (defined in the 'scripts' section of the package.json) 
    ```
        npm run build:dev # builds 'development' versions, with additional tools for debugging
        npm run build:prod # builds the higher-performing 'production' version, which should be used for production modules
   
        # yarn alternatives
        # yarn run build:dev
        # yarn run build:prod
    ```
	
#### Note

While this example uses [React](https://facebook.github.io/react/) and [Redux](http://redux.js.org/), it should be noted that React is *not* a requirement for Ignition status pages.  Webpack transpiles this React source code into a javascript file which is located in `hce-gateway/src/main/resources/mounted/js`.  That transpiled javascript is what is ultimately mounted and served by Ignition. 

For Ignition 7.9, 8.0, and 8.1, we provide React and Redux as part of our gateway api (which is why they are marked as 'externals' in the webpack config), but as a module developer, you are free to use whatever client side technologies you'd like to build the javascript that gets mounted for your pages. The components used in the Ignition Gateway Status section (such as Tables, Charts, etc) are implemented as reusable React components through the 'ignition-react' javascript package, so it may be easier for you to maintain a consistent appearance with the rest of Ignition's web components through their reuse.  
