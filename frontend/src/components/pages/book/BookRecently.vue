<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="searchForm" @submit.native.prevent>
        <el-form-item>
          <el-input
              clearable
              placeholder="图书名"
              v-model="searchForm.mTitle"
              @keydown.enter.native="getNewBookTable">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="getNewBookTable">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table
          v-loading.fullscreen.lock="newBookTableLoading"
          :data="newBookTable"
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
            width="300"
            align="center"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="bookExtend.mPublisher"
            label="出版社"
            width="200"
            align="center"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="bookExtend.mPubYear"
            label="出版年份"
            width="100"
            align="center"
            :formatter="changePubYearFormat"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="bookExtend.status"
            label="状态"
            width="100"
            align="center"
            :formatter="changeStateFormat"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="operate"
            label="操作"
            width="230"
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
          :total="totalCount"
          layout="total,sizes,prev,pager,next,jumper"
          style="margin-top: 30px">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "BookRecently",
  data() {
    return {
      searchForm: {
        mTitle: ""
      },
      loginForm: {},
      newBookTable: [],
      pageIndex: 1,
      pageSize: 10,
      totalCount: 0,
      newBookTableLoading: false,
      buttonType: ""
    }
  },
  methods: {
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getNewBookTable()
    },
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getNewBookTable()
    },
    getNewBookTable() {
      this.loginForm = JSON.parse(window.sessionStorage.getItem('loginForm'))
      if (this.newBookTableLoading)
        return;
      this.newBookTableLoading = true
      let params = {
        params: {
          pageIndex: this.pageIndex, // 页码
          pageSize: this.pageSize, // 当前页的容量
          mCallNo: this.newBookTable.mCallNo,
          certId: this.loginForm.certId,
          mTitle: this.searchForm.mTitle
        }
      }
      this.$http.get("/library/newBook/queryNewBook?currentPage=" + this.pageIndex, params).then((res) => {
        this.newBookTable = res.data.data.list
        this.totalCount = res.data.data.totalCount
        this.newBookTableLoading = false
      })
    },
    changeStateFormat(row) {
      if (row.bookExtend.status === "True")
        return "已借出"
      else if (row.bookExtend.status === "False")
        return "未借出"
    },
    changePubYearFormat(row) {
      return row.bookExtend.mPubYear.substring(0, 4)
    },
    updateState(row) {
      if (this.newBookTableLoading)
        return
      this.newBookTableLoading = true
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
        this.newBookTableLoading = false
      })
      this.$http.post("/library/lend/save?callNo="
          + book.mCallNo + "&account=" + this.loginForm.account)
          .then((res) => {
            this.newBookTableLoading = false
          })
      this.$router.push("/Book/BookRecently")
    },
    updateCollectState(row) {
      if (this.newBookTableLoading)
        return
      this.newBookTableLoading = true
      this.$http.post("/library/collect/saveCollect?certId="
          + this.loginForm.certId + "&callNo=" + row.mCallNo).then(()=>{
        this.newBookTableLoading = false
        this.getNewBookTable()
        this.$router.push("/Book/BookRecently")
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
    }
  },
  mounted() {
    this.getNewBookTable()
  }
}
</script>

<style scoped>

</style>