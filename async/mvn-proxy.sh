#!/usr/bin/env bash
HOST=${HOST:-127.0.0.1}
PORT=${PORT:-11032}
export MAVEN_OPTS="${MAVEN_OPTS} -DsocksProxyHost=${HOST} -DsocksProxyPort=${PORT}"