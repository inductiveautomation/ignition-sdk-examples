/**
 * Webpack build configuration file.  Uses generic configuration that is appropriate for development.  Depending on
 * the needs of your module, you'll likely want to add appropriate 'production' configuration to this file in order
 * do do things such as minify, postcss, etc.
 *
 * To learn more about webpack, visit https://webpack.js.org/
 */

const webpack = require('webpack'),
    path = require('path'),
    fs = require('fs'),
    MiniCssExtractPlugin = require("mini-css-extract-plugin"),
    AfterBuildPlugin = require('@fiverr/afterbuild-webpack-plugin');

const LibName = "RadComponents";

// function that copies the result of the webpack from the dist/ folder into the  generated-resources folder which
// ultimately gets included in a 'web.jar'.  This jar is included in the module's gateway scope, and its contents are
// accessible as classpath resources just as if they were included in the gateway jar itself.
function copyToResources() {
    const generatedResourcesDir = path.resolve(__dirname, '../..', 'build/generated-resources/mounted/');
    const jsToCopy = path.resolve(__dirname, "dist/", `${LibName}.js`);
    const cssToCopy = path.resolve(__dirname, "dist/", `${LibName}.css`);
    const jSResourcePath = path.resolve(generatedResourcesDir, `${LibName}.js`);
    const cssResourcePath = path.resolve(generatedResourcesDir, `${LibName}.css`);


    const toCopy = [{from:jsToCopy, to: jSResourcePath}, {from: cssToCopy, to: cssResourcePath}];

    // if the desired folder doesn't exist, create it
    if (!fs.existsSync(generatedResourcesDir)){
        fs.mkdirSync(generatedResourcesDir, {recursive: true})
    }

    toCopy.forEach( file => {
        console.log(`copying ${file} into ${generatedResourcesDir}...`);

        try {
            fs.access(file.from, fs.constants.R_OK, (err) => {
                if (!err) {
                    fs.createReadStream(file.from)
                        .pipe(fs.createWriteStream(file.to));
                } else {
                    console.log(`Error when attempting to copy ${file.from} into ${file.to}`)
                }
            });
        } catch (err) {
            console.error(err);
            // rethrow to fail build
            throw err;
        }
    });
}


const config = {

    // define our entry point, from which we build our source tree for bundling
    entry: {
        RadComponents:  path.join(__dirname, "./typescript/rad-client-components.ts")
    },

    output: {
        library: [LibName],  // name as it will be accessible by on the webpack when linked as a script
        path: path.join(__dirname, "dist"),
        filename: `${LibName}.js`,
        libraryTarget: "umd",
        umdNamedDefine: true
    },

    // Enable sourcemaps for debugging webpack's output.  Should be changed for production builds.
    devtool: "source-map",

    resolve: {
        extensions: [".jsx", ".js", ".ts", ".tsx", ".d.ts", ".css", ".scss"],
        modules: [
            path.resolve(__dirname, "../../node_modules")  // look at the local as well as shared node modules when resolving dependencies
        ]
    },

    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: {
                    loader: 'ts-loader',
                    options: {
                        transpileOnly: false
                    }
                },
                exclude: /node_modules/,
            },
            {
                test: /\.css$|.scss$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    {
                        loader: 'css-loader',
                        options: {
                            // tells css-loader not to treat `url('/some/path')` as things that need to resolve at build time
                            // in other words, the url value is simply passed-through as written in the css/sass
                            url: false
                        }
                    },
                    {
                        loader: "sass-loader",
                    }
                ]
            }
        ]
    },
    plugins: [
        new AfterBuildPlugin(function(stats) {
            copyToResources();
        }),
        // pulls CSS out into a single file instead of dynamically inlining it
        new MiniCssExtractPlugin({
            filename: "[name].css"
        })
    ],

    // IMPORTANT -- this tells the webpack build tooling "don't include these things as part of the webpack bundle".
    // They are 'provided' 'externally' via perspective/ignition at runtime, and we don't want multiple copies in the
    // browser.  Any libraries used that are also used in perspective should be excluded.
    externals: {
        "react": "React",
        "react-dom": "ReactDOM",
        "mobx": "mobx",
        "mobx-react": "mobxReact",
        "@inductiveautomation/perspective-client": "PerspectiveClient"
    },
    optimization: {
        splitChunks: {
            cacheGroups: {
                styles: {
                    name: 'styles',
                    test: /\.css$/,
                    chunks: 'all',
                    enforce: true,
                },
            },
        },
    },
};


module.exports = () => config;
