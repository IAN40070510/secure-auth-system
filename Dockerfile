# 使用 Maven 建置專案 (第一階段)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# 跳過測試進行打包，加快速度
RUN mvn clean package -DskipTests

# 使用輕量級 JDK 執行專案 (第二階段)
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
# 從第一階段複製打包好的 jar 檔
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]