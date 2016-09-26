var webpack = require('webpack');
var path = require('path');
// var FailPlugin = require("../../../../../../Platform/gateway-api/src/main/javascript/ia-webpack-plugins/PerrysCheesyWebpackFailPlugin");



var outputFile = 'homeconnectstatus.js';

var config = {
  entry: './src/index.js',
  devtool: 'source-map',
  output: {
    path: path.join(
        __dirname,
        '../resources/mounted/js'),
    filename: outputFile,
    publicPath: '/dist',
    library: 'homeconnectstatus',
    libraryTarget: 'var'
  },
  resolve: {
    modulesDirectories: ['node_modules']
  },
  module: {
    loaders: [
      {
        test: /(\.jsx|\.js)$/,
        loader: 'babel-loader',
        exclude: /(node_modules|bower_components)/,
        query: {
          presets: ['es2015', 'react']
        }
      }
    ]
  },
  plugins: [
      // new webpack.optimize.UglifyJsPlugin({minimize:true})
      // new FailPlugin(),
  ],
  externals: [
    {react: 'React'},
    {'react-dom': 'ReactDOM'},
    {'ignition-react': 'IgnitionReact'},
    {'ignition-lib': 'IgnitionLib'},
    {'moment': 'moment'},
    {'numeral': 'numeral'}
      // moment and numeral are helpful for formatting time and numbers. Remove these lines if you don't need them.
  ]
};

module.exports = config;