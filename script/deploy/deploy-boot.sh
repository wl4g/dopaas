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
scriptBaseUrl="https://raw.githubusercontent.com/wl4g/xcloud-devops/master/script/deploy"
secondaryScriptBaseUrl="https://gitee.com/wl4g/xcloud-devops/raw/master/script/deploy"

# Download deploy scripts.
function downloadScripts() {
  local baseUrl=$1
  curl --connect-timeout 10 -m 20 -O "$baseUrl/deploy-env.sh"
  curl --connect-timeout 10 -m 20 -O "$baseUrl/deploy-common.sh"
  curl --connect-timeout 10 -m 20 -O "$baseUrl/deploy-host.sh"
  curl --connect-timeout 10 -m 20 -O "$baseUrl/deploy-host.csv"
  curl --connect-timeout 10 -m 20 -O "$baseUrl/deploy-docker.sh"
  return $?
}
downloadScripts $scriptBaseUrl
if [ $? -ne 0 ]; then
  echo "Downloading from backup address: $secondaryScriptBaseUrl ..."
  downloadScripts $secondaryScriptBaseUrl # e.g connection refused, fuck gfw!
fi

# Choose deploy config.
while true
do
  read -t 10 -p "Do you want to install with the default configuration(yes|no)? (Modify to customize configuration: $currDir/deploy-env.sh)" confirm
  if [ "$(echo $confirm|egrep -i 'yes')" ]; then
    break;
  elif [ "$(echo $confirm|egrep -i 'no')" ]; then
    echo "Please try again after modified: $currDir/deploy-env.sh"
    exit 0
  else
    echo "Please reenter it!"
  fi
done

# Choose deploy mode.
while true
do
  read -t 10 -p "Please choose deployment mode? (host|docker)" deployMode
  if [ -n "$(echo $deployMode|egrep -i 'host|HOST')" ]; then
    deployMode="HOST"
    break;
  elif [ -n "$(echo $deployMode|egrep -i 'docker|DOCKER')" ]; then
    deployMode="DOCKER"
    echo "Docker deployment is not supported yet, please look forward to it! Welcome to join us, contact: <wanglsir@gmail.com, 983708408@qq.com>"
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
  echo "Unknown deploy mode!"
fi

# Cleanup
\rm -rf "$currDir/deploy-*.sh"
