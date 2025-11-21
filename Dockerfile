FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*jar-with-dependencies.jar app.jar
EXPOSE 8989
CMD ["java", "-jar", "app.jar"]