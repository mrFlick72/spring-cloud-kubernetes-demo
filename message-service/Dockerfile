FROM openjdk:17

ADD build/libs/message-service.jar /usr/local/message-service/

WORKDIR /usr/local/message-service/

EXPOSE 8080

CMD ["java", "-jar", "message-service.jar"]