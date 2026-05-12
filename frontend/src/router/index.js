import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from "@/components/common/Login";
import Register from "@/components/common/Register";
import Home from '@/components/common/Home'
import Index from '@/components/common/Index'
import BookRecommend from "@/components/pages/book/BookRecommend";
import BookRecently from "@/components/pages/book/BookRecently";
import BookBorrow from "@/components/pages/book/BookBorrow";
import BookInfo from "@/components/pages/book/BookInfo";
import UserManage from "@/components/pages/user/UserManage";
import UserBorrowInfo from "@/components/pages/user/UserBorrowInfo";
import UserCollection from "@/components/pages/user/UserCollection";


Vue.use(VueRouter)

const router = new VueRouter({
    mode: 'hash',
    base: process.env.BASE_URL,
    routes: [
        {
            path: '/',
            name: 'Login',
            component: Login
        }, {
            path: '/Register',
            name: 'Register',
            component: Register
        },
        {
            path: '/Index',
            name: 'Index',
            component: Index,
            children: [
                {
                    path: '/Home',
                    name: 'Home',
                    component: Home
                }, {
                    path: '/User/UserManage',
                    name: 'UserManage',
                    component: UserManage
                },
                {
                    path: '/User/UserBorrowInfo',
                    name: 'UserBorrowInfo',
                    component: UserBorrowInfo
                }, {
                    path: '/User/UserCollection',
                    name: 'UserCollection',
                    component: UserCollection
                }, {
                    path: "/Book/BookRecommend",
                    name: "BookRecommend",
                    component: BookRecommend
                }, {
                    path: '/Book/BookRecently',
                    name: 'BookRecently',
                    component: BookRecently
                }, {
                    path: '/Book/BookBorrow',
                    name: 'BookBorrow',
                    component: BookBorrow
                }, {
                    path: '/Book/BookInfo',
                    name: 'BookInfo',
                    component: BookInfo,
                }
            ]
        }
    ],
    scrollBehavior: () => {
        history.pushState(null, null, document.URL)
    }
})

export default router