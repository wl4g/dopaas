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
# @see: http://www.huati365.com/answer/j6BxQYLqYVeWe4k

# ----------------------- Initialization. -----------------------------------------------------------
[ -z "$currDir" ] && export currDir=$(cd "`dirname $0`"/ ; pwd)

[ "$loadedDeployEnvBaseWithProcessNum" != "$$" ] && . $currDir/deploy-env-base.sh && export loadedDeployEnvBaseWithProcessNum="$$"
[ "$loadedDeployLoggingWithProcessNum" != "$$" ] && . $currDir/deploy-logging.sh && export loadedDeployLoggingWithProcessNum="$$"

globalExportedEnvStr=""
# Initilization and merge the services configuration that the runtime depends. ---------------------------------
function initRuntimeEnvConfiguration() {
  # Common environment configuration.
  [ -z "$runtimeAppSpringProfilesActive" ] && export runtimeAppSpringProfilesActive="pro"
  globalExportedEnvStr="${globalExportedEnvStr}\nexport runtimeAppSpringProfilesActive='${runtimeAppSpringProfilesActive}'"

  # IAM environment configuration.
  [ -z "$IAM_DB_URL" ] && export IAM_DB_URL="jdbc:mysql://localhost:3306/iam?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true"
  [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: IAM_DB_URL='$(eval echo $IAM_DB_URL)'"

  [ -z "$IAM_DB_USER" ] && export IAM_DB_USER="iam"
  [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: IAM_DB_USER='$(eval echo $IAM_DB_USER)'"

  [ -z "$IAM_DB_PASSWD" ] && export IAM_DB_PASSWD="123456"
  [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: IAM_DB_PASSWD='$(eval echo $IAM_DB_PASSWD)'"

  [ -z "$IAM_REDIS_PASSWD" ] && export IAM_REDIS_PASSWD="123456"
  [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: IAM_REDIS_PASSWD='$(eval echo $IAM_REDIS_PASSWD)'"

  [ -z "$IAM_REDIS_NODES" ] && export IAM_REDIS_NODES="localhost:6379"
  [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: IAM_REDIS_NODES='$(eval echo $IAM_REDIS_NODES)'"

  globalExportedEnvStr="${globalExportedEnvStr}\nexport IAM_DB_URL='${IAM_DB_URL}'"
  globalExportedEnvStr="${globalExportedEnvStr}\nexport IAM_DB_USER='${IAM_DB_USER}'"
  globalExportedEnvStr="${globalExportedEnvStr}\nexport IAM_DB_PASSWD='${IAM_DB_PASSWD}'"
  globalExportedEnvStr="${globalExportedEnvStr}\nexport IAM_REDIS_PASSWD='${IAM_REDIS_PASSWD}'"
  globalExportedEnvStr="${globalExportedEnvStr}\nexport IAM_REDIS_NODES='${IAM_REDIS_NODES}'"

  # DoPaaS environment configuration.
  if [ "$runtimeMode" == "standalone" ]; then
    export STANDALONE_DOPAAS_DB_URL='jdbc:mysql://localhost:3306/dopaas_standalone?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true'
    export STANDALONE_DOPAAS_DB_USER='dopaas_standalone'
    export STANDALONE_DOPAAS_DB_PASSWD='123456'
    export STANDALONE_DOPAAS_REDIS_PASSWD='123456'
    export STANDALONE_DOPAAS_REDIS_NODES='localhost:6379'
    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_DB_URL='${STANDALONE_DOPAAS_DB_URL}'"
    [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: STANDALONE_DOPAAS_DB_URL='$(eval echo $STANDALONE_DOPAAS_DB_URL)'"

    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_DB_USER='${STANDALONE_DOPAAS_DB_USER}'"
    [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: STANDALONE_DOPAAS_DB_USER='$(eval echo $STANDALONE_DOPAAS_DB_USER)'"

    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_DB_PASSWD='${STANDALONE_DOPAAS_DB_PASSWD}'"
    [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: STANDALONE_DOPAAS_DB_PASSWD='$(eval echo $STANDALONE_DOPAAS_DB_PASSWD)'"

    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_REDIS_PASSWD='${STANDALONE_DOPAAS_REDIS_PASSWD}'"
    [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: STANDALONE_DOPAAS_REDIS_PASSWD='$(eval echo $STANDALONE_DOPAAS_REDIS_PASSWD)'"

    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_REDIS_NODES='${STANDALONE_DOPAAS_REDIS_NODES}'"
    [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: STANDALONE_DOPAAS_REDIS_NODES='$(eval echo $STANDALONE_DOPAAS_REDIS_NODES)'"
  elif [ "$runtimeMode" == "cluster" ]; then
    local knownModuleNames=()
    for ((i=0;i<${#deployClusterBuildModules[@]};i++)) do
      local buildModule=${deployClusterBuildModules[i]}
      local appName=$(echo "$buildModule"|awk -F ',' '{print $1}')
      local appShortNameUpper=$(echo $appName|tr '[a-z]' '[A-Z]'|awk -F '-' '{print $1}') # e.g cmdb-facade => CMDB
      local appShortNameLower=$(echo $appShortNameUpper|tr '[A-Z]' '[a-z]') # e.g cmdb-facade => cmdb
      if [[ "$appShortNameUpper" == "IAM" || "${knownModuleNames[@]}"  =~ "${appShortNameUpper}" ]]; then # Skip and remove duplicate.
        continue
      fi
      knownModuleNames[${#knownModuleNames[@]}]="$appShortNameUpper"
      # For example: export CMDB_DOPAAS_DB_USER="dopaas_cmdb"
      local key1="${appShortNameUpper}_DOPAAS_DB_URL" && export "$key1"="jdbc:mysql://localhost:3306/dopaas_$appShortNameLower?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true"
      [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: $key1='$(eval echo '$'$key1)'"

      local key2="${appShortNameUpper}_DOPAAS_DB_USER" && export "$key2"="dopaas_${appShortNameLower}"
      [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: $key2='$(eval echo '$'$key2)'"

      local key3="${appShortNameUpper}_DOPAAS_DB_PASSWD" && export "$key3"='123456'
      [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: $key3='$(eval echo '$'$key3)'"

      local key4="${appShortNameUpper}_DOPAAS_REDIS_PASSWD" && export "$key4"='123456'
      [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: $key4='$(eval echo '$'$key4)'"

      local key5="${appShortNameUpper}_DOPAAS_REDIS_NODES" && export "$key5"='localhost:6379'
      [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: $key5='$(eval echo '$'$key5)'"

      globalExportedEnvStr="${globalExportedEnvStr}\nexport $key1='$(eval echo '$'$key1)'"
      globalExportedEnvStr="${globalExportedEnvStr}\nexport $key2='$(eval echo '$'$key2)'"
      globalExportedEnvStr="${globalExportedEnvStr}\nexport $key3='$(eval echo '$'$key3)'"
      globalExportedEnvStr="${globalExportedEnvStr}\nexport $key4='$(eval echo '$'$key4)'"
      globalExportedEnvStr="${globalExportedEnvStr}\nexport $key5='$(eval echo '$'$key5)'"
    done
  else
    echo "Invalid runtime mode to $runtimeMode"; exit -1
  fi
}

initRuntimeEnvConfiguration
