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
COPY build.gradle settings.gradle ./
COPY gradle gradle

# 의존성 캐시 활용
RUN ./gradlew dependencies --no-daemon || return 0

# ✅ 개발 환경: 소스 코드 실시간 반영
COPY . .
EXPOSE 8080

# ✅ 변경된 부분: JAR 실행 → Gradle bootRun으로 변경
CMD ["./gradlew", "bootRun", "--no-daemon"]

# 2️⃣ 배포 환경 설정
FROM openjdk:17-jdk-slim AS production
WORKDIR /app

# 빌드한 JAR 파일 복사 (배포용)
COPY --from=development /app/build/libs/*.jar app.jar

# JVM 성능 최적화
EXPOSE 8080
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:+UseG1GC", "-jar", "app.jar"]
