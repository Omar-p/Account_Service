FROM adoptopenjdk/openjdk11:alpine
WORKDIR /app
COPY ["Account Service/task/build.gradle", "./Account Service/task/"]
COPY ["gradlew.bat", "gradlew", "docker/settings.gradle", "build.gradle", "./"] 
COPY ["gradle","./gradle"]
COPY ["util", "./util"]
RUN ["./gradlew", "dependencies"]


COPY ["./Account Service/task/src/", "./Account Service/task/src/"]
COPY ["./Account Service/task/test/", "./Account Service/task/test/"]
RUN ["./gradlew", "build"]

FROM fabric8/java-alpine-openjdk11-jre
COPY --from=0 ["/app/Account Service/task/build/libs/Account_Service-task.jar", "."]
ENTRYPOINT ["java", "-jar", "Account_Service-task.jar"]
