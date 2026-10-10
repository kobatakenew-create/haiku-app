FROM node:24-bookworm-slim AS frontend-build

WORKDIR /app

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build


FROM maven:3.9-eclipse-temurin-21 AS backend-build

WORKDIR /app

COPY pom.xml ./
COPY src/ ./src/

COPY --from=frontend-build /app/dist/ ./src/main/resources/static/

RUN mvn -B -DskipTests package


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=backend-build /app/target/haiku-app-0.0.1-SNAPSHOT.jar ./app.jar

COPY src/main/resources/certs/aiven-ca.pem ./aiven-ca.pem

RUN keytool -importcert -noprompt \
    -alias aiven-ca \
    -file /app/aiven-ca.pem \
    -keystore /app/aiven-truststore.p12 \
    -storetype PKCS12 \
    -storepass changeit

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=60.0", "-Duser.timezone=Asia/Tokyo", "-jar", "/app/app.jar"]