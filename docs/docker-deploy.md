# BookRecommendation Docker 全链路部署指南（含大数据实时推荐）

本文档说明如何用 Docker 完整复刻原架构：前端 + Spring Boot 后端 + MySQL 业务库 + Kafka 行为日志 + Spark（Hive 内嵌）推荐计算链路。

## 一、版本兼容说明（重要）

| 组件 | 版本 | 说明 |
| --- | --- | --- |
| Spark | **3.5.2（Scala 2.13）**（自建镜像 `bigdata/Dockerfile`：eclipse-temurin 17 + 官方 `spark-3.5.2-bin-hadoop3-scala2.13` 二进制包，amd64） | bigdata 模块按 **3.3.2（Scala 2.13）** 编译的 jar **先在 3.5.2 上运行**（Spark minor 版本二进制兼容，基础 RDD/DataFrame API 无差异） |
| Scala | 2.13 | 镜像与 pom 均匹配 |
| Hive | 内嵌 derby metastore（官方镜像自带 spark-hive jars） | Hive 表数据落在命名卷 `book-bigdata-warehouse`（`/opt/spark/work-dir`） |
| Hadoop / HDFS / YARN | 本 Docker 链路不启用 | 推荐链路（Spark Streaming + Hive 表 + MySQL 回写）不依赖 HDFS/YARN 守护进程；真实 Hadoop/Hive 见本地 Windows 原生链路（running-local.md） |
| Kafka | 3.7（bitnamilegacy/kafka；原 bitnami/kafka 标签已下架） | 行为日志 topic: `userLog` |
| MySQL | 8.0 | 业务库 `library`（含预置推荐数据） |

> **若 3.3.2 jar 在 Spark 3.5.2 上运行报 API 差异**：将 `bigdata/pom.xml` 的 `<spark.version>` 改为 `3.5.2` 重新编译（`cd bigdata && mvn package`），代码使用的均为基础 API，编译即可通过。

## 二、启动步骤

### 1. 编译 bigdata 模块（产出 jar）

```bash
cd bigdata
mvn package -DskipTests
# 产物：bigdata/target/*.jar（compose 挂载到容器 /opt/bigdata-jars）
```

### 2. 启动依赖（MySQL + Kafka + 大数据全家桶）

```bash
docker compose up -d mysql kafka bigdata
```

首次启动 bigdata 容器会拉起 Spark master（官方镜像默认命令），`docker logs -f book-bigdata` 看到 `Successfully registered signal handlers` 等 Spark 日志即就绪。

### 3. 启动应用（后端 + 前端）

```bash
docker compose up -d backend frontend
```

### 4. 运行实时推荐链路（在 bigdata 容器内）

```bash
# 进入容器
docker exec -it book-bigdata bash

# 启动 Spark Streaming 实时推荐（消费 Kafka userLog → Hive → 计算 → 回写 MySQL）
# jar 产物：bigdata/target/recommend_bigdata-1.0.jar（compose 挂载到 /opt/bigdata-jars）
spark-submit \
  --class com.hytc.bigdata.SparkStreamingRunner \
  --master local[*] \
  --packages org.apache.spark:spark-streaming-kafka-0-10_2.13:3.5.2,com.mysql:mysql-connector-j:8.0.33 \
  /opt/bigdata-jars/recommend_bigdata-1.0.jar
```

> Kafka/MySQL 依赖 jar 通过 `--packages` 拉取（镜像内可访问 Maven 中央仓库）；若离线，可将本机 `spark/jars/` 下的 `spark-streaming-kafka-0-10_2.13-3.5.2.jar`、`spark-token-provider-kafka-0-10_2.13-3.5.2.jar`、`kafka-clients-3.4.1.jar`、`mysql-connector-j-8.0.33.jar` 一并挂载/拷入容器后去掉 `--packages`。

> 本机 Windows 环境已用 **Spark 3.5.2（Scala 2.13）+ 真实 Hive 4.0 metastore（元数据存 MySQL）+ Kafka 3.7** 完整验证同一 jar 与链路（见 `running-local.md`），Docker 镜像（Spark 3.5.2 + Hive 4.0）与本机版本一致，运行配置可直接复用。

## 三、地址配置说明（代码现状）

仓库代码中的开发地址已统一改为本机默认值，Docker 部署时由 compose 环境变量覆盖，无需改代码：

| 位置 | 当前值 | compose 覆盖 |
| --- | --- | --- |
| `backend/src/main/resources/application.yml` → `spring.kafka.bootstrap-servers` | `localhost:9092`（可通过 `SPRING_KAFKA_BOOTSTRAP_SERVERS` 覆盖） | `kafka:9092`（compose 已注入） |
| `backend/src/main/resources/application-dev.yml` → `spring.datasource.druid.*` | `jdbc:mysql://localhost:3306/library` / `root` / `root`（均可通过 `SPRING_DATASOURCE_DRUID_URL/USERNAME/PASSWORD` 覆盖） | `SPRING_DATASOURCE_DRUID_URL=jdbc:mysql://mysql:3306/library`（**必须用 Druid 前缀**，`SPRING_DATASOURCE_URL` 绑不到 druid 配置） |
| `frontend/.env.docker` → `VUE_APP_API_BASE_URL` | `/book_recommendation`（相对路径） | 前端 Dockerfile 以 `--mode docker` 构建，经 nginx 代理到 `backend:8081`，全链路不依赖 Render 在线后端 |
| `bigdata/.../KafkaUtil.scala` → bootstrap.servers | `localhost:9092`（经 KafkaUtil 读取：环境变量 `BOOK_KAFKA_BROKER` > config.properties `kafka.broker` > 默认值） | compose 的 bigdata 服务注入 `BOOK_KAFKA_BROKER=kafka:9092`，无需改代码 |
| `bigdata/.../Step8.scala`、`RelatedBookRecommend.scala`、`NewBookRecommend.scala` → JDBC url | `jdbc:mysql://localhost:3306/library`（经 `com.hytc.util.JdbcConfig` 读取：环境变量 `BOOK_DB_*` > config.properties `db.*` > 默认值） | compose 的 bigdata 服务注入 `BOOK_DB_URL=jdbc:mysql://mysql:3306/library`，无需改代码 |

## 四、验证

1. 前端：`http://localhost:8080/`，登录 `2020001/123456`
2. 后端：`http://localhost:8081/book_recommendation/library/recommend/getRecommend?certId=2020001&pageSize=4&currentPage=1`
3. 推荐链路：在前端借阅一本图书 → 后端写 Kafka `userLog` → Spark 消费 → Hive 中间表 → 回写 `library.recommend`
4. Spark UI：`http://localhost:4040/`

## 五、常见问题

| 问题 | 处理 |
| --- | --- |
| bigdata 容器未就绪 | `docker logs book-bigdata` 查看 Spark 启动日志；确认内存充足（mem_limit 4g） |
| spark-submit 缺 Kafka 依赖 | `--jars` 显式指定 spark-streaming-kafka jar |
| 3.3.2 jar 报 API 错误 | 按第一节改 pom 为 3.5.2 重编译 |
| 推荐结果被清空 | Step8 已加空批次保护（空批次不覆盖 recommend 表） |
