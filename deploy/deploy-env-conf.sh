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

. ${currDir}/deploy-env-base.sh

# Initilization and merge the services configuration that the runtime depends. ---------------------------------
function initRuntimeEnvironmentConfiguration() {
  # Common environment configuration.
  [ -z "$runtimeAppSpringProfilesActive" ] && export runtimeAppSpringProfilesActive="pro"

  # IAM environment configuration.
  [ -z "$IAM_DB_URL" ] && export IAM_DB_URL="jdbc:mysql://localhost:3306/iam?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true"
  [ -z "$IAM_DB_USER" ] && export IAM_DB_USER="iam"
  [ -z "$IAM_DB_PASSWD" ] && export IAM_DB_PASSWD="123456"
  [ -z "$IAM_REDIS_PASSWD" ] && export IAM_REDIS_PASSWD="123456"
  [ -z "$IAM_REDIS_NODES" ] && export IAM_REDIS_NODES="localhost:6379"

  # DoPaaS environment configuration.
  if [ "$runtimeMode" == "standalone" ]; then
      local key="STANDALONE_DOPAAS_DB_URL" && [ -z "$key" ] && eval export "$key"="jdbc:mysql://localhost:3306/dopaas?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true"
      local key="STANDALONE_DOPAAS_DB_USER" && [ -z "$key" ] && eval export "$key"="dopaas_$appShortNameLower"
      local key="STANDALONE_DOPAAS_DB_PASSWD" && [ -z "$key" ] && eval export "$key"="123456"
      local key="STANDALONE_DOPAAS_REDIS_PASSWD" && [ -z "$key" ] && eval export "$key"="123456"
      local key="STANDALONE_DOPAAS_REDIS_NODES" && [ -z "$key" ] && eval export "$key"="localhost:6379"
  elif [ "$runtimeMode" == "cluster" ]; then
    for ((i=0;i<${#deployClusterBuildModules[@]};i++)) do
      local buildModule=${deployClusterBuildModules[i]}
      local appName=$(echo "$buildModule"|awk -F ',' '{print $1}')
      local appShortNameUpper=$(echo $appName|tr '[a-z]' '[A-Z]'|awk -F '-' '{print $1}') # e.g cmdb-facade => CMDB
      local appShortNameLower=$(echo $appShortNameUpper|tr '[A-Z]' '[a-z]') # e.g cmdb-facade => cmdb
      # For example: export CMDB_DOPAAS_DB_USER="dopaas_cmdb"
      local key="$appShortNameUpper_DOPAAS_DB_URL" && [ -z "$key" ] && eval export "$key"="jdbc:mysql://localhost:3306/dopaas?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true"
      local key="$appShortNameUpper_DOPAAS_DB_USER" && [ -z "$key" ] && eval export "$key"="dopaas_$appShortNameLower"
      local key="$appShortNameUpper_DOPAAS_DB_PASSWD" && [ -z "$key" ] && eval export "$key"="123456"
      local key="$appShortNameUpper_DOPAAS_REDIS_PASSWD" && [ -z "$key" ] && eval export "$key"="123456"
      local key="$appShortNameUpper_DOPAAS_REDIS_NODES" && [ -z "$key" ] && eval export "$key"="localhost:6379"
    done
  else
    echo "Invalid runtime mode to $runtimeMode"; exit -1
  fi
}
initRuntimeEnvironmentConfiguration
