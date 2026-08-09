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

## 五、大数据实时链路（本机 Windows 原生，已验证）

原架构实时链路（已在本机 Windows 完整跑通）：

```
后端行为日志 → Kafka(userLog) → Spark Streaming → Hive 中间表 → Spark 推荐计算 → 回写 MySQL recommend
```

### 一键启动（推荐）

```powershell
powershell -ExecutionPolicy Bypass -File scripts\start-book.ps1
```

脚本按顺序启动 **MySQL → Kafka 3.7（KRaft 单机）→ Hive 4.0 metastore（元数据存 MySQL）→ Spring Boot 后端 → Spark Streaming → Vue 前端**，并检查各端口就绪；停止用 `scripts\stop-book.ps1`。日志输出到仓库 `logs/`。

### 环境与软件（官网下载，统一放 `D:\soft\program`）

| 组件 | 版本 | 位置 | 说明 |
| --- | --- | --- | --- |
| Oracle JDK 17 | 17.0.11 | `D:\soft\program\Java\jdk-17.0.11` | Spark / Hive metastore 运行 |
| JDK 8 | 1.8 | `D:\soft\program\Java\jdk-8` | 后端 / bigdata 编译 |
| JDK 26 | 26.0.1 | `D:\soft\program\Java\jdk-26.0.1` | Kafka 运行 |
| Kafka | 3.7.2 | `D:\soft\program\kafka_2.13-3.7.2` | KRaft 单机（config/kraft/server.properties） |
| Spark | 3.5.2（Scala 2.13） | `D:\soft\program\spark-3.5.2-bin-hadoop3-scala2.13` | 含 spark-hive |
| Hadoop | 3.3.6 | `D:\soft\program\Hadoop` | 仅提供 winutils.exe / hadoop.dll（Windows 兼容层，无需启动服务） |
| Hive | 4.0.0 | `D:\soft\program\apache-hive-4.0.0-bin` | 运行 metastore 服务（thrift 9083），元数据存本机 MySQL `hive` 库 |
| MySQL | 8.x | 本机服务 | 业务库 `library` + Hive 元数据库 `hive` |

> Windows 限制说明：Hive CLI / HiveServer2 官方不支持 Windows，本机运行的是 **metastore 服务**（推荐数据的元数据管理）；需要 HiveServer2 查询时用 Docker 部署（镜像自带完整 Hive 4.0）。

### 手动启动步骤（等价于脚本内容）

1. **MySQL**：Windows 服务（`sc start MySQL`），建 `library`（业务）与 `hive`（元数据）两库。
2. **Kafka**（KRaft，端口 9092，JDK 26）：

```bash
cd D:\soft\program\kafka_2.13-3.7.2
# 首次格式化（只需一次）：
#   java -cp "libs/*" kafka.tools.StorageTool random-uuid   → 得到 UUID
#   java -cp "libs/*" kafka.tools.StorageTool format -t <UUID> -c config/kraft/server.properties
# 启动：
D:\soft\program\Java\jdk-26.0.1\bin\java.exe -cp "libs/*" kafka.Kafka config/kraft/server.properties
```

3. **Hive metastore**（端口 9083，JDK 17，classpath 需含 Hadoop jars）：

```bash
cd D:\soft\program\apache-hive-4.0.0-bin
# 首次初始化 MySQL 元数据库（hive 库，只需一次）：
#   mysql -uroot -proot < scripts/metastore/upgrade/mysql/hive-schema-4.0.0.mysql.sql
# 启动：
java -cp "conf;lib/*;D:\soft\program\Hadoop\share\hadoop\common\*;...hdfs\*;...mapreduce\*;...yarn\*" \
     -Dhadoop.home.dir=D:/soft/program/Hadoop -Djava.library.path=D:/soft/program/Hadoop/bin \
     org.apache.hadoop.hive.metastore.HiveMetaStore
```

4. **后端**（端口 8081，JDK 8）：`cd backend && set SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092 && mvnw.cmd spring-boot:run`
5. **Spark Streaming**（端口 4040，JDK 17）：

```bash
cd bigdata
# 编译（scala-maven-plugin 4.9.2 已修复，产出 recommend_bigdata-1.0.jar）
mvnw.cmd -q clean package -DskipTests -f ../bigdata/pom.xml
# 运行（Kafka/MySQL 依赖已放入 spark/jars，无需 --packages）
spark-submit.cmd --class com.hytc.bigdata.SparkStreamingRunner \
  --conf "spark.driver.extraJavaOptions=-Dhadoop.home.dir=D:/soft/program/Hadoop -Djava.library.path=D:/soft/program/Hadoop/bin" \
  --conf "spark.executor.extraJavaOptions=-Dhadoop.home.dir=D:/soft/program/Hadoop -Djava.library.path=D:/soft/program/Hadoop/bin" \
  target/recommend_bigdata-1.0.jar
```

6. **前端**（端口 8080）：`cd frontend && npm run serve`

验证：用 `2020001 / 123456` 登录 → 浏览/收藏/借阅图书 → 1 分钟内 Spark Streaming 消费行为 → MySQL `recommend` 表更新 → "猜你喜欢"页展示新推荐。

### 常见问题

| 问题 | 处理 |
| --- | --- |
| Spark 报 `Hadoop bin directory does not exist` | 下载 winutils.exe + hadoop.dll 到 `D:\soft\program\Hadoop\bin`，并传 `-Dhadoop.home.dir` |
| Spark 报 `NativeIO$Windows.access0` UnsatisfiedLinkError | 传 `-Djava.library.path=D:/soft/program/Hadoop/bin`（hadoop.dll 所在目录） |
| Spark 报 `NoSuchMethodError: ScalaRunTime.wrapRefArray` | 必须使用 **Scala 2.13 版 Spark** 发行版（`spark-3.5.2-bin-hadoop3-scala2.13`） |
| 推荐表被清空 | 空批次不会覆盖（代码已处理）；确认 Kafka topic 有消息且 Spark 消费组正常 |
| Hive 表建表报 LOCATION_ALREADY_EXISTS | 切换 metastore 后清理旧 `spark-warehouse/` 与 `metastore_db/`（derby 残留） |

## 六、无 Kafka 时的行为说明

后端使用 `KafkaTemplate` 发送行为日志（`lend` 借阅时写 `userLog` topic）。Kafka 生产者是**懒初始化**的——broker 不可达时后端照常启动，借阅、收藏等业务操作正常完成，仅行为日志发送失败（日志中可见异常，不影响页面响应）。预置推荐模式下业务功能不受影响。

## 七、常见问题

| 问题 | 处理 |
| --- | --- |
| `Access denied for user 'root'@'localhost'` | 修改 `application-dev.yml` 的 password 为本机 MySQL 密码 |
| 前端接口 404 | 确认后端已启动、`frontend/src/main.js` 的代理地址为 `http://localhost:8081/book_recommendation` |
| 登录提示账号错误 | 演示账号见 `database/init.sql`（`2020001/123456`） |
| 推荐页为空 | 确认 `library.recommend` 表有数据（`init.sql` 已预置） |
