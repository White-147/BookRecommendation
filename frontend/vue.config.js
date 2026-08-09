const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // GitHub Pages 部署在仓库子目录（https://white-147.github.io/BookRecommendation/）
  // 生产构建资源必须加子目录前缀，否则 js/css 引用根路径全部 404
  publicPath: process.env.NODE_ENV === 'production' ? '/BookRecommendation/' : '/'
})
