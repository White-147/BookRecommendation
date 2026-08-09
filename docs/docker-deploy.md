# BookRecommendation Docker 全链路部署指南（含大数据实时推荐）

本文档说明如何用 Docker 完整复刻原架构：前端 + Spring Boot 后端 + MySQL 业务库 + Kafka 行为日志 + Hadoop/Hive/Spark 推荐计算链路。

## 一、版本兼容说明（重要）

| 组件 | 版本 | 说明 |
| --- | --- | --- |
| Spark | **3.5.2**（镜像内置） | bigdata 模块按 **3.3.2（Scala 2.13）** 编译的 jar **先在 3.5.2 上运行**（Spark minor 版本二进制兼容，基础 RDD/DataFrame API 无差异） |
| Scala | 2.13 | 镜像与 pom 均匹配 |
| Hadoop | 3.3.6（镜像内置） | HDFS/YARN |
| Hive | 4.0.0（镜像内置） | HiveServer2 + derby metastore（单机演示） |
| Kafka | 3.7（bitnami/kafka） | 行为日志 topic: `userLog` |
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

首次启动 bigdata 容器会初始化 HDFS（等待 NameNode 就绪约 30-60s）。

### 3. 启动应用（后端 + 前端）

```bash
docker compose up -d backend frontend
```

### 4. 运行实时推荐链路（在 bigdata 容器内）

```bash
# 进入容器
docker exec -it book-bigdata bash

# 启动 Spark Streaming 实时推荐（消费 Kafka userLog → Hive → 计算 → 回写 MySQL）
spark-submit \
  --class com.hytc.bigdata.SparkStreamingRunner \
  --master local[*] \
  --jars /opt/bigdata-jars/spark-streaming-kafka-0-10_2.13-3.3.2.jar \
  /opt/bigdata-jars/bigdata-1.0-SNAPSHOT.jar
```

> Kafka 依赖 jar（`spark-streaming-kafka-0-10_2.13-3.3.2.jar`）在 `bigdata/target/` 下（maven 依赖拷贝），或从 Maven 仓库获取。

## 三、部署前需修改的地址（代码内配置）

原代码中的开发地址（Ubuntu 时代）需改为 compose 服务名：

| 位置 | 原值 | 改为 |
| --- | --- | --- |
| `backend/src/main/resources/application.yml` → `spring.kafka.bootstrap-servers` | `192.168.10.12:9092` | `kafka:9092` |
| `backend/src/main/resources/application-dev.yml` → datasource url | `jdbc:mysql://hadoopPD:3306/library` | `jdbc:mysql://mysql:3306/library` |
| `bigdata/.../KafkaUtil.scala` → bootstrap.servers | `192.168.10.12:9092` | `kafka:9092` |
| `bigdata/.../Step8.scala` 等 → JDBC url | `jdbc:mysql://hadoopPD:3306/library` | `jdbc:mysql://mysql:3306/library` |

> 本地单机开发保持原值不受影响；部署时用环境变量（compose 已注入 SPRING_*）或一次性修改。

## 四、验证

1. 前端：`http://localhost:8080/`，登录 `2020001/123456`
2. 后端：`http://localhost:8081/book_recommendation/library/recommend/getRecommend?certId=2020001&pageSize=4&currentPage=1`
3. 推荐链路：在前端借阅一本图书 → 后端写 Kafka `userLog` → Spark 消费 → Hive 中间表 → 回写 `library.recommend`
4. Spark UI：`http://localhost:4040/`；HDFS UI：`http://localhost:9870/`

## 五、常见问题

| 问题 | 处理 |
| --- | --- |
| bigdata 容器 HDFS 未就绪 | 等待 30-60s；`docker logs book-bigdata` 查看 NameNode 日志 |
| spark-submit 缺 Kafka 依赖 | `--jars` 显式指定 spark-streaming-kafka jar |
| 3.3.2 jar 报 API 错误 | 按第一节改 pom 为 3.5.2 重编译 |
| 推荐结果被清空 | Step8 已加空批次保护（空批次不覆盖 recommend 表） |
