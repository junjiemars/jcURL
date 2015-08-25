#!/usr/bin/env bash

HTTP_URL=${HTTP_URL:-"http://cn.bing.com"}
HTTP_NIO_TIMEOUT=${HTTP_NIO_TIMEOUT:-200000}
HTTP_BIO_TIMEOUT=${HTTP_BIO_TIMEOUT:-2000}
WEB_DIR=${WEB_DIR:-"/opt/web/tomcat/7062/webapps/"}
JPS_WEB=${JPS_WEB:-"Bootstrap"}

#export GRADLE_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"
##gradlew :NHttpWeb:jettyRun &

#### just for tomcat7x
##export JPDA_OPTS='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005'

##gradlew --stacktrace :NHttpWeb:cargoRunLocal &

## for Idea
kill -15 $(jps | grep ${JPS_WEB} | tr -d ${JPS_WEB})
rm -rv ${WEB_DIR}nio*
./gradlew :NHttpWeb:war
cp -v web/build/libs/nio.war ${WEB_DIR}
JAVA_OPTS="-Dhttp.url=${HTTP_URL} -Dhttp.nio.timeout=${HTTP_NIO_TIMEOUT}" \
    VER=7062 \
    DEBUG=1 \
    tomcat-start.sh start

