# syntax=docker/dockerfile:1

# ---- Build stage ----------------------------------------------------------
# Gradle 래퍼로 bootJar를 만든다. 의존성 캐시를 위해 래퍼·빌드스크립트를 먼저 복사.
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Gradle 래퍼와 빌드 설정 먼저 복사 → 소스만 바뀌면 의존성 다운로드 레이어 재사용
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

# 소스 복사 후 jar 빌드 (테스트는 라이브 IT 포함이라 빌드 단계에서 제외)
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

# ---- Runtime stage --------------------------------------------------------
# 빌드 산출 jar만 가진 가벼운 JRE 이미지
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

# Railway가 PORT 환경변수를 주입. application.yml의 ${PORT:8080}이 받음.
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

# 컨테이너 가용 메모리의 75%까지 힙으로 사용(기본 25%). 큰 응답 처리 시 GC 쥐어짜기 완화.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
