<template>
  <div>
    <el-container>
      <el-header height="100">
        <img src="@/assets/image/common/logo.png" alt="">
      </el-header>

      <el-main>
        <div class="register_container">
          <div class="register_form">
            <p class="register_title">注册账号</p>
            <el-form
                style="width: 420px;"
                ref="registerForm"
                :model="registerForm"
                :rules="registerFormRules"
                :hide-required-asterisk="true"
                label-width="80px">
              <el-form-item label="账号" prop="account">
                <el-input
                    prefix-icon="el-icon-user"
                    clearable
                    placeholder="请输入账号"
                    v-model="registerForm.account">
                </el-input>
              </el-form-item>

              <el-form-item label="用户名" prop="username">
                <el-input
                    prefix-icon="el-icon-user-solid"
                    clearable
                    placeholder="请输入用户名"
                    v-model="registerForm.username">
                </el-input>
              </el-form-item>

              <el-form-item label="密码" prop="password">
                <el-input
                    prefix-icon="el-icon-lock"
                    type="password"
                    clearable
                    placeholder="请输入密码"
                    show-password
                    v-model="registerForm.password">
                </el-input>
              </el-form-item>

              <el-form-item>
                <a style="font-size: 14px;color: #606266;padding-right: 10px;">是否为本校学生</a>
                <el-switch v-model="flag"></el-switch>
              </el-form-item>

              <el-form-item
                  v-if="flag"
                  label="学号"
                  prop="certId">
                <el-input
                    prefix-icon="el-icon-star-on"
                    clearable
                    placeholder="请输入学号"
                    v-model="registerForm.certId">
                </el-input>
              </el-form-item>

              <el-form-item align="center">
                <el-button
                    plain
                    type="primary"
                    class="main-button"
                    @click="submitRegister()">
                  注册
                </el-button>
                <el-button @click="returnLogin">返回</el-button>
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
  name: "Register",
  data() {
    let checkAccount = (rule, value, callback) => {
      let regAccount = /[a-zA-z0-9]$/
      if (value === "")
        callback(new Error("请输入账号"))
      else if (regAccount.test(value) === false)
        callback(new Error("请输入英文或数字"))
      else {
        this.$http.get("/sys/user/selectByAccount?account=" + this.registerForm.account).then((res) => {
          if (res.data.data)
            callback(new Error("账号已被注册"))
          // else callback()
          else {
            this.$http.get("/sys/user/selectByCertId?certId=" + this.registerForm.account).then((res) => {
              if (res.data.data)
                callback(new Error("该账号不可用"))
              else callback()
            })
          }
        })
      }
    };
    let checkUserName = (rule, value, callback) => {
      let regUserName = /^[\u4E00-\u9FA5A-Za-z0-9]+$/
      if (value === "")
        callback(new Error("请输入用户名"))
      else if (regUserName.test(value) === false)
        callback(new Error("请输入中文、英文或数字组合"))
      else callback()
    };
    let checkCertId = (rule, value, callback) => {
      if (value === "")
        callback(new Error("请输入学号"))
      else {
        this.$http.get("/library/reader/checkCertId?certId=" + this.registerForm.certId).then((res) => {
          if (!res.data.data)
            callback(new Error("学号不存在"))
          else {
            this.$http.get("/sys/user/selectByCertId?certId=" + this.registerForm.certId).then((res) => {
              if (res.data.data)
                callback(new Error("学号已被注册"))
              else callback()
            })
          }
        })
      }
    };
    return {
      registerForm: {
        account: "",
        username: "",
        password: "",
        certId: "",
        status: 1
      },
      registerFormRules: {
        account: [{validator: checkAccount, trigger: 'blur'}],
        username: [{validator: checkUserName, trigger: 'blur'}],
        password: [{required: true, message: '请输入密码', trigger: 'blur'}],
        certId: [{validator: checkCertId, trigger: 'blur'}]
      },
      flag: false
    }
  },
  methods: {
    submitRegister() {
      this.$refs['registerForm'].validate((valid) => {
        if (valid) {
          if(this.registerForm.certId==="")
            this.registerForm.certId = this.registerForm.account
          this.$http.post("/sys/user/save", this.registerForm).then(() => {
            this.$message({
              type: 'success',
              message: '注册成功'
            })
            this.$router.push('/')
          })
        } else
          this.$message.error("注册失败")
      })
    },
    returnLogin() {
      this.$router.push("/")
    }
  }
}
</script>

<style scoped>
.register_container {
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

.register_title {
  font-weight: 700;
  text-decoration: none;
  color: #30bdd6;
  font-size: 32px;
  margin-top: 20px;
  margin-bottom: 30px;
  text-align: center;
}

.register_form {
  width: 500px;
  margin: 0 auto;
  padding: 0 55px 15px 35px;
  border: none;
  border-radius: 5px;
  box-shadow: 0 0 25px #cac6c6;
  background-color: rgba(255, 255, 255, 0.85);
}

.register_form::before {
  background: inherit;
  box-shadow: inset 0 0 0 200px rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(3px);
  margin: -30px;
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