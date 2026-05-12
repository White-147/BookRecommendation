<template>
  <el-container>
    <el-aside width="200px">
      <el-menu
          :default-active="myPath"
          background-color="#333744"
          text-color="#fff"
          active-text-color="#ffd04b">
        <h3>图书推荐系统</h3>

        <el-menu-item
            v-for="item in noChildren"
            :key="item.name"
            :index="item.name"
            @click="clickMenu(item)">
          <i :class="`el-icon-${item.icon}`"></i>
          <span slot="title">{{ item.label }}</span>
        </el-menu-item>

        <el-submenu
            v-for="item in hasChildren"
            :key="item.name"
            :index="item.name">
          <template slot="title">
            <i :class="`el-icon-${item.icon}`"></i>
            <span>{{ item.label }}</span>
          </template>
          <el-menu-item-group
              v-for="subItem in item.children"
              :key="subItem.name">
            <el-menu-item
                :index=subItem.name
                @click="clickMenu(subItem)">
              {{ subItem.label }}
            </el-menu-item>
          </el-menu-item-group>
        </el-submenu>

      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="l-container">
          <el-breadcrumb>
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="current.label!==undefined">
              {{ current.label }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="r-container">
          <el-dropdown @command="handleCommend" style="cursor: pointer;">
            <span class="profile">
              <img :src="getPath()" alt="暂未找到图片">
            </span>
            <el-dropdown-menu>
              <el-dropdown-item command="userInfo">个人信息</el-dropdown-item>
              <el-dropdown-item command="logout">退出</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <div class="tabs">
        <el-tag
            v-for="item in tags"
            :key="item.path"
            :effect="$route.name===item.name?'dark':'plain'"
            @click="changeMenu(item)"
            @close="handleClose(item)"
            :closable="item.name!=='Home'">
          {{ item.label }}
        </el-tag>
      </div>

      <el-main>
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import {mapState} from 'vuex'

export default {
  name: "Home",
  data() {
    return {
      menuData: [
        {
          path: "/Home",
          label: "首页",
          name: "Home",
          icon: "s-home",
          url: "/Home"
        }, {
          label: "图书中心",
          name: "Book",
          icon: "notebook-2",
          children: [
            {
              path: "/Book/BookBorrow",
              name: "BookBorrow",
              label: "图书借阅",
              url: "/Book/BookBorrow"
            }, {
              path: "/Book/BookRecommend",
              name: "BookRecommend",
              label: "猜你喜欢",
              url: "/Book/BookRecommend"
            }, {
              path: "/Book/BookRecently",
              name: "BookRecently",
              label: "新书速递",
              url: "/Book/BookRecently"
            }
          ]
        },
        {
          label: "个人中心",
          name: "User",
          icon: "s-custom",
          children: [
            {
              path: "/User/UserManage",
              name: "UserManage",
              label: "用户管理",
              url: "/User/UserManage"
            }, {
              path: "/User/UserCollection",
              name: "UserCollection",
              label: '用户收藏',
              url: '/User/UserCollection'
            }, {
              path: "/User/UserBorrowInfo",
              name: "UserBorrowInfo",
              label: "借阅信息",
              url: "/User/UserBorrowInfo"
            }
          ]
        }
      ],
      myPath: this.$route.name,
      loginForm: JSON.parse(sessionStorage.getItem('loginForm'))
    }
  },
  methods: {
    clickMenu(item) {
      if (this.$route.path !== item.path) {
        this.$router.push(item.path)
      }
      this.$store.commit('selectMenu', item)
      this.$store.commit('menuChange', item)
    },
    changeMenu(item) {
      this.myPath = item.name
      this.$router.push(item.path)
      this.$store.commit('changeBread', item)
    },
    handleClose(item) {
      this.myPath = 'Home'
      this.$store.commit('closeTag', item)
      if (item.name !== 'Home')
        this.$router.push(this.tags[0])
    },
    handleCommend(command) {
      if (command === 'logout') {
        // 清空session存储
        sessionStorage.clear()
        // 重置vuex中全局变量
        this.$store.commit('setCurrentMenu',[])
        this.$store.commit('setTabList',[{path: "/Home", label: "首页", name: "Home"}])
        // 跳转到登录页
        this.$router.push('/')
      }
      if (command === 'userInfo') {
        this.myPath = 'UserManage'
        this.$router.push('/User/UserManage')
        this.$store.commit('selectMenu', this.menuData[2].children[0])
        this.$store.commit('menuChange', this.menuData[2].children[0])
      }
    },
    getPath() {
      return this.loginForm.head ? require('@/assets/image/head/' + this.loginForm.head) : ''
    }
  },
  computed: {
    hasChildren() {
      return this.menuData.filter(item => item.children)
    },
    noChildren() {
      return this.menuData.filter(item => !item.children)
    },
    ...mapState({
      current: state => state.tab.currentMenu,
      tags: state => state.tab.tabList
    })
  },
  mounted() {
    this.$store.dispatch('loadCurrentMenuStatus');
    this.$store.dispatch('loadTabListStatus');
  }
}
</script>

<style scoped>
.el-container {
  height: 100%;
  width: 100%;
}

.el-header {
  background-color: #242f42;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-breadcrumb ::v-deep .el-breadcrumb__inner {
  color: #666 !important;
}

.el-breadcrumb__item:last-child ::v-deep .el-breadcrumb__inner {
  color: #fff !important;
}

.l-container span {
  color: #fff;
  font-size: 14px;
}

.el-breadcrumb ::v-deep .el-breadcrumb__separator {
  margin-left: 1px;
}

.r-container {
  padding-right: 10px;
}

.profile img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.tabs {
  position: relative;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 5px 10px #ddd;
  min-height: 32px;
  padding-bottom: 5px;
  padding-top: 5px;
  padding-left: 10px;
}

.tabs .el-tag {
  margin-right: 15px;
  cursor: pointer;
}

.el-aside {
  background-color: #333744;
}

.el-menu {
  height: 100vh;
  border: 0;
}

.el-menu h3 {
  color: white;
  text-align: center;
}
</style>