#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/moduform
cd $REPOSITORY

APP_LOG=/home/ubuntu/moduform/application.log
ERROR_LOG=/home/ubuntu/moduform/error.log
DEPLOY_LOG=/home/ubuntu/moduform/application.log

APP_NAME=checkmate

JAR_NAME=$(ls $REPOSITORY | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/$JAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 종료할 애플리케이션이 없습니다."
else
  echo "> kill -9 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> Deploy - $JAR_PATH "
nohup java -jar -Duser.timezone=Asia/Seoul $JAR_PATH --logging.level.org.hibernate.SQL=DEBUG > $APP_LOG 2>>$ERROR_LOG &
