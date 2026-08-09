# BookRecommendation 后端服务镜像（Render Docker 部署用）
# 构建：Maven 3.8 + JDK 8；运行：JDK 8 JRE
FROM maven:3.8-openjdk-8 AS build
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B -q || true
COPY backend/src ./src
RUN mvn package -DskipTests -B -q

FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /app/target/recommendation_data-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENV JAVA_OPTS="-Xmx384m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
