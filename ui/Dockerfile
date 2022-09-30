FROM openjdk:11

ADD target/ui-interface.jar /usr/local/ui-interface/

WORKDIR /usr/local/ui-interface/

EXPOSE 8080

CMD ["java", "-Xms512m", "-Xmx1g", "-jar", "ui-interface.jar"]