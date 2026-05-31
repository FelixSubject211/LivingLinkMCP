# Dockerfile
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY gradle.properties .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY server/build.gradle.kts server/build.gradle.kts
COPY server/src server/src

RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon :server:installDist


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /workspace/server/build/install/server ./

EXPOSE 3000

ENTRYPOINT ["./bin/server"]