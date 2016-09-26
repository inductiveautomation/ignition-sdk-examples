This is the javascript source for the frontend code loaded up by HomeConnectStatusRoutes.java

The javascript file next to that class is created by webpack from the source in this folder.

If you want to alter homeconnectstatus.js, you need to change source here and run webpack.

1. Install NPM

2. Install webpack
	`npm install webpack -g`
	
3. Install the dependencies defined in package.json
	`npm install` in this directory

4. Run webpack
	`webpack`
   Or, run webpack in watch mode to automatically run every time any source is changed.
	`webpack --watch`