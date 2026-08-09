# BookRecommendation 免费在线部署（Render + TiDB Cloud + GitHub Pages）

> **✅ 已实际部署验证（2026-08-09）**
> - 前端：https://white-147.github.io/BookRecommendation/ （GitHub Pages + Actions 自动部署）
> - 后端：https://book-recommendation-9542.onrender.com/book_recommendation （Render Free + Docker）
> - 数据库：TiDB Cloud Serverless（MySQL 兼容，端口 4000）
> - 测试账号：`2020001 / 123456`

本文档说明如何**不购买服务器**，把 BookRecommendation 的可交互演示部署到公网，作为作品集 / 简历的"在线体验"入口。以下步骤全部经过实际验证。

## 一、方案说明

| 组件 | 托管 | 免费额度 | 说明 |
| --- | --- | --- | --- |
| 后端（Spring Boot） | [Render](https://render.com) Free Web Service | 750 小时/月，512MB 内存 | 免绑卡 |
| 数据库 | [TiDB Cloud](https://tidbcloud.com) Serverless | 免费 5GB | **MySQL 兼容**，代码零改动 |
| 前端 | GitHub Pages | 无限 | baseURL 指向 Render 后端（后端 CORS 已全开） |

> **大数据实时链路（Kafka/Spark/Hive）在免费层不运行**。演示的是"预置推荐模式"：推荐结果由 `database/init.sql` 预置，浏览 / 搜索 / 收藏 / 借阅 / 推荐展示等业务功能完整可用（Kafka 生产者懒初始化，broker 不可达时后端照常启动）。完整大数据链路见 [running-local.md](./running-local.md)（本机）与 [docker-deploy.md](./docker-deploy.md)（Docker）。

## 二、准备

1. 注册 [Render](https://render.com)（GitHub 账号最方便）
2. 注册 [TiDB Cloud](https://tidbcloud.com) → 创建 **Serverless** 集群 → **Connect** 拿到连接信息：host（形如 `gateway01.xxx.prod.aws.tidbcloud.com`）、端口 `4000`、用户名（形如 `<cluster>.root`）、密码
3. 克隆本仓库（后端源码与 `database/init.sql`）

## 三、数据库初始化（TiDB）

TiDB Serverless 建库后，用 TiDB Cloud 控制台 SQL Editor（或本机 `mysql` 客户端，端口 4000）执行 `database/init.sql` 建表与演示数据：

```bash
mysql -h <tidb-host> -P 4000 -u <user> -p < database/init.sql
```

> TiDB 与 MySQL 8 语法高度兼容，`init.sql` 无需修改。TiDB Serverless 免费层**并发连接数很低（约 10）**，连接池参数需要调小（见第四节环境变量）。

## 四、后端部署（Render，Docker 方式）

1. Render Dashboard → **New** → **Web Service** → 连接你的 GitHub 仓库
2. 配置：
   - **Name**：`book-recommendation`（会生成 `https://book-recommendation-xxxx.onrender.com`）
   - **Environment**：**Docker**（仓库根目录 `Dockerfile`，GitHub push 自动重新构建部署）
   - **Region**：任意（建议选离你近的，如 Singapore）
   - **Health Check Path**：**清空留空**（项目无 actuator，默认探测 `/` 返回 404 会被判不健康导致重启循环）
3. **Environment Variables**（Spring Boot 环境变量覆盖 `application-dev.yml` 的 druid 前缀配置，无需改 profile）：
   - `SPRING_DATASOURCE_DRUID_URL`：`jdbc:mysql://<tidb-host>:4000/library?sslMode=VERIFY_IDENTITY&enabledTLSProtocols=TLSv1.2,TLSv1.3&useUnicode=true&characterEncoding=utf-8&nullCatalogMeansCurrent=true&connectTimeout=10000&socketTimeout=60000`
   - `SPRING_DATASOURCE_DRUID_USERNAME`：TiDB 用户名（形如 `<cluster>.root`）
   - `SPRING_DATASOURCE_DRUID_PASSWORD`：TiDB 密码
   - `SPRING_DATASOURCE_DRUID_INITIAL_SIZE`：`2`
   - `SPRING_DATASOURCE_DRUID_MIN_IDLE`：`2`
   - `SPRING_DATASOURCE_DRUID_MAX_ACTIVE`：`10`
   - `SPRING_KAFKA_BOOTSTRAP_SERVERS`：**不设置**（Kafka 懒初始化，无 broker 照常启动）

> 为什么是这些值：
> - 项目数据源配置在 `application-dev.yml` 的 `spring.datasource.druid:` 前缀下（druid-spring-boot-starter 1.2.4 绑定该前缀），环境变量用 `SPRING_DATASOURCE_DRUID_*` 才能覆盖
> - TiDB Cloud Serverless **强制 TLS**，官方 JDBC 推荐 `sslMode=VERIFY_IDENTITY`（证书为 DigiCert 公共 CA，JVM 内置信任）
> - 默认连接池 initial-size 10 / max-active 100 会打爆 Serverless 的连接数限制

### Dockerfile 说明

仓库根目录 `Dockerfile`（**已验证可构建**）：

- 构建阶段：`eclipse-temurin:8-jdk-jammy` + 手动安装 Maven 3.8.8（从 Maven Central 下载二进制）
- 运行阶段：`eclipse-temurin:8-jre-jammy`
- **不要使用** `openjdk` / `maven:3.8-openjdk-8` 官方镜像——Docker Hub 已下架 openjdk 官方镜像，构建会报 `not found`

4. 创建后等待构建（首次约 5-10 分钟；`java -Xmx384m`，启动约 3 分钟）。**Free 实例闲置 15 分钟休眠，再次访问自动唤醒（首次约 30-60s 冷启动）**。
5. 后端端口 8081，context-path `/book_recommendation`，Render 免费层固定提供 `https://<name>.onrender.com` 公网地址。

## 五、前端部署（GitHub Pages，自动部署）

1. `frontend/.env.production` 已配置生产 baseURL 指向 Render 后端（本地开发不受影响，默认 `http://localhost:8081/book_recommendation`）：

```ini
VUE_APP_API_BASE_URL=https://book-recommendation-9542.onrender.com/book_recommendation
```

2. `frontend/vue.config.js` 已配置 `publicPath` 为 `/BookRecommendation/`（GitHub Pages 子路径部署，否则 js/css 全部 404）
3. `.github/workflows/deploy-pages.yml` 已配置：push `main` 时自动 `npm ci && npm run build` 并部署到 GitHub Pages
4. 仓库 Settings → Pages → Source 选 **GitHub Actions**，首次开启后自动发布

前端地址：`https://<用户名>.github.io/BookRecommendation/`（登录账号 `2020001 / 123456`）

> 部署流程里踩过的坑（供参考）：GitHub Actions 的 deploy-pages 失败 `HttpError: Not Found` = Pages 未在 Settings 里开启/未选 GitHub Actions 源；publicPath 不设会资源 404 白屏；渲染器失败日志里指向 main.js 的红行可能是 Babel 编译提示而非报错，看 `##[error]` 行才是真错误。

## 六、验证（已通过）

```bash
# 登录（返回 {"msg":"认证通过","code":200}）
curl -X POST https://<name>.onrender.com/book_recommendation/login \
  -H "Content-Type: application/json" \
  -d '{"account":"2020001","password":"123456"}'

# 图书列表（分页参数是 pageIndex，不是 currentPage；currentPage 是推荐接口的参数）
curl "https://<name>.onrender.com/book_recommendation/library/book/list?pageIndex=1&pageSize=4"

# 推荐
curl "https://<name>.onrender.com/book_recommendation/library/recommend/getRecommend?certId=2020001&pageSize=4&currentPage=1"
```

浏览器访问前端：登录 → 首页图书 → 图书详情 → 收藏 / 借阅 → "猜你喜欢"预置推荐（recommend 表 12 条）均正常。

## 七、成本与限制

- **免费**：Render 750h/月 + TiDB 5GB + GitHub Pages 无限。
- **限制**：Render Free 冷启动约 30-60s；闲置 15 分钟自动休眠（再次访问自动唤醒）；每月 750 小时约覆盖 31 天 × 24h（单个服务长期运行足够，多个服务需注意额度）。
- 大数据实时链路不在本方案内（见 running-local / docker-deploy）。
