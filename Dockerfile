FROM eclipse-temurin:23.0.2_7-jdk-alpine-3.21 AS builder
WORKDIR /application
COPY . .
RUN --mount=type=cache,target=/root/.m2  chmod +x mvnw && ./mvnw clean install -Dmaven.test.skip

FROM eclipse-temurin:23.0.2_7-jre-alpine-3.21 AS layers
WORKDIR /application
COPY --from=builder /application/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:23.0.2_7-jre-alpine-3.21
VOLUME /tmp
RUN adduser -S spring-user
USER spring-user
RUN mkdir -p /home/spring-user/logs && \
    chown spring-user:spring-user /home/spring-user/logs && \
    chmod 775 /home/spring-user/logs
USER spring-user
COPY --from=layers /application/dependencies/ ./
COPY --from=layers /application/spring-boot-loader/ ./
COPY --from=layers /application/snapshot-dependencies/ ./
COPY --from=layers /application/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
