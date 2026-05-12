<template>
  <div>
    <el-container>
      <el-header height="100">
        <img src="@/assets/image/common/logo.png" alt="">
      </el-header>

      <el-main>
        <div class="login_container">
          <div class="login_form">

            <p class="login_title">欢迎使用图书推荐系统</p>

            <el-form
                style="width: 420px;"
                ref="loginForm"
                :model="loginForm"
                :rules="loginFormRules"
                :hide-required-asterisk="true"
                label-width="80px">
              <el-form-item label="账号" prop="account">
                <el-input
                    prefix-icon="el-icon-user"
                    clearable
                    placeholder="请输入账号"
                    v-model="loginForm.account"
                    @keydown.enter.native="enterLogin(loginForm)">
                </el-input>
              </el-form-item>

              <el-form-item label="密码" prop="password" style="margin-bottom: 0">
                <el-input
                    prefix-icon="el-icon-lock"
                    type="password"
                    clearable
                    placeholder="请输入密码"
                    show-password
                    v-model="loginForm.password"
                    @keydown.enter.native="enterLogin(loginForm)">
                </el-input>
              </el-form-item>

              <p id="forgetPass" @click="openForgetWindow"><a>忘记密码?</a></p>

              <el-form-item align="center">
                <el-button
                    plain
                    type="primary"
                    @click="submitFormData">
                  登陆
                </el-button>
                <el-button @click="handlerRegister">注册</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>

export default {
  name: "Login",
  data() {
    return {
      loginForm: {
        account: "",
        password: "",
        username: "",
        time: "",
        cert_id: "",
        state: 0,
        head: ""
      },
      loginFormRules: {
        account: [{
          required: true,
          message: '请输入账号',
          trigger: 'blur'
        }],
        password: [{
          required: true,
          message: '请输入密码',
          trigger: 'blur'
        }]
      }
    }
  },
  methods: {
    // 提交登录表单的数据
    submitFormData() {
      this.$refs['loginForm'].validate((valid) => {
        if (valid) {
          this.$http.post("/login", this.loginForm).then((res) => {
                if (res.data.code === 200) {
                  // 表示登录成功
                  // 存储相关的token信息  token信息在响应的header中
                  sessionStorage.setItem("token", res.headers.authorization)
                  this.$http.get("/sys/user/selectByAccount?account=" + this.loginForm.account).then((res) => {
                    this.loginForm = res.data.data
                    sessionStorage.setItem("loginForm", JSON.stringify(this.loginForm))
                    this.$router.push("/Home")
                  }).catch((err) => {
                    console.log(err)
                  })
                } else {
                  // 表示登录失败
                  this.$message.error(res.data.msg)
                }
              })
        } else return false;
      })
    },
    enterLogin(item) {
      if (item.account === "")
        this.$message.error("请输入账号")
      else if (item.password === "")
        this.$message.error("请输入密码")
      else this.submitFormData()
    },
    openForgetWindow() {
      this.$prompt('请输入账户', '忘记密码', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({value}) => {
        this.$http.get("/sys/user/selectByAccount?account=" + value).then((res) => {
          if (res.data.data !== null) {
            let userList = res.data.data
            this.$prompt('请输入重置的密码', '忘记密码', {
              confirmButtonText: '确定',
              cancelButtonText: '取消'
            }).then(({value}) => {
              if (value === userList.password)
                this.$message({
                  type: 'info',
                  message: '重置密码与原密码相同'
                })
              else {
                userList.password = value
                this.$http.post("/sys/user/update", userList).then((res) => {
                  this.$message({
                    type: 'success',
                    message: '重置密码成功'
                  })
                })
              }
            }).catch(() => {
              this.$message({
                type: 'info',
                message: '取消输入'
              })
            })
          } else {
            this.$message({
              type: 'info',
              message: '账号不存在'
            })
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消输入'
        })
      })
    },
    handlerRegister() {
      this.$router.push('/Register')
    }
  }
}
</script>

<style scoped>
.login_container {
  width: 100%;
  background-size: 1278px 559px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
  background: inherit;
  border-radius: 18px;
  overflow: hidden;
}

.login_form {
  width: 500px;
  margin: 0 auto;
  padding: 0 55px 15px 35px;
  border: none;
  border-radius: 5px;
  box-shadow: 0 0 25px #cac6c6;
  background-color: rgba(255, 255, 255, 0.85);
}

.login_form::before {
  background: inherit;
  box-shadow: inset 0 0 0 200px rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(3px);
  margin: -30px;
}

.login_title {
  font-weight: 700;
  text-decoration: none;
  color: #30bdd6;
  font-size: 32px;
  margin-top: 50px;
  margin-bottom: 30px;
  text-align: center;
}

#forgetPass{
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

#forgetPass a{
  color: brown;
  cursor: pointer;
}

.el-container {
  width: 100%;
  min-height: 89%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.el-header {
  background-color: #30bdd6;
  padding-left: 0;
  box-shadow: -10px 10px 10px -10px;
}

.el-main {
  position: absolute;
  background: center no-repeat url("@/assets/image/common/background.jpg");
  background-size: 100%;
  top: 90px;
  bottom: 0;
  width: 100%;
}
</style>