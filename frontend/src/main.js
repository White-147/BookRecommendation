import Vue from 'vue'
import App from './App.vue'
import router from './router'
import VueRouter from "vue-router";
import store from './store'
import ElementUI from "element-ui";
import './plugins/element.js'
import './assets/css/global.css'
import Axios from 'axios'
import tab from "@/store/tab";
// 嵌入作品集演示模式（VUE_APP_EMBEDDED_DEMO=true）时的本地 mock 数据
import DEMO_MOCKS from './mock/mocks'

// 配置elementUI
Vue.use(ElementUI)
Vue.config.productionTip = false

// 配置Axios
// 修改内部$http为Axios
Vue.prototype.$http = Axios
// 生产环境通过 .env.production 的 VUE_APP_API_BASE_URL 指向线上后端（Render）；本地开发默认 localhost
Axios.defaults.baseURL = process.env.VUE_APP_API_BASE_URL || "http://localhost:8081/book_recommendation"

// —— 嵌入作品集演示模式（VUE_APP_EMBEDDED_DEMO=true）：拦截全部 API 请求返回本地 mock 数据，
//    无后端也能展示完整登录后界面；登录响应携带演示 token（headers.authorization）——
if (process.env.VUE_APP_EMBEDDED_DEMO === 'true') {
  Axios.defaults.adapter = (config) => {
    const url = config.url || ''
    let payload = null
    for (const key of Object.keys(DEMO_MOCKS)) {
      if (url.includes(key)) {
        payload = DEMO_MOCKS[key](config)
        break
      }
    }
    if (payload === null) {
      payload = { code: 200, msg: 'ok', data: null }
    }
    const headers = { 'content-type': 'application/json' }
    if (url.includes('/login')) {
      headers.authorization = 'Bearer demo-token-2020001'
    }
    return Promise.resolve({
      data: payload,
      status: 200,
      statusText: 'OK',
      headers,
      config,
    })
  }
}

Axios.interceptors.request.use(config => {
    // 每次发送请求时携带Token信息
    config.headers['Authorization'] = sessionStorage.getItem('token');
    return config
}, error => {
    return Promise.reject(error)
})

// —— 在线演示：未登录访问时自动使用演示账号（2020001/123456）登录并直入首页，
//    避免访客停留在登录页；手动登录 / 注册不受影响；后端未就绪时回退到登录页 ——
async function tryDemoLogin() {
    if (sessionStorage.getItem('loginForm')) return true
    if (sessionStorage.getItem('demoLoginTried') === '1') return false
    sessionStorage.setItem('demoLoginTried', '1')
    try {
        const res = await Axios.post('/login', { account: '2020001', password: '123456' })
        if (res.data.code === 200 && res.headers.authorization) {
            sessionStorage.setItem('token', res.headers.authorization)
            const user = await Axios.get('/sys/user/selectByAccount?account=2020001')
            sessionStorage.setItem('loginForm', JSON.stringify(user.data.data))
            router.push('/Home')
            return true
        }
    } catch (e) {
        // 后端冷启动/暂不可用：停留在登录页供手动重试
    }
    return false
}

router.beforeEach(async (to, from, next) => {
    if (to.name === 'Login' || to.name === 'Register') {
        // 在线演示：访问登录页自动登录直入首页；注册页跳过
        if (to.name === 'Login') {
            const ok = await tryDemoLogin()
            if (ok) return
        }
        next()
    } else if (to.name !== 'Login' && !sessionStorage.getItem('loginForm')) {
        next({name: 'Login'})
        ElementUI.Message.error('请先登录')
    } else next()
})

router.afterEach((to, from) => {
    // 保持 hash 形式 URL：以相对形式 push（原实现拼 protocol+host 少了一对 //，
    // 且绝对 URL 会丢弃部署子路径前缀，导致刷新 404）
    history.pushState(null, null, '#' + to.path)
})

// 捕获重复访问路由的错误
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

new Vue({
    router,
    store,
    Axios,
    render: h => h(App),
    tab
}).$mount('#app')