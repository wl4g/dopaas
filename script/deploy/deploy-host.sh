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
. $currDir/deploy-common.sh
loadi18n

# Global variables.
globalAllNodes=()
globalAllNodesString=""
globalDeployStatsMsg="" # Deployed stats message.

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
      # Check deployer user of root group.
      local deployerUserGroups=$(doRemoteCmd "$user" "$passwd" "$host" "$(echo groups)" "true" "true")
      if [ ! "$deployerUserGroups" = "root" ]; then
        logErr "Please use the remote host user belonging to the root groups to perform the deployment !" && exit -1
      fi
      # Storage deployer all nodes. 
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
  local localRepoPathPrefix="$(echo $apacheMvnLocalRepoDir|cut -c 1-6)"
  if [ "$localRepoPathPrefix" == "/root/" ]; then
    export apacheMvnLocalRepoDirOfUser="root"
  elif [[ "$localRepoPathPrefix" == "/home/" || "$localRepoPathPrefix" == "/Users" ]]; then # fix: MacOS(/Users/)
    export apacheMvnLocalRepoDirOfUser="$(echo $apacheMvnLocalRepoDir|awk -F '/' '{print $3}')"
  else
    logErr "Invalid maven local repository path. for example: $USER/.m2/repository"; exit -1
  fi
}

# Pull project sources. (Return 'Y' for pull success)
function pullSources() {
  local projectName=$1 # e.g xcloud-dopaas
  local cloneUrl=$2
  local branch=$3
  local projectDir="$currDir/$projectName"
  if [ ! -d "$projectDir" ]; then
    log "Git clone $projectName from ($branch)$cloneUrl ..."
    cd $currDir && git clone $cloneUrl 2>&1 | tee -a $logFile
    cd $projectDir && git checkout $branch
    [ $? -ne 0 ] && exit -1
    return 0
  else
    log "Git pull $projectName from ($branch)$cloneUrl ..."
    # Check and set remote url.
    local oldRemoteUrl=$(cd $projectDir && git remote -v|grep fetch|awk '{print $2}';cd ..)
    if [ "$oldRemoteUrl" != "$cloneUrl" ]; then
      log "Updating origin remote url to \"$cloneUrl\" ..."
      cd $projectDir && git remote set-url origin $cloneUrl
    fi
    # Check and pull
    local pullResult=$(cd $projectDir && git pull 2>&1 | tee -a $logFile)
    cd $projectDir && git checkout $branch
    [ $? -ne 0 ] && exit -1
    if [[ "$pullResult" != "Already up-to-date."* || "$buildForcedOnPullUpToDate" == "true" ]]; then
      return 0
    else
      log "Skip build of $projectName(latest)"
      # Tips rebuild usage.
      if [ "$buildForcedOnPullUpToDate" != "true" ]; then
        log " [Tips]: If you still want to recompile, you can usage: export buildForcedOnPullUpToDate='true' to set it."
        return 1
      fi
    fi
  fi
}

# Pull and maven compile.
function pullAndMvnCompile() {
  local projectName=$1 # e.g xcloud-dopaas
  local cloneUrl=$2
  local branch=$3
  local projectDir="$currDir/$projectName"
  # Pulling project sources.
  pullSources "$projectName" "$cloneUrl" "$branch"
  if [ $? -eq 0 ]; then
    log "Compiling $projectName ..."
    cd $projectDir
    $cmdMvn -Dmaven.repo.local=$apacheMvnLocalRepoDir clean install -DskipTests -T 2C -U -P $buildPkgType 2>&1 | tee -a $logFile
    [ ${PIPESTATUS[0]} -ne 0 ] && exit -1 # or use 'set -o pipefail', see: http://www.huati365.com/answer/j6BxQYLqYVeWe4k

    # If the mvn command is currently executed as root, but the local warehouse directory owner is another user, 
    # the owner should be reset (because there may be a newly downloaded dependent library)
    chown -R $apacheMvnLocalRepoDirOfUser:$apacheMvnLocalRepoDirOfUser $projectDir
    chown -R $apacheMvnLocalRepoDirOfUser:$apacheMvnLocalRepoDirOfUser $apacheMvnLocalRepoDir
  else
    log "[WARNING] Skip compiling project for $projectName"
  fi
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

  # Add deployed xcloud-dopaas primary service host.
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
    if [ "$deployAsync" == "true" ]; then
      doDeployToNodeOfCluster "$appName" "$appInstallDir" "$buildFilePath" "$host" "$user" "$passwd" "$springProfilesActive" &
    else
      doDeployToNodeOfCluster "$appName" "$appInstallDir" "$buildFilePath" "$host" "$user" "$passwd" "$springProfilesActive"
    fi
    [ $? -ne 0 ] && exit -1
  done
  [ "$deployAsync" == "true" ] && wait # Wait all instances async deploy complete.
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
  # Restart app service.
  log "[$appName/cluster/$host] Restarting for $appName ..."
  doRemoteCmd "$user" "$passwd" "$host" "su - $appName -c \"$cmdRestart\"" "true"
  log "[$appName/cluster/$host] Deployed $appName completed."
}

# Do deploy app.
function doDeployBackendApp() {
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

  # Add deployed xcloud-dopaas primary services names.
  globalDeployStatsMsg="${globalDeployStatsMsg}\n
[${appName}]:\n
\t          Install Home: ${deployAppBaseDir}/${appName}-package/${appName}-${buildPkgVersion}-bin/\n
\t            Config Dir: ${deployAppBaseDir}/${appName}-package/${appName}-${buildPkgVersion}-bin/conf/\n
\t       Profiles Active: ${springProfilesActive}\n
\t              PID File: /mnt/disk1/${appName}/${appName}.pid\n
\t       Restart Command: /etc/init.d/$appName.service restart\n
\t             Logs File: /mnt/disk1/log/${appName}/${appName}_${springProfilesActive}.log\n
\t        Deployed Hosts:"

  if [ "$runtimeMode" == "standalone" ]; then # The 'standalone' mode is only deployed to the local host
    log "[$appName/standalone] deploying to local ..."
    if [ "$deployAsync" == "true" ]; then
      deployToLocalOfStandalone "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName" "$springProfilesActive" &
    else
      deployToLocalOfStandalone "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName" "$springProfilesActive"
    fi
    [ $? -ne 0 ] && exit -1
    log "[$appName/standalone] Deployed to local completed !"
  elif [ "$runtimeMode" == "cluster" ]; then # The 'cluster' mode is deployed to the remote hosts
    log "[$appName/cluster] Deploying to cluster nodes ..."
    if [ "$deployAsync" == "true" ]; then
      deployToNodesOfCluster "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName" "$springProfilesActive" "${nodeArr[*]}" &
    else
      deployToNodesOfCluster "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName" "$springProfilesActive" "${nodeArr[*]}"
    fi
    log "[$appName/cluster] Deployed to cluster nodes completed !"

    # Add deployed xcloud-dopaas primary service host.
    globalDeployStatsMsg="${globalDeployStatsMsg} ${globalAllHostsString}"
    [ $? -ne 0 ] && exit -1
    log "[$appName/cluster] Deployed to remote all nodes !"
  fi
}

# Deploy DoPaaS all apps and startup.
function deployBackendApps() {
  # Prepare deploy backend apps.
  prepareDeployBackendApps
  # Deploy DoPaaS apps.
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
      doDeployBackendApp "$buildModule" "${springProfilesActive}" "${globalAllNodes[*]}"
    done
    [ "$deployAsync" == "true" ] && wait # Wait all apps async deploy complete.
  fi
  return 0
}

# Prepare deploy backend applications.
function prepareDeployBackendApps() {
  log "Pulling and compile backend project sources ..."
  pullAndMvnCompile "xcloud-component" "$gitXCloudComponentUrl" "$gitComponentBranch"
  pullAndMvnCompile "xcloud-iam" "$gitXCloudIamUrl" "$gitIamBranch"
  pullAndMvnCompile "xcloud-dopaas" "$gitXCloudDoPaaSUrl" "$gitDoPaaSBranch"
  # Deploying dependent for eureka servers.
  deployEurekaServers
}

# Check and deploy eureka servers.
function deployEurekaServers() {
  if [ "$runtimeMode" == "cluster" ]; then
    # Deploying eureka servers.
    if [ ${#globalAllNodes[@]} -lt 3 ]; then # Building pseudo cluster.
      local appName=$(echo "$deployEurekaBuildModule"|awk -F ',' '{print $1}')
      local cmdRestart="/etc/init.d/${appName}.service restart"
      local node1=${globalAllNodes[0]}
      local host1=$(echo $node1|awk -F 'ξ' '{print $1}')
      local user1=$(echo $node1|awk -F 'ξ' '{print $2}')
      local passwd1=$(echo $node1|awk -F 'ξ' '{print $3}')
      # Node1:
      log "[eureka/$host1] Deploy eureka by peer1 (Disguised) ..."
      doDeployBackendApp "$deployEurekaBuildModule" "ha,peer1" "$node1"
      # Due to the asynchronous call of the previous function, we have to wait for 
      # synchronous execution to ensure the integrity of the first build package.
      wait
      # Node2 and Node3: (only start new instance)
      log "[eureka/$host1] Deploy eureka by peer2 (Disguised) ..."
      doRemoteCmd "$user1" "$passwd1" "$host1" "export SPRING_PROFILES_ACTIVE='ha,peer2' && $cmdRestart" "true" &
      log "[eureka/$host1] Deploy eureka by peer3 (Disguised) ..."
      doRemoteCmd "$user1" "$passwd1" "$host1" "export SPRING_PROFILES_ACTIVE='ha,peer3' && $cmdRestart" "true" &
      # Check add dns resloving.
      addDnsResolving "$host1" "$host1" "$host1"
    else # Building a real cluster.
      # Node1:
      local node1=${globalAllNodes[0]}
      local host1=$(echo $node1|awk -F 'ξ' '{print $1}')
      log "[eureka/$host1] Deploy eureka by peer1 ..."
      doDeployBackendApp "$deployEurekaBuildModule" "ha,peer1" "$node1"
      # Node2:
      local node2=${globalAllNodes[1]}
      local host2=$(echo $node2|awk -F 'ξ' '{print $1}')
      log "[eureka/$host2] Deploy eureka by peer2 ..."
      doDeployBackendApp "$deployEurekaBuildModule" "ha,peer2" "$node2"
      # Node3:
      local node3=${globalAllNodes[2]}
      local host3=$(echo $node3|awk -F 'ξ' '{print $1}')
      log "[eureka/$host3] Deploy eureka by peer3 ..."
      doDeployBackendApp "$deployEurekaBuildModule" "ha,peer3" "$node3"
      # Check add dns resloving.
      addDnsResolving "$host1" "$host2" "$host3"
    fi
  else # In standalone mode, Eureka does not need to be deployed.
    log "Skip eureka servers deploy, because runtime mode is standalone."
  fi
}

# Check add resolving dns for peers.
function addDnsResolving() {
  local host1=$1
  local host2=$2
  local host3=$3
  # Transform to ip(if host is not ip).
  [ -z "$(echo $host1|egrep '(^[0-9]+)\.')" ] && host1=$(ping $host1 -c 1 -w 3 2>/dev/null|sed '1{s/[^(]*(//;s/).*//;q}')
  [ -z "$(echo $host2|egrep '(^[0-9]+)\.')" ] && host2=$(ping $host2 -c 1 -w 3 2>/dev/null|sed '1{s/[^(]*(//;s/).*//;q}')
  [ -z "$(echo $host3|egrep '(^[0-9]+)\.')" ] && host3=$(ping $host3 -c 1 -w 3 2>/dev/null|sed '1{s/[^(]*(//;s/).*//;q}')
  # Check mapping dns?
  local resolvingPeer1=$(ping -c 1 -w 3 peer1 >/dev/null 2>&1; echo "$?")
  local resolvingPeer2=$(ping -c 1 -w 3 peer2 >/dev/null 2>&1; echo "$?")
  local resolvingPeer3=$(ping -c 1 -w 3 peer3 >/dev/null 2>&1; echo "$?")
  if [[ $resolvingPeer1 != 0 || $resolvingPeer2 != 0 || $resolvingPeer3 != 0 ]]; then
    echo "# Auto generated by DoPaaS Deployer(host)" >> /etc/hosts
    echo "$host1 peer1" >> /etc/hosts
    log "Added dns resoling peer1 to $host1"
    echo "$host2 peer2" >> /etc/hosts
    log "Added dns resoling peer2 to $host2"
    echo "$host3 peer3" >> /etc/hosts
    log "Added dns resoling peer3 to $host3"
  fi
}

# Deploy frontend apps to nginx.
function deployFrontendApps() {
  # Check is skiped.
  if [ "$deployFrontendSkip" == "true" ]; then
    log "Skiped for deploy frontend application, you can set export deployFrontendSkip=false to turn off the skip deployment frontend!"
    return 0
  fi
  local appName="xcloud-dopaas-view"
  local appInstallDir="${deployFrontendAppBaseDir}/${appName}-package"
  local node=${globalAllNodes[0]} # First node deploy the nginx by default.
  local host=$(echo $node|awk -F 'ξ' '{print $1}')
  local user=$(echo $node|awk -F 'ξ' '{print $2}')
  local passwd=$(echo $node|awk -F 'ξ' '{print $3}')
  if [[ "$host" == "" || "$user" == "" ]]; then
    logErr "[$appName] Invalid cluster node info, host/user is required! host: $host, user: $user, password: $passwd"; exit -1
  fi
  # Add deployed xcloud-dopaas primary service host.
  globalDeployStatsMsg="${globalDeployStatsMsg}\n
[${appName}]:\n
\t          Install Home: ${appInstallDir}/${appName}-${buildPkgVersion}-bin/\n
\t            Config Dir: /etc/nginx/nginx.conf or /etc/nginx/conf.d/\n
\t       Profiles Active: ${springProfilesActive}\n
\t              PID File: /run/nginx.pid\n
\t       Restart Command: sudo systemctl restart nginx or sudo /etc/init.d/nginx.service restart\n
\t             Logs File: /var/log/nginx/access.log or /var/log/nginx/error.log\n
\t        Deployed Hosts: $host"

  {
    # Check install to remote nginx
    local checkRemoteNginxResult=$(doRemoteCmd "$user" "$passwd" "$host" "command -v nginx" "true" "true")
    if [ -z "$checkRemoteNginxResult" ]; then
      # Installing nginx.
      local scriptFilename="install-nginx.sh"
      doScp "$user" "$passwd" "$host" "$currDir/$scriptFilename" "/tmp/$scriptFilename" "true"
      doRemoteCmd "$user" "$passwd" "$host" "chmod +x /tmp/$scriptFilename && bash /tmp/$scriptFilename" "true" "true"
    fi
    # Make nginx configuration.
    cd $workspaceDir && rm -rf nginx && cp -r $currDir/xcloud-dopaas/nginx . && cd nginx && tar -cf nginx.tar *
    doScp "$user" "$passwd" "$host" "$workspaceDir/nginx/nginx.tar" "/etc/nginx" "true"
    doRemoteCmd "$user" "$passwd" "$host" "cd /etc/nginx/ && tar --overwrite-dir --overwrite -xf nginx.tar && rm -rf nginx.tar" "true" "true"

    ## Pull frontend.
    pullSources "xcloud-dopaas-view" "$gitXCloudDoPaaSFrontendUrl" "$gitDoPaaSFrontendBranch"

    # Compile frontend.
    sudo $cmdNpm install 2>&1 | tee -a $logFile
    sudo $cmdNpm run build 2>&1 | tee -a $logFile
    [ $? -ne 0 ] && exit -1 || return 0

    # Deploy frontend.
    local deployFrontendDir="${appInstallDir}/${appName}-${buildPkgVersion}-bin"
    local fProjectDir="$currDir/xcloud-dopaas-view"
    doRemoteCmd "$user" "$passwd" "$host" "mkdir -p $deployFrontendDir && \rm -rf $deployFrontendDir/*" "true" "true"
    cd $fProjectDir && tar -cf dist.tar dist/
    doScp "$user" "$passwd" "$host" "$fProjectDir/dist.tar" "$deployFrontendDir" "true"
    doRemoteCmd "$user" "$passwd" "$host" "cd $deployFrontendDir && tar -xf dist.tar --strip-components=1 && rm -rf dist.tar" "true" "true"
    # Replace nginx bind domain.
    doRemoteCmd "$user" "$passwd" "$host" "sed -i 's/wl4g.com/wl4g.$springProfilesActive/g' /etc/nginx/conf.d/dopaas_http*.conf" "true" "true"
    # Restart nginx(first install).
    doRemoteCmd "$user" "$passwd" "$host" "[ -n \"$(echo command -v systemctl)\" ] && sudo systemctl restart nginx || /etc/init.d/nginx.service restart" "true" "true"
    [ $? -ne 0 ] && exit -1 || return 0
  } &
}

# ----- Main call. -----
function main() {
  [ -n "$(command -v clear)" ] && clear # e.g centos8+ not clear
  log ""
  log "「 Welcome to XCloud DoPaaS Deployer (Host) 」"
  log ""
  log " Wiki: https://github.com/wl4g/xcloud-dopaas/blob/master/README.md"
  log " Wiki(CN): https://gitee.com/wl4g/xcloud-dopaas/blob/master/README_CN.md"
  log " Authors: <Wanglsir@gmail.com, 983708408@qq.com>"
  log " Version: 2.0.0"
  log " Time: $(date '+%Y-%m-%d %H:%M:%S')"
  log " Installation logs writing: $logFile"
  log " -------------------------------------------------------------------"
  log ""
  [ "$deployAsync" == "true" ] && log "Using asynchronous deployment, you can usage: export deployAsync='false' to set it."
  beginTime=`date +%s`
  initConfiguration
  checkInstallInfraSoftware
  deployFrontendApps
  deployBackendApps
  wait
  deployStatus=$([ $? -eq 0 ] && echo "SUCCESS" || echo "FAILURE")
  costTime=$[$(echo `date +%s`)-$beginTime]
  log "--------------------------------------------------------------------"
  log "Deployed APPs Summary:\n${globalDeployStatsMsg}"
  log "--------------------------------------------------------------------"
  log "DEPLOY $deployStatus"
  log "--------------------------------------------------------------------"
  log "Total time: ${costTime} sec (Wall Clock)"
  log "Finished at: $(date '+%Y-%m-%d %H:%M:%S')"
  log "Installing details logs see: $logFile"
  log "--------------------------------------------------------------------"
}
main
