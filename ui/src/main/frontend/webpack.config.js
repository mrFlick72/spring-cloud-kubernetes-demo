var path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const BUID_DIR = path.resolve(__dirname + "../../../../target/classes/static");

module.exports = {
    // mode: 'production',
    entry: {
        site: path.resolve(__dirname, './app/site/index.js'),
        messagesSite: path.resolve(__dirname, './app/messages-site/index.js')
    },
    resolve: {
        extensions: [".js", ".jsx"]
    },
    plugins: [
        new HtmlWebpackPlugin({
            chunks: ['site'],
            filename: "index.html",
            template: path.resolve(__dirname, "../resources/static/index.html")
        }),
        new HtmlWebpackPlugin({
            chunks: ['messagesSite'],
            filename: "messages.html",
            template: path.resolve(__dirname, "../resources/static/messages.html")
        })
    ],
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            },
            {
                test: path.join(__dirname, "."),
                exclude: path.resolve(__dirname, "node_modules"),
                use: {
                    loader: "babel-loader",
                    options: {
                        presets: ["env", "react"]
                    }
                }

            }
        ]
    },
    output: {
        filename: '[name]_[hash]_bundle.js',
        path: BUID_DIR
    }
};