# BookRecommendation 后端服务镜像（Render Docker 部署用）
# 注意：docker.io 官方 openjdk 与 maven 镜像已下架，构建/运行全部改用 eclipse-temurin
# 构建：Eclipse Temurin JDK 8 + 项目自带 Maven Wrapper（自动从 Maven Central 下载 Maven 3.8.6）
# 运行：Eclipse Temurin JRE 8
FROM eclipse-temurin:8-jdk-jammy AS build
WORKDIR /app
COPY backend/mvnw backend/.mvn backend/pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline -q || true
COPY backend/src ./src
RUN ./mvnw -B package -DskipTests -q

FROM eclipse-temurin:8-jre-jammy
WORKDIR /app
COPY --from=build /app/target/recommendation_data-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENV JAVA_OPTS="-Xmx384m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
