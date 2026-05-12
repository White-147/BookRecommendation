<template>
  <div>
    <el-card>
      <el-table
          v-loading.fullscreen.lock="stateStatusLoading"
          empty-text="还没有推荐数据哦，去多借些书看看吧~"
          :data="recommendTable"
          border style="width: 100%">
        <el-table-column
            prop="bookExtend.mTitle"
            label="书名"
            align="center"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="bookExtend.mAuthor"
            label="作者"
            width="350"
            align="center"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="bookExtend.mPublisher"
            label="出版社"
            width="300"
            align="center"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="bookExtend.status"
            label="状态"
            width="200"
            align="center"
            :formatter="changeStateFormat"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="borrow"
            label="操作"
            width="250"
            align="center"
            :resizable="false">
          <template slot-scope="scope">
            <el-button
                size="mini"
                plain
                @click="toBookInfo(scope.row.bookExtend)">
              查看
            </el-button>
            <el-button
                v-if="scope.row.bookExtend.collect===null"
                size="mini"
                type="warning"
                plain
                @click="updateCollectState(scope.row.bookExtend)">
              收藏
            </el-button>
            <el-button
                v-if="scope.row.bookExtend.collect!==null"
                type="info"
                size="mini"
                :disabled="true">
              收藏
            </el-button>
            <el-button
                v-if="scope.row.bookExtend.status==='True'"
                type="info"
                size="mini"
                key="True"
                :disabled="true">
              借阅
            </el-button>
            <el-button
                v-if="scope.row.bookExtend.status==='False'"
                type="primary"
                size="mini"
                plain
                @click="updateState(scope.row.bookExtend)">
              借阅
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          @size-change="sizeChangeHandle"
          @current-change="currentChangeHandle"
          :current-page="pageIndex"
          :page-size="pageSize"
          :page-sizes="pageSizes"
          :total="totalCount"
          layout="total,sizes,prev,pager,next,jumper"
          style="margin-top: 30px">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "BookRecommend",
  data() {
    return {
      loginForm: {},
      recommendTable: [],
      pageIndex: 1,
      pageSize: 10,
      pageSizes: [10, 15, 20, 25],
      totalCount: 0,
      stateStatusLoading: false
    }
  },
  methods: {
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getRecommend()
    },
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getRecommend()
    },
    getRecommend() {
      this.stateStatusLoading = true
      this.loginForm = JSON.parse(window.sessionStorage.getItem('loginForm'))
      let params = {
        params: {
          pageIndex: this.pageIndex, // 页码
          pageSize: this.pageSize, // 当前页的容量
          certId: this.loginForm.certId
        }
      }
      this.$http.get("/library/recommend/getRecommend?pageSize="
          + this.pageSize + "&currentPage=" + this.pageIndex, params).then((res) => {
        this.recommendTable = res.data.data.list
        this.totalCount = res.data.data.totalCount
        this.stateStatusLoading = false
      })
    },
    toBookInfo(item) {
      this.$router.push({
        name: "BookInfo",
        params: {
          callNo: item.mCallNo,
          certId: this.loginForm.certId
        }
      })
    },
    updateCollectState(row) {
      if (this.stateStatusLoading)
        return
      this.stateStatusLoading = true
      this.$http.post("/library/collect/saveCollect?certId="
          + this.loginForm.certId + "&callNo=" + row.mCallNo).then(() => {
        this.stateStatusLoading = false
        this.getRecommend()
        this.$router.push("/Book/BookRecommend")
      })
    },
    changeStateFormat(row) {
      if (row.bookExtend.status === "True")
        return "已借出"
      else if (row.bookExtend.status === "False")
        return "未借出"
    },
    updateState(row) {
      if (this.stateStatusLoading)
        return
      this.stateStatusLoading = true
      row.status = "True"
      let book =
          {
            mAuthor: row.mAuthor,
            mCallNo: row.mCallNo,
            mIsbn: row.mIsbn,
            mPubYear: row.mPubYear,
            mPublisher: row.mPublisher,
            mTitle: row.mTitle,
            status: row.status
          }
      this.$http.post("/library/book/save", book).then((res) => {
        this.stateStatusLoading = false
      })
      this.$http.post("/library/lend/save?callNo="
          + book.mCallNo + "&account=" + this.loginForm.account)
          .then((res) => {
            this.stateStatusLoading = false
            this.$router.push("/Book/BookRecommend")
          })
    },
  },
  mounted() {
    this.getRecommend()
  }
}
</script>

<style scoped>

</style>