# BookRecommendation 免费在线部署指南（Render + TiDB Cloud + GitHub Pages）

本文档说明如何**不购买服务器**，把 BookRecommendation 的可交互演示部署到公网，作为作品集 / 简历的"在线体验"入口。

## 一、方案说明

| 组件 | 托管 | 免费额度 | 说明 |
| --- | --- | --- | --- |
| 后端（Spring Boot） | [Render](https://render.com) Free Web Service | 750 小时/月，512MB 内存 | 免绑卡 |
| 数据库 | [TiDB Cloud](https://tidbcloud.com) Serverless | 免费 5GB | **MySQL 兼容**，代码零改动 |
| 前端 | GitHub Pages | 无限 | baseURL 指向 Render 后端（后端 CORS 已全开） |

> **大数据实时链路（Kafka/Spark/Hive）在免费层不运行**。演示的是"预置推荐模式"：推荐结果由 `database/init.sql` 预置，浏览 / 搜索 / 收藏 / 借阅 / 推荐展示等业务功能完整可用（Kafka 生产者懒初始化，broker 不可达时后端照常启动）。完整大数据链路见 [running-local.md](./running-local.md)（本机）与 [docker-deploy.md](./docker-deploy.md)（Docker）。

## 二、准备

1. 注册 [Render](https://render.com)（GitHub 账号最方便）
2. 注册 [TiDB Cloud](https://tidbcloud.com) → 创建 **Serverless** 集群 → **Connect** 拿到连接串（形如 `mysql://root:xxx@host:4000/db`）
3. 克隆本仓库（后端源码与 `database/init.sql`）

## 三、数据库初始化（TiDB）

TiDB Serverless 建库后，在 TiDB Cloud 控制台 SQL Editor 或本机 `mysql` 客户端执行建表与演示数据：

```bash
# 方式一：TiDB Cloud 控制台 SQL Editor 直接粘贴 database/init.sql 内容
# 方式二：本机 mysql 客户端（tiup 或任意 MySQL 客户端，注意 TiDB 端口为 4000）
mysql -h <tidb-host> -P 4000 -u <user> -p < database/init.sql
```

> TiDB 与 MySQL 8 语法高度兼容，`init.sql` 无需修改。若个别语句报错（如引擎关键字），删除对应行即可（不影响其余建表与数据）。

## 四、后端部署（Render）

1. Render Dashboard → **New** → **Web Service** → 连接你的 GitHub 仓库（或直接 Push to Deploy 用现有仓库）
2. 配置：
   - **Name**：`book-recommendation`
   - **Environment**：`Docker`（仓库根目录已有 `Dockerfile`？—— 无，需按下方修改）或 `Java`
   - **Build Command**：`cd backend && mvn package -DskipTests`
   - **Start Command**：`java -jar backend/target/*.jar`
3. **Environment Variables**（Spring Boot 环境变量优先级高于 yml，直接覆盖 `application-dev.yml` 的本地默认值，无需改 profile）：
   - `SPRING_DATASOURCE_URL`：`jdbc:mysql://<tidb-host>:4000/library?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8`
   - `SPRING_DATASOURCE_USERNAME`：TiDB 用户名（形如 `<cluster>.root`）
   - `SPRING_DATASOURCE_PASSWORD`：TiDB 密码
   - `SPRING_KAFKA_BOOTSTRAP_SERVERS`：**留空/不设置**（Kafka 懒初始化，无 broker 照常启动；若设置则指向不存在的地址即可）
4. 创建后等待构建（首次约 5-10 分钟），**Free 实例冷启动约 1 分钟**。
5. 后端默认端口 8081；Render 免费层固定提供 `https://<name>.onrender.com` 公网地址。

> **注意**：Render Free 实例磁盘为临时文件系统（重启即清空），H2/文件型数据不可用——本项目业务数据全部在 TiDB（外部库），无此问题。

## 五、前端部署（GitHub Pages）

1. 修改 `frontend/src/main.js` 中 axios baseURL 为 Render 后端地址：

```js
Axios.defaults.baseURL = "https://book-recommendation.onrender.com/book_recommendation"
```

2. 构建并推送到 GitHub Pages（可选独立仓库或 `gh-pages` 分支）：

```bash
cd frontend
npm install
npm run build          # 产物 dist/
# 将 dist/ 内容推送到 gh-pages 分支（或作品集网站的子路径）
```

3. 访问 `https://<你的用户名>.github.io/<repo>/` 即可在线体验（账号 `2020001 / 123456`）。

## 六、验证

- 首页 / 登录（`2020001 / 123456`）正常
- 图书列表、图书详情、收藏、借阅可用
- "猜你喜欢"展示预置推荐结果（recommend 表 12 条）
- 后端日志无致命错误（Kafka 连接异常为预期，不影响业务）

## 七、成本与限制

- **免费**：Render 750h/月 + TiDB 5GB + GitHub Pages 无限。
- **限制**：Render Free 冷启动约 1 分钟；闲置 15 分钟自动休眠（再次访问自动唤醒）；每月 750 小时约覆盖 31 天 × 24h（单个服务长期运行足够，多个服务需注意额度）。
- 大数据实时链路不在本方案内（见 running-local / docker-deploy）。
