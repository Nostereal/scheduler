FROM openjdk:11
WORKDIR /app
COPY . .
RUN ./gradlew build
#COPY ./build/libs/*.jar ./scheduler-ktor.jar
ENTRYPOINT ["java","-jar","build/libs/scheduler-ktor-0.0.1.jar"]
#ENTRYPOINT ["java","-cp","build/libs/scheduler-ktor-0.0.1.jar", "com.scheduler.ApplicationKt"]
