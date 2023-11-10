FROM ubuntu:latest as BUILD

RUN apt-get update
RUN apt-get install openjdk-17-jdk postgresql -y
RUN service postgresql start && \
  psql -h localhost -U postgres -d postgres -c "CREATE DATABASE todospring"


COPY . .

RUN apt-get install maven -y
RUN mvn clean install

FROM openjdk:17-jdk-slim
EXPOSE 8080

COPY --from=build /target/todolist-1.0.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
