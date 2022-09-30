FROM openjdk:11

ADD build/libs/hello-service.jar /usr/local/hello-service/

WORKDIR /usr/local/hello-service/

EXPOSE 8080

CMD ["java", "-Xms512m", "-Xmx1g", "-jar", "hello-service.jar"]