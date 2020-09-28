// see http://vuejs-templates.github.io/webpack for documentation.
var path = require('path')

module.exports = {
  build: {
    env: require('./prod.env'),
    index: path.resolve(__dirname, '../dist/index.html'),
    assetsRoot: path.resolve(__dirname, '../dist'),
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    // When true, all source code in the production environment will be visible, which is not recommended.
    productionSourceMap: false,
    // Gzip off by default as many popular static hosts such as
    // Surge or Netlify already gzip all static assets for you.
    // Before setting to `true`, make sure to:
    // npm install --save-dev compression-webpack-plugin
    productionGzip: false,
    productionGzipExtensions: ['js', 'css'],
    // Run the build command with an extra argument to
    // View the bundle analyzer report after build finishes:
    // `npm run build --report`
    // Set to `true` or `false` to always turn it on or off
    bundleAnalyzerReport: process.env.npm_config_report,
  },
  dev: {
    env: require('./dev.env'),
    port: 80,
    autoOpenBrowser: false,
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    devtool: 'eval-source-map',
    cacheBusting: false,
    // @Deprecated
    proxyTable: {
      /*'/scm-server': {
        target: 'http://localhost:14043',
        changeOrigin: true,
        pathRewrite: {
          '^/scm': 'scm-server'
        }
      },
      '/ci-server': {
        target: 'http://localhost:14046',
        changeOrigin: true,
        pathRewrite: {
          '^/ci-server': 'ci-server'
        }
      },
      '/umc-manager': {
        target: 'http://localhost:14048',
        changeOrigin: true,
        pathRewrite: {
          '^/umc-manager': 'umc-manager'
        }
      },
      '/share-manager': {
        target: 'http://localhost:14051',
        changeOrigin: true,
        pathRewrite: {
          '^/share-manager': 'share-manager'
        }
      },
      '/srm': {
        target: 'http://localhost:15050',
        changeOrigin: true,
        pathRewrite: {
          '^/srm': 'srm-manager'
        }
      },*/
    },

    // CSS Sourcemaps off by default because relative paths are "buggy"
    // with this option, according to the CSS-Loader README
    // (https://github.com/webpack/css-loader#sourcemaps)
    // In our experience, they generally work as expected,
    // just be aware of this issue when enabling this option.
    cssSourceMap: false
  }
}
