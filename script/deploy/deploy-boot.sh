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
set -e

# Global definition.
currDir=$([ "$currDir" == "" ] && echo "$(cd "`dirname "$0"`"/; pwd)" || echo $currDir) && cd $currDir
scriptBaseUrl="https://github.com/wl4g/xcloud-devops/tree/master/script/deploy"

# Download deploy scripts.
curl --connect-timeout 10 -m 10 -O $scriptBaseUrl/deploy-env.sh
curl --connect-timeout 10 -m 10 -O $scriptBaseUrl/deploy-common.sh
curl --connect-timeout 10 -m 10 -O $scriptBaseUrl/deploy-host.sh
curl --connect-timeout 10 -m 10 -O $scriptBaseUrl/deploy-docker.sh

# Choose deploy mode.
while true
do
  read -t 10 -p "Please choose deployment mode? (host|docker)" deployMode
  if [ -n "$(echo $deployMode|egrep -i 'host|HOST')" ]; then
    deployMode="HOST"
    break;
  elif [ -n "$(echo $deployMode|egrep -i 'docker|DOCKER')" ]; then
    deployMode="DOCKER"
    logErr "Docker deployment is not supported yet, please look forward to it! Welcome to join us, contact: <wanglsir@gmail.com, 983708408@qq.com>"
    exit -1;
  else
    echo "Please reenter it!"
  fi
done

# Deploying
if [ "$deployMode" == "HOST" ]; then
  bash $currDir/deploy-host.sh
elif [ "$deployMode" == "DOCKER" ]; then
  bash $currDir/deploy-docker.sh
else
  logErr "Unknown deploy mode!"
fi
