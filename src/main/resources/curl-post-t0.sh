#!/usr/bin/env bash
ADDR=http://10.32.65.73:8080/obsh_ecp/xwecp.do
CONT="Content-Type:text/xml;charset=UTF-8"
FILE=t0.xml
curl -v -X POST --header ${CONT} -d @${FILE} ${ADDR}