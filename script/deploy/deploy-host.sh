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

# Initialization
[ -z "$currDir" ] && export currDir=$(echo "$(cd "`dirname "$0"`"/; pwd)")
. ${currDir}/deploy-common.sh
[ -n "$(command -v clear)" ] && clear # e.g centos8+ not clear

log ""
log "「 Welcome to XCloud DevOps Deployer(Host) 」"
log ""
log " Wiki: https://github.com/wl4g/xcloud-devops/blob/master/README.md"
log " Wiki(CN): https://gitee.com/wl4g/xcloud-devops/blob/master/README_CN.md"
log " Authors: <Wanglsir@gmail.com, 983708408@qq.com>"
log " Version: 2.0.0"
log " Time: $(date -d today +'%Y-%m-%d %H:%M:%S')"
#log " Installation logs writing: $logFile"
log " -----------------------------------------------------------------------"
log ""

# Pull and compile.
function pullAndCompile() {
  local projectName=$1 # e.g xcloud-devops
  local cloneUrl=$2
  local projectDir="$currDir/$projectName"
  if [ ! -d "$projectDir" ]; then
    log "Git clone $projectName from $cloneUrl ..."
    cd $currDir && git clone $cloneUrl 2>&1 | tee -a $logFile
    log "Compiling $projectName ..."
    cd $projectDir && $cmdMvn -Dmaven.repo.local=$apacheMvnLocalRepoDir clean install -DskipTests -T 2C -U -P $buildPkgType 2>&1 | tee -a $logFile
    [ ${PIPESTATUS[0]} -ne 0 ] && exit -1 # or use 'set -o pipefail', see: http://www.huati365.com/answer/j6BxQYLqYVeWe4k
  else
    log "Git pull $projectName from $cloneUrl ..."
    # Check update remote url
    local oldRemoteUrl=$(cd $projectDir && git remote -v|grep fetch|awk '{print $2}';cd ..)
    if [ ! "$oldRemoteUrl" == "$cloneUrl" ]; then
      log "Updating origin remote url to \"$cloneUrl\" ..."
      cd $projectDir && git remote set-url origin $cloneUrl
    fi
    # Check already updated?
    local pullResult=$(cd $projectDir && git pull 2>&1 | tee -a $logFile)
    if [[ "$pullResult" != "Already up-to-date."* || "$rebuildOfGitPullAlreadyUpToDate" == "true" ]]; then
      log "Compiling $projectName ..."
      cd $projectDir && $cmdMvn -Dmaven.repo.local=$apacheMvnLocalRepoDir clean install -DskipTests -T 2C -U -P $buildPkgType 2>&1 | tee -a $logFile
      [ ${PIPESTATUS[0]} -ne 0 ] && exit -1 # or use 'set -o pipefail', see: http://www.huati365.com/answer/j6BxQYLqYVeWe4k
    else
      log "Skip build of $projectName(latest)"
      # Tips usage rebuild
      if [ "$rebuildOfGitPullAlreadyUpToDate" != "true" ]; then
        log " [Tips]: If you still want to recompile, usage: export rebuildOfGitPullAlreadyUpToDate=\"true\" before execution !!!"
      fi
    fi
  fi
  # If the mvn command is currently executed as root, but the local warehouse directory owner is another user, 
  # the owner should be reset (because there may be a newly downloaded dependent library)
  chown -R $apacheMvnLocalRepoDirOfUser:$apacheMvnLocalRepoDirOfUser $projectDir
  chown -R $apacheMvnLocalRepoDirOfUser:$apacheMvnLocalRepoDirOfUser $apacheMvnLocalRepoDir
}

# Deploy & startup all(standalone).
function deployAndStartupAllWithStandalone() {
  local buildFilePath=$1
  local buildFileName=$2
  local cmdRestart=$3
  local appName=$4
  local appInstallDir=${deployBaseDir}/${appName}-package && mkdir -p $appInstallDir
  log "[$appName/standalone] Cleanup older install files: $appInstallDir/* ..."
  secDeleteLocal "$appInstallDir/*"
  [ ${PIPESTATUS[0]} -ne 0 ] && exit -1
  if [ "$buildPkgType" == "mvnAssTar" ]; then
    log "[$appName/standalone/local] Uncompress $buildFilePath to $appInstallDir ..."
    tar -xf $buildFilePath -C $appInstallDir/
  elif [ "$buildPkgType" == "springExecJar" ]; then
    log "[$appName/standalone/local] Copying $buildFilePath to $appInstallDir/ ..."
    unalias -a cp
    cp -Rf ${appName}-${buildPkgVersion}-bin.jar $appInstallDir/
  else
    logErr "[$appName/standalone/local] Invalid config buildPkgType: $buildPkgType"; exit -1
  fi
  [ ${PIPESTATUS[0]} -ne 0 ] && exit -1
  # Check app services script.
  log "[$appName/standalone/local] Checking for app services script ..."
  checkRemoteHasService "$appName" "$USER" "$passwd" "localhost"
  exec $cmdRestart
  [ ${PIPESTATUS[0]} -ne 0 ] && exit -1
}

# Deploy & startup all(cluster).
function deployAndStartupAllWithCluster() {
  local buildFilePath=$1
  local buildFileName=$2
  local cmdRestart=$3
  local appName=$4
  local appInstallDir=${deployBaseDir}/${appName}-package
  local k=0
  for node in `cat $deployClusterNodesConfigPath`
  do
    ((k+=1))
    if [ $k == 1 ]; then # Skip title row(first)
      continue
    fi
    # Extract node info & trim
    local host=$(echo $node|awk -F ',' '{print $1}'|sed -e 's/^\s*//' -e 's/\s*$//')
    local user=$(echo $node|awk -F ',' '{print $2}'|sed -e 's/^\s*//' -e 's/\s*$//')
    local passwd=$(echo $node|awk -F ',' '{print $3}'|sed -e 's/^\s*//' -e 's/\s*$//')
    if [[ "$host" == "" || "$user" == "" ]]; then
      logErr "[$appName/cluster] Invalid cluster node info, host/user is required! host: $host, user: $user, password: $passwd"; exit -1
    fi
    # Do deploy to instance.
    if [ "$asyncDeploy" == "true" ]; then
      doDeployAndStartupToClusterInstance $appName $appInstallDir $buildFilePath $buildPkgType $host $user $passwd &
    else
      doDeployAndStartupToClusterInstance $appName $appInstallDir $buildFilePath $buildPkgType $host $user $passwd
    fi
    [ ${PIPESTATUS[0]} -ne 0 ] && exit -1 # or use 'set -o pipefail', see: http://www.huati365.com/answer/j6BxQYLqYVeWe4k
  done
  [ "$asyncDeploy" == "true" ] && wait # Wait all instances async deploy complete.
}

# Deploy build assets to cluster remote instance.
function doDeployAndStartupToClusterInstance() {
  local appName=$1
  local appInstallDir=$2
  local buildFilePath=$3
  local buildPkgType=$4
  local host=$5
  local user=$6
  local passwd=$7

  # Deployement to remote.
  log "[$appName/cluster/$host] Cleanup older install files: \"$appInstallDir/*\" ..."
  [[ "$appInstallDir" != "" && "$appInstallDir" != "/" ]] && doRemoteCmd "$user" "$passwd" "$host" "rm -rf $appInstallDir/*" "true"
  doRemoteCmd "$user" "$passwd" "$host" "mkdir -p $appInstallDir" "false"
  log "[$appName/cluster/$host] Transfer \"$buildFilePath\" to remote \"$appInstallDir\" ..."
  doScp "$user" "$passwd" "$host" "$buildFilePath" "$appInstallDir/$buildFileName" "true"
  if [ "$buildPkgType" == "mvnAssTar" ]; then
    log "[$appName/cluster/$host] Uncompress \"$appInstallDir/$buildFileName\" to \"$appInstallDir/\" ..."
    doRemoteCmd "$user" "$passwd" "$host" "tar -xf $appInstallDir/$buildFileName -C $appInstallDir && rm -rf $appInstallDir/$buildFileName" "true"
  elif [ "$buildPkgType" == "springExecJar" ]; then
    log "" # Nothing
  else
    logErr "[$appName/cluster/$host] Invalid config buildPkgType: $buildPkgType"; exit -1
  fi
  # Check installed services script?
  log "[$appName/cluster/$host] Checking for app services installization ..."
  checkRemoteHasService "$appName" "$user" "$passwd" "$host"
  [ ${PIPESTATUS[0]} -ne 0 ] && exit -1 # or use 'set -o pipefail', see: http://www.huati365.com/answer/j6BxQYLqYVeWe4k
  # Exec restart
  log "[$appName/cluster/$host] Restarting for $appName ..."
  doRemoteCmd "$user" "$passwd" "$host" "$cmdRestart" "true"
  log "[$appName/cluster/$host] Deployed $appName completed."
}

# Deploy & startup all.
function deployAndStartupAll() {
  if [ "$runtimeMode" == "standalone" ]; then
    deployBuildTargets=("${deployStandaloneBuildTargets[@]}") # Copy build targets array
  elif [ "$runtimeMode" == "cluster" ]; then # The 'cluster' mode is deploy to the remote hosts
    deployBuildTargets=("${deployClusterBuildTargets[@]}") # Copy build targets array
  else
    logErr "Invalid config runtimeMode: $runtimeMode"; exit -1
  fi
  # Call deploying
  deployBuildTargetsLen=${#deployBuildTargets[@]}
  if [ $deployBuildTargetsLen -gt 0 ]; then
    for((i=0;i<${#deployBuildTargets[@]};i++)) do
      local buildTargetDir=${deployBuildTargets[i]}
      local buildFileName=$(ls -a "$buildTargetDir"|grep -E "*-${buildPkgVersion}-bin.tar|*-${buildPkgVersion}-bin.jar")
      if [ -z "$buildFileName" ]; then
         logErr "Failed to read build assets from target direct: $buildTargetDir"; exit -1
      fi
      local appName=$(echo "$(basename $buildFileName)"|awk -F "-${buildPkgVersion}-bin.tar|-${buildPkgVersion}-bin.jar" '{print $1}')
      local cmdRestart="/etc/init.d/${appName}.service restart"
      if [ "$runtimeMode" == "standalone" ]; then # The 'standalone' mode is only deployed to the local host
        log "[$appName/standalone] deploying to local ..."
        if [ "$asyncDeploy" == "true" ]; then
          deployAndStartupAllWithStandalone "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName" &
        else
          deployAndStartupAllWithStandalone "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName"
        fi
        [ ${PIPESTATUS[0]} -ne 0 ] && exit -1 # or use 'set -o pipefail', see: http://www.huati365.com/answer/j6BxQYLqYVeWe4k
        log "[$appName/standalone] Deployed to local completed !"
      elif [ "$runtimeMode" == "cluster" ]; then # The 'cluster' mode is deployed to the remote hosts
        log "[$appName/cluster] Deploying to remote all nodes ..."
        if [ "$asyncDeploy" == "true" ]; then
          deployAndStartupAllWithCluster "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName" &
        else
          deployAndStartupAllWithCluster "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName"
        fi
        [ ${PIPESTATUS[0]} -ne 0 ] && exit -1 # or use 'set -o pipefail', see: http://www.huati365.com/answer/j6BxQYLqYVeWe4k
        log "[$appName/cluster] Deployed to remote all nodes !"
      fi
    done;
    [ "$asyncDeploy" == "true" ] && wait # Wait all apps async deploy complete.
  fi
}

# ----- Main call. -----
if [[ "$(echo groups)" == "root" ]]; then
  logErr "Please execute the scripts as a user with root privileges !" && exit -1
fi
beginTime=`date +%s`

checkPreDependencies
pullAndCompile "xcloud-component" $gitXCloudComponentUrl
pullAndCompile "xcloud-iam" $gitXCloudIamUrl
pullAndCompile "xcloud-devops" $gitXCloudDevOpsUrl
deployAndStartupAll
deployStatus=$([ ${PIPESTATUS[0]} -eq 0 ] && echo "SUCCESS" || echo "FAILURE")

costTime=$[$(echo `date +%s`)-$beginTime]
log " ---------------------------------------------------------------------"
log " DEPLOY $deployStatus"
log " ---------------------------------------------------------------------"
log " Total time: ${costTime} sec (Wall Clock)"
log " Finished at: $(date -d today +'%Y-%m-%d %H:%M:%S')"
log " More details logs see: $logFile"
log " ---------------------------------------------------------------------"
