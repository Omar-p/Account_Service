FROM openjdk:17-alpine
WORKDIR /app
COPY ["Account Service/task/build.gradle", "./task/"]
COPY ["gradlew.bat", "gradlew", "docker/settings.gradle", "build.gradle", "./"] 
COPY ["gradle","./gradle"]
COPY ["util", "./util"]

COPY ["./Account Service/task/src/", "./task/src/"]
COPY ["./Account Service/task/test/", "./task/test/"]


CMD ["sh"]
