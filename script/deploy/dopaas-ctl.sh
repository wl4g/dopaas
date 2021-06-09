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
log "「 Welcome to XCloud DoPaaS Ctl (Host) 」"
log ""
log " Wiki: https://github.com/wl4g/xcloud-dopaas/blob/master/README.md"
log " Wiki(CN): https://gitee.com/wl4g/xcloud-dopaas/blob/master/README_CN.md"
log " Authors: <Wanglsir@gmail.com, 983708408@qq.com>"
log " Version: 2.0.0"
log " Time: $(date '+%Y-%m-%d %H:%M:%S')"
log " Installation logs writing: $logFile"
log " -----------------------------------------------------------------------"
log ""

function doCommandApps() {
  local cmd=$1 # Requires
  local targetAppName=$2 # Optional
  [[ -z "$cmd" ]] && logErr "Do ctl command is requires!" && exit -1

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
      local appPort=$(echo "$buildModule"|awk -F ',' '{print $2}')
      # Matching target appName. (if necessary)
      if [[ -n "$targetAppName" && "$targetAppName" != "$appName" ]]; then
        continue
      fi
      # Exec remote commands to nodes of appName.
      {
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
            logErr "[$appName:$appPort/cluster] Invalid cluster node info, host/user is required! host: $host, user: $user, password: $passwd"; exit -1
          fi
          log "[$appName:$appPort/$host] Manage ($cmd) on $host ..." 
          if [ "$deployAsync" == "true" ]; then
            doRemoteCmd "$user" "$passwd" "$host" "[ -n \"$(command -v systemctl)\" ] && systemctl ${cmd} ${appName} || /etc/init.d/${appName}.service ${cmd}" "false" "true" &
          else
            doRemoteCmd "$user" "$passwd" "$host" "[ -n \"$(command -v systemctl)\" ] && systemctl ${cmd} ${appName} || /etc/init.d/${appName}.service ${cmd}" "false" "true"
          fi
        done
        [ "$deployAsync" == "true" ] && wait
      } &
    done
  fi
}

function doCommandZookeeper() {
  local cmd=$1
  [[ -z "$cmd" ]] && logErr "Do ctl command is requires!" && exit -1

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
      logErr "[zookeeper/$host] Invalid cluster node info, host/user is required! host: $host, user: $user, password: $passwd"; exit -1
    fi
    log "[zookeeper/$host] Manage ($cmd) on $host ..." 
    if [ "$deployAsync" == "true" ]; then
      doRemoteCmd "$user" "$passwd" "$host" "sudo ${zkHome}/bin/zkServer.sh ${cmd}" "false" "true" &
    else
      doRemoteCmd "$user" "$passwd" "$host" "sudo ${zkHome}/bin/zkServer.sh ${cmd}" "false" "true"
    fi
  done
  [ "$deployAsync" == "true" ] && wait
}

function waitForComplete() {
  [ "$deployAsync" == "true" ] && wait
  log "--------------------------------------------------------------------"
  log "Execution $1 to all nodes finished !"
  exit 0
}

function usage() {
  echo "
Usage: {start-all|stop-all|restart-all|status-all|<appName> <start|stop|restart|status>}
  start-all        Start all remote nodes apps.
  stop-all         stop all remote nodes apps.
  restart-all      Restart all remote nodes apps.
  status-all       Query status all remote nodes apps.
  <appName> <start|stop|restart|status>  For example: \$ cmdb-facade restart
    Restart only the cmdb-facade application of all remote nodes,
    The optionals are: zookeeper/eureka-server/iam-web/iam-facade/iam-data/cmdb-facade/cmdb-manager/... etc.
    "
}

# ----- Main call. -----
arg1=$1
arg2=$2
case $arg1 in
  help|-help|--help|-h)
    usage
    ;;
  status-all)
    doCommandApps "status"
    doCommandZookeeper "status"
    waitForComplete "status"
    ;;
  start-all)
    doCommandApps "start"
    doCommandZookeeper "start"
    waitForComplete "start"
    ;;
  stop-all)
    doCommandApps "stop"
    doCommandZookeeper "stop"
    waitForComplete "stop"
    ;;
  restart-all)
    doCommandApps "restart"
    doCommandZookeeper "restart"
    waitForComplete "restart"
    ;;
  *)
    if [[ -z "$arg1" || -z "$arg2" ]]; then
      usage; exit -1
    fi
    if [ "$arg1" == "zookeeper" ]; then
      doCommandZookeeper "$arg2"
    else
      doCommandApps "$arg2" "$arg1"
    fi
    waitForComplete "$arg1 $arg2"
    ;;
    *)
  exit -1
esac
