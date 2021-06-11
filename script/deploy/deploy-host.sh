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
. $currDir/deploy-common.sh && loadi18n

# Global variables.
globalAllNodes=()
globalAllHostsString=""
globalDeployStatsMsg="" # Deployed stats message.
isBackendPullHasUpdate='false' # Backend project pull has update? includes dependent project.

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
      if [ "$globalAllHostsString" == "" ]; then
        globalAllHostsString="$host"
      else
        globalAllHostsString="${globalAllHostsString}, $host"
      fi
      # 为了最佳性能减少ssh交互, 暂时忽略此逻辑, 同时在deployBackendAll()函数中已对多节点多应用并发部署做了优化.
      #configureRemoteSshd "$host" "$user" "$passwd"
    done
    # Check nodes must > 0
    if [ ${#globalAllNodes[@]} -le 0 ]; then
      logErr "Please reconfigure '$currDir/deploy-host.csv', deploy at least one cluster node !"
    fi
  else
    # Save default local deploy node. 
    globalAllNodes[index]="localhostξrootξ"
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

# Enlarge remote sshd_config 'MaxSessions'. (FIXED: ssh_exchange_identification: Connection closed by remote host)
#function configureRemoteSshd() {
#  local host=$1
#  local user=$2
#  local passwd=$3
#  if [[ -z "$host" || -z "$user" || -z "$passwd" ]]; then
#    logErr "Configure remote sshd_config, host/user/passwd is requires!"; exit -1
#  fi
#  local cmd1="cd /etc/ssh/ && [[ -z \"\$(cat sshd_config|grep -E '^MaxSessions(\s*[0-9]+)')\" ]] && echo 'MaxSessions 30' >>sshd_config || sed -i -r 's/^MaxSessions(\s*[0-9]+)/MaxSessions 30/g' sshd_config"
#  local cmd2="cd /etc/ssh/ && [[ -z \"\$(cat sshd_config|grep -E '^MaxStartups(\s+)(\S+)')\" ]] && echo 'MaxStartups 30:30:100' >>sshd_config || sed -i -r 's/^MaxStartups(\s+)(\S+)/MaxStartups 30:30:100/g' sshd_config"
#  local cmd="$cmd1; $cmd2; [ -n \"$(command -v systemctl)\" ] && systemctl restart sshd || service sshd restart"
#  log "[$host] Configure remote sshd_config cmd: $cmd"
#  doRemoteCmd "$user" "$passwd" "$host" "$cmd" "true"
#}

# Pull project sources, return(0/1)
function pullSources() {
  local projectName=$1 # e.g: xcloud-dopaas
  local cloneUrl=$2
  local branch=$3
  local projectDir="$currDir/$projectName"
  if [ ! -d "$projectDir" ]; then
    log "Git clone $projectName from [${branch}]:$cloneUrl ..."
    cd $currDir && timeout --foreground 300 git clone $cloneUrl 2>&1 | tee -a $logFile
    [ ${PIPESTATUS[0]} -ne 0 ] && exit -1
    cd $projectDir && git checkout $branch
    return 1
  else
    log "Git pull $projectName from [${branch}]:$cloneUrl ..."
    # Check and set remote url.
    local oldRemoteUrl=$(cd $projectDir && git remote -v|grep fetch|awk '{print $2}';cd ..)
    if [ "$oldRemoteUrl" != "$cloneUrl" ]; then
      log "Updating origin remote url to \"$cloneUrl\" ..."
      cd $projectDir && git remote set-url origin $cloneUrl
    fi
    # Check and pull
    cd $projectDir && git config pull.rebase false && git reset --hard
    local pullResult=$(timeout --foreground 90 git pull -f 2>&1 | tee -a $logFile)
    [ ${PIPESTATUS[0]} -ne 0 ] && exit -1
    cd $projectDir && git checkout $branch
    if [[ "$pullResult" != 'Already up to date.' ]]; then
      isBackendPullHasUpdate='true' # There are upstream dependencies and updates
      return 1 # Has new commits, need compile.
    else
      return 0 # Not new commits, no need compile. 
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
  if [[ $? == 1 || "$buildForcedOnPullUpToDate" == 'true' || "$isBackendPullHasUpdate" == 'true' ]]; then
    log "Compiling $cmdMvn $projectName ..."
    cd $projectDir
    $cmdMvn -Dmaven.repo.local=$apacheMvnLocalRepoDir clean install -DskipTests -T 2C -U -P $buildPkgType 2>&1 | tee -a $logFile
    [ ${PIPESTATUS[0]} -ne 0 ] && exit -1 # or use 'set -o pipefail', see: http://www.huati365.com/answer/j6BxQYLqYVeWe4k

    # If the mvn command is currently executed as root, but the local warehouse directory owner is another user, 
    # the owner should be reset (because there may be a newly downloaded dependent library)
    chown -R $apacheMvnLocalRepoDirOfUser:$apacheMvnLocalRepoDirOfUser $projectDir
    chown -R $apacheMvnLocalRepoDirOfUser:$apacheMvnLocalRepoDirOfUser $apacheMvnLocalRepoDir
  else
    log "[WARNING] Skip building project for $projectName"
    # Tips rebuild usage.
    if [ "$buildForcedOnPullUpToDate" != "true" ]; then
      log " [Tips]: If you still want to recompile, you can usage: export buildForcedOnPullUpToDate='true' to set it."
    fi
  fi
}

# Deploy app(standalone) to local.
function deployStandaloneToLocal() {
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

  # Add deployed dopaas primary service host.
  globalDeployStatsMsg="${globalDeployStatsMsg}"$(echo -n " localhost")

  log "[$appName/standalone] Cleanup older install files: $appInstallDir/* ..."
  secDeleteLocal "$appInstallDir/*"
  if [[ "$buildPkgType" == "mvnAssTar" ]]; then
    log "[$appName/standalone/local] Uncompress $buildFilePath to $appInstallDir ..."
    tar -xf $buildFilePath -C $appInstallDir/
  elif [[ "$buildPkgType" == "springExecJar" ]]; then
    log "[$appName/standalone/local] Copying $buildFilePath to $appInstallDir/ ..."
    unalias cp >/dev/null 2>&1
    cp -Rf ${appName}-${buildPkgVersion}-bin.jar $appInstallDir/
  else
    logErr "[$appName/standalone/local] Invalid config buildPkgType: $buildPkgType"; exit -1
  fi
  [ $? -ne 0 ] && exit -1
  # Check install service script
  log "[$appName/standalone/local] Checking installation app service script ..."
  checkInstallServiceScript "$appName" "$USER" "$passwd" "localhost" "$springProfilesActive" "false"
  # Restart.
  log "[$appName/cluster/$host] Restarting for $appName($springProfilesActive) ..."
  $cmdRestart
}

# Deploy app(cluster) to remote node.
function doDeployClusterToNode() {
  local buildFilePath=$1
  local buildFileName=$2
  local cmdRestart=$3
  local appName=$4
  local springProfilesActive=$5
  local host=$6
  local user=$7
  local passwd=$8
  # Check args.
  if [[ "$buildFilePath" == "" || "$buildFileName" == "" || "$cmdRestart" == "" || "$appName" == "" || "$springProfilesActive" == "" || "$host" == "" || "$user" == "" ]]; then
    logErr "Failed to deploy to nodes, because buildFilePath/buildFileName/cmdRestart/appName/nodeArr is required!
buildFilePath=$buildFilePath, buildFileName=$buildFileName, cmdRestart=$cmdRestart, appName=$appName, 
springProfilesActive=$springProfilesActive, host=$host, user=$user"
    exit -1
  fi

  local appInstallDir="${deployAppBaseDir}/${appName}-package"
  # Do deploy to instance.
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

  # Check install service script.
  log "[$appName/cluster/$host] Checking installation app service script ..."
  checkInstallServiceScript "$appName" "$user" "$passwd" "$host" "$springProfilesActive" "false"
  [ $? -ne 0 ] && exit -1 # or use 'set -o pipefail', see: http://www.huati365.com/answer/j6BxQYLqYVeWe4k
  # Restart.
  log "[$appName/cluster/$host] Restarting for $appName($springProfilesActive) ..."
  doRemoteCmd "$user" "$passwd" "$host" "$cmdRestart" "true"
  [ $? -ne 0 ] && logErr "[$appName/cluster/$host] Failed to deploy cluster!" && exit -1
  log "[$appName/cluster/$host] Deployed $appName($springProfilesActive) completed."
}

# Do deploy backend app.
function doDeployBackendApp() {
  local buildModule=$1
  local springProfilesActive=$2 # Priority custom active.
  local node=$3
  local host=$(echo $node|awk -F 'ξ' '{print $1}')
  local user=$(echo $node|awk -F 'ξ' '{print $2}')
  local passwd=$(echo $node|awk -F 'ξ' '{print $3}')
  if [[ "$host" == "" || "$user" == "" ]]; then
    logErr "[$appName/cluster] Failed to deploy backend, invalid cluster node info, host/user is required! host: $host, user: $user, password: $passwd"; exit -1
  fi
  # Gets build info.
  local appName=$(echo "$buildModule"|awk -F ',' '{print $1}')
  local appPort=$(echo "$buildModule"|awk -F ',' '{print $2}')
  if [ -z "$appName" ]; then
    logErr "Failed to deploy, appName is required! all args: '$@'"; exit -1
  fi
  local buildTargetDir=$(echo "$buildModule"|awk -F ',' '{print $4}')
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
  local cmdRestart="sudo chmod -R 755 $deployAppBaseDir && [ -n $(command -v systemctl) ] && sudo systemctl restart ${appName} || su - $appName -c \"/etc/init.d/${appName}.service restart\""

  if [ "$runtimeMode" == "standalone" ]; then # The 'standalone' mode is only deployed to the local host
    log "[$appName:$appPort/standalone] >>> Deploying standalone to local ..."
    deployStandaloneToLocal "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName" "$springProfilesActive"
    log "[$appName:$appPort/standalone] <<< Deployed standalone to local completed."
  elif [ "$runtimeMode" == "cluster" ]; then # The 'cluster' mode is deployed to the remote hosts
    log "[$appName:$appPort/cluster/$host] >>> Deploying to cluster remote nodes ..."
    doDeployClusterToNode "$buildTargetDir/$buildFileName" "$buildFileName" "$cmdRestart" "$appName" "$springProfilesActive" "${host}" "${user}" "${passwd}"
    log "[$appName:$appPort/cluster/$host] <<< Deployed cluster to remote nodes completed."
  fi
}

# Deploy DoPaaS backend apps all.
function deployBackendAll() {
  # Check is skiped backend.
  if [ "$deployBackendSkip" == "true" ]; then
    log "Skiped for deploy backend application, You can set export deployBackendSkip='true' to skip deploying the backend!"; return 0
  fi

  # Deploying prepare services.
  log "Pulling and compile backend project sources ..."
  deployZookeeperServers
  pullAndMvnCompile "$gitXCloudComponentProjectName" "$gitXCloudComponentUrl" "$gitComponentBranch"
  deployEurekaServers
  pullAndMvnCompile "$gitXCloudIamProjectName" "$gitXCloudIamUrl" "$gitIamBranch"
  pullAndMvnCompile "$gitXCloudDoPaaSProjectName" "$gitXCloudDoPaaSUrl" "$gitDoPaaSBranch"
  deployNginxServers

  # Gets apps modules.
  local deployBuildModulesSize=0
  if [ "$runtimeMode" == "standalone" ]; then
    local deployBuildModules=("${deployStandaloneBuildModules[@]}") # Copy build targets array
  elif [ "$runtimeMode" == "cluster" ]; then # The 'cluster' mode is deploy to the remote hosts
    local deployBuildModules=("${deployClusterBuildModules[@]}") # Copy build targets array
  else
    logErr "Invalid config runtime mode: $runtimeMode"; exit -1
  fi
  local deployBuildModulesSize=${#deployBuildModules[@]}
  if [ $deployBuildModulesSize -le 0 ]; then
    log "No modules found, skip deploy to remote nodes!"; return 0
  fi

  # Add backend app deployed summary.
  for ((i=0;i<${#deployBuildModules[@]};i++)); do
    local buildModule=${deployBuildModules[i]}
    local appName=$(echo "$buildModule"|awk -F ',' '{print $1}')
    globalDeployStatsMsg="${globalDeployStatsMsg}\n
[${appName}]:
          Install Home: ${deployAppBaseDir}/${appName}-package/${appName}-${buildPkgVersion}-bin/
            Config Dir: ${deployAppBaseDir}/${appName}-package/${appName}-${buildPkgVersion}-bin/conf/
       Profiles Active: ${springProfilesActive}
              PID File: /mnt/disk1/${appName}/${appName}.pid
       Restart Command: sudo systemctl restart ${appName}    or   su - ${appName} -c \"/etc/init.d/${appName}.service restart\"
             Logs File: ${deployAppLogBaseDir}/${appName}/${appName}_${springProfilesActive}.log
        Instance Hosts: ${globalAllHostsString}"
  done

  # Deploying backend apps.
  # a. 对n个节点并发执行部署, n节点就fork n个进程并行执行; 
  # b. 对于每个节点每次只并行部署concurrent个app, 这是因为远程频繁新建ssh连接时会触发sshd拒绝连接限制的错误:ssh_exchange_identification: read: Connection reset by peer
  # c. 暂时不对n个节点并发执行做限制, 通常4C/16G/100Mbps并行执行上几百个任务问题不大, 若上千节点部署可能出现性能问题, 可使用saltstack/chenf等工具.
  for node in ${globalAllNodes[@]}; do # issue: https://blog.csdn.net/mdx20072419/article/details/103901329
    local host=$(echo $node|awk -F 'ξ' '{print $1}')
    {
      log "[$host] ** Deploying backend apps on $host ..."
      # Define.
      local concurrent="$deployConcurrent"
      local pfileFD="$RANDOM" # 需多次调用, 使用shell内置random函数, 范围:[0-32767)
      local pfile="${workspaceDir}/${pfileFD}.fifo"
      # Make FIFO FD.
      [ ! -p "$pfile" ] && mkfifo $pfile
      eval "exec $pfileFD<>$pfile"
      \rm -f $pfile
      # Init FD
      eval "for ((i=0;i<${concurrent};i++)); do echo ; done >&${pfileFD}"
      # Exec deploying
      for ((i=0;i<${#deployBuildModules[@]};i++)); do
        local buildModule=${deployBuildModules[i]}
        eval "read -u${pfileFD}" # 每次读一个, 读完一个就少一个(fifo队列)
        {
          doDeployBackendApp "$buildModule" "$springProfilesActive" "$node"
          eval "echo >&${pfileFD}" # 每执行完一个task, 继续又添加一个换行符, 目的是为了永远保持fd里有concurrent个标识, 直到任务执行完.
        } &
      done
     wait
     log "[$host] ** Deployed backend apps on $host completed."
     eval "exec ${pfileFD}>&-" # 关闭fd
    } &
  done
  wait
  log "* Deployed backend all apps completed."
  return 0
}

# Check deploy nginx servers.
function deployNginxServers() {
  local node=${globalAllNodes[0]} # First node deploy the nginx by default.
  local host=$(echo $node|awk -F 'ξ' '{print $1}')
  local user=$(echo $node|awk -F 'ξ' '{print $2}')
  local passwd=$(echo $node|awk -F 'ξ' '{print $3}')
  # Add nginx deployed summary.
  globalDeployStatsMsg="${globalDeployStatsMsg}\n
[nginx]:
          Install Home: /etc/nginx/
            Config Dir: /etc/nginx/nginx.conf or /etc/nginx/conf.d/
       Profiles Active: 
              PID File: /run/nginx.pid\
       Restart Command: sudo systemctl restart nginx or /etc/init.d/nginx.service restart
             Logs File: /var/log/nginx/access.log or /var/log/nginx/error.log
        Instance Hosts: $host"

  {
    # Check install nginx.
    local checkRemoteNginxResult=$(doRemoteCmd "$user" "$passwd" "$host" "command -v nginx" "true" "true")
    if [ -z "$checkRemoteNginxResult" ]; then
      local osType=$(getOsTypeAndCheck)
      if [ "$deployNetworkMode" == "extranet" ]; then
        log "Online installing nginx to $host ..."
        local scriptFilename="install-nginx.sh"
        doScp "$user" "$passwd" "$host" "$currDir/$scriptFilename" "/tmp/$scriptFilename" "true"
        doRemoteCmd "$user" "$passwd" "$host" "chmod +x /tmp/$scriptFilename && bash /tmp/$scriptFilename" "true" "true"
      elif [ "$deployNetworkMode" == "intranet" ]; then
        log "Offline installing nginx to $host ..."
        local tmpNgxTarFile="$workspaceDir/nginx-current-bin.tar.gz"
        if [ "$osType" == "centos6_x64" ]; then
          downloadFile "$localNgxDownloadUrlForCentos6x64" "$tmpNgxTarFile"
        elif [ "$osType" == "centos7_x64" ]; then
          downloadFile "$localNgxDownloadUrlForCentos7x64" "$tmpNgxTarFile"
        elif [ "$osType" == "centos8_x64" ]; then
          downloadFile "$localNgxDownloadUrlForCentos8x64" "$tmpNgxTarFile"
        elif [ "$osType" == "ubuntu_x64" ]; then
          downloadFile "$localNgxDownloadUrlForUbuntu20x64" "$tmpNgxTarFile"
        fi
        # Installing to remote.
        doScp "$user" "$passwd" "$host" "$tmpNgxTarFile" "/tmp/nginx-current-bin.tar.gz" "true"
        doRemoteCmd "$user" "$passwd" "$host" "cd /tmp && tar -zxf nginx-current-bin.tar.gz && cd nginx-* && chmod +x install.sh && ./install.sh" "true" "true"
      else
        logErr "Invalid deployNetworkMode is '$deployNetworkMode' !"; exit -1
      fi
    fi
    # Configure nginx configuration and install.
    log "Configuring the nginx configuration file of dopaas services ..."
    cd $workspaceDir && rm -rf nginx && cp -r $currDir/$gitXCloudDoPaaSProjectName/nginx .
    makeNginxConf "$workspaceDir/nginx/conf.d/dopaas.conf" "$springProfilesActive" "${globalAllNodes[*]}" 
    cd nginx && tar -cf nginxconf.tar *
    doScp "$user" "$passwd" "$host" "$workspaceDir/nginx/nginxconf.tar" "/etc/nginx/" "true"
    doRemoteCmd "$user" "$passwd" "$host" "cd /etc/nginx/ && tar --overwrite-dir --overwrite -xf nginxconf.tar && rm -rf nginxconf.tar && rm -rf conf.d/example*" "true" "true"
  } &
}

# Check deploy eureka servers.
function deployEurekaServers() {
  if [ "$runtimeMode" == "cluster" ]; then
    log "Deploying eureka servers ..."
    local appName=$(echo "$deployEurekaBuildModule"|awk -F ',' '{print $1}')
    if [ ${#globalAllNodes[@]} -lt 3 ]; then # Building pseudo cluster.
      local springProfilesActive="ha,peer1"
      local node1=${globalAllNodes[0]}
      local host1=$(echo $node1|awk -F 'ξ' '{print $1}')
      local user1=$(echo $node1|awk -F 'ξ' '{print $2}')
      local passwd1=$(echo $node1|awk -F 'ξ' '{print $3}')
      # Node1:
      log "[eureka/$host1] Deploying eureka($springProfilesActive) (Disguised) ..."
      # Add eureka-server deployed summary.
      globalDeployStatsMsg="${globalDeployStatsMsg}\n
[${appName}]:
          Install Home: ${deployAppBaseDir}/${appName}-package/${appName}-${buildPkgVersion}-bin/
            Config Dir: ${deployAppBaseDir}/${appName}-package/${appName}-${buildPkgVersion}-bin/conf/
       Profiles Active: ${springProfilesActive}
              PID File: /mnt/disk1/${appName}/${appName}.pid
       Restart Command: sudo systemctl restart $appName or /etc/init.d/$appName.service restart
             Logs File: ${deployAppLogBaseDir}/${appName}/${appName}.log
        Instance Hosts: $host1"
      doDeployBackendApp "$deployEurekaBuildModule" "$springProfilesActive" "$node1" &
      # Adding DNS to nodes.
      addPeersHosts "$host1" "$user1" "$passwd1" "$host1" "$host1" "$host1" &
    else # Building a real cluster.
      # Node1:
      local node1=${globalAllNodes[0]}
      local host1=$(echo $node1|awk -F 'ξ' '{print $1}')
      local user1=$(echo $node1|awk -F 'ξ' '{print $2}')
      local passwd1=$(echo $node1|awk -F 'ξ' '{print $3}')
      log "[eureka/$host1] Deploy eureka by peer1 ..."
      doDeployBackendApp "$deployEurekaBuildModule" "ha,peer1" "$node1" &
      # Node2:
      local node2=${globalAllNodes[1]}
      local host2=$(echo $node2|awk -F 'ξ' '{print $1}')
      local user2=$(echo $node2|awk -F 'ξ' '{print $2}')
      local passwd2=$(echo $node2|awk -F 'ξ' '{print $3}')
      log "[eureka/$host2] Deploy eureka by peer2 ..."
      doDeployBackendApp "$deployEurekaBuildModule" "ha,peer2" "$node2" &
      # Node3:
      local node3=${globalAllNodes[2]}
      local host3=$(echo $node3|awk -F 'ξ' '{print $1}')
      local user3=$(echo $node3|awk -F 'ξ' '{print $2}')
      local passwd3=$(echo $node3|awk -F 'ξ' '{print $3}')
      log "[eureka/$host3] Deploy eureka by peer3 ..."
      doDeployBackendApp "$deployEurekaBuildModule" "ha,peer3" "$node3" &
      # Add eureka-server deployed summary.
      globalDeployStatsMsg="${globalDeployStatsMsg}\n
[${appName}]:
          Install Home: ${deployAppBaseDir}/${appName}-package/${appName}-${buildPkgVersion}-bin/
            Config Dir: ${deployAppBaseDir}/${appName}-package/${appName}-${buildPkgVersion}-bin/conf/
       Profiles Active: ${springProfilesActive}
              PID File: /mnt/disk1/${appName}/${appName}.pid
       Restart Command: sudo systemctl restart $appName or /etc/init.d/$appName.service restart
             Logs File: ${deployAppLogBaseDir}/${appName}/${appName}.log
        Instance Hosts: ${host1}, ${host2}, ${host3}"
      # Adding DNS to nodes.
      {
        addPeersHosts "$host1" "$user1" "$passwd1" "$host1" "$host2" "$host3"
        addPeersHosts "$host2" "$user2" "$passwd2" "$host1" "$host2" "$host3"
        addPeersHosts "$host3" "$user3" "$passwd3" "$host1" "$host2" "$host3"
      } &
    fi
  else # In standalone mode, Eureka does not need to be deployed.
    log "Skip eureka servers deploy, because runtime mode is standalone."
  fi
}

# Check deploy zookeeper servers.
function deployZookeeperServers() {
  if [ "$runtimeMode" == "cluster" ]; then
    log "Deploying zookeeper servers ..."
    # Download package.
    local tmpZkTarFile="$workspaceDir/zookeeper.tar.gz"
    if [ ! -f "$tmpZkTarFile" ]; then
      if [ "$deployNetworkMode" == "extranet" ]; then
        if [ "$isChinaLANNetwork" == "N" ]; then
          downloadFile "$zkDownloadUrl" "$tmpZkTarFile" "180"
        else
          downloadFile "$secondaryZkDownloadUrl" "$tmpZkTarFile" "180"
        fi
      elif [ "$deployNetworkMode" == "intranet" ]; then
        downloadFile "$localZkDownloadUrl" "$tmpZkTarFile"
      else
        logErr "Invalid deployNetworkMode is '$deployNetworkMode' !"; exit -1
      fi
    fi
    # Deploying zookeeper servers.
    if [ ${#globalAllNodes[@]} -lt 3 ]; then # Building pseudo cluster.
      local node1=${globalAllNodes[0]}
      local host1=$(echo $node1|awk -F 'ξ' '{print $1}')
      local user1=$(echo $node1|awk -F 'ξ' '{print $2}')
      local passwd1=$(echo $node1|awk -F 'ξ' '{print $3}')
      # Node1:
      # Add zookeeper deployed summary.
      log "[zookeeper/$host1] Deploying zookeeper for peer1 (Single) ..."
      globalDeployStatsMsg="${globalDeployStatsMsg}\n
[zookeeper]:
          Install Home: ${zkHome}
            Config Dir: ${zkHome}/conf/
              PID File: /tmp/zookeeper/zookeeper.pid
       Restart Command: sudo ${zkHome}/bin/zkServer.sh restart
              Logs Dir: ${deployAppLogBaseDir}/zookeeper/
        Instance Hosts: $host1"
      {
        doScp "$user1" "$passwd1" "$host1" "$tmpZkTarFile" "/tmp/" "true"
        doRemoteCmd "$user1" "$passwd1" "$host1" "mkdir -p $zkHome && rm -rf $zkHome/* && cd /tmp && tar -xf zookeeper.tar.gz --strip-components=1 -C $zkHome" "false" "true"
        # fixed example: bin/zkServer.sh: line 213: kill: (3913)
        doRemoteCmd "$user1" "$passwd1" "$host1" "export ZOO_LOG_DIR=${deployAppLogBaseDir}/zookeeper && mkdir -p $ZOO_LOG_DIR && cd $zkHome/conf && cp zoo_sample.cfg zoo.cfg && echo 'admin.serverPort=18887' >> zoo.cfg && ../bin/zkServer.sh restart" "false" "true"
        # Adding DNS to nodes.
        addPeersHosts "$host1" "$user1" "$passwd1" "$host1" "$host1" "$host1"
      } &
    else # Building a real cluster.
      # Make zoo.cfg template.
      local tmpZooCfgFile="$workspaceDir/zoo.cfg"
      cat<<EOF>$tmpZooCfgFile
tickTime=2000
initLimit=10
syncLimit=5
dataDir=/mnt/disk1/zookeeper/
clientPort=2181
maxClientCnxns=500
autopurge.snapRetainCount=3
autopurge.purgeInterval=1
admin.serverPort=18887
metricsProvider.className=org.apache.zookeeper.metrics.prometheus.PrometheusMetricsProvider
metricsProvider.httpPort=7000
metricsProvider.exportJvmInfo=true
server.1=peer1:2888:3888
server.2=peer2:2888:3888
server.3=peer3:2888:3888
EOF
      # Node1:
      local node1=${globalAllNodes[0]}
      local host1=$(echo $node1|awk -F 'ξ' '{print $1}')
      local user1=$(echo $node1|awk -F 'ξ' '{print $2}')
      local passwd1=$(echo $node1|awk -F 'ξ' '{print $3}')
      log "[zookeeper/$host1] Deploying zookeeper for peer1 ..."
      {
        doScp "$user1" "$passwd1" "$host1" "$tmpZkTarFile" "/tmp/" "true"
        doRemoteCmd "$user1" "$passwd1" "$host1" "mkdir -p $zkHome && rm -rf $zkHome/* && cd /tmp && tar -xf zookeeper.tar.gz --strip-components=1 -C $zkHome" "false" "true"
        doScp "$user1" "$passwd1" "$host1" "$tmpZooCfgFile" "$zkHome/conf/" "true"
        # fixed example: bin/zkServer.sh: line 213: kill: (3913)
        doRemoteCmd "$user1" "$passwd1" "$host1" "export ZOO_LOG_DIR=${deployAppLogBaseDir}/zookeeper && mkdir -p $ZOO_LOG_DIR && mkdir -p /mnt/disk1/zookeeper && echo 1 >/mnt/disk1/zookeeper/myid && $zkHome/bin/zkServer.sh restart" "false" "true"
      } &
      # Node2:
      local node2=${globalAllNodes[1]}
      local host2=$(echo $node2|awk -F 'ξ' '{print $1}')
      local user2=$(echo $node2|awk -F 'ξ' '{print $2}')
      local passwd2=$(echo $node2|awk -F 'ξ' '{print $3}')
      log "[zookeeper/$host2] Deploying zookeeper for peer2 ..."
      {
        doScp "$user2" "$passwd2" "$host2" "$tmpZkTarFile" "/tmp/" "true"
        doRemoteCmd "$user2" "$passwd2" "$host2" "mkdir -p $zkHome && rm -rf $zkHome/* && cd /tmp && tar -xf zookeeper.tar.gz --strip-components=1 -C $zkHome" "false" "true"
        doScp "$user2" "$passwd2" "$host2" "$tmpZooCfgFile" "$zkHome/conf/" "true"
        # fixed example: bin/zkServer.sh: line 213: kill: (3913)
        doRemoteCmd "$user2" "$passwd2" "$host2" "export ZOO_LOG_DIR=${deployAppLogBaseDir}/zookeeper && mkdir -p $ZOO_LOG_DIR && mkdir -p /mnt/disk1/zookeeper && echo 2 >/mnt/disk1/zookeeper/myid && $zkHome/bin/zkServer.sh restart" "false" "true"
      } &
      # Node3:
      local node3=${globalAllNodes[2]}
      local host3=$(echo $node3|awk -F 'ξ' '{print $1}')
      local user3=$(echo $node3|awk -F 'ξ' '{print $2}')
      local passwd3=$(echo $node3|awk -F 'ξ' '{print $3}')
      log "[zookeeper/$host3] Deploying zookeeper for peer3 ..."
      {
        doScp "$user3" "$passwd3" "$host3" "$tmpZkTarFile" "/tmp/" "true"
        doRemoteCmd "$user3" "$passwd3" "$host3" "mkdir -p $zkHome && rm -rf $zkHome/* && cd /tmp && tar -xf zookeeper.tar.gz --strip-components=1 -C $zkHome" "false" "true"
        doScp "$user3" "$passwd3" "$host3" "$tmpZooCfgFile" "$zkHome/conf/" "true"
        # fixed example: bin/zkServer.sh: line 213: kill: (3913)
        doRemoteCmd "$user3" "$passwd3" "$host3" "export ZOO_LOG_DIR=${deployAppLogBaseDir}/zookeeper && mkdir -p $ZOO_LOG_DIR && mkdir -p /mnt/disk1/zookeeper && echo 3 >/mnt/disk1/zookeeper/myid && $zkHome/bin/zkServer.sh restart" "false" "true"
      } &
      # Add zookeeper deployed summary.
      globalDeployStatsMsg="${globalDeployStatsMsg}\n
[zookeeper]:
          Install Home: ${zkHome}
            Config Dir: ${zkHome}/conf/
              PID File: /mnt/disk1/zookeeper/data/zookeeper.pid
       Restart Command: sudo ${zkHome}/bin/zkServer.sh restart
              Logs Dir: ${deployAppLogBaseDir}/zookeeper/
        Instance Hosts: ${host1}, ${host2}, ${host3}"
      # Adding DNS to nodes.
      {
        addPeersHosts "$host1" "$user1" "$passwd1" "$host1" "$host2" "$host3"
        addPeersHosts "$host2" "$user2" "$passwd2" "$host1" "$host2" "$host3"
        addPeersHosts "$host3" "$user3" "$passwd3" "$host1" "$host2" "$host3"
      } &
    fi
  else # In standalone mode, Eureka does not need to be deployed.
    log "Skip zookeeper servers deploy, because runtime mode is standalone."
  fi
}

# Check configure regcenter dns.
# for example: addPeersHosts "10.0.0.162" "root" "123456" "127.0.0.1" "127.0.0.1" "127.0.0.1"
function addPeersHosts() {
  local host=$1
  local user=$2
  local passwd=$3
  local h1=$4
  local h2=$5
  local h3=$6
  if [[ -z "$host" || -z "$user" || -z "$passwd" || -z "$h1" || -z "$h2" || -z "$h3" ]]; then
    logErr "Adding peers args host/user/passwd/h1/h2/h3 is requires !"; exit -1
  fi
  cd $workspaceDir
  local tmpBashFilename="tmp-add-peers-hosts.sh"
cat<<EOF>$tmpBashFilename
#!/bin/bash
host1=$h1
host2=$h2
host3=$h3
# Exchage host to IP (if necessary).
[ -z "\$(echo \$host1|egrep '(^[0-9]+)\.')" ] && host1=\$(ping \$host1 -c 1 -w 3 2>/dev/null|sed '1{s/[^(]*(//;s/).*//;q}')
[ -z "\$(echo \$host2|egrep '(^[0-9]+)\.')" ] && host2=\$(ping \$host2 -c 1 -w 3 2>/dev/null|sed '1{s/[^(]*(//;s/).*//;q}')
[ -z "\$(echo \$host3|egrep '(^[0-9]+)\.')" ] && host3=\$(ping \$host3 -c 1 -w 3 2>/dev/null|sed '1{s/[^(]*(//;s/).*//;q}')
# Check mapped?
resolvingPeer1=\$(ping -c 1 -w 3 peer1 >/dev/null 2>&1; echo "\$?")
resolvingPeer2=\$(ping -c 1 -w 3 peer2 >/dev/null 2>&1; echo "\$?")
resolvingPeer3=\$(ping -c 1 -w 3 peer3 >/dev/null 2>&1; echo "\$?")
if [[ \$resolvingPeer1 != 0 || \$resolvingPeer2 != 0 || \$resolvingPeer3 != 0 ]]; then
  echo "# Auto generated by DoPaaS Deployer(host)" >> /etc/hosts
  echo "\$host1 peer1" >> /etc/hosts
  echo "\$host2 peer2" >> /etc/hosts
  echo "\$host3 peer3" >> /etc/hosts
  echo "Added dns resolution for: peer1 -> $host1, peer2 -> $host2, peer3 -> $host3"
else
  echo "Already DNS resolution for peer1,peer2,peer3 !"
fi
EOF
  sudo chmod +x $workspaceDir/$tmpBashFilename
  doScp "$user" "$passwd" "$host" "$workspaceDir/$tmpBashFilename" "/tmp/" "true"
  doRemoteCmd "$user" "$passwd" "$host" "sudo chmod +x /tmp/$tmpBashFilename && sudo /tmp/$tmpBashFilename" "true" "true"
  log "Finished dns resolution for: peer1 -> $h1, peer2 -> $h2, peer3 -> $h3"
}

# Deploy frontend apps to nginx html dir.
function deployFrontendAll() {
  # Check is skiped frontend.
  if [ "$deployFrontendSkip" == "true" ]; then
    log "Skiped for deploy frontend application, You can set export deployFrontendSkip='true' to skip deploying the frontend!"; return 0
  fi
  local appName="$gitXCloudDoPaaSViewProjectName"
  local appInstallDir="${deployFrontendAppBaseDir}/${appName}-package"
  local node=${globalAllNodes[0]} # First node deploy the nginx by default.
  local host=$(echo $node|awk -F 'ξ' '{print $1}')
  local user=$(echo $node|awk -F 'ξ' '{print $2}')
  local passwd=$(echo $node|awk -F 'ξ' '{print $3}')
  if [[ "$host" == "" || "$user" == "" ]]; then
    logErr "[$appName] Failed to deploy frontend, invalid cluster node info, host/user is required! host: $host, user: $user, password: $passwd"; exit -1
  fi
  {
    log "Deploying of dopaas $appName ..."
    # Pull frontend.
    pullSources "$gitXCloudDoPaaSViewProjectName" "$gitXCloudDoPaaSViewUrl" "$gitDoPaaSViewBranch"
    # Compile frontend.
    if [[ $? == 1 || "$buildForcedOnPullUpToDate" == 'true' ]]; then
      log "Compiling $cmdNpm $gitXCloudDoPaaSViewProjectName ..."
      sudo $cmdNpm install 2>&1 | tee -a $logFile
      sudo $cmdNpm run build 2>&1 | tee -a $logFile
      [ ${PIPESTATUS[0]} -ne 0 ] && exit -1
    fi

    # Deploy frontend.
    local deployFrontendDir="${appInstallDir}/${appName}-${buildPkgVersion}-bin"
    local fProjectDir="$currDir/$gitXCloudDoPaaSViewProjectName"
    # Check build dist files.
    if [[ ! -d "$fProjectDir" || "$(ls $fProjectDir/dist/*|wc -l)" -le 0 ]]; then
      logErr "Cannot reading frontend build assets, because dist directory not exists!"; exit -1 
    fi
    cd $fProjectDir && tar -zcf dist.tar.gz dist/
    doRemoteCmd "$user" "$passwd" "$host" "mkdir -p $deployFrontendDir && \rm -rf $deployFrontendDir/*" "true" "true"
    log "[$gitXCloudDoPaaSViewProjectName/$host] Transfer frontend assets to $deployFrontendDir ..."
    doScp "$user" "$passwd" "$host" "$fProjectDir/dist.tar.gz" "$deployFrontendDir" "true"
    doRemoteCmd "$user" "$passwd" "$host" "cd $deployFrontendDir && tar -zxf dist.tar.gz --strip-components=1 && rm -rf dist.tar.gz && chmod 755 -R $deployFrontendDir" "true" "true"
    # Restart nginx(first install).
    doRemoteCmd "$user" "$passwd" "$host" "[ -n \"$(echo command -v systemctl)\" ] && sudo systemctl restart nginx || /etc/init.d/nginx.service restart" "true" "true"
    [ $? -ne 0 ] && exit -1
  } &
}

# ----- Main call. -----
function main() {
  if [ "$(getOsTypeAndCheck)" == "_" ]; then
    echo "Unsupported current OS, only CentOS 6/CentOS 7/CentOS 8/Ubuntu is supported for the time being!"; exit -1
  fi
  [ -n "$(command -v clear)" ] && clear # e.g centos8+ not clear
  log ""
  log "「 Welcome to XCloud DoPaaS Deployer (Host) 」"
  log "  ___       ___            ___ "
  log " | . \ ___ | . \ ___  ___ / __>"
  log " | | |/ . \|  _/<_> |<_> |\__ \\"
  log " |___/\___/|_|  <___|<___|<___/"
  log ""
  log " Wiki: https://github.com/wl4g/xcloud-dopaas/blob/master/README.md"
  log " Wiki(CN): https://gitee.com/wl4g/xcloud-dopaas/blob/master/README_CN.md"
  log " Authors: <Wanglsir@gmail.com, 983708408@qq.com>"
  log " Version: 2.0.0"
  log " Time: $(date '+%Y-%m-%d %H:%M:%S')"
  log " Installation logs writing: $logFile"
  log " -------------------------------------------------------------------"
  log ""
  beginTime=`date +%s`
  checkInstallInfraSoftware
  initConfiguration
  deployFrontendAll
  deployBackendAll
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
