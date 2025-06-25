FROM gradle:jdk21-graal-jammy as builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x ./gradlew

RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew build --no-daemon

FROM ghcr.io/graalvm/jdk-community:21

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
