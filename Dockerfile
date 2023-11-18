# Build stage
FROM maven:3.9.5 AS build
WORKDIR /home/app
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean install

# Runtime stage
FROM openjdk:18
EXPOSE 8081
COPY --from=build /home/app/target/savecloudmanagerbackend.jar /savecloudmanagerbackend.jar
ENTRYPOINT ["java", "-jar", "/savecloudmanagerbackend.jar"]
