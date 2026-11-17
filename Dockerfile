FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --create-home --home-dir /home/spring --uid 10001 spring \
    && mkdir -p /home/spring/trash-busters/uploads \
    && chown -R spring:spring /home/spring

COPY --from=build /workspace/target/*.jar app.jar

USER spring
EXPOSE 8080

# -Xmx380m evita estouro nos 512MB do plano gratuito do Render (aumente se usar VPS de 2GB)
ENTRYPOINT ["java", "-Xmx380m", "-Xms256m", "-jar", "/app/app.jar"]