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

if [[ "$(echo groups)" == "root" ]]; then
  logErr "Please execute the scripts as a user with root privileges !"; exit -1
fi

# Global definition.
[ -z "$currDir" ] && export currDir=$(cd "`dirname $0`"/ ; pwd)
export scriptsBaseUrl="https://raw.githubusercontent.com/wl4g/xcloud-devops/master/script/deploy"
export scriptsBaseUrlBackup1="https://gitee.com/wl4g/xcloud-devops/raw/master/script/deploy"
export gitBaseUrl="https://github.com/wl4g"
export gitBaseUrlBackup1="https://gitee.com/wl4g"

# Detect the host network and choose the fast resources intelligently.
echo "Analyzing network and intelligent configuration resources ..."
ipArea=$(curl --connect-timeout 10 -m 20 -sSL cip.cc)
if [ $? == 0 ]; then
  export isNetworkInGfwWall=$([[ "$ipArea" =~ "中国" || "$ipArea" =~ "朝鲜" ]] && echo Y || echo N)
else # Fallback
  ipArea=$(curl --connect-timeout 10 -m 20 -sSL ipinfo.io)
  export isNetworkInGfwWall=$([[ "$ipArea" =~ "\"country\": \"CN\"" ]] && echo Y || echo N)
fi
# Choose best resources.
if [ "$isNetworkInGfwWall" == "Y" ]; then
  export gitBaseUri="$gitBaseUrlBackup1" # for speed-up, fuck gfw!
  export scriptsBaseUrl="$scriptsBaseUrlBackup1"
fi

# Download deploy dependencies scripts.
cd $currDir
\rm -rf $(ls deploy-*.sh 2>/dev/null|grep -v $0) # Cleanup scripts.
echo "Downloading deploy scripts dependencies ..."
curl --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-env.sh"; [ $? -ne 0 ] && exit -1
curl --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-common.sh"; [ $? -ne 0 ] && exit -1
curl --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-host.sh"; [ $? -ne 0 ] && exit -1
curl --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-docker.sh"; [ $? -ne 0 ] && exit -1
curl --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-host.csv"; [ $? -ne 0 ] && exit -1
chmod 750 $currDir/deploy-*.sh

# Choose deployment mode.
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

# Choose runtime mode.
while true
do
  read -t 300 -p """Please choose apps services runtime mode:
  Notes:
    If you choose stand-alone mode, it will be deployed to the local host in the smallest mode;
    If you choose cluster mode, it will be deployed to multiple remote hosts as distributed microservices, 
    you need to edit 11 files to define the host list.
    please choose (standalone|cluster)? """ runtimeMode
  if [ "$runtimeMode" == "standalone" ]; then
    export runtimeMode="standalone"
    break
  elif [ "$runtimeMode" == "cluster" ]; then
    export runtimeMode="cluster"
    echo "Please edit \"$currDir/deploy-host.csv\", and then re-execute \".$currDir/deploy-boot.sh\" again"
    exit 0
  else
    continue
  fi
done

# Call deployer.
if [ "$deployMode" == "host" ]; then
  bash $currDir/deploy-host.sh
elif [ "$deployMode" == "docker" ]; then
  bash $currDir/deploy-docker.sh
else
  echo "Unknown deploy mode of \"$deployMode\" !"
fi

#cd $currDir && \rm -rf $(ls deploy-*.sh 2>/dev/null|grep -v $0) # Cleanup scripts.
exit 0
