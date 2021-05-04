#!/bin/sh
# - - - - - - - -
export TOMCAT_HOME=/Library/Tomcat
export WEBAPP_NAME=hey
# - - - - - - - -
mkdir -p target/WEB-INF/classes
cd src
javac -cp .:$TOMCAT_HOME/lib/*:../WebContent/WEB-INF/lib/* -d ../target/WEB-INF/classes hey/action/*.java
javac -cp .:$TOMCAT_HOME/lib/*:../WebContent/WEB-INF/lib/* -d ../target/WEB-INF/classes hey/model/*.java
cd ..
cp -r WebContent/* target
cp src/*.* target/WEB-INF/classes
cp -r src/hey target/WEB-INF/classes
cd target
jar cf ../$WEBAPP_NAME.war *
cd ..
# deploy the 'hey.war' archive into Tomcat's webapps:
# cp $WEBAPP_NAME.war $TOMCAT_HOME/webapps