# About this Javascript Example

With Ignition 7.9, we have added the ability to easily mount javascript files through your module.  This is the javascript source for the frontend code loaded up by HomeConnectStatusRoutes.java.  

The javascript file next to that class is created by [webpack](https://webpack.github.io/) from the source in this folder.

If you want to alter homeconnectstatus.js, you need to change source here in `src/main/javascript` and run webpack before building your module.  To do so, you will need Node and the Node Package Manager (npm) installed.  See the [instructions for installing node and npm](https://docs.npmjs.com/getting-started/installing-node) for if you don't have them installed on your development machine.  Once installed, follow these instructions at the command line:

1. Install webpack
	`npm install webpack -g`
	
2. Install the dependencies defined in package.json
	`npm install` in this directory

3. Run webpack
	`webpack`
   Or, run webpack in watch mode to automatically run every time any source is changed.
	`webpack --watch`
	
### Note

While this example uses [React](https://facebook.github.io/react/) and [Redux](http://redux.js.org/), it should be noted that React is not a requirement for Ignition status pages.  Webpack transpiles this React source code into a javascript file which is located in `hce-gateway/src/main/resources/mounted/js`.  It is this javascript which is ultimately mounted and served by Ignition when this module is loaded.  

We provide react and redux as part of our gateway api (which is why they are marked as 'externals' in the webpack config), but as a module developer, you are free to use whichever client side technologies you'd like to build the javascript that gets mounted for your pages.  The components used in the Ignition Gateway Status section (such as Tables, Charts, etc) are implemented as reusable React components through the 'ignition-react' javascript package, so it may be easier for you to maintain a consistent appearance with the rest of Ignition 7.9's through their reuse.  
