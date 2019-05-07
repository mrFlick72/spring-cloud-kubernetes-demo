#!/usr/bin/env bash

docker-compose up 2>docker.log &

cd ..

PROJECT_HOME=`pwd`

HELLO_SERVICE_JAR_DIR=$PROJECT_HOME/hello-service/build/libs
MESSAGE_SERVICE_JAR_DIR=$PROJECT_HOME/message-service/build/libs
UI_JAR_DIR=$PROJECT_HOME/ui/target

echo "The content of PROJECT_HOME is: $PROJECT_HOME"
echo "The content of HELLO_SERVICE_JAR_DIR is: $HELLO_SERVICE_JAR_DIR"
echo "The content of MESSAGE_SERVICE_JAR_DIR is: $MESSAGE_SERVICE_JAR_DIR"
echo "The content of UI_JAR_DIR is: $UI_JAR_DIR"


echo "start of hello-service"
cd $HELLO_SERVICE_JAR_DIR
java -jar -Dspring.profiles.active=netflix hello-service.jar >$PROJECT_HOME/hello-service.log &


echo "start of message-service"
cd $MESSAGE_SERVICE_JAR_DIR
java -jar -Dspring.profiles.active=netflix message-service.jar >$PROJECT_HOME/message-service.log &


echo "start of ui"
cd $UI_JAR_DIR
java -jar -Dspring.profiles.active=netflix ui-interface.jar >$PROJECT_HOME/ui-interface.log &