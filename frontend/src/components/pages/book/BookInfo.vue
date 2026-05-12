<template>
  <div>
    <el-card>
      <el-page-header @back="goBack"></el-page-header>
      <el-row>
        <el-col :span="12">
          <div class="title_page">
            <el-image v-if="book.img!=='not found'"
                      :src=book.img
                      style="text-align: center;">
              <div slot="error">
                <el-image style="cursor:default;height:200px;width:200px;"></el-image>
              </div>
            </el-image>
            <el-image v-if="book.img==='not found'"
                      style="cursor:default;height:200px;width:200px;">
            </el-image>
          </div>

        </el-col>
        <el-col :span="12">
          <div class="info_title">
            {{ book.mTitle }}
          </div>
          <div class="book_info"
               v-loading.fullscreen.lock="collectLoading">
            <p v-if="book.mAuthor!==null">作者：{{ book.mAuthor }}</p>
            <p v-if="book.mPublisher!==null">出版社：{{ book.mPublisher }}</p>
            <p v-if="book.mPubYear!==null">出版年份：{{ book.mPubYear }}</p>
          </div>
          <div class="info_button">
            <el-button
                v-if="book.collect===null"
                type="warning"
                plain
                @click="updateCollectState(book)">
              收藏
            </el-button>
            <el-button
                v-if="book.collect!==null"
                type="info"
                :disabled="true">
              收藏
            </el-button>
            <el-button
                v-if="book.status==='True'"
                type="info"
                key="True"
                :disabled="true">
              借阅
            </el-button>
            <el-button
                v-if="book.status==='False'"
                type="primary"
                plain
                @click="updateState(book)">
              借阅
            </el-button>
          </div>
        </el-col>
      </el-row>

      <el-divider v-if="Object.keys(this.relatedBooks).length !== 0">其他用户感兴趣</el-divider>

      <el-table v-if="Object.keys(this.relatedBooks).length !== 0"
                :data="relatedBooks"
                class="relatedBooksTable"
                :show-header="false"
                border>
        <el-table-column width="200">
          <template slot-scope="scope">
            <el-image v-if="scope.row.bookExtend.img!=='not found'"
                      :src=scope.row.bookExtend.img>
              <div slot="error">
                <el-image style="cursor:default;height:180px;width:180px;">
                </el-image>
              </div>
            </el-image>
            <el-image v-if="scope.row.bookExtend.img==='not found'"
                      style="cursor:default;height:180px;width:180px;">
            </el-image>
          </template>
        </el-table-column>
        <el-table-column>
          <template slot-scope="scope">
            <div class="relatedBooksInfo"
                 @click="toRelatedBookInfo(scope.row.bookExtend)">
              <p id="relatedBooksTitle">{{ scope.row.bookExtend.mTitle }}</p>
              <p v-if="scope.row.bookExtend.mAuthor!==null">{{ scope.row.bookExtend.mAuthor }}</p>
              <p v-if="scope.row.bookExtend.mPubYear!==null">出版年份:{{ scope.row.bookExtend.mPubYear }}</p>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
<script>
import {mapState} from 'vuex'

export default {
  name: "BookInfo",
  data() {
    return {
      currentMenu: {
        path: "/Book/BookInfo",
        label: "图书详情",
        name: "BookInfo",
        url: "/Book/BookInfo"
      },
      relatedBooks: [],
      loginForm: {},
      callNo: '',
      certId: '',
      flashLoading: false,
      collectLoading: false,
      book: {}
    }
  },
  methods: {
    goBack() {
      this.$router.push(this.tags[this.tags.length - 2].path)
      this.$store.commit('closeTag', this.currentMenu)
      this.$store.commit('selectMenu', this.tags[this.tags.length - 1])
      this.$store.commit('menuChange', this.tags[this.tags.length - 1])
      sessionStorage.removeItem('flashLoading');
    },
    setCurrentMenu() {
      if (window.sessionStorage.getItem('flashLoading'))
        this.flashLoading = JSON.parse(window.sessionStorage.getItem('flashLoading'))
      if (this.flashLoading)
        return
      this.flashLoading = true
      sessionStorage.setItem("flashLoading", this.flashLoading)
      this.$store.commit('selectMenu', this.currentMenu)
      this.$store.commit('menuChange', this.currentMenu)
    },
    getBook() {
      this.collectLoading = true
      if (this.callNo === '' && this.certId === '') {
        this.callNo = sessionStorage.getItem('callNo')
        this.certId = sessionStorage.getItem('certId')
      }
      this.$http.get("/library/book/selectBookExtendByCallNo?callNo="
          + this.callNo + "&&certId=" + this.certId).then((res) => {
        this.book = res.data.data
        this.getRelatedBook()
      })
    },
    getRelatedBook() {
      this.$http.get("/library/relatedBook/getRelatedBook?certId=" +
          this.certId + "&callNo=" + this.callNo).then((res) => {
        this.relatedBooks = res.data.data
        this.collectLoading = false
      })
    },
    updateState(row) {
      if (this.collectLoading)
        return
      this.collectLoading = true
      row.status = "True"
      let book = {
        mAuthor: row.mAuthor,
        mCallNo: row.mCallNo,
        mPubYear: row.mPubYear,
        mPublisher: row.mPublisher,
        mTitle: row.mTitle,
        status: row.status
      }
      this.$http.post("/library/book/save", book).then((res) => {
        this.collectLoading = false
      })
      this.$http.post("/library/lend/save?callNo="
          + book.mCallNo + "&account=" + this.loginForm.account)
          .then((res) => {
            this.stateStatusLoading = false
            this.$router.push("/Book/BookInfo")
          })
    },
    updateCollectState(row) {
      if (this.collectLoading)
        return
      this.collectLoading = true
      this.$http.post("/library/collect/saveCollect?certId="
          + this.certId + "&callNo=" + row.mCallNo).then(() => {
        this.collectLoading = false
        this.getBook()
        this.$router.push("/Book/BookInfo")
      })
    },
    toRelatedBookInfo(item) {
      sessionStorage.setItem("callNo", item.mCallNo)
      sessionStorage.setItem("certId", this.certId)
      this.$router.go(0)
    }
  },
  computed: {
    ...mapState({
      current: state => state.tab.currentMenu,
      tags: state => state.tab.tabList
    })
  },
  created() {
    if (this.$route.params.callNo && this.$route.params.certId) {
      this.callNo = this.$route.params.callNo
      this.certId = this.$route.params.certId
      sessionStorage.setItem("callNo", this.$route.params.callNo)
      sessionStorage.setItem("certId", this.$route.params.certId)
    }
    this.loginForm = JSON.parse(window.sessionStorage.getItem('loginForm'))
    this.setCurrentMenu()
    this.getBook()
  }

}
</script>

<style scoped>
.title_page {
  display: flex;
  justify-content: center;
  align-items: center;
}

.title_page img {
  width: 450px;
  height: 450px;
}

.info_title {
  font-weight: bolder;
  font-size: 40px;
}

.book_info {
  margin-top: 20px;
  font-size: 15px;
  color: #999999;
}

.book_info p {
  padding-bottom: 5px;
}

.info_button {
  margin-top: 30px;
}

.relatedBooksTable {
  width: 70%;
  margin-left: 15%;
}

#relatedBooksTitle {
  color: black;
  font-size: 24px;
}

.relatedBooksInfo p {
  cursor: pointer;
  color: #999999;
  font-size: 15px;
}
</style>