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
# Basic deploy environment variables.
[ -z "$workspaceDir" ] && export workspaceDir="${HOME}/.deploy-workspace" && mkdir -p $workspaceDir
[ -z "$deployDebug" ] && export deployDebug="false"
[ -z "$scriptsBaseUrl" ] && export scriptsBaseUrl="https://raw.githubusercontent.com/wl4g/xcloud-dopaas/master/script/deploy"
[ -z "$scriptsBaseUrlBackup1" ] && export scriptsBaseUrlBackup1="https://gitee.com/wl4g/xcloud-dopaas/raw/master/script/deploy"

# Detecting network environment.
function detectingNetwork() {
  echo "Detecting networking to fetch best resources allocating  ..."
  export isChinaLANNetwork="$(cat $workspaceDir/.isChinaLANNetwork 2>/dev/null)" # Load last configuration first.
  if [ -z "$isChinaLANNetwork" ]; then # Primary checker url1
    #ipArea=$(curl --connect-timeout 10 -m 20 -sSL "http://ip.taobao.com/outGetIpInfo?ip=113.109.55.66&accessKey=alibaba-inc" 2>/dev/null)
    ipArea=$(curl --connect-timeout 10 -m 20 -sSL "http://ip.taobao.com/outGetIpInfo?ip=myip&accessKey=alibaba-inc" 2>/dev/null)
    export isChinaLANNetwork=$([[ "$ipArea" =~ "中国" || "$ipArea" =~ "朝鲜" ]] && echo Y || echo "")
  fi
  if [ -z "$isChinaLANNetwork" ]; then # Fallback checker url2
    echo "Try checking the network again with http://cip.cc ..."
    ipArea=$(curl --connect-timeout 10 -m 20 -sSL "http://cip.cc" 2>/dev/null)
    export isChinaLANNetwork=$([[ "$ipArea" =~ "中国" || "$ipArea" =~ "朝鲜" ]] && echo Y || echo "")
  fi
  if [ -z "$isChinaLANNetwork" ]; then # Fallback checker url3
    echo "Try checking the network again with http://ipinfo.io ..."
    ipArea=$(curl --connect-timeout 10 -m 20 -sSL "http://ipinfo.io" 2>/dev/null)
    export isChinaLANNetwork=$([[ "$ipArea" =~ "\"country\": \"CN\"" ]] && echo Y || echo "")
  fi
  if [ -z "$isChinaLANNetwork" ]; then # Fallback checker url4
    echo "Try checking the network again with https://api.myip.com ..."
    ipArea=$(curl --connect-timeout 10 -m 20 -sSL "https://api.myip.com" 2>/dev/null)
    export isChinaLANNetwork=$([[ "$ipArea" =~ "China" ]] && echo Y || echo "")
  fi
  [ "$isChinaLANNetwork" != "Y" ] && export isChinaLANNetwork="N"
  echo "$isChinaLANNetwork" > "$workspaceDir/.isChinaLANNetwork"
  # Choose best fast-resources intelligently.
  if [ "$isChinaLANNetwork" == "Y" ]; then
    export scriptsBaseUrl="$scriptsBaseUrlBackup1"
  fi
}

# Download deployer dependencies scripts.
function downloadDeployerDependencies() {
  if [ "$deployDebug" == "false" ]; then # Debug mode does not need to download depend scripts.
    cd $currDir
    #\rm -rf $(ls deploy-*.sh 2>/dev/null|grep -v $0) # Cleanup scripts.
    echo "Downloading deployer scripts dependencies on '$scriptsBaseUrl' ..."
    curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-i18n-zh_CN.sh"; [ $? -ne 0 ] && exit -1
    curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-i18n-en_US.sh"; [ $? -ne 0 ] && exit -1
    curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-env.sh"; [ $? -ne 0 ] && exit -1
    curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-base.sh"; [ $? -ne 0 ] && exit -1
    curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-logging.sh"; [ $? -ne 0 ] && exit -1
    curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-common.sh"; [ $? -ne 0 ] && exit -1
    curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-host.sh"; [ $? -ne 0 ] && exit -1
    curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-host.csv.tpl"; [ $? -ne 0 ] && exit -1
    curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/deploy-docker.sh"; [ $? -ne 0 ] && exit -1
    curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/install-nginx.sh"; [ $? -ne 0 ] && exit -1
    curl -sLk --connect-timeout 10 -m 20 -O "$scriptsBaseUrl/undeploy-host.sh"; [ $? -ne 0 ] && exit -1
    chmod 750 $currDir/*-*.sh
  fi
}

# Check package local repo. (if necessary)
function checkPkgLocalRepo() {
  local osType=$(getOsTypeAndCheck)
  local notFoundPkgTip=""
  # jdk
  if [ ! -f "$(echo $localJdk8DownloadUrl|sed 's/file://g')" ]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg local pkg '$localJdk8DownloadUrl', \n  please manual download from: 'https://download.java.net/openjdk/jdk8u41/ri/openjdk-8u41-b04-linux-x64-14_jan_2020.tar.gz'"
  fi
  # sshpass
  if [[ "$osType" == "centos6_x64" &&  ! -f "$(echo $localSshpassForCentos6x64|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localSshpassForCentos6x64', \n  please manual download from: '$sshpassForCentos6x64'"
  fi
  if [[ "$osType" == "centos7_x64" &&  ! -f "$(echo $localSshpassForCentos7x64|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localSshpassForCentos7x64', \n  please manual download from: '$sshpassForCentos7x64'"
  fi
  if [[ "$osType" == "centos8_x64" &&  ! -f "$(echo $localSshpassForCentos8x64|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localSshpassForCentos8x64', \n  please manual download from: '$sshpassForCentos8x64'"
  fi
  if [[ "$osType" == "ubuntu_x64" &&  ! -f "$(echo $localSshpassForUbuntu20x64|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localSshpassForUbuntu20x64', \n  please manual download from: '$sshpassForUbuntu20x64'"
  fi
  # git
  if [[ "$osType" == "centos6_x64" &&  ! -f "$(echo $localGitDownloadUrlForCentos6x64|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localGitDownloadUrlForCentos6x64', \n  please manual download from: '$gitDownloadUrlForCentos6x64'"
  fi
  if [[ "$osType" == "centos7_x64" &&  ! -f "$(echo $localGitDownloadUrlForCentos7x64|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localGitDownloadUrlForCentos7x64', \n  please manual download from: '$gitDownloadUrlForCentos7x64'"
  fi
  if [[ "$osType" == "centos8_x64" &&  ! -f "$(echo $localGitDownloadUrlForCentos8x64|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localGitDownloadUrlForCentos8x64', \n  please manual download from: '$gitDownloadUrlForCentos8x64'"
  fi
  if [[ "$osType" == "ubuntu_x64" &&  ! -f "$(echo $localGitDownloadUrlForUbuntu20x64|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localGitDownloadUrlForUbuntu20x64', \n  please manual download from: '$gitDownloadUrlForUbuntu20x64'"
  fi
  # maven
  if [[ ! -f "$(echo $localApacheMvnDownloadTarUrl|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localApacheMvnDownloadTarUrl', \n  please manual download from: '$secondaryApacheMvnDownloadTarUrl'"
  fi
  # zookeeper
  if [[ ! -f "$(echo $localZkDownloadUrl|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localZkDownloadUrl', \n  please manual download from: '$zkDownloadUrl'"
  fi
  # nginx
  if [[ "$osType" == "centos6_x64" &&  ! -f "$(echo $localNgxDownloadUrlForCentos6x64|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localNgxDownloadUrlForCentos6x64', \n  please manual download from: '$nginxDownloadUrlForCentos6x64'"
  fi
  if [[ "$osType" == "centos7_x64" &&  ! -f "$(echo $localNgxDownloadUrlForCentos7x64|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localNgxDownloadUrlForCentos7x64', \n  please manual download from: '$nginxDownloadUrlForCentos7x64'"
  fi
  if [[ "$osType" == "centos8_x64" &&  ! -f "$(echo $localNgxDownloadUrlForCentos8x64|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localNgxDownloadUrlForCentos8x64', \n  please manual download from: '$nginxDownloadUrlForCentos8x64'"
  fi
  if [[ "$osType" == "ubuntu_x64" &&  ! -f "$(echo $localNgxDownloadUrlForUbuntu20x64|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localNgxDownloadUrlForUbuntu20x64', \n  please manual download from: '$nginxDownloadUrlForUbuntu20x64'"
  fi
  # nodejs
  if [[ ! -f "$(echo $localNodejsDownloadTarUrl|sed 's/file://g')" ]]; then
    notFoundPkgTip="${notFoundPkgTip}\n\nNot found local pkg '$localNodejsDownloadTarUrl', \n  please manual download from: '$nodejsDownloadTarUrl'"
  fi
  echo -e "$notFoundPkgTip"
}

# Execution of environment configuration.
function execWithConfirmingEnvironment() {
  # Import common scripts(i18n).
  . $currDir/deploy-common.sh && loadi18n

  # Check supporting OS.
  if [ "$(getOsTypeAndCheck)" == "_" ]; then
    echo "Unsupported current OS, only CentOS 6/CentOS 7/CentOS 8/Ubuntu is supported for the time being!"; exit -1
  fi

  # Option1: Choose deploy network mode.
  echo ""
  while true
  do
    echo -e "$confirmDeployerNetworkModeEnvVarMsg"
    read -t 300 -p """
    $confirmDeployerNetworkModeEnvVarTip1Msg
    $confirmDeployerNetworkModeEnvVarTip2Msg 
    $confirmDeployerNetworkModeEnvVarTip3Msg """ confirm
    if [[ "$confirm" == "1" ]]; then
      deployNetworkMode="extranet"
      break
    elif [ "$confirm" == "2" ]; then
      deployNetworkMode="intranet"
      local result=$(checkPkgLocalRepo)
      if [ -n "$result" ]; then
        log "$confirmDeployerNetworkModeEnvVarTip4Msg"
        echo "$result"; exit -1
      else
        break
      fi
    elif [ "$confirm" == "" ]; then
      deployNetworkMode="extranet"
      break
    else
      continue
    fi
  done
  echo "$confirmDeployerNetworkModeEnvVarTip5Msg '$deployNetworkMode'"

  # Option2: Confim dependent services env configuration. (eg: jdbc/redis/...)
  echo ""
  while true
  do
    echo -e """$confirmServicesRuntimeConfigEnvVarMsg
  $globalExportedEnvStr"""
    read -t 300 -p """
    $confirmServicesRuntimeConfigEnvVarTip1Msg
    $confirmServicesRuntimeConfigEnvVarTip2Msg
    $confirmServicesRuntimeConfigEnvVarTip3Msg """ confirm
    if [[ "$confirm" == "n" ]]; then
      log "$confirmServicesRuntimeConfigEnvVarTip4Msg '$currDir/deploy-boot.sh'"; exit -1
    elif [ "$confirm" == "y" ]; then
      break
    else
      continue
    fi
  done

  # Option3: Choose deployment mode.
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
      log "$choosingDeployModeTip4Msg"
      exit -1
    else
      continue
    fi
  done
  log "$choosingDeployModeTip5Msg '$deployMode'"

  # Option4: Choose runtime mode.
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
        log "Please create '$currDir/deploy-host.csv' from '$currDir/deploy-host.csv.tpl', and then reexecute '.$currDir/deploy-boot.sh' again !"
        exit -1
      else
        break
      fi
    else
      continue
    fi
  done
  log "$choosingRuntimeModeTip4Msg '$runtimeMode'"

  # Call deployer.
  if [ "$deployMode" == "host" ]; then
    bash $currDir/deploy-host.sh
  elif [ "$deployMode" == "docker" ]; then
    bash $currDir/deploy-docker.sh
  else
    log "Unknown deploy mode of '$deployMode' !"; exit -1
  fi
}

# ----- Main call. -----
function main() {
  [ -n "$(command -v clear)" ] && clear # e.g centos8+ not clear
  echo ""
  echo "「 Welcome to XCloud DoPaaS Deployer (Boot) 」"
  echo ""
  echo " Wiki: https://github.com/wl4g/xcloud-dopaas/blob/master/README.md"
  echo " Wiki(CN): https://gitee.com/wl4g/xcloud-dopaas/blob/master/README_CN.md"
  echo " Authors: <Wanglsir@gmail.com, 983708408@qq.com>"
  echo " Version: 2.0.0"
  echo " Time: $(date '+%Y-%m-%d %H:%M:%S')"
  detectingNetwork
  downloadDeployerDependencies
  execWithConfirmingEnvironment
  #cd $currDir && \rm -rf $(ls deploy-*.sh 2>/dev/null|grep -v "deploy-boot.sh"|grep -v "undeploy-host.sh") # Cleanup scripts.
  exit 0
}
main
