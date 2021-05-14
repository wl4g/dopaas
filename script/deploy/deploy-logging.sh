#!/bin/bash

#/*
# * Copyright 2017 ~ 2025 the original author or authors. <Wanglsir@gmail.com, 983708408@qq.com>
# *
# * Licensed under the Apache License, Version 2.0 (the "License");
# * you may not use this file except in compliance with the License.
# * You may obtain a copy of the License at
# *
# *      http://www.apache.org/licenses/LICENSE-2.0
# *
# * Unless required by applicable law or agreed to in writing, software
# * distributed under the License is distributed on an "AS IS" BASIS,
# * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# * See the License for the specific language governing permissions and
# * limitations under the License.
# */
[ -z "$currDir" ] && export currDir=$(cd "`dirname $0`"/ ; pwd)

function getCurrPid() {
  local pid=$!
  if [ "$pid" == "" ]; then
    echo "main"
    return 0
  fi
  echo "pid/$pid"
  return 0
}

# Core logging.
# e.g1: log "error" "Failed to xxx"
# e.g2: log "xxx complete!"
function log() {
  local logLevel="INFO "
  local logContent=$1
  if [[ $# > 1 ]]; then
    logLevel=$(echo -e "$1" | tr 'a-z' 'A-Z') # To UpperCase
    logContent=$2
  fi
  local logMsg="[$logLevel] $(date '+%Y-%m-%d %H:%M:%S') - [$(getCurrPid)] $logContent"
  echo -e $logMsg
  echo -e $logMsg >> ${logFile}
}

# Error logging.
function logErr() {
  log "ERROR" "$@"
}
