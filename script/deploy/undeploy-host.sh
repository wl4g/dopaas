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
. ${currDir}/deploy-common.sh
[ -n "$(command -v clear)" ] && clear # e.g centos8+ not clear

log ""
log "「 Welcome to XCloud DevOps Uninstaller(Host) 」"
log ""
log " Wiki: https://github.com/wl4g/xcloud-devops/blob/master/README.md"
log " Wiki(CN): https://gitee.com/wl4g/xcloud-devops/blob/master/README_CN.md"
log " Authors: <Wanglsir@gmail.com, 983708408@qq.com>"
log " Version: 2.0.0"
log " Time: $(date -d today +'%Y-%m-%d %H:%M:%S')"
log " Installation logs writing: $logFile"
log " -----------------------------------------------------------------------"
log ""

# Removing tmp apache maven.
function removeTmpApacheMaven() {
  local tmpApacheMavenPath="$apacheMvnInstallDir/apache-maven*"
  if [ -d $tmpApacheMavenPath ]; then
    log "Removing directory $tmpApacheMavenPath"
    secDeleteLocal "$tmpApacheMavenPath"
  fi
}

# Removing all apps resources.
function removeAllAppsResources() {
  local deployBuildTargetsSize=0
  if [ "$runtimeMode" == "standalone" ]; then
    deployBuildTargets=("${deployStandaloneBuildTargets[@]}") # Copy build targets array
  elif [ "$runtimeMode" == "cluster" ]; then # The 'cluster' mode is deploy to the remote hosts
    deployBuildTargets=("${deployClusterBuildTargets[@]}") # Copy build targets array
  else
    logErr "Invalid config runtimeMode: $runtimeMode"; exit -1
  fi
  # Add other apps resources.
  deployBuildTargets[${#deployBuildTargets[@]}]="$deployEurekaBuildTarget"
  # Do undeploy apps.
  deployBuildTargetsSize=${#deployBuildTargets[@]}
  if [ $deployBuildTargetsSize -gt 0 ]; then
    for ((i=0;i<${#deployBuildTargets[@]};i++)) do
      local buildTargetDir=${deployBuildTargets[i]}
      local buildFileName=$(ls -a "$buildTargetDir"|grep -E "*-${buildPkgVersion}-bin.tar|*-${buildPkgVersion}-bin.jar")
      if [ -z "$buildFileName" ]; then
         logErr "Failed to read build assets from target direct: $buildTargetDir"; exit -1
      fi
      local appName=$(echo "$(basename $buildFileName)"|awk -F "-${buildPkgVersion}-bin.tar|-${buildPkgVersion}-bin.jar" '{print $1}')
      # Uninstall app all nodes.
      local k=0
      for node in `cat $deployClusterNodesConfigPath`; do
        ((k+=1))
        [ $k == 1 ] && continue # Skip title row(first)
        # Extract node info & trim
        local host=$(echo $node|awk -F ',' '{print $1}'|sed -e 's/^\s*//' -e 's/\s*$//')
        local user=$(echo $node|awk -F ',' '{print $2}'|sed -e 's/^\s*//' -e 's/\s*$//')
        local passwd=$(echo $node|awk -F ',' '{print $3}'|sed -e 's/^\s*//' -e 's/\s*$//')
        if [[ "$host" == "" || "$user" == "" ]]; then
          logErr "[$appName/cluster] Invalid cluster node info, host/user is required! host: $host, user: $user, password: $passwd"; exit -1
        fi
        log "[$appName/$host] Removing resources on $host ..." 
        if [ "$asyncDeploy" == "true" ]; then
          removeAppFilesWithRemoteInstance "$appName" "$user" "$passwd" "$host" &
        else
          removeAppFilesWithRemoteInstance "$appName" "$user" "$passwd" "$host"
        fi
      done
    done
    [ "$asyncDeploy" == "true" ] && wait
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
  # First stopping all running services.
  log "[$appName/$host] Checking if ${appName} has stopped ..."
  doRemoteCmd "$user" "$passwd" "$host" "[ -f \"$appServiceFile\" ] && $appServiceFile stop" "false"
  # Remove installed all files.
  log "[$appName/$host] Removing directory $appHomeParent"
  doRemoteCmd "$user" "$passwd" "$host" "[ -d \"$appHomeParent\" ] && rm -rf $appHomeParent" "false"
  log "[$appName/$host] Removing directory $appDataBaseDir"
  doRemoteCmd "$user" "$passwd" "$host" "[ -d \"$appDataBaseDir\" ] && rm -rf $appDataBaseDir" "false"
  log "[$appName/$host] Removing directory $appLogDir"
  doRemoteCmd "$user" "$passwd" "$host" "[ -d \"$appLogDir\" ] && rm -rf $appLogDir" "false"
  log "[$appName/$host] Removing file $appServiceFile"
  doRemoteCmd "$user" "$passwd" "$host" "[ -f \"$appServiceFile\" ] && rm -rf $appServiceFile" "false"
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
[ "$asyncDeploy" == "true" ] && log "Using asynchronous deployment, you can usage: export asyncDeploy=\"false\" to set it."
beginTime=`date +%s`
removeTmpApacheMaven
removeAllAppsResources
deployStatus=$([ $? -eq 0 ] && echo "SUCCESS" || echo "FAILURE")
costTime=$[$(echo `date +%s`)-$beginTime]
log "-------------------------------------------------------------------"
log "UNINSTALL $deployStatus"
log "-------------------------------------------------------------------"
log "Total time: ${costTime} sec (Wall Clock)"
log "Finished at: $(date -d today +'%Y-%m-%d %H:%M:%S')"
log "Installing details logs see: $logFile"
log "-------------------------------------------------------------------"
