# BookRecommendation 本地完整运行指南

本指南说明如何在单台 Windows 机器上把整个项目跑起来（前端 + 后端 + 推荐展示全功能），并给出大数据实时链路的可选部署方案。

## 一、运行架构（简化版）

```
Vue 前端 (localhost:8081)
   │
   ▼
Spring Boot 后端 (localhost:8081/book_recommendation)
   │
   ├── MySQL 8.x（本地，library 库：用户/图书/收藏/借阅/推荐结果）
   └── Kafka（可选，行为日志采集；缺失时后端仍可启动）
```

推荐结果数据由 `database/init.sql` 预置（与 Spark 计算回写的表结构一致），因此**不依赖 Hadoop/Kafka/Spark/Hive 即可完整演示推荐功能**；实时计算链路见第五节。

## 二、环境要求

| 组件 | 版本 | 说明 |
| --- | --- | --- |
| JDK | 1.8 | 后端编译运行（`java -version` 验证） |
| Maven | 内置 | 使用仓库自带 `mvnw.cmd`，无需单独安装 |
| Node.js | 14+ | 前端（仓库锁定 Vue 2 依赖） |
| MySQL | 8.x | 本机数据库（root/root 示例，可按实际修改） |

## 三、数据库初始化

```bash
# 1. 进入仓库根目录，执行初始化脚本（建库 + 建表 + 演示数据 + 预置推荐）
mysql -uroot -proot < database/init.sql
```

脚本会创建 `library` 库与 8 张表（user / reader / book / collect / lend / recommend / relatedbook / newbook），并灌入：

- 3 个演示账号：`2020001` / `2020002` / `2020003`，密码均为 `123456`
- 12 本计算机类图书、收藏 6 条、借阅 4 条
- **预置推荐结果 12 条**（recommend 表，按用户区分）、相关图书 6 对、新书 4 本

> 后端登录逻辑会对库中密码再做一次 BCrypt 校验（`matches(明文, BCrypt(明文))` 恒成立），因此演示账号密码以明文存储即可正常登录。

## 四、启动前后端

### 1. 后端（端口 8081，上下文 /book_recommendation）

```bash
cd backend
mvnw.cmd spring-boot:run
```

数据库连接在 `backend/src/main/resources/application-dev.yml`：

```yaml
url: jdbc:mysql://localhost:3306/library?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8
username: root
password: root
```

如本机 MySQL 密码不同，改这里的 `password` 即可。

### 2. 前端（Vite 开发服务器，端口 8081 反向代理到后端）

```bash
cd frontend
npm install
npm run serve
```

浏览器访问 `http://localhost:8081/`，使用 `2020001 / 123456` 登录后可看到：首页图书、收藏/借阅、**个性化推荐**（预置数据）、相关推荐、新书推荐与统计图表。

### 3. 爬虫（可选，补全缺失图书封面）

```bash
cd crawler
pip install -r requirements.txt
python main.py
```

## 五、大数据实时链路（可选，完整复刻原架构）

原架构中 Kafka / Spark / Hive 运行在 Ubuntu，实时链路为：

```
后端行为日志 → Kafka(userLog) → Spark Streaming → Hive 中间表 → Spark 推荐计算 → 回写 MySQL recommend
```

本地复刻有两种方式（二选一）：

### 方式 A：Docker 全家桶（推荐，最接近原架构）

1. 安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/)（启用 WSL2 后端，内存建议 ≥ 10GB）
2. 拉取大数据镜像（含 Hadoop + Spark + Hive + Kafka + Jupyter）：

```bash
docker pull ramtricks/hadoop-bootstrap:v0.0.20-r1
docker run -d -p 8088:8088 -p 9000:9000 -p 9092:9092 --name bigdata ramtricks/hadoop-bootstrap:v0.0.20-r1
```

3. 修改两处地址（原 `192.168.10.12` 改为容器地址）：
   - `backend/src/main/resources/application.yml` → `spring.kafka.bootstrap-servers: localhost:9092`
   - `bigdata/` 模块中的 JDBC/Kafka 配置指向 `localhost`
4. 在容器内执行 `bigdata/` 的 Spark 任务（Spark 3.3.2 / Scala 2.13，见 `docs/deployment-linux.md`）

> 镜像为社区维护，版本与 Spark 3.3.2 可能存在差异，如有兼容问题可按 `bigdata/pom.xml` 版本自建镜像。

### 方式 B：WSL2 + Ubuntu 手动安装

```bash
wsl --install -d Ubuntu-22.04
# 在 Ubuntu 内安装 Hadoop、Kafka、Spark、Hive（可参考 docs/deployment-linux.md 原文档）
```

工作量较大，仅在需要完整复刻实时链路时选择。

## 六、无 Kafka 时的行为说明

后端使用 `KafkaTemplate` 发送行为日志（`lend` 借阅时写 `userLog` topic）。Kafka 生产者是**懒初始化**的——broker 不可达时后端照常启动，借阅、收藏等业务操作正常完成，仅行为日志发送失败（日志中可见异常，不影响页面响应）。预置推荐模式下业务功能不受影响。

## 七、常见问题

| 问题 | 处理 |
| --- | --- |
| `Access denied for user 'root'@'localhost'` | 修改 `application-dev.yml` 的 password 为本机 MySQL 密码 |
| 前端接口 404 | 确认后端已启动、`frontend/src/main.js` 的代理地址为 `http://localhost:8081/book_recommendation` |
| 登录提示账号错误 | 演示账号见 `database/init.sql`（`2020001/123456`） |
| 推荐页为空 | 确认 `library.recommend` 表有数据（`init.sql` 已预置） |
