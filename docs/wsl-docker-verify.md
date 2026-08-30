# WSL + Docker 全链路验证指南（本机验证 Docker 编排）

> 目标：在没有 Linux 服务器的情况下，用 **WSL2 + Docker Desktop** 在本机把项目的 Docker 全链路编排（Spark + Hadoop + Hive + Kafka + MySQL + 后端 + 前端）跑起来，作为作品集的"大数据实时链路"演示环境。
>
> 前提背景：Windows 单机原生全链路（Kafka KRaft + Spark 3.5.2 + Hive 4.0 + MySQL）见 [running-local.md](./running-local.md)；Docker 编排说明见 [docker-deploy.md](./docker-deploy.md)。

## 一、先确认本机状态

在 PowerShell 中执行：

```powershell
wsl --status        # 查看 WSL 运行状态
wsl -l -v           # 列出已安装发行版
```

三种情况：

| 情况 | 处理 |
| --- | --- |
| 报 `Wsl/EnumerateDistros/Service/E_ACCESSDENIED`，且 `wsl --status` 正常 | 通常是**没有安装任何发行版**（WSL 运行时已装）。直接进入第二步安装发行版即可 |
| 显示 Ubuntu 且 VERSION 为 2 | WSL 环境已就绪，跳到第三步 |
| 提示"未安装 WSL" | 先管理员运行 `wsl --install` 安装运行时，再进入第二步 |

> 常见误区：WSL "运行时"（内核 + 服务）和"发行版"（Ubuntu 等）是两回事。只有运行时、没有发行版时，`wsl -l -v` 会枚举失败或显示空列表。

## 二、安装 WSL 发行版（管理员 PowerShell）

```powershell
# 1. 安装 Ubuntu（自动启用所需 Windows 功能，按提示重启）
wsl --install -d Ubuntu

# 2. 重启后首次启动会要求创建 Linux 用户名/密码
wsl

# 3. 回到 Windows PowerShell 验证
wsl -l -v          # 应显示 Ubuntu 且 VERSION = 2

# 4.（可选）配置国内 apt 源加速，然后更新
sudo sed -i 's/archive.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list
sudo apt update && sudo apt upgrade -y
```

## 三、安装 Docker Desktop（WSL2 后端）

1. 下载安装 [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/)（安装时保持默认勾选 **Use WSL 2 instead of Hyper-V**）。
2. 启动 Docker Desktop → **Settings → General** → 勾选 **Use the WSL 2 based engine**。
3. **Settings → Resources → WSL Integration** → 勾选 **Ubuntu**（若只从 Windows 侧用 docker，可跳过）。
4. 验证：

```powershell
docker version        # Client / Server 都正常输出
docker info | Select-String "Operating System"   # 显示 WSL2 内核信息
docker run --rm hello-world   # 能拉镜像并运行即 OK
```

> 无 WSL 时 Docker Desktop 无法使用 WSL2 后端（这就是本项目当初没法在本机验证 Docker 编排的直接原因）。安装完发行版后此问题即解除。

## 四、构建并启动全链路（仓库根目录执行）

### 1. 构建 bigdata jar（compose 挂载到容器）

```powershell
cd backend
.\mvnw.cmd -q -f ..\bigdata\pom.xml clean package -DskipTests
# 产物：bigdata/target/recommend_bigdata-1.0.jar
```

### 2. 启动依赖（MySQL + Kafka + 大数据全家桶）

```powershell
cd ..
# bigdata 为自建镜像（bigdata/Dockerfile：官方 spark-3.5.2-bin-hadoop3-scala2.13 二进制包，amd64，内嵌 Hive/derby），
# 首次构建约 5-10 分钟（下载约 410MB 发行包），构建后启动即拉起 Spark master
docker compose up -d --build mysql kafka bigdata
docker logs -f book-bigdata   # 看到 "Running Spark version 3.5.2" 即就绪（Ctrl+C 退出跟踪）
```

### 3. 启动应用（后端 + 前端）

```powershell
docker compose up -d backend frontend
```

> 后端镜像构建阶段会从 Maven Central 下载依赖（首次约 5-10 分钟）；前端以 `--mode docker` 构建，baseURL 为相对路径 `/book_recommendation`，由 nginx 代理到容器内后端，**全链路不依赖 Render 在线后端**。

### 4. 验证清单

```powershell
# ① 登录（返回 {"msg":"认证通过","code":200}）
curl -X POST http://localhost:8081/book_recommendation/login -H "Content-Type: application/json" `
  -d '{"account":"2020001","password":"123456"}'

# ② 图书列表
curl "http://localhost:8081/book_recommendation/library/book/list?pageIndex=1&pageSize=4"

# ③ 预置推荐（recommend 表 12 条）
curl "http://localhost:8081/book_recommendation/library/recommend/getRecommend?certId=2020001&pageSize=4&currentPage=1"

# ④ 页面与 UI
#   前端   http://localhost:8080/   （登录 2020001 / 123456）
#   Spark  http://localhost:4040/   （spark-submit 驱动的 Web UI）
```

### 5. 跑实时推荐链路（可选，验证 Kafka → Spark → Hive → MySQL）

```bash
# 进入 bigdata 容器
docker exec -it book-bigdata bash

# 启动 Spark Streaming（消费 Kafka userLog → Hive 中间表 → 计算 → 回写 MySQL recommend）
spark-submit \
  --class com.hytc.bigdata.SparkStreamingRunner \
  --master local[*] \
  --packages org.apache.spark:spark-streaming-kafka-0-10_2.13:3.5.2,com.mysql:mysql-connector-j:8.0.33 \
  /opt/bigdata-jars/recommend_bigdata-1.0.jar
```

随后在前端收藏/借阅一本图书 → 后端写 Kafka `userLog` → 观察容器日志与 Hive 表（`userlog` / `step1`~`step8`）→ 检查 `library.recommend` 是否更新。

> 算法说明：`Step5` 已在**全用户空间**（含"双方均未操作"的 (0,0) 样本点）上计算皮尔逊系数，`> 0.0` 过滤后只会保留正相关（共同偏好）的图书对，可真实产出推荐结果；不再依赖 `database/init.sql` 的预置数据。

## 五、（可选）WSL 内原生运行大数据链路

若不想用 Docker，可在 Ubuntu 里按 [deployment-linux.md](./deployment-linux.md) 安装 JDK / Hadoop / Hive / Spark / Kafka / MySQL 原生运行，替代 Windows 的 winutils 兼容层，行为更贴近生产环境。装好后同样用 `scripts/start-book.ps1` 思路启动（Linux 下改 shell 脚本即可）。

## 六、停止与清理

```powershell
docker compose down          # 停止并移除容器（保留数据卷）
docker compose down -v       # 连 MySQL 数据一并清除，回到初始状态
```

释放 WSL 磁盘空间（vhdx 自动膨胀后收缩）：

```powershell
wsl --shutdown
Optimize-VHD -Path "$env:LOCALAPPDATA\Packages\CanonicalGroupLimited.Ubuntu*\LocalState\ext4.vhdx" -Mode Full
# Optimize-VHD 需要 Hyper-V 模块；没有时可用 diskpart 的 compact vhdx
```

## 七、常见问题

| 问题 | 处理 |
| --- | --- |
| `docker compose up backend` 报镜像 `maven:3.8-openjdk-8` / `openjdk:8-jre-slim` not found | 官方 openjdk/maven 镜像已从 Docker Hub 下架。本项目 backend/Dockerfile 已改用 `eclipse-temurin:8-jdk-jammy` + 手动安装 Maven 3.8.8，拉取最新代码即可 |
| 后端连不上 MySQL / 报 `Access denied` | 检查 compose 环境变量必须是 `SPRING_DATASOURCE_DRUID_*` 前缀（Druid starter 绑定该前缀），见 docker-deploy.md 第三节 |
| 端口 3306 / 9092 被本机原生服务占用 | 先跑 `scripts\stop-book.ps1` 停掉 Windows 原生链路，或改 compose 对外端口映射 |
| 内存不足（默认 4g+1g+1g） | Docker Desktop Settings → Resources 调大内存；或建 `%UserProfile%\.wslconfig` 设置 `memory=8GB` |
| 前端打开后请求打到 Render 线上后端 | 前端镜像必须用 `--mode docker` 构建（相对路径 `/book_recommendation`），确认 Dockerfile 与 `.env.docker` 存在 |
| WSL 枚举报 E_ACCESSDENIED | 大概率是没装发行版，走第二步安装 Ubuntu 后重试 |
