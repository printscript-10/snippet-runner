FROM gradle:8.5-jdk21 AS build
COPY  . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle assemble
FROM eclipse-temurin:21-jre

ARG USERNAME=$USERNAME
ARG TOKEN=$TOKEN

EXPOSE 8082

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production","/app/snippet-runner.jar"]
