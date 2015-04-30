#!/usr/bin/env bash
#export GRADLE_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"
##gradlew :NHttpWeb:jettyRun &

## just for tomcat7x
export JPDA_OPTS='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005'

gradlew --stacktrace :NHttpWeb:cargoRunLocal &
