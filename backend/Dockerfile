#FROM openjdk:17
#ADD /build/libs/*.jar app.jar
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
# 빌드 이미지
FROM gradle:8.4-jdk17 AS build
WORKDIR /app
COPY . /app
RUN gradle clean build --no-daemon

# 런타임 이미지
FROM openjdk:17
COPY --from=build /app/build/libs/*.jar /app/reactMapping.jar

EXPOSE 8080
# 시작 지연 추가 (10초 대기 후 애플리케이션 시작)
ENTRYPOINT ["sh", "-c", "sleep 10 && java -jar /app/reactMapping.jar"]

