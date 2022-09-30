FROM openjdk:17

ADD target/ui-interface.jar /usr/local/ui-interface/

WORKDIR /usr/local/ui-interface/

EXPOSE 8080

CMD ["java", "-jar", "ui-interface.jar"]