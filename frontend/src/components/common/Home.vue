<template>
  <div>
    <el-row>
      <el-col :span="8">
        <el-card class="box-card">
          <div v-cloak class="user">
            <img :src="getPath()" alt="暂未找到图片">
            <div class="userInfo">
              <p class="name">{{ loginForm.username }}</p>
              <p class="userId">{{ loginForm.certId }}</p>
            </div>
          </div>
          <div class="loginInfo">
            <p>登陆时间:<span>{{ loginForm.time }}</span></p>
          </div>
        </el-card>

        <el-card class="lend_info"
                 style="margin-top: 20px;">
          <el-table
              v-loading.fullscreen.lock="lendListLoading"
              :data="pageLendList"
              empty-text="还没有借书哦"
              stripe
              style="width: 100%">
            <el-table-column
                prop="lendDate"
                label="日期"
                width="100">
            </el-table-column>
            <el-table-column
                prop="name"
                label="姓名"
                width="100">
            </el-table-column>
            <el-table-column
                prop="mTitle"
                label="图书">
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="16" style="padding-left: 20px;">
        <el-card class="box-card" style="margin-bottom: 20px;">
          <div slot="header" class="announcement">
            <span>系统公告</span>
          </div>
          <div v-for="item in lendList" :key="item.callId" class="announcement_context item">
            <i class="el-icon-timer">【借阅提醒】{{ item.name }}同学请于
              {{ getLendDate(item) }}之前归还《{{ item.mTitle }}》。</i>
          </div>
        </el-card>

        <el-card class="echarts">
          <div ref="echarts" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: "Home",
  data() {
    return {
      loginForm: {},
      pageLendList: [],
      lendList: [],
      pageIndex: 1,
      pageSize: 5,
      pageSizes: 10,
      totalCount: 0,
      lendListLoading: false,
      leaveTime: 7
    }
  },
  methods: {
    getLendDate(item) {
      let date = this.getDate(parseInt(item.lendDate.split("-")[0]),
          parseInt(item.lendDate.split("-")[1]),
          parseInt(item.lendDate.split("-")[2]) + this.leaveTime)
      return String(date[0]) +
          "-" + (date[1] < 10 ? "0" + String(date[1]) : String(date[1])) +
          "-" + (date[2] < 10 ? "0" + String(date[2]) : String(date[2]))
    },
    getDate(year, month, day) {
      let eachMonth = [31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
      if ((year % 4 === 0 && year % 100 !== 0 || year % 400 === 0)) {
      } else
        eachMonth[1] = 28
      if (day <= eachMonth[month - 1])
        return [year, month, day]
      else
        return [year, month + 1, day - eachMonth[month - 1]]
    },
    getPath() {
      return this.loginForm.head ? require('@/assets/image/head/' + this.loginForm.head) : ''
    },
    getLendList() {
      if (this.lendListLoading)
        return
      this.lendListLoading = true
      let time = new Date().toLocaleDateString().split('/').join('-')
      this.loginForm = JSON.parse(window.sessionStorage.getItem('loginForm'))
      this.loginForm.time = time
      let params = {
        params: {
          pageIndex: this.pageIndex,
          pageSize: this.pageSize,
          certId: this.loginForm.certId
        }
      }
      this.$http.get("/library/lend/list", params).then((res) => {
        this.pageLendList = res.data.data.list
        this.lendListLoading = false
      })
      this.$http.get("/library/lend/queryLends?certId=" + this.loginForm.certId).then((res) => {
        this.lendList = res.data.data
        this.lendListLoading = false
      })
      let form = {
        account: this.loginForm.account,
        username: this.loginForm.username,
        time: this.loginForm.time,
      }
      this.$http.post("/sys/user/update", form).then(() => {
        this.lendListLoading = false
      })
    },
    getEChart() {
      let date = []
      let num = {}
      let numEachDay = []
      this.$http.get("/library/lend/queryLends?certId=" + this.loginForm.certId).then((res) => {
        res.data.data.forEach(item => {
          date.push(item.lendDate)
          if (!num[item.lendDate])
            num[item.lendDate] = 1
          else
            num[item.lendDate] += 1
        })
        for (let key in num)
          numEachDay.push(num[key])
        date = [...new Set(date)]
        let myECharts = echarts.init(this.$refs.echarts)
        let option = {
          title: {text: '用户借阅频次'},
          xAxis: {
            type: 'category',
            name: '借阅时间',
            boundaryGap: false,
            data: date
          },
          yAxis: {
            type: 'value',
            name: '借阅量',
            min: 0,
            max: 5
          },
          tooltip: { // 鼠标悬浮提示框显示 X和Y 轴数据
            trigger: 'axis',
            backgroundColor: 'rgba(32, 33, 36,.7)',
            borderColor: 'rgba(32, 33, 36,0.20)',
            borderWidth: 1,
            textStyle: { // 文字提示样式
              color: '#fff',
              fontSize: '12'
            },
            axisPointer: { // 坐标轴虚线
              type: 'cross',
              label: {backgroundColor: '#6a7985'}
            }
          },
          series: [{data: numEachDay, type: 'line'}]
        }
        myECharts.setOption(option);
      })
    }
  },
  created() {
    setTimeout(() => {
      this.getEChart();
    }, 1000)
  },
  mounted() {
    this.getLendList()
  }
}
</script>

<style scoped>
.el-row-inline {
  display: flex;
  flex-wrap: wrap;
}

.el-card {
  height: 100%;
}

[v-cloak] {
  display: none;
}

.user {
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #999999;
  display: flex;
  align-items: center;
}

.user img {
  margin-left: 40px;
  width: 150px;
  height: 150px;
  border-radius: 50%;
}

.userInfo {
  margin-left: 40px;
}

.name {
  font-size: 30px;
  margin-bottom: 20px;
}

.loginInfo p {
  font-size: 14px;
  color: #999999;
  line-height: 28px;
}

.loginInfo p span {
  color: #666666;
  margin-left: 16px;
}

.announcement span {
  font-size: 22px;
  color: black;
}

.announcement_context {
  padding-bottom: 10px;
  margin-bottom: 10px;
  border-bottom: 1px #f2f2f2 solid;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.foot_table_title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  text-decoration: none;
  color: #666666;
  text-align: center;
}
</style>