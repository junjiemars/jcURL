#!/usr/bin/env bash
ADDR=${ADDR:-"http://10.32.65.73:8081/obsh_ecp/xwecp.do"}
CONT=${CONT:-"Content-Type:text/xml;charset=UTF-8"}
FILE=${FILE:-t0.xml}
curl -v -X POST --header ${CONT} -d @${FILE} ${ADDR}
