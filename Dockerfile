#
# Build stage
#
FROM maven:3.9.3 AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:17
COPY --from=build /target/blog-api-0.0.1-SNAPSHOT.jar blog-api.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","blog-api.jar"]
