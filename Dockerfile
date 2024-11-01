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
ENTRYPOINT ["java", "-jar", "/app/reactMapping.jar"]


