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

[ -z "$currDir" ] && export currDir=$(echo "$(cd "`dirname "$0"`"/; pwd)")
export logFile="$currDir/undeploy-host.log"; rm -rf $logFile
. ${currDir}/deploy-common.sh
[ -n "$(command -v clear)" ] && clear # e.g centos8+ not clear

log ""
log "「 Welcome to DoPaaS Uninstaller(Host) 」"
log ""
log " Wiki: https://github.com/wl4g/dopaas/blob/master/README.md"
log " Wiki(CN): https://gitee.com/wl4g/dopaas/blob/master/README_CN.md"
log " Authors: <Wanglsir@gmail.com, 983708408@qq.com>"
log " Version: 2.0.0"
log " Time: $(date '+%Y-%m-%d %H:%M:%S')"
log " Installation logs writing: $logFile"
log " -----------------------------------------------------------------------"
log ""

# Global variables.
globalAllNodes=()

# Init configuration.
function initConfiguration() {
  # 1. Load cluster nodes information.
  if [ "$runtimeMode" == "cluster" ]; then # Only cluster mode need a hosts csv file.
    if [ ! -f "$deployClusterNodesConfigPath" ]; then
      logErr "No found configuration file: '$currDir/deploy-host.csv', because you have selected the runtime mode is 'cluster',
please refer to the template file: '$currDir/deploy-host.csv.tpl'"
      exit -1
    fi
    # Init nodes info.
    local count=-1
    local index=-1
    for node in `cat $deployClusterNodesConfigPath`; do
      ((count+=1))
      if [[ $count == 0 || -n "$(echo $node|grep -E '^#')" ]]; then
        continue # Skip head or annotation rows.
      fi
      ((index+=1))
      # Extract node info & trim
      local host=$(echo $node|awk -F ',' '{print $1}'|sed -e 's/^\s*//' -e 's/\s*$//')
      local user=$(echo $node|awk -F ',' '{print $2}'|sed -e 's/^\s*//' -e 's/\s*$//')
      local passwd=$(echo $node|awk -F ',' '{print $3}'|sed -e 's/^\s*//' -e 's/\s*$//')
      if [[ -z "$host" || -z "$user" ]]; then
        logErr "[$appName/cluster] Failed to init, invalid cluster node info, host/user is required! host: $host, user: $user, password: $passwd"; exit -1
      fi
      # Check deployer user of root group.
      local deployerUserGroups=$(doRemoteCmd "$user" "$passwd" "$host" "$(echo groups)" "true" "true")
      if [[ ! "$deployerUserGroups" =~ "root" ]]; then
        logErr "Host=$host, User=$user, Must use the remote host user belonging to the root groups to perform the deployment !"; exit -1
      fi
      # Storage deployer all nodes. 
      globalAllNodes[index]="${host}ξ${user}ξ${passwd}"
    done
    # Check nodes must > 0
    if [ ${#globalAllNodes[@]} -le 0 ]; then
      logErr "Please reconfigure '$currDir/deploy-host.csv', deploy at least one cluster node !"
    fi
  else
    # Save default local deploy node. 
    globalAllNodes[index]="localhostξrootξ"
  fi
}

# Removing infra softwares.
function removeInfraSoftwares() {
  # Remove workspace directory(sshpass).
  if [ -d $workspaceDir ]; then
    log "Removing directory $workspaceDir"
    secDeleteLocal "$workspaceDir"
  fi
  # Remove git.
  if [ -d $gitInstallDir ]; then
    log "Removing directory $gitInstallDir"
    secDeleteLocal "$gitInstallDir"
  fi
  # Remove zookeeper.
  removeZookeeperAll
  # Remove nginx.
  removeNginxAll
}

function removeNginxAll() {
  local node=${globalAllNodes[0]} # First node deploy the nginx by default.
  local host=$(echo $node|awk -F 'ξ' '{print $1}')
  local user=$(echo $node|awk -F 'ξ' '{print $2}')
  local passwd=$(echo $node|awk -F 'ξ' '{print $3}')
  local appName="$gitDoPaaSViewProjectName"
  local appInstallDir="${deployFrontendAppBaseDir}/${appName}-package"
  log "[nginx/$host] Removing nginx for /etc/nginx/conf.d/dopaas.conf ..."
  doRemoteCmd "$user" "$passwd" "$host" "[ -f /etc/nginx/conf.d/dopaas.conf ] && rm -rf /etc/nginx/conf.d/dopaas.conf" "false" "true"
  log "[nginx/$host] Removing $appInstallDir ..."
  doRemoteCmd "$user" "$passwd" "$host" "[ -d \"$appInstallDir\" ] && rm -rf $appInstallDir" "false" "true"
}

function removeZookeeperAll() {
  if [ "$runtimeMode" == "cluster" ]; then
    log "Removing zookeeper servers ..."
    if [ ${#globalAllNodes[@]} -lt 3 ]; then
      local node1=${globalAllNodes[0]}
      local host1=$(echo $node1|awk -F 'ξ' '{print $1}')
      local user1=$(echo $node1|awk -F 'ξ' '{print $2}')
      local passwd1=$(echo $node1|awk -F 'ξ' '{print $3}')
      log "[zookeeper/$host1] Removing zookeeper for peer1 ..."
      doRemoteCmd "$user1" "$passwd1" "$host1" "$zkHome/bin/zkServer.sh stop; rm -rf ${deployAppLogBaseDir}/zookeeper; rm -rf $zkHome" "false" "true"
    else
      # Node1:
      local node1=${globalAllNodes[0]}
      local host1=$(echo $node1|awk -F 'ξ' '{print $1}')
      local user1=$(echo $node1|awk -F 'ξ' '{print $2}')
      local passwd1=$(echo $node1|awk -F 'ξ' '{print $3}')
      log "[zookeeper/$host1] Removing zookeeper for peer1 ..."
      doRemoteCmd "$user1" "$passwd1" "$host1" "$zkHome/bin/zkServer.sh stop; rm -rf ${deployAppLogBaseDir}/zookeeper; rm -rf $zkHome" "false" "true"
      # Node2:
      local node2=${globalAllNodes[1]}
      local host2=$(echo $node2|awk -F 'ξ' '{print $1}')
      local user2=$(echo $node2|awk -F 'ξ' '{print $2}')
      local passwd2=$(echo $node2|awk -F 'ξ' '{print $3}')
      log "[zookeeper/$host2] Deploying zookeeper for peer2 ..."
      doRemoteCmd "$user2" "$passwd2" "$host2" "$zkHome/bin/zkServer.sh stop; rm -rf ${deployAppLogBaseDir}/zookeeper; rm -rf $zkHome" "false" "true"
      # Node3:
      local node3=${globalAllNodes[2]}
      local host3=$(echo $node3|awk -F 'ξ' '{print $1}')
      local user3=$(echo $node3|awk -F 'ξ' '{print $2}')
      local passwd3=$(echo $node3|awk -F 'ξ' '{print $3}')
      log "[zookeeper/$host3] Deploying zookeeper for peer3 ..."
      doRemoteCmd "$user3" "$passwd3" "$host3" "$zkHome/bin/zkServer.sh stop; rm -rf ${deployAppLogBaseDir}/zookeeper; rm -rf $zkHome" "false" "true"
    fi
  else # In standalone mode, Eureka does not need to be deployed.
    log "Skip remove zookeeper servers, because runtime mode is standalone."
  fi
}

# Removing all apps resources.
function removeAppsAll() {
  local deployBuildModulesSize=0
  if [ "$runtimeMode" == "standalone" ]; then
    local deployBuildModules=("${deployStandaloneBuildModules[@]}") # Copy build targets array
  elif [ "$runtimeMode" == "cluster" ]; then # The 'cluster' mode is deploy to the remote hosts
    local deployBuildModules=("${deployClusterBuildModules[@]}") # Copy build targets array
  else
    logErr "Invalid config runtime mode: $runtimeMode"; exit -1
  fi
  deployBuildModules[${#deployBuildModules[@]}]="$deployEurekaBuildModule"
  deployBuildModulesSize=${#deployBuildModules[@]}
  if [ $deployBuildModulesSize -gt 0 ]; then
    for ((i=0;i<${#deployBuildModules[@]};i++)) do
      local buildModule=${deployBuildModules[i]}
      local appName=$(echo "$buildModule"|awk -F ',' '{print $1}')
      #{
        # Uninstall app all nodes.
        local count=-1
        for node in `cat $deployClusterNodesConfigPath`; do
          ((count+=1))
          if [[ $count == 0 || -n "$(echo $node|grep -E '^#')" ]]; then
            continue # Skip head or annotation rows.
          fi
          # Extract node info & trim
          local host=$(echo $node|awk -F ',' '{print $1}'|sed -e 's/^\s*//' -e 's/\s*$//')
          local user=$(echo $node|awk -F ',' '{print $2}'|sed -e 's/^\s*//' -e 's/\s*$//')
          local passwd=$(echo $node|awk -F ',' '{print $3}'|sed -e 's/^\s*//' -e 's/\s*$//')
          if [[ "$host" == "" || "$user" == "" ]]; then
            logErr "[$appName/cluster] Invalid cluster node info, host/user is required! host: $host, user: $user, password: $passwd"; exit -1
          fi
          log "[$appName/$host] Removing resources on $host ..." 
          removeAppFilesWithRemoteInstance "$appName" "$user" "$passwd" "$host" &
        done
      #} &
    done
    wait
  fi
}

# Removing remote node app files.
function removeAppFilesWithRemoteInstance() {
  local appName=$1
  local user=$2
  local passwd=$3
  local host=$4
  local appHomeParent="${deployAppBaseDir}/${appName}-package"
  local appDataBaseDir="${deployAppDataBaseDir}/${appName}"
  local appLogDir="${deployAppLogBaseDir}/${appName}"
  local appServiceFile="/etc/init.d/${appName}.service"
  local appSystemdServiceFile="/lib/systemd/system/${appName}.service"
  # Stopping all running services.
  log "[$appName/$host] Checking if ${appName} has stopped ..."
  # Notes: for example, Eureka may deploy multiple instances on a single host (as shown below) spring.profiles.active When unloading,
  # Sets to "None" means no discrimination spring.profiles.active To ensure that multiple instance processes can be stopped.
  doRemoteCmd "$user" "$passwd" "$host" "[ -n \"$(command -v systemctl)\" ] && systemctl stop ${appName} || /etc/init.d/${appName}.service stop" "false"
  doRemoteCmd "$user" "$passwd" "$host" "export SPRING_PROFILES_ACTIVE='None'; $appServiceFile stop" "false"
  # Remove installed all files.
  log "[$appName/$host] Removing directory /tmp/hsperfdata_$appName/"
  doRemoteCmd "$user" "$passwd" "$host" "[ -d \"/tmp/hsperfdata_$appName/\" ] && \rm -rf /tmp/hsperfdata_$appName/" "false"
  log "[$appName/$host] Removing directory $appHomeParent"
  doRemoteCmd "$user" "$passwd" "$host" "[ -d \"$appHomeParent\" ] && \rm -rf $appHomeParent" "false"
  log "[$appName/$host] Removing directory $appDataBaseDir"
  doRemoteCmd "$user" "$passwd" "$host" "[ -d \"$appDataBaseDir\" ] && \rm -rf $appDataBaseDir" "false"
  log "[$appName/$host] Removing directory $appLogDir"
  doRemoteCmd "$user" "$passwd" "$host" "[ -d \"$appLogDir\" ] && \rm -rf $appLogDir" "false"
  log "[$appName/$host] Removing file $appServiceFile"
  doRemoteCmd "$user" "$passwd" "$host" "[ -f \"$appServiceFile\" ] && \rm -rf $appServiceFile" "false"
  log "[$appName/$host] Removing file $appSystemdServiceFile"
  doRemoteCmd "$user" "$passwd" "$host" "[ -f \"$appSystemdServiceFile\" ] && \rm -rf $appSystemdServiceFile" "false"
  log "[$appName/$host] Removing app user to $appName"
  doRemoteCmd "$user" "$passwd" "$host" "[ -n \"\$(cat /etc/passwd|grep '^$appName:')\" ] && userdel -rfRZ $appName" "false"
}

# ----- Main call. -----
while true
do
  read -t 300 -p """
【WARNING】 Are you sure you want to uninstall all instance nodes of all apps,
        remove the irrecoverable data files at the same time. please handle with caution !!!
        Do you want to continue to uninstall? (yes|no) """ confirm
  if [[ "$confirm" == "yes" ]]; then
    break
  elif [ "$confirm" == "no" ]; then
    echo "Uninstall task was cancelled !"
    exit 0
  else
    continue
  fi
done
beginTime=`date +%s`
initConfiguration
removeAppsAll
removeInfraSoftwares
deployStatus=$([ $? -eq 0 ] && echo "SUCCESS" || echo "FAILURE")
costTime=$[$(echo `date +%s`)-$beginTime]
log "-------------------------------------------------------------------"
log "UNINSTALL $deployStatus"
log "-------------------------------------------------------------------"
log "Total time: ${costTime} sec (Wall Clock)"
log "Finished at: $(date '+%Y-%m-%d %H:%M:%S')"
log "Installing details logs see: $logFile"
log "-------------------------------------------------------------------"
