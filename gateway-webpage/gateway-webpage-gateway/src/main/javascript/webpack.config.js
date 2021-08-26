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
    modules: ['node_modules']
  },
  module: {
    rules: [
      {
        test: /(\.jsx|\.js)$/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ["@babel/preset-env", "@babel/preset-react"]
          }
        },
        exclude: /(node_modules|bower_components)/,
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
