#!/usr/bin/env bash
#export GRADLE_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"
##gradlew :NHttpWeb:jettyRun &

#### just for tomcat7x
##export JPDA_OPTS='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005'

##gradlew --stacktrace :NHttpWeb:cargoRunLocal &

## for Idea
kill -15 $(jps | grep Bootstrap | tr -d 'Bootstrap')
rm -rv /opt/web/tomcat/7062/webapps/nio*
./gradlew :NHttpWeb:war
cp -v web/build/libs/nio.war /opt/web/tomcat/7062/webapps/
JAVA_OPTS="-Dhttp.url=http://www.bing.com -Dhttp.nio.timeout=200000" VER=7062 DEBUG=1 tomcat-start.sh start

