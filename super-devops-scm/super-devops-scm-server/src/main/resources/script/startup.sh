#!/bin/bash
echo "Application start ..."
EXEC_CMD="java -server  -Xms256M -Xmx1G -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:+DisableExplicitGC -Djava.awt.headless=true -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/mnt/disk1/log/${APP_NAME}/jvm_dump.hprof   -Dfile.encoding=UTF-8 -cp .:${APP_HOME}/${APP_BIN_NAME}/libs/* com.wl4g.devops.ScmServer  --spring.profiles.active=test --server.tomcat.basedir=/mnt/disk1/${APP_HOME} --logging.file=/mnt/disk1/log/${APP_HOME}/${APP_HOME}.log"
$EXEC_CMD


