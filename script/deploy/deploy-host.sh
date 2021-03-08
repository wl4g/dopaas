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

# Global variables.
globalAllNodes=()
globalAllNodesString=""
globalDeployStatsMsg="" # Deployed stats message.

# Init configuration.
function initConfig() {
  # 1. Load cluster nodes information.
  if [ "$runtimeMode" == "cluster" ]; then # Only cluster mode need a hosts csv file.
    if [ ! -f "$deployClusterNodesConfigPath" ]; then
      logErr "No found configuration file: '$currDir/deploy-host.csv', because you have selected the runtime mode is 'cluster',
please refer to the template file: '$currDir/deploy-host.csv.tpl'"
      exit -1
    fi
    # Init nodes info.
    local k=0
    local index=0
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
      globalAllNodes[index]="${host}ξ${user}ξ${passwd}"
      if [ "$globalAllHostsString" == "" ]; then
        globalAllHostsString="$host"
      else
        globalAllHostsString="${globalAllHostsString}, $host"
      fi
    done
    # Check nodes must > 0
    if [ ${#globalAllNodes[@]} -le 0 ]; then
      logErr "Please reconfigure '$currDir/deploy-host.csv', deploy at least one cluster node !"
    fi
  fi
  # 2. Maven local repo user.
  local localRepoPathPrefix="$(echo $apacheMvnLocalRepoDir|cut -c 1-5)"
  if [ "$localRepoPathPrefix" == "/root" ]; then
    export apacheMvnLocalRepoDirOfUser="root"
  elif [[ "$localRepoPathPrefix" == "/home" || "$localRepoPathPrefix" == "/Users" ]]; then # fix: MacOS(/Users/)
    export apacheMvnLocalRepoDirOfUser="$(echo $apacheMvnLocalRepoDir|awk -F '/' '{print $3}')"
  else
    logErr "Invalid maven local repository path. for example: \$USER/.m2/repository"; exit -1
  fi
}

# Pull and compile.
function pullAndCompile() {
  local projectName=$1 # e.g xcloud-devops
  local cloneUrl=$2
  local projectDir="$currDir/$projectName"
  if [ ! -d "$projectDir" ]; then
    log "Git clone $projectName from $cloneUrl ..."
    cd $currDir && git clone $cloneUrl 2>&1 | tee -a $logFile
    log "Compiling $projectName ..."
    cd $projectDir
    $cmdMvn -Dmaven.repo.local=$apacheMvnLocalRepoDir clean install -DskipTests -T 2C -U -P $buildPkgType 2>&1 | tee -a $logFile
    [ ${PIPESTATUS[0]} -ne 0 ] && exit -1 # or use 'set -o pipefail', see: http://www.huati365.com/answer/j6BxQYLqYVeWe4k
  else
    log "Git pull $projectName from $cloneUrl ..."
    # Check and update remote url.
    local oldRemoteUrl=$(cd $projectDir && git remote -v|grep fetch|awk '{print $2}';cd ..)
    if [ "$oldRemoteUrl" != "$cloneUrl" ]; then
      log "Updating origin remote url to \"$cloneUrl\" ..."
      cd $projectDir && git remote set-url origin $cloneUrl
    fi
    # Check already updated?
    local pullResult=$(cd $projectDir && git pull 2>&1 | tee -a $logFile)
    if [[ "$pullResult" != "Already up-to-date."* || "$rebuildOfGitPullAlreadyUpToDate" == "true" ]]; then
      log "Compiling $projectName ..."
      cd $projectDir
      $cmdMvn -Dmaven.repo.local=$apacheMvnLocalRepoDir clean install -DskipTests -T 2C -U -P $buildPkgType 2>&1 | tee -a $logFile
      [ ${PIPESTATUS[0]} -ne 0 ] && exit -1 # or use 'set -o pipefail', see: http://www.huati365.com/answer/j6BxQYLqYVeWe4k
    else
      log "Skip build of $projectName(latest)"
      # Tips rebuild usage.
      if [ "$rebuildOfGitPullAlreadyUpToDate" != "true" ]; then
        log " [Tips]: If you still want to recompile, you can usage: export rebuildOfGitPullAlreadyUpToDate=\"true\" to set it."
      fi
    fi
  fi
  # If the mvn command is currently executed as root, but the local warehouse directory owner is another user, 
  # the owner should be reset (because there may be a newly downloaded dependent library)
  chown -R $apacheMvnLocalRepoDirOfUser:$apacheMvnLocalRepoDirOfUser $projectDir
  chown -R $apacheMvnLocalRepoDirOfUser:$apacheMvnLocalRepoDirOfUser $apacheMvnLocalRepoDir
}

# Deploy app to local. (standalone)
function deployToLocalOfStandalone() {
  local buildFilePath=$1
  local buildFileName=$2
  local cmdRestart=$3
  local appName=$4
  local springProfilesActive=$5
  # Check args.
  if [[ "$buildFilePath" == "" || "$buildFileName" == "" || "$cmdRestart" == "" || "$appName" == "" ]]; then
    logErr "Failed to deploy to local, because buildFilePath/buildFileName/cmdRestart/appName is required!
buildFilePath=$buildFilePath, buildFileName=$buildFileName, cmdRestart=$cmdRestart, appName=$appName"
    exit -1
  fi
  local appInstallDir=${deployAppBaseDir}/${appName}-package && mkdir -p $appInstallDir

  # Add deployed xcloud-devops primary service host.
  globalDeployStatsMsg="${globalDeployStatsMsg}"$(echo -n " localhost")

  log "[$appName/standalone] Cleanup older install files: $appInstallDir/* ..."
  secDeleteLocal "$appInstallDir/*"
  if [[ "$buildPkgType" == "mvnAssTar" ]]; then
    log "[$appName/standalone/local] Uncompress $buildFilePath to $appInstallDir ..."
    tar -xf $buildFilePath -C $appInstallDir/
  elif [[ "$buildPkgType" == "springExecJar" ]]; then
    log "[$appName/standalone/local] Copying $buildFilePath to $appInstallDir/ ..."
    unalias -a cp
    cp -Rf ${appName}-${buildPkgVersion}-bin.jar $appInstallDir/
  else
    logErr "[$appName/standalone/local] Invalid config buildPkgType: $buildPkgType"; exit -1
  fi
  [ $? -ne 0 ] && exit -1
  # Check install services script?
  log "[$appName/standalone/local] Checking app services script installation ..."
  checkInstallServiceScript "$appName" "$USER" "$passwd" "localhost" "$springProfilesActive" "false"
  $cmdRestart
}

# Deploy app to all nodes. (cluster)
function deployToNodesOfCluster() {
  local buildFilePath=$1
  local buildFileName=$2
  local cmdRestart=$3
  local appName=$4
  local springProfilesActive=$5
  local nodeArr=$6
  # Check args.
  if [[ "$buildFilePath" == "" || "$buildFileName" == "" || "$cmdRestart" == "" || "$appName" == "" || "$springProfilesActive" == "" || "$nodeArr" == "" ]]; then
    logErr "Failed to deploy to nodes, because buildFilePath/buildFileName/cmdRestart/appName/nodeArr is required!
buildFilePath=$buildFilePath, buildFileName=$buildFileName, cmdRestart=$cmdRestart, appName=$appName, springProfilesActive=$springProfilesActive, nodeArr=$nodeArr"
    exit -1
  fi
  local appInstallDir="${deployAppBaseDir}/${appName}-package"
  for node in ${nodeArr[@]}; do # issue: https://blog.csdn.net/mdx20072419/article/details/103901329
    local host=$(echo $node|awk -F 'ξ' '{print $1}')
    local user=$(echo $node|awk -F 'ξ' '{print $2}')
    local passwd=$(echo $node|awk -F 'ξ' '{print $3}')
    if [[ "$host" == "" || "$user" == "" ]]; then
      logErr "[$appName/cluster] Invalid cluster node info, host/user is required! host: $host, user: $user, password: $passwd"; exit -1
    fi
    # Do deploy to instance.
    if [ "$asyncDeploy" == "true" ]; then
      doDeployToNodeOfCluster "$appName" "$appInstallDir" "$buildFilePath" "$host" "$user" "$passwd" "$springProfilesActive" &
    else
      doDeployToNodeOfCluster "$appName" "$appInstallDir" "$buildFilePath" "$host" "$user" "$passwd" "$springProfilesActive"
    fi
    [ $? -ne 0 ] && exit -1
  done
  [ "$asyncDeploy" == "true" ] && wait # Wait all instances async deploy complete.
  return 0
}

# Deploy to cluster remote instance.
function doDeployToNodeOfCluster() {
  local appName=$1
  local appInstallDir=$2
  local buildFilePath=$3
  local host=$4
  local user=$5
  local passwd=$6
  local springProfilesActive=$7
  # Deployement to remote.
  log "[$appName/cluster/$host] Cleanup older install files: '$appInstallDir/*' ..."
  [[ "$appInstallDir" != "" && "$appInstallDir" != "/" ]] && doRemoteCmd "$user" "$passwd" "$host" "rm -rf $appInstallDir/*" "true"
  doRemoteCmd "$user" "$passwd" "$host" "mkdir -p $appInstallDir" "false"
  log "[$appName/cluster/$host] Transfer '$buildFilePath' to remote '$appInstallDir' ..."
  doScp "$user" "$passwd" "$host" "$buildFilePath" "$appInstallDir/$buildFileName" "true"
  if [ "$buildPkgType" == "mvnAssTar" ]; then
    if [ -n "$(echo $buildFileName|grep .tar)" ]; then
      log "[$appName/cluster/$host] Uncompress \"$appInstallDir/$buildFileName\" to \"$appInstallDir/\" ..."
      doRemoteCmd "$user" "$passwd" "$host" "tar -xf $appInstallDir/$buildFileName -C $appInstallDir && rm -rf $appInstallDir/$buildFileName" "true"
    else
      log "Skip $buildFileName($appName) uncompress, because assets filename no '.tar' suffix."
    fi
  elif [ "$buildPkgType" == "springExecJar" ]; then
    log "" # Nothing
  else
    logErr "[$appName/cluster/$host] Invalid config buildPkgType: $buildPkgType"; exit -1
  fi
  # Check install services script?
  log "[$appName/cluster/$host] Checking app services script installation ..."
  checkInstallServiceScript "$appName" "$user" "$passwd" "$host" "$springProfilesActive" "false"
  [ $? -ne 0 ] && exit -1 # or use 'set -o pipefail', see: http://www.huati365.com/answer/j6BxQYLqYVeWe4k
  # Exec restart
  log "[$appName/cluster/$host] Restarting for $appName ..."
  doRemoteCmd "$user" "$passwd" "$host" "$cmdRestart" "true"
  log "[$appName/cluster/$host] Deployed $appName completed."
}

# Do deploy app.
function doDeployApp() {
  local buildModule=$1
  local springProfilesActive=$2 # Priority custom active.
  local nodeArr=$3
  # Gets build info.
  local appName=$(echo "$buildModule"|awk -F ',' '{print $1}')
  if [ -z "$appName" ]; then
    logErr "Failed to deploy, appName is required! all args: '$@'"; exit -1
  fi
  local buildTargetDir=$(echo "$buildModule"|awk -F ',' '{print $2}')
  if [ -z "$buildTargetDir" ]; then
    logErr "Failed to deploy, buildTargetDir is required! all args: '$@'"; exit -1
  fi
  if [ "$buildPkgType" == "mvnAssTar" ]; then
    local buildFileName=$(ls -a "$buildTargetDir"|grep -E "*-${buildPkgVersion}-bin.tar")
  elif [ "$buildPkgType" == "springExecJar" ]; then
    local buildFileName=$(ls -a "$buildTargetDir"|grep -E "*-${buildPkgVersion}-bin.jar")
  fi
  if [ -z "$buildFileName" ]; then
    logErr "Failed to deploy, buildFileName is required! all args: '$@'"; exit -1
  fi
  #local appName=$(echo "$(basename $buildFileName)"|awk -F "-${buildPkgVersion}-bin.tar|-${buildPkgVersion}-bin.jar" '{print $1}')
  local cmdRestart="/etc/init.d/${appName}.service restart"

  # Add deployed xcloud-devops primary services names.
  globalDeployStatsMsg="${globalDeployStatsMsg}"$(echo -n -e """
[${appName}]:
          Install Home: ${deployAppBaseDir}/${appName}-package/${appName}-${buildPkgVersion}-bin/
            Config Dir: ${deployAppBaseDir}/${appName}-package/${appName}-${buildPkgVersion}-bin/conf/
       Profiles Active: ${springProfilesActive}
              PID File: /mnt/disk1/${appName}/${appName}.pid
       Restart Command: /etc/init.d/$appName.service restart
             Logs File: /mnt/disk1/log/${appName}/${appName}_${springProfilesActive}.log
        Deployed Hosts:""")

  if [ "$runtimeMode" == "standalone" ]; then # The 'standalone' mode is only deployed to the local host
    log "[$appName/standalone] deploying to local ..."
    if [ "$asyncDeploy" == "true" ]; then
      deployToLocalOfStandalone "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName" "$springProfilesActive" &
    else
      deployToLocalOfStandalone "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName" "$springProfilesActive"
    fi
    [ $? -ne 0 ] && exit -1
    log "[$appName/standalone] Deployed to local completed !"
  elif [ "$runtimeMode" == "cluster" ]; then # The 'cluster' mode is deployed to the remote hosts
    log "[$appName/cluster] Deploying to cluster nodes ..."
    if [ "$asyncDeploy" == "true" ]; then
      deployToNodesOfCluster "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName" "$springProfilesActive" "${nodeArr[*]}" &
    else
      deployToNodesOfCluster "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName" "$springProfilesActive" "${nodeArr[*]}"
    fi
    log "[$appName/cluster] Deployed to cluster nodes completed !"

    # Add deployed xcloud-devops primary service host.
    globalDeployStatsMsg="${globalDeployStatsMsg} ${globalAllHostsString}"
    [ $? -ne 0 ] && exit -1
    log "[$appName/cluster] Deployed to remote all nodes !"
  fi
}

# Deploy and startup devops all apps.
function deployDevopsAppsAll() {
  local deployBuildModulesSize=0
  if [ "$runtimeMode" == "standalone" ]; then
    local deployBuildModules=("${deployStandaloneBuildModules[@]}") # Copy build targets array
  elif [ "$runtimeMode" == "cluster" ]; then # The 'cluster' mode is deploy to the remote hosts
    local deployBuildModules=("${deployClusterBuildModules[@]}") # Copy build targets array
  else
    logErr "Invalid config runtime mode: $runtimeMode"; exit -1
  fi
  deployBuildModulesSize=${#deployBuildModules[@]}
  if [ $deployBuildModulesSize -gt 0 ]; then
    for ((i=0;i<${#deployBuildModules[@]};i++)) do
      local buildModule=${deployBuildModules[i]}
      doDeployApp "$buildModule" "${runtimeAppSpringProfilesActive}" "${globalAllNodes[*]}"
    done
    [ "$asyncDeploy" == "true" ] && wait # Wait all apps async deploy complete.
  fi
  return 0
}

# Check and deploy dependency services. (e.g: eureka-server/redis/mysql/...)
function deployPreDependsServices() {
  deployEurekaServers
}

# Check and deploy eureka servers.
function deployEurekaServers() {
  if [ "$runtimeMode" == "cluster" ]; then
    if [ ${#globalAllNodes[@]} -lt 3 ]; then # Building pseudo cluster.
      local appName=$(echo "$deployEurekaBuildModule"|awk -F ',' '{print $1}')
      local cmdRestart="/etc/init.d/${appName}.service restart"
      local node1=${globalAllNodes[0]}
      local host1=$(echo $node1|awk -F 'ξ' '{print $1}')
      local user1=$(echo $node1|awk -F 'ξ' '{print $2}')
      local passwd1=$(echo $node1|awk -F 'ξ' '{print $3}')
      # Node1:
      log "[eureka/$host1] Deploy eureka by peer1 (Pseudo) ..."
      doDeployApp "$deployEurekaBuildModule" "ha,peer1" "$node1"
      # Due to the asynchronous call of the previous function, we have to wait for 
      # synchronous execution to ensure the integrity of the first build package.
      wait
      # Node2 and Node3: (only start new instance)
      log "[eureka/$host1] Deploy eureka by peer2 (Pseudo) ..."
      doRemoteCmd "$user1" "$passwd1" "$host1" "export SPRING_PROFILES_ACTIVE='ha,peer2' && $cmdRestart" "true" &
      log "[eureka/$host1] Deploy eureka by peer3 (Pseudo) ..."
      doRemoteCmd "$user1" "$passwd1" "$host1" "export SPRING_PROFILES_ACTIVE='ha,peer3' && $cmdRestart" "true" &
    else # Building a real cluster.
      # Node1:
      local node1=${globalAllNodes[0]}
      local host1=$(echo $node1|awk -F 'ξ' '{print $1}')
      log "[eureka/$host1] Deploy eureka by peer1 ..."
      doDeployApp "$deployEurekaBuildModule" "ha,peer1" "$node1"
      # Node2:
      local node2=${globalAllNodes[1]}
      local host2=$(echo $node2|awk -F 'ξ' '{print $1}')
      log "[eureka/$host2] Deploy eureka by peer2 ..."
      doDeployApp "$deployEurekaBuildModule" "ha,peer2" "$node2"
      # Node3:
      local node3=${globalAllNodes[2]}
      local host3=$(echo $node3|awk -F 'ξ' '{print $1}')
      log "[eureka/$host3] Deploy eureka by peer3 ..."
      doDeployApp "$deployEurekaBuildModule" "ha,peer3" "$node3"
    fi
  else # In standalone mode, Eureka does not need to be deployed.
    log "Skip eureka servers deploy, because runtime mode is standalone."
  fi
}

# ----- Main call. -----
function main() {
  [ -n "$(command -v clear)" ] && clear # e.g centos8+ not clear
  log ""
  log "「 Welcome to XCloud DevOps Deployer(Host) 」"
  log ""
  log " Wiki: https://github.com/wl4g/xcloud-devops/blob/master/README.md"
  log " Wiki(CN): https://gitee.com/wl4g/xcloud-devops/blob/master/README_CN.md"
  log " Authors: <Wanglsir@gmail.com, 983708408@qq.com>"
  log " Version: 2.0.0"
  log " Time: $(date -d today +'%Y-%m-%d %H:%M:%S')"
  log " Installation logs writing: $logFile"
  log " -------------------------------------------------------------------"
  log ""
  if [[ "$(echo groups)" == "root" ]]; then
    logErr "Please execute the scripts as a user with root privileges !" && exit -1
  fi
  [ "$asyncDeploy" == "true" ] && log "Using asynchronous deployment, you can usage: export asyncDeploy=\"false\" to set it."
  beginTime=`date +%s`
  initConfig
  checkInstallBasicSoftware
  pullAndCompile "xcloud-component" $gitXCloudComponentUrl
  pullAndCompile "xcloud-iam" $gitXCloudIamUrl
  pullAndCompile "xcloud-devops" $gitXCloudDevOpsUrl
  deployPreDependsServices
  deployDevopsAppsAll
  deployStatus=$([ $? -eq 0 ] && echo "SUCCESS" || echo "FAILURE")
  costTime=$[$(echo `date +%s`)-$beginTime]
  echo -n "---------------------------------------------------------------"
  echo -e "\nDeployed apps statistics details:\n${globalDeployStatsMsg}"
  log "-------------------------------------------------------------------"
  log "DEPLOY $deployStatus"
  log "-------------------------------------------------------------------"
  log "Total time: ${costTime} sec (Wall Clock)"
  log "Finished at: $(date -d today +'%Y-%m-%d %H:%M:%S')"
  log "Installing details logs see: $logFile"
  log "-------------------------------------------------------------------"
}
main
