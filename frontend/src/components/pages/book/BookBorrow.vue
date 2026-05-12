<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="dataForm" @submit.native.prevent>
        <el-form-item>
          <el-input
              clearable
              placeholder="图书名"
              v-model="dataForm.mTitle"
              @keydown.enter.native="getDataTable">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataTable">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table
          v-loading.fullscreen.lock="dataTableLoading"
          :data="dataTable"
          border style="width: 100%">
        <el-table-column
            prop="mTitle"
            label="书名"
            align="center"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="mAuthor"
            label="作者"
            width="300"
            align="center"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="mPublisher"
            label="出版社"
            width="300"
            align="center"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="status"
            label="状态"
            width="150"
            align="center"
            :formatter="changeStateFormat"
            :resizable="false">
        </el-table-column>
        <el-table-column
            prop="operate"
            label="操作"
            width="250"
            align="center"
            :resizable="false">
          <template slot-scope="scope">
            <el-button
                size="mini"
                plain
                @click="toBookInfo(scope.row)">
              查看
            </el-button>
            <el-button
                v-if="scope.row.collect===null"
                size="mini"
                type="warning"
                plain
                @click="updateCollectState(scope.row)">
              收藏
            </el-button>
            <el-button
                v-if="scope.row.collect!==null"
                type="info"
                size="mini"
                :disabled="true">
              收藏
            </el-button>
            <el-button
                v-if="scope.row.status==='True'"
                type="info"
                size="mini"
                key="True"
                :disabled="true">
              借阅
            </el-button>
            <el-button
                v-if="scope.row.status==='False'"
                type="primary"
                size="mini"
                plain
                @click="updateState(scope.row)">
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
  name: "BookBorrow",
  data() {
    return {
      dataForm: {
        mTitle: ""
      },
      loginForm: {},
      dataTable: [],
      pageIndex: 1,
      pageSize: 10,
      totalCount: 0,
      dataTableLoading: false,
      buttonType: ""
    }
  },
  methods: {
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getDataTable()
    },
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getDataTable()
    },
    getDataTable() {
      if (this.dataTableLoading)
        return;
      this.dataTableLoading = true
      let params = {
        params: {
          pageIndex: this.pageIndex, // 页码
          pageSize: this.pageSize, // 当前页的容量
          mCallNo: this.dataForm.mCallNo,
          certId: this.loginForm.certId,
          mTitle: this.dataForm.mTitle
        }
      }
      this.$http.get("/library/book/queryBook?pageSize="
          + this.pageSize + "&currentPage=" + this.pageIndex, params).then((res) => {
        this.dataTable = res.data.data.list
        this.totalCount = res.data.data.totalCount
        this.dataTableLoading = false
      })
    },
    changeStateFormat(row) {
      if (row.status === "True")
        return "已借出"
      else if (row.status === "False")
        return "未借出"
    },
    updateState(row) {
      if (this.dataTableLoading)
        return
      this.dataTableLoading = true
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
        this.dataTableLoading = false
      })
      this.$http.post("/library/lend/save?callNo="
          + book.mCallNo + "&account=" + this.loginForm.account)
          .then((res) => {
            this.dataTableLoading = false
            this.$router.push("/Book/BookBorrow")
          })
    },
    updateCollectState(row) {
      if (this.dataTableLoading)
        return
      this.dataTableLoading = true
      this.$http.post("/library/collect/saveCollect?certId="
          + this.loginForm.certId + "&callNo=" + row.mCallNo).then(() => {
        this.dataTableLoading = false
        this.getDataTable()
        this.$router.push("/Book/BookBorrow")
      })
    },
    toBookInfo(item) {
      this.$router.push({
        name: "BookInfo",
        params: {
          callNo: item.mCallNo,
          certId: this.loginForm.certId
        },
        replace: true
      })
    }
  },
  mounted() {
    this.getDataTable();
  }, created() {
    this.loginForm = JSON.parse(window.sessionStorage.getItem('loginForm'))
  }
}
</script>

<style scoped>
</style>