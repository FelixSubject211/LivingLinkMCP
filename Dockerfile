# Dockerfile
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon installDist


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /workspace/build/install/livinglink ./

EXPOSE 3000

ENTRYPOINT ["./bin/livinglink"]