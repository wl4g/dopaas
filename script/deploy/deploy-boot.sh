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

if [[ "$(echo groups)" == "root" ]]; then
  logErr "Please execute the scripts as a user with root privileges !" && exit -1
fi

# Global definition.
[ -z "$currDir" ] && export currDir=$(echo "$(cd "`dirname "$0"`"/; pwd)")
export workspaceDir="/tmp/.deploy-workspace"
export scriptsBaseUrl="https://raw.githubusercontent.com/wl4g/xcloud-devops/master/script/deploy"
export scriptsBaseUrlBackup1="https://gitee.com/wl4g/xcloud-devops/raw/master/script/deploy"
export gitBaseUrl="https://github.com/wl4g"
export gitBaseUrlBackup1="https://gitee.com/wl4g"

# Choose deploy mode.
while true
do
  read -t 20 -p "Please choose deployment mode (host|docker)? " deployMode
  if [ -n "$(echo $deployMode|grep -i 'host')" ]; then
    deployMode="host"
    break
  elif [ -n "$(echo $deployMode|grep -i 'docker')" ]; then
    deployMode="docker"
    echo "Docker deployment is not supported yet, please look forward to it! Welcome to join us, contact: <wanglsir@gmail.com, 983708408@qq.com>"
    exit -1;
  else
    continue
  fi
done

# Detect the host network and choose the fast resources intelligently.
echo "Analyzing network and intelligent configuration resources ..."
ipArea=$(curl --connect-timeout 10 -m 20 -sSL cip.cc)
if [ $? == 0 ]; then
  export isNetworkInGfwWall=$([[ "$ipArea" =~ "中国" || "$ipArea" =~ "朝鲜" ]] && echo Y || echo N)
else # Fallback
  ipArea=$(curl --connect-timeout 10 -m 20 -sSL ipinfo.io)
  export isNetworkInGfwWall=$([[ "$ipArea" =~ "\"country\": \"CN\"" ]] && echo Y || echo N)
fi
# Choose best resources URL.
if [ "$isNetworkInGfwWall" == "Y" ]; then
  export gitBaseUri="$gitBaseUrlBackup1" # for speed-up, fuck gfw!
  export scriptsBaseUrl="$scriptsBaseUrlBackup1"
fi

# Download deploy dependencies scripts.
mkdir -p $workspaceDir; cd $workspaceDir
[ -n "$(ls deploy-*.sh 2>/dev/null)" ] && \rm -rf $(ls deploy-*.sh|grep -v $0) # Cleanup scripts.
curl --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-env.sh"; [ $? -ne 0 ] && exit -1
curl --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-common.sh"; [ $? -ne 0 ] && exit -1
curl --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-host.sh"; [ $? -ne 0 ] && exit -1
curl --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-docker.sh"; [ $? -ne 0 ] && exit -1
cd $currDir && curl --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-host.csv"; [ $? -ne 0 ] && exit -1
chmod 750 $workspaceDir/deploy-*.sh

# Confirm deploy environments.
while true
do
  read -t 300 -p "Please confirm use the default configuration deployment (y|n)? (if you need to custom please edit \"$currDir/deploy-env.sh\") " confirm1
  if [ "$confirm1" == "y" ]; then
    break
  elif [ "$confirm1" == "n" ]; then
    echo "Please custom edit \"$currDir/deploy-env.sh\" first, and then re-execute \".$currDir/deploy-boot.sh\" to deploying !" && exit 0
  else
    continue
  fi
done

# Call deployer.
if [ "$deployMode" == "host" ]; then
  while true
  do
    read -t 300 -p "Do you just want to deploy to the local node first (y|n)? " confirm2
    if [ "$confirm2" == "y" ]; then
      # Auto generate localhost to deploy-host.csv
cat<<EOF>$currDir/deploy-host.csv
Host,User,Password
localhost,$USER,
EOF
      break
    elif [ "$confirm2" == "n" ]; then
      echo "You have chosen to deploy to the remote cluster node, so you need to configure the remote node information first. Please edit \"$currDir/deploy-host.csv\", and then re-execute \".$currDir/deploy-boot.sh\" again"
      exit 0
    else
      continue
    fi
  done
  bash $workspaceDir/deploy-host.sh
elif [ "$deployMode" == "docker" ]; then
  bash $workspaceDir/deploy-docker.sh
else
  echo "Unknown deploy mode of \"$deployMode\" !"
fi

cd $workspaceDir && [ -n "$(ls deploy-*.sh 2>/dev/null)" ] && \rm -rf $(ls deploy-*.sh|grep -v $0) # Cleanup scripts.
exit 0
