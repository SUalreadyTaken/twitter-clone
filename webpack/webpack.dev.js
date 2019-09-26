const webpack = require('webpack');
const writeFilePlugin = require('write-file-webpack-plugin');
const webpackMerge = require('webpack-merge');
const BrowserSyncPlugin = require('browser-sync-webpack-plugin');
const ForkTsCheckerWebpackPlugin = require('fork-ts-checker-webpack-plugin');
const FriendlyErrorsWebpackPlugin = require('friendly-errors-webpack-plugin');
const SimpleProgressWebpackPlugin = require('simple-progress-webpack-plugin');
const WebpackNotifierPlugin = require('webpack-notifier');
const TerserPlugin = require('terser-webpack-plugin');
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;
const path = require('path');
const utils = require('./utils.js');
const commonConfig = require('./webpack.common.js');

const ENV = 'development';

module.exports = (options) => webpackMerge(commonConfig({
    env: ENV
}), {
    devtool: 'inline-source-map',
    devServer: {
        contentBase: './target/www',
        proxy: [{
            context: [
                /* jhipster-needle-add-entity-to-webpack - JHipster will add entity api paths here */
                '/api',
                '/management',
                '/swagger-resources',
                '/v2/api-docs',
                '/h2-console',
                '/auth'
            ],
            target: `http${options.tls ? 's' : ''}://127.0.0.1:8080`,
            secure: false,
            changeOrigin: options.tls,
            headers: {
                host: 'localhost:9000'
            }
        },{
            context: [
                '/socket'
            ],
            target: 'ws://127.0.0.1:8080',
            ws: true
        }],

        stats: options.stats,
        watchOptions: {
            ignored: /node_modules/
        }
    },
    node: {
        net: 'empty'
    },
    entry: {
        polyfills: './src/main/webapp/app/polyfills',
        global: './src/main/webapp/content/css/global.css',
        main: './src/main/webapp/app/app.main'
    },
    output: {
        path: utils.root('target/www'),
        filename: 'app/[name].bundle.js',
        chunkFilename: 'app/[id].chunk.js'
    },
    module: {
        rules: [{
                test: /\.ts$/,
                enforce: 'pre',
                loader: 'tslint-loader',
                exclude: [/(node_modules)/, new RegExp('reflect-metadata\\' + path.sep + 'Reflect\\.ts')]
            },
            {
                test: /\.ts$/,
                use: [
                    'angular2-template-loader',
                    {
                        loader: 'cache-loader',
                        options: {
                            cacheDirectory: path.resolve('target/cache-loader')
                        }
                    },
                    {
                        loader: 'thread-loader',
                        options: {
                            // there should be 1 cpu for the fork-ts-checker-webpack-plugin
                            workers: require('os').cpus().length - 1
                        }
                    },
                    {
                        loader: 'ts-loader',
                        options: {
                            transpileOnly: true,
                            happyPackMode: true
                        }
                    },
                    'angular-router-loader'
                ],
                exclude: /(node_modules)/
            },
            {
                test: /\.css$/,
                use: ['to-string-loader', 'css-loader'],
                exclude: /(vendor\.css|global\.css)/
            },
            {
                test: /(vendor\.css|global\.css)/,
                use: ['style-loader', 'css-loader']
            }
        ]
    },
    optimization: {
        runtimeChunk: false,
        splitChunks: {
            cacheGroups: {
                commons: {
                    test: /[\\/]node_modules[\\/]/,
                    name: 'vendors',
                    chunks: 'all'
                }
            }
        },
        minimizer: [
          new TerserPlugin({
              parallel: true,
              cache: true,
              terserOptions: {
                  ie8: false,
                  // sourceMap: true, // Enable source maps. Please note that this will slow down the build
                  compress: {
                      dead_code: true,
                      warnings: false,
                      properties: true,
                      drop_debugger: true,
                      conditionals: true,
                      booleans: true,
                      loops: true,
                      unused: true,
                      toplevel: true,
                      if_return: true,
                      inline: true,
                      join_vars: true
                  },
                  output: {
                      comments: false,
                      beautify: false,
                      indent_level: 2
                  }
              }
          })
      ]
    },
    stats: process.env.JHI_DISABLE_WEBPACK_LOGS ? 'none' : options.stats,
    plugins: [
        process.env.JHI_DISABLE_WEBPACK_LOGS ?
        null :
        new SimpleProgressWebpackPlugin({
            format: options.stats === 'minimal' ? 'compact' : 'expanded'
        }),
        new FriendlyErrorsWebpackPlugin(),
        new ForkTsCheckerWebpackPlugin(),
        new BrowserSyncPlugin({
            host: 'localhost',
            port: 9000,
            proxy: {
                target: 'http://localhost:9060'
            },
            ghostMode: {
                clicks: false,
                forms: false,
                scroll: false
            },
            socket: {
                clients: {
                    heartbeatTimeout: 60000
                }
            }
        }, {
            reload: false
        }),
        new webpack.ContextReplacementPlugin(
            /angular(\\|\/)core(\\|\/)/,
            path.resolve(__dirname, './src/main/webapp')
        ),
        new writeFilePlugin(),
        new webpack.WatchIgnorePlugin([
            utils.root('src/test'),
        ]),
        // new BundleAnalyzerPlugin(),
        new WebpackNotifierPlugin({
            title: 'JHipster',
            contentImage: path.join(__dirname, 'logo-jhipster.png')
        })
    ].filter(Boolean),
    mode: 'development'
});
