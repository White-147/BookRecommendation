# BookRecommendation 后端镜像（docker-compose 用；根目录 Dockerfile 供 Render 在线部署用）
# 注意：docker.io 官方 openjdk 与 maven 镜像已下架，统一改用 eclipse-temurin
# 构建：Eclipse Temurin JDK 8 + 手动安装 Maven 3.8.8（从 Maven Central 下载二进制制品，不依赖镜像仓库）
# 运行：Eclipse Temurin JRE 8
FROM eclipse-temurin:8-jdk-jammy AS build
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates wget \
    && wget -q https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.8.8/apache-maven-3.8.8-bin.tar.gz \
    && tar -xzf apache-maven-3.8.8-bin.tar.gz -C /opt \
    && ln -s /opt/apache-maven-3.8.8/bin/mvn /usr/local/bin/mvn \
    && rm apache-maven-3.8.8-bin.tar.gz
COPY pom.xml .
RUN mvn -B dependency:go-offline -q || true
COPY src ./src
RUN mvn -B package -DskipTests -q

FROM eclipse-temurin:8-jre-jammy
WORKDIR /app
COPY --from=build /app/target/recommendation_data-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENV JAVA_OPTS="-Xmx384m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
