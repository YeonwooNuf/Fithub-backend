# ✅ Backend용 Dockerfile

# 1️⃣ 개발 환경 설정
FROM openjdk:17-jdk-slim AS development
WORKDIR /app

# Gradle 캐시 활용을 위해 필요한 파일 먼저 복사
COPY build.gradle settings.gradle .
COPY gradle gradle
RUN ./gradlew build -x test || return 0  # 테스트는 개발 단계에서 생략 가능

# 전체 소스 코드 복사 후 빌드
COPY . .
RUN ./gradlew build -x test

# 핫 리로드를 위한 DevTools 설정
EXPOSE 8080
CMD ["java", "-jar", "build/libs/MusinsaBackend-0.0.1-SNAPSHOT.jar"]

# 2️⃣ 배포 환경 설정
FROM openjdk:17-jdk-slim AS production
WORKDIR /app

# 개발 단계에서 빌드한 JAR 파일 복사
COPY --from=development /app/build/libs/*.jar app.jar

# 최적화된 설정으로 애플리케이션 실행
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]