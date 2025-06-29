FROM gradle:jdk21-graal-jammy as builder

ARG SKIP_TESTS=true

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN chmod +x ./gradlew

RUN ./gradlew dependencies --no-daemon

COPY src src

RUN if [ "$SKIP_TESTS" = "true" ]; then \
        ./gradlew build -x test --no-daemon; \
    else \
        ./gradlew build --no-daemon; \
    fi

FROM ghcr.io/graalvm/jdk-community:21

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
