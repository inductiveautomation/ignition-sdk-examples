# Gateway Webpage (Home Connect) Example

This simple module gives an example of how you add top-level navigational items to the Configuration and Status pages of the 
Ignition Gateway.

### Javascript

From Ignition 7.9, we have added the ability to easily mount javascript files through your module.  This is the javascript source for the frontend code loaded up by HomeConnectStatusRoutes.java.  

The javascript file next to that class is created by [webpack](https://webpack.github.io/) from the source in this folder.

If you want to alter `homeconnectstatus.js`, you need to change the source in `src/main/javascript`, then run webpack before building your module. To do so, you will need Node and the Node Package Manager (npm) installed. See the [instructions for installing node and npm](https://docs.npmjs.com/getting-started/installing-node) if you don't have them installed on your development machine. Once installed, follow these instructions at the command line:

1. Install webpack and the webpack command-line interface globally
	```
	npm install webpack@3 -g
	npm install webpack-cli -g
	```
2. Navigate to the source javascript directory:
    ```
    gateway-webpage-gateway/src/main/javascript
    ```
2. Install the dependencies defined in package.json
	```
	npm install
	```
3. From the same directory, run webpack:
	```
	webpack
	```
    Or, run webpack in watch mode to automatically run every time any source is changed.
    ```
    webpack --watch
    ```
	
#### Note

While this example uses [React](https://facebook.github.io/react/) and [Redux](http://redux.js.org/), it should be noted that React is *not* a requirement for Ignition status pages.  Webpack transpiles this React source code into a javascript file which is located in `hce-gateway/src/main/resources/mounted/js`.  That transpiled javascript is what is ultimately mounted and served by Ignition. 

We provide React and Redux as part of our gateway api (which is why they are marked as 'externals' in the webpack config), but as a module developer, you are free to use whatever client side technologies you'd like to build the javascript that gets mounted for your pages. The components used in the Ignition Gateway Status section (such as Tables, Charts, etc) are implemented as reusable React components through the 'ignition-react' javascript package, so it may be easier for you to maintain a consistent appearance with the rest of Ignition 7.9's through their reuse.  
