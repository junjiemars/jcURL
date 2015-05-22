#!/usr/bin/env bash
JAVA_OPTS="-Dhttp.url=http://as3:8080/nio/echo -Decho.timeout=2000" tomcat-start.sh
