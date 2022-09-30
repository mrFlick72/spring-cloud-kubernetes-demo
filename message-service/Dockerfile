FROM openjdk:17

ADD build/libs/message-service.jar /usr/local/message-service/

WORKDIR /usr/local/message-service/

EXPOSE 8080

CMD ["java", "-Xms512m", "-Xmx1g", "-jar", "message-service.jar"]