# ============================================================
# BookRecommendation 大数据镜像（自建，amd64）
#
# 背景：官方 apache/spark 3.5.x 镜像仅提供 Scala 2.12（scala2.13 标签只存在于
#       Spark 4.x，而 Spark 4 移除了本项目使用的 Spark Streaming DStream API）。
#       本项目 bigdata 模块 jar 以 Scala 2.13 编译，且本地 Windows 原生链路
#       亦使用 spark-3.5.2-bin-hadoop3-scala2.13，因此基于官方二进制发行包
#       自建镜像，与本地工具链完全一致。
#
# 构建：eclipse-temurin 17 + spark-3.5.2-bin-hadoop3-scala2.13.tgz（archive.apache.org）
# 运行：默认启动 Spark master 保持容器存活；推荐链路通过 docker exec spark-submit 运行
# ============================================================
FROM eclipse-temurin:17-jdk-jammy

ENV SPARK_HOME=/opt/spark \
    SPARK_VERSION=3.5.2 \
    SPARK_SCALA=2.13 \
    PATH=$PATH:/opt/spark/bin:/opt/spark/sbin

RUN apt-get update && apt-get install -y --no-install-recommends wget ca-certificates \
    && wget -q https://archive.apache.org/dist/spark/spark-3.5.2/spark-3.5.2-bin-hadoop3-scala2.13.tgz -O /tmp/spark.tgz \
    && tar -xzf /tmp/spark.tgz -C /opt \
    && mv /opt/spark-3.5.2-bin-hadoop3-scala2.13 /opt/spark \
    && rm /tmp/spark.tgz \
    && apt-get purge -y wget && apt-get autoremove -y \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/spark/work-dir

# 默认启动 Spark master（7077 / Web UI 8080 仅容器内可见，外部只映射 4040）
# 注意：主类全名是 org.apache.spark.deploy.master.Master（.master 是包名）
CMD ["/opt/spark/bin/spark-class", "org.apache.spark.deploy.master.Master", "--host", "0.0.0.0", "--port", "7077", "--webui-port", "8080"]
