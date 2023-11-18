FROM openjdk:18
EXPOSE 8081
ADD target/savecloudmanagerbackend.jar savecloudmanagerbackend.jar
ENTRYPOINT ["java","-jar","/savecloudmanagerbackend.jar"]