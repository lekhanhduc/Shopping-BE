FROM maven:3.9.8-amazoncorretto-21 AS build
LABEL maintainer="lekhanhduccc@gmail.com"
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

FROM amazoncorretto:21.0.4
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]