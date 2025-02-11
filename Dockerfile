# ✅ Backend용 Dockerfile

# 1️⃣ 개발 환경 설정
FROM openjdk:17-jdk-slim AS development
WORKDIR /app

# Gradle 캐시 폴더 설정 (빌드 속도 최적화)
VOLUME ["/root/.gradle"]

# Gradle Wrapper 권한 설정
COPY gradlew .
RUN chmod +x gradlew

# Gradle 설정 파일 복사
COPY build.gradle settings.gradle
COPY gradle gradle

# 의존성 캐시 활용
RUN ./gradlew dependencies --no-daemon || return 0

# 전체 소스 코드 복사 후 빌드
COPY . .
RUN ./gradlew build -x test --no-daemon

# 핫 리로드를 위한 DevTools 설정
EXPOSE 8080
CMD ["java", "-jar", "build/libs/MusinsaBackend-0.0.1-SNAPSHOT.jar"]

# 2️⃣ 배포 환경 설정
FROM openjdk:17-jdk-slim AS production
WORKDIR /app

# 빌드한 JAR 파일 복사 (이름 고정)
COPY --from=development /app/build/libs/*.jar app.jar

# JVM 성능 최적화 옵션 추가
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:+UseG1GC", "-jar", "app.jar"]
