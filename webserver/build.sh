#!/bin/sh
# - - - - - - - -
export TOMCAT_HOME=/Library/Tomcat
export WEBAPP_NAME=webserver
# - - - - - - - -
mkdir -p target/WEB-INF/classes

cd src
javac -cp .:$TOMCAT_HOME/lib/*:../WebContent/WEB-INF/lib/* -d ../target/WEB-INF/classes webserver/action/*.java
javac -cp .:$TOMCAT_HOME/lib/*:../WebContent/WEB-INF/lib/* -d ../target/WEB-INF/classes webserver/model/*.java
javac -cp .:$TOMCAT_HOME/lib/*:../WebContent/WEB-INF/lib/* -d ../target/WEB-INF/classes ws/*.java

cd ..
cp -r WebContent/* target
cp src/*.* target/WEB-INF/classes
cp -r src/webserver target/WEB-INF/classes
cp -r src/ws target/WEB-INF/classes

cd target
jar cf ../$WEBAPP_NAME.war *
cd ..
# deploy the 'webserver.war' archive into Tomcat's webapps:
# cp $WEBAPP_NAME.war $TOMCAT_HOME/webapps