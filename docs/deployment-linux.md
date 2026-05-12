# Linux 与大数据部署说明

本项目的完整运行硬性依赖 Linux/Ubuntu 上的 Hadoop 生态环境。只启动前端和后端，可以看到普通业务系统的一部分能力；但如果缺少 Hadoop、Hive、Spark 和 Kafka，则无法完整运行用户行为采集、推荐计算和推荐结果回写链路。

## 环境分层

| 环境 | 主要运行内容 |
| --- | --- |
| Windows / 本地开发机 | Vue 前端、Spring Boot 后端、Selenium 爬虫、代码调试 |
| Linux / Ubuntu | Hadoop、Hive、Spark、Kafka、大数据推荐计算 |
| MySQL | `library` 业务库、Hive metastore 数据库 |

毕业设计时期的环境中，业务系统和大数据服务通过局域网地址或主机名互相访问。当前代码中仍保留了历史配置，例如 `hadoopPD`、`192.168.10.12:9092` 等。

## 主要依赖

| 类型 | 依赖 |
| --- | --- |
| Java | JDK 8 |
| 后端构建 | Maven |
| 前端构建 | Node.js、npm |
| 数据库 | MySQL |
| 消息队列 | Kafka |
| 大数据存储 | Hadoop / HDFS |
| 数据仓库 | Hive |
| 计算引擎 | Spark |
| 爬虫 | Python、Selenium、Chrome、ChromeDriver |

`bigdata/pom.xml` 中使用 Spark 3.3.2、Hadoop 3.3.2、Scala 2.13.x 相关依赖。实际部署时需要注意本机 Spark 发行包、Scala 版本和 Maven 依赖版本的一致性。

## 推荐启动顺序

完整环境建议按以下顺序启动：

1. 启动 MySQL，准备 `library` 业务库和 Hive metastore 数据库。
2. 启动 Hadoop / HDFS。
3. 启动 Hive metastore 相关服务。
4. 启动 Kafka，并创建或确认 `userLog` topic。
5. 启动 Spring Boot 后端。
6. 启动 Vue 前端。
7. 启动 Spark Streaming 推荐计算任务。
8. 如需补全封面，再运行 `crawler` 爬虫工具。

## 配置检查点

### 后端业务库

后端开发配置位于：

```text
backend/src/main/resources/application-dev.yml
```

其中包含 MySQL 连接地址、用户名、密码、端口和后端上下文路径。当前历史配置指向 `hadoopPD:3306/library`，迁移到新环境时需要按实际地址修改。

### Kafka

后端通用配置位于：

```text
backend/src/main/resources/application.yml
```

大数据模块配置位于：

```text
bigdata/src/main/resources/config.properties
```

两处 Kafka 地址需要保持一致。当前历史配置中 Kafka broker 为 `192.168.10.12:9092`。

### Hive

大数据模块 Hive 配置位于：

```text
bigdata/src/main/resources/hive-site.xml
```

该文件中配置了 Hive metastore 使用的 MySQL 地址。迁移环境时需要确认：

- Hive metastore 数据库已创建。
- MySQL 用户和密码正确。
- Ubuntu 主机可以访问 MySQL。
- Spark 任务运行时可以找到 `hive-site.xml`。

### 前端后端地址

前端 Axios 默认后端地址位于：

```text
frontend/src/main.js
```

当前默认值为：

```text
http://localhost:8081/book_recommendation
```

如果后端部署在远程 Linux 或其他主机，需要同步调整前端请求地址。

## 最小验证路径

在数据库重建后，可以按以下顺序验证系统是否打通：

1. 后端可以连接 MySQL，并正常启动。
2. 前端可以访问登录页，并调用后端接口。
3. 注册本校学生时，可以根据 `reader.CERT_ID` 校验学号。
4. 浏览、收藏或借阅图书时，后端可以写入 Kafka。
5. Spark Streaming 可以消费 `userLog` topic。
6. Spark 可以写入 Hive 中间表。
7. Spark 可以把推荐结果回写到 MySQL。
8. 前端可以通过后端查询推荐图书。

## 当前不可直接复现的部分

由于原始数据库数据缺失，当前仓库不能直接完成上述完整验证。要恢复可运行状态，需要先补充脱敏数据库结构和演示数据，再按实际环境调整 MySQL、Kafka、Hive、Spark 等配置。
