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

// 配置elementUI
Vue.use(ElementUI)
Vue.config.productionTip = false

// 配置Axios
// 修改内部$http为Axios
Vue.prototype.$http = Axios
// 生产环境通过 .env.production 的 VUE_APP_API_BASE_URL 指向线上后端（Render）；本地开发默认 localhost
Axios.defaults.baseURL = process.env.VUE_APP_API_BASE_URL || "http://localhost:8081/book_recommendation"
Axios.interceptors.request.use(config => {
    // 每次发送请求时携带Token信息
    config.headers['Authorization'] = sessionStorage.getItem('token');
    return config
}, error => {
    return Promise.reject(error)
})

router.beforeEach((to, from, next) => {
    let loginInfo = sessionStorage.getItem('loginForm')
    if (to.name === 'Login' || to.name === 'Register')
        next()
    else if (to.name !== 'Login' && !loginInfo) {
        next({name: 'Login'})
        ElementUI.Message.error('请先登录')
    } else next()
})

router.afterEach((to, from) => {
    history.pushState(null, null, location.protocol + '/' + location.host + '/#' + to.path)
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