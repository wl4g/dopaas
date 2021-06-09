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

function doCommandAll() {
  local cmd=$1
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
          if [ "$asyncDeploy" == "true" ]; then
            doRemoteCmd "$user" "$passwd" "$host" "[ -n \"$(command -v systemctl)\" ] && systemctl ${cmd} ${appName} || /etc/init.d/${appName}.service ${cmd}" "false" "true" &
          else
            doRemoteCmd "$user" "$passwd" "$host" "[ -n \"$(command -v systemctl)\" ] && systemctl ${cmd} ${appName} || /etc/init.d/${appName}.service ${cmd}" "false" "true"
          fi
        done
      } &
    done
    [ "$asyncDeploy" == "true" ] && wait
  fi
}

# ----- Main call. -----
case $1 in
  status-all)
    doCommandAll "status"
    ;;
  start-all)
    doCommandAll "start"
    ;;
  stop-all)
    doCommandAll "stop"
    ;;
  restart-all)
    doCommandAll "restart"
    ;;
    *)
  echo $"Usage: {start-all|stop-all|restart-all|status-all}"
  exit 2
esac
