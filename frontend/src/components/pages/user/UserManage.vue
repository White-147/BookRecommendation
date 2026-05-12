<template>
  <div>
    <el-card>
      <el-descriptions
          title="用户信息"
          v-loading.fullscreen.lock="userLoading"
          :column="1" border
          :labelStyle="LS"
          :contentStyle="CS">
        <template slot="extra">
          <el-button
              type="primary"
              size="small"
              @click="dialogFormVisible = true">
            编辑
          </el-button>
        </template>
        <el-descriptions-item label="头像">
          <div v-cloak>
            <img id="userImg" :src="getPath()" alt="未找到图片">
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="账号">{{ this.loginForm.account }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ this.loginForm.username }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ this.loginForm.certId }}</el-descriptions-item>
      </el-descriptions>

      <el-dialog title="编辑用户信息" :visible.sync="dialogFormVisible">
        <el-form :model="editUserForm">
          <el-form-item label="用户名" :label-width="formLabelWidth">
            <el-input v-model="editUserForm.username" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="用户头像" :label-width="formLabelWidth">
            <el-upload
                ref="upload"
                action="http://localhost:8081/book_recommendation/sys/user/upload"
                :headers="header"
                :limit="1"
                :show-file-list="false"
                :auto-upload="false">
              <el-button size="mini" type="primary">上传头像</el-button>
            </el-upload>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="getUser">确 定</el-button>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "UserManage",
  data() {
    return {
      userLoading: false,
      dialogFormVisible: false,
      formLabelWidth: '120px',
      editUserForm: {
        account: JSON.parse(sessionStorage.getItem('loginForm')).account,
        username: ""
      },
      loginForm: JSON.parse(sessionStorage.getItem('loginForm')),
      header: {
        Authorization: sessionStorage.getItem("token"),
        Account: JSON.parse(sessionStorage.getItem('loginForm')).account
      },
      LS:{
        'text-align': 'center',
        'min-width': '110px',
        'word-break': 'keep-all'
      },
      CS:{
        "max-width":"300px",
        'text-align': 'center',
        'min-width': '250px',
        'word-break': 'break-all'
      }
    }
  },
  methods: {
    getPath() {
      return this.loginForm.head ? require('@/assets/image/head/' + this.loginForm.head) : ''
    },
    getUser() {
      if(this.userLoading)
        return
      this.userLoading = true
      if (this.editUserForm.username !== "") {
        this.$http.post("/sys/user/updateUserName?username=" +
            this.editUserForm.username + "&account=" +
            this.editUserForm.account).then((res) => {
          this.$http.get("/sys/user/selectByAccount?account=" +
              this.loginForm.account).then((res) => {
            if (this.$refs.upload._data.uploadFiles.length > 0)
              this.$refs.upload.submit()
            this.loginForm = res.data.data
            sessionStorage.setItem("loginForm", JSON.stringify(this.loginForm))
            this.dialogFormVisible = false
            this.userLoading = false
            this.editUserForm.username = ""
          })
        })
      } else {
        if (this.$refs.upload._data.uploadFiles.length > 0)
          this.$refs.upload.submit()
        this.$http.get("/sys/user/selectByAccount?account=" +
            this.loginForm.account).then((res) => {
          this.loginForm = res.data.data
          sessionStorage.setItem("loginForm", JSON.stringify(this.loginForm))
          this.dialogFormVisible = false
          this.userLoading = false
        })
      }
    }
  }
}
</script>

<style scoped>
[v-cloak] {
  display: none;
}

#userImg {
  width: 100px;
  height: 100px;
}
</style>