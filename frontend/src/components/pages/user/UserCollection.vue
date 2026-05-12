<template>
  <div>
    <el-card>
      <el-table
          border
          v-loading.fullscreen.lock="collectListLoading"
          empty-text="快去收藏些喜欢的书吧~"
          :data="collectionList"
          style="width: 100%">
        <el-table-column
            property="reader.certId"
            label="学号"
            align="center"
            width="150"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="reader.name"
            label="姓名"
            align="center"
            width="150"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="book.mTitle"
            label="书名"
            align="center"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="book.mAuthor"
            label="作者"
            align="center"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="book.mPublisher"
            label="出版社"
            align="center"
            width="300"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="operate"
            label="操作"
            align="center"
            width="250"
            :resizable="false">
          <template slot-scope="scope">
            <el-button
                size="mini"
                plain
                @click="toBookInfo(scope.row.book)">
              查看
            </el-button>
            <el-button
                v-if="scope.row.book.status==='True'"
                type="info"
                size="mini"
                key="True"
                :disabled="true">
              借阅
            </el-button>
            <el-button
                v-if="scope.row.book.status==='False'"
                type="primary"
                size="mini"
                plain
                @click="updateState(scope.row.book)">
              借阅
            </el-button>
            <el-button
                size="mini"
                type="warning"
                plain
                @click="cancelCollect(scope.row.book)">
              取消收藏
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "UserCollection",
  data() {
    return {
      loginForm: {},
      collectionList: [],
      collectListLoading: false
    }
  }, methods: {
    getCollectionList() {
      if (this.collectListLoading)
        return
      this.collectListLoading = true
      this.loginForm = JSON.parse(window.sessionStorage.getItem('loginForm'))
      this.$http.get("/library/collect/queryByCertId?certId=" + this.loginForm.certId).then((res) => {
        this.collectionList = res.data.data
        this.collectListLoading = false
      })
    },
    updateState(row) {
      if (this.collectListLoading)
        return
      this.collectListLoading = true
      row.status = "True"
      let book =
          {
            mAuthor: row.mAuthor,
            mCallNo: row.mCallNo,
            mPubYear: row.mPubYear,
            mPublisher: row.mPublisher,
            mTitle: row.mTitle,
            status: row.status
          }
      this.$http.post("/library/book/save", book).then((res) => {
        this.collectListLoading = false
      })
      this.loginForm = JSON.parse(window.sessionStorage.getItem('loginForm'))
      this.$http.post("/library/lend/save?callNo="
          + book.mCallNo + "&account=" + this.loginForm.account).then((res) => {
        this.collectListLoading = false
        this.$router.push("/User/UserCollection")
      })
    },
    cancelCollect(row) {
      this.$http.post("/library/collect/deleteCollect?certId="
          + this.loginForm.certId + "&callNo=" + row.mCallNo).then(() => {
        this.getCollectionList()
        this.$router.push("/User/UserCollection")
      })
    },
    toBookInfo(item) {
      this.loginForm = JSON.parse(window.sessionStorage.getItem('loginForm'))
      this.$router.push({
        name: "BookInfo",
        params: {
          callNo: item.mCallNo,
          certId: this.loginForm.certId
        }
      })
    }
  },
  created() {
    this.getCollectionList()
  }
}
</script>

<style scoped>
</style>