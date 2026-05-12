import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex)
export default {
    state: {
        currentMenu: [],
        tabList: [{
            path: "/Home",
            label: "首页",
            name: "Home"
        }]
    },
    mutations: {
        selectMenu(state, val) {
            if (val.name === "Home") {
                state.currentMenu = []
                state.tabList = []
                state.tabList.push({
                    path: "/Home",
                    label: "首页",
                    name: "Home"
                })
            } else
                state.currentMenu = val
            window.sessionStorage.setItem('tabList', JSON.stringify(state.tabList))
            window.sessionStorage.setItem('currentMenu', JSON.stringify(state.currentMenu))
        },
        menuChange(state, val) {
            if (val.name !== "Home") {
                let index = state.tabList.findIndex(item => item.name === val.name)
                if (index === -1) {
                    state.tabList.push(val)
                }
                window.sessionStorage.setItem('tabList', JSON.stringify(state.tabList))
            }
        },
        changeBread(state, item) {
            if (item.name !== state.currentMenu.name)
                state.currentMenu = item
            if (item.name === 'Home') {
                state.currentMenu = []
                state.tabList = []
                state.tabList.push({
                    path: "/Home",
                    label: "首页",
                    name: "Home"
                })
            } else state.currentMenu = item
            window.sessionStorage.setItem('currentMenu', JSON.stringify(state.currentMenu))
            window.sessionStorage.setItem('tabList', JSON.stringify(state.tabList))
        },
        closeTag(state, val) {
            let index = state.tabList.findIndex(item => item.name === val.name)
            state.tabList.splice(index, 1)
            state.currentMenu = []
            window.sessionStorage.setItem('currentMenu', JSON.stringify(state.currentMenu))
            window.sessionStorage.setItem('tabList', JSON.stringify(state.tabList))
        },
        setCurrentMenu(state, status) {
            if (status !== null)
                state.currentMenu = status
        },
        setTabList(state, status) {
            if (status !== null)
                state.tabList = status
        }
    },
    actions: {
        loadCurrentMenuStatus({commit}) {
            let currentMenuStatus = JSON.parse(window.sessionStorage.getItem('currentMenu'))
            commit('setCurrentMenu', currentMenuStatus)
        },
        loadTabListStatus({commit}) {
            let tabListStatus = JSON.parse(window.sessionStorage.getItem('tabList'))
            commit('setTabList', tabListStatus)
        },
    }
}