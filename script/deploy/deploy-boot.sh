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
export currDir=$(cd "`dirname $0`"/ ; pwd)
export scriptsBaseUrl="https://raw.githubusercontent.com/wl4g/xcloud-devops/master/script/deploy"
export scriptsBaseUrlBackup1="https://gitee.com/wl4g/xcloud-devops/raw/master/script/deploy"
export gitBaseUrl="https://github.com/wl4g"
export gitBaseUrlBackup1="https://gitee.com/wl4g"
# Runtime dependency external services configuration.
[ -z "$runtimeMysqlUrl" ] && export runtimeMysqlUrl="jdbc:mysql://localhost:3306/devops?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8"
[ -z "$runtimeMysqlUser" ] && export runtimeMysqlUser="root"
[ -z "$runtimeMysqlPassword" ] && export runtimeMysqlPassword="123456"
[ -z "$runtimeRedisNodes" ] && export runtimeRedisNodes="localhost:6379"
[ -z "$runtimeRedisPassword" ] && export runtimeRedisPassword="123456"

# Check runtime dependency external services. (e.g: mysql/redis/...)
echo ""
while true
do
  read -t 300 -p """Option1: Do you use the following dependent middleware configuration:
    export runtimeMysqlUrl=\"$runtimeMysqlUrl\"
    export runtimeMysqlUser=\"$runtimeMysqlUser\"
    export runtimeMysqlPassword=\"$runtimeMysqlPassword\"
    export runtimeRedisNodes=\"$runtimeRedisNodes\"
    export runtimeRedisPassword=\"$runtimeRedisPassword\"
  [y] Confirm to use the above configuration;
  [n] Exit and then customize the reconfiguration;
  please confirm to (y|n)? """ confirm
  if [[ "$confirm" == "n" ]]; then
    echo "Please the re-export environment variables, re-execute \"$currDir/deploy-boot.sh\""
    exit -1;
  elif [ "$confirm" == "y" ]; then
    break
  else
    continue
  fi
done

# Detect the host network and choose the fast resources intelligently.
echo "Analyzing network to best packages resources are automatically allocate ..."
ipArea=$(curl --connect-timeout 10 -m 20 -sSL cip.cc 2>/dev/null)
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
curl --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-host.csv.tpl"; [ $? -ne 0 ] && exit -1
chmod 750 $currDir/deploy-*.sh

# Choose deployment mode.
if [ "$deployMode" == "" ]; then
  echo ""
  while true
  do
    read -t 300 -p """Option2: Choosing deployment mode:
    [1] If you choose host deploy mode, next the app services is deployed to the remote host;
    [2] If you choose docker deploy mode, next the app services is deployed to the remote docker;
    please choose to (1|2|default:1)? """ depMode
    if [[ "$depMode" == "" || "$depMode" == "1" ]]; then
      export deployMode="host"
      break
    elif [ "$depMode" == "2" ]; then
      export deployMode="docker"
      echo "Docker deployment is not supported yet, please look forward to it! Welcome to join us, contact: <wanglsir@gmail.com, 983708408@qq.com>"
      exit -1
    else
      continue
    fi
  done
else
  echo "Option2: Choosed deployment mode use \"$deployMode\""
fi

# Choose runtime mode.
if [ "$runtimeMode" == "" ]; then
  echo ""
  while true
  do
    read -t 300 -p """Option3: Choosing apps services runtime mode:
    [1] If you choose standalone runtime mode, it will be deployed to the local host in the smallest mode;
    [2] If you choose cluster runtime mode, it will be deployed to multiple remote hosts as distributed
    microservices, you need to create \"$currDir/deploy-host.csv\" to define the hosts list.
    please choose to (1|2|default:1)? """ rtMode
    if [[ "$rtMode" == "" || "$rtMode" == "1" ]]; then
      export runtimeMode="standalone"
      break
    elif [ "$rtMode" == "2" ]; then
      export runtimeMode="cluster"
      if [ ! -f "$currDir/deploy-host.csv" ]; then
        echo "Please create \"$currDir/deploy-host.csv\" from \"$currDir/deploy-host.csv.tpl\", and then re-execute \".$currDir/deploy-boot.sh\" again !"
        exit 0
      else
        break
      fi
    else
      continue
    fi
  done
else
  echo "Option3: Choosed apps services runtime mode use \"$runtimeMode\""
fi

# Call deployer.
if [ "$deployMode" == "host" ]; then
  bash $currDir/deploy-host.sh
elif [ "$deployMode" == "docker" ]; then
  bash $currDir/deploy-docker.sh
else
  echo "Unknown deploy mode of \"$deployMode\" !"
fi

cd $currDir && \rm -rf $(ls deploy-*.sh 2>/dev/null|grep -v $0) # Cleanup scripts.
exit 0
