<template>
  <div>
    <el-card>
      <div>
        <el-table
            v-loading.fullscreen.lock="lendListLoading"
            empty-text="快去借阅想看的书吧~"
            :data="lendList"
            border style="width: 100%">
          <el-table-column
              property="certId"
              label="学号"
              align="center"
              width="150"
              :resizable="false">
          </el-table-column>
          <el-table-column
              property="name"
              label="姓名"
              align="center"
              width="150"
              :resizable="false">
          </el-table-column>
          <el-table-column
              prop="mTitle"
              label="书名"
              align="center"
              :resizable="false">
          </el-table-column>
          <el-table-column
              property="mAuthor"
              label="作者"
              align="center"
              :resizable="false">
          </el-table-column>
          <el-table-column
              property="mPublisher"
              label="出版社"
              align="center"
              :resizable="false">
          </el-table-column>
          <el-table-column
              property="lendDate"
              label="借阅日期"
              align="center"
              width="150"
              :resizable="false">
          </el-table-column>
          <el-table-column
              label="归还"
              align="center"
              width="200"
              :resizable="false">
            <template slot-scope="scope">
              <el-button
                  size="mini"
                  plain
                  @click="toBookInfo(scope.row)">
                查看
              </el-button>
              <el-button
                  size="mini"
                  type="primary"
                  @click="handleReturn(scope.row)">
                归还
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="pageIndex"
            :page-size="pageSize"
            :total="totalPage"
            layout="total,sizes,prev,pager,next,jumper"
            style="margin-top: 30px">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>

export default {
  name: "UserBorrowInfo",
  data() {
    return {
      loginForm: {},
      lendList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      lendListLoading: false,
    }
  },
  methods: {
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getLendList()
    },
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getLendList()
    },
    getLendList() {
      if (this.lendListLoading)
        return
      this.lendListLoading = true
      this.loginForm = JSON.parse(window.sessionStorage.getItem('loginForm'))
      let params = {
        params: {
          pageIndex: this.pageIndex,
          pageSize: this.pageSize,
          certId: this.loginForm.certId
        }
      }
      this.$http.get("/library/lend/list", params).then((res) => {
        this.lendList = res.data.data.list
        this.totalPage = res.data.data.totalCount
        this.lendListLoading = false
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
    handleReturn(data) {
      this.$confirm('归还后需要重新借阅, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (this.lendListLoading)
          return
        this.lendListLoading = true
        this.$http.post("/library/lend/delete?certId=" + data.certId
            + "&callNo=" + data.mCallNo).then((res) => {
          this.lendListLoading = false
        })
        this.$http.get("/library/book/selectByCallNo?callNo="
            + data.mCallNo + "&certId=" + data.certId).then((res) => {
          let book = res.data.data
          book.status = "False"
          this.$http.post("/library/book/save", book).then((res) => {
            this.lendListLoading = false
          })
          this.getLendList()
          this.$router.push("/User/UserBorrowInfo")
        })
        this.$message({
          type: 'success',
          message: '归还成功!',
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消归还'
        })
      })
    }
  },
  mounted() {
    this.getLendList()
  }
}
</script>

<style scoped>
</style>