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
[ -n "$(command -v clear)" ] && clear # e.g centos8+ not clear

# Global definition.
export currDir=$(cd "`dirname $0`"/ ; pwd)
# Basic deploy environment variables.
[ -z "$workspaceDir" ] && export workspaceDir="/tmp/.deploy-workspace" && mkdir -p $workspaceDir
[ -z "$deployDebug" ] && export deployDebug="false"
[ -z "$scriptsBaseUrl" ] && export scriptsBaseUrl="https://raw.githubusercontent.com/wl4g/xcloud-dopaas/master/deploy"
[ -z "$scriptsBaseUrlBackup1" ] && export scriptsBaseUrlBackup1="https://gitee.com/wl4g/xcloud-dopaas/raw/master/deploy"
[ -z "$gitBaseUrl" ] && export gitBaseUrl="https://github.com/wl4g"
[ -z "$gitBaseUrlBackup1" ] && export gitBaseUrlBackup1="https://gitee.com/wl4g"
# Deploy services runtime depend environment variables.
[ -z "$runtimeMysqlUrl" ] && export runtimeMysqlUrl="jdbc:mysql://localhost:3306/dopaas?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true"
[ -z "$runtimeMysqlUser" ] && export runtimeMysqlUser="root"
[ -z "$runtimeMysqlPassword" ] && export runtimeMysqlPassword="123456"
[ -z "$runtimeRedisNodes" ] && export runtimeRedisNodes="localhost:6379"
[ -z "$runtimeRedisPassword" ] && export runtimeRedisPassword="123456"
[ -z "$runtimeAppSpringProfilesActive" ] && export runtimeAppSpringProfilesActive="pro"

# Checking the host networking.
echo "Checking network to best resources and automatically allocating  ..."
export isNetworkInGfwWall="$(cat $workspaceDir/isNetworkInGfwWall 2>/dev/null)" # Load last configuration first.
if [ -z "$isNetworkInGfwWall" ]; then # Checking url1
  #ipArea=$(curl --connect-timeout 10 -m 20 -sSL "http://ip.taobao.com/outGetIpInfo?ip=113.109.55.66&accessKey=alibaba-inc" 2>/dev/null)
  ipArea=$(curl --connect-timeout 10 -m 20 -sSL "http://ip.taobao.com/outGetIpInfo?ip=myip&accessKey=alibaba-inc" 2>/dev/null)
  export isNetworkInGfwWall=$([[ "$ipArea" =~ "中国" || "$ipArea" =~ "朝鲜" ]] && echo Y || echo "")
fi
if [ -z "$isNetworkInGfwWall" ]; then # Checking url2
  echo "Try checking the network again with http://cip.cc ..."
  ipArea=$(curl --connect-timeout 10 -m 20 -sSL "http://cip.cc" 2>/dev/null)
  export isNetworkInGfwWall=$([[ "$ipArea" =~ "中国" || "$ipArea" =~ "朝鲜" ]] && echo Y || echo "")
fi
if [ -z "$isNetworkInGfwWall" ]; then # Checking url3
  echo "Try checking the network again with http://ipinfo.io ..."
  ipArea=$(curl --connect-timeout 10 -m 20 -sSL "http://ipinfo.io" 2>/dev/null)
  export isNetworkInGfwWall=$([[ "$ipArea" =~ "\"country\": \"CN\"" ]] && echo Y || echo "")
fi
if [ -z "$isNetworkInGfwWall" ]; then # Checking url4
  echo "Try checking the network again with https://api.myip.com ..."
  ipArea=$(curl --connect-timeout 10 -m 20 -sSL "https://api.myip.com" 2>/dev/null)
  export isNetworkInGfwWall=$([[ "$ipArea" =~ "China" ]] && echo Y || echo "")
fi
[ "$isNetworkInGfwWall" != "Y" ] && export isNetworkInGfwWall="N"
echo "$isNetworkInGfwWall" > "$workspaceDir/isNetworkInGfwWall"
# Choose best fast-resources intelligently.
if [ "$isNetworkInGfwWall" == "Y" ]; then
  export gitBaseUri="$gitBaseUrlBackup1" # for speed-up, fuck gfw!
  export scriptsBaseUrl="$scriptsBaseUrlBackup1"
fi

# Download deploy dependencies scripts.
if [ "$deployDebug" == "false" ]; then # Debug mode does not need to download depend scripts.
  cd $currDir
  #\rm -rf $(ls deploy-*.sh 2>/dev/null|grep -v $0) # Cleanup scripts.
  echo "Downloading deploy scripts dependencies ..."
  curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-i18n-zh_CN.sh"; [ $? -ne 0 ] && exit -1
  curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-i18n-en_US.sh"; [ $? -ne 0 ] && exit -1
  curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-env.sh"; [ $? -ne 0 ] && exit -1
  curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-common.sh"; [ $? -ne 0 ] && exit -1
  curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-host.sh"; [ $? -ne 0 ] && exit -1
  curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-host.csv.tpl"; [ $? -ne 0 ] && exit -1
  curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-docker.sh"; [ $? -ne 0 ] && exit -1
  curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/install-nginx.sh"; [ $? -ne 0 ] && exit -1
  curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/undeploy-host.sh"; [ $? -ne 0 ] && exit -1
  chmod 750 $currDir/*-*.sh
fi

# Depend scripts.
. $currDir/deploy-common.sh
loadi18n

# Option1: Check runtime dependency external services. (e.g: mysql/redis/...)
echo ""
while true
do
  read -t 300 -p """$confirmServicesRuntimeConfigEnvVarMsg
    export runtimeMysqlUrl='$runtimeMysqlUrl'
    export runtimeMysqlUser='$runtimeMysqlUser'
    export runtimeMysqlPassword='$runtimeMysqlPassword'
    export runtimeRedisNodes='$runtimeRedisNodes'
    export runtimeRedisPassword='$runtimeRedisPassword'
    export runtimeAppSpringProfilesActive='$runtimeAppSpringProfilesActive'
  $confirmServicesRuntimeConfigEnvVarTip1Msg
  $confirmServicesRuntimeConfigEnvVarTip2Msg
  $confirmServicesRuntimeConfigEnvVarTip3Msg """ confirm
  if [[ "$confirm" == "n" ]]; then
    echo "$confirmServicesRuntimeConfigEnvVarTip4Msg '$currDir/deploy-boot.sh'"
    exit -1
  elif [ "$confirm" == "y" ]; then
    break
  else
    continue
  fi
done

# Option2: Choose deployment mode.
echo ""
while true
do
  read -t 300 -p """$choosingDeployModeMsg
  $choosingDeployModeTip1Msg
  $choosingDeployModeTip2Msg
  $choosingDeployModeTip3Msg """ depMode
  if [[ "$depMode" == "" || "$depMode" == "1" ]]; then
    export deployMode="host"
    break
  elif [ "$depMode" == "2" ]; then
    export deployMode="docker"
    echo "$choosingDeployModeTip4Msg"
    exit -1
  else
    continue
  fi
done
echo "$choosingDeployModeTip5Msg '$deployMode'"

# Option3: Choose runtime mode.
echo ""
while true
do
  read -t 300 -p """$choosingRuntimeModeMsg
  $choosingRuntimeModeTip1Msg
  $choosingRuntimeModeTip2Msg
  $choosingRuntimeModeTip3Msg """ rtMode
  if [[ "$rtMode" == "" || "$rtMode" == "1" ]]; then
    export runtimeMode="standalone"
    break
  elif [ "$rtMode" == "2" ]; then
    export runtimeMode="cluster"
    if [ ! -f "$currDir/deploy-host.csv" ]; then
      echo "Please create '$currDir/deploy-host.csv' from '$currDir/deploy-host.csv.tpl', and then re-execute '.$currDir/deploy-boot.sh' again !"
      exit -1
    else
      break
    fi
  else
    continue
  fi
done
echo "$choosingRuntimeModeTip4Msg '$runtimeMode'"

# Call deployer.
if [ "$deployMode" == "host" ]; then
  bash $currDir/deploy-host.sh
elif [ "$deployMode" == "docker" ]; then
  bash $currDir/deploy-docker.sh
else
  echo "Unknown deploy mode of '$deployMode' !"; exit -1
fi

#cd $currDir && \rm -rf $(ls deploy-*.sh 2>/dev/null|grep -v "deploy-boot.sh"|grep -v "undeploy-host.sh") # Cleanup scripts.
exit 0

