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
function removeAllAppResources() {
  local deployBuildTargetsLen=0
  if [ "$runtimeMode" == "standalone" ]; then
    deployBuildTargets=("${deployStandaloneBuildTargets[@]}") # Copy build targets array
  elif [ "$runtimeMode" == "cluster" ]; then # The 'cluster' mode is deploy to the remote hosts
    deployBuildTargets=("${deployClusterBuildTargets[@]}") # Copy build targets array
  else
    logErr "Invalid config runtimeMode: $runtimeMode"; exit -1
  fi
  deployBuildTargetsLen=${#deployBuildTargets[@]}
  if [ $deployBuildTargetsLen -gt 0 ]; then
    for ((i=0;i<${#deployBuildTargets[@]};i++)) do
      local buildTargetDir=${deployBuildTargets[i]}
      local buildFileName=$(ls -a "$buildTargetDir"|grep -E "*-${buildPkgVersion}-bin.tar|*-${buildPkgVersion}-bin.jar")
      if [ -z "$buildFileName" ]; then
         logErr "Failed to read build assets from target direct: $buildTargetDir"; exit -1
      fi
      local appName=$(echo "$(basename $buildFileName)"|awk -F "-${buildPkgVersion}-bin.tar|-${buildPkgVersion}-bin.jar" '{print $1}')
      # Remove install resources.
      local appHome="$deployBaseDir/$appName-package/$appName-$buildPkgVersion-bin"
      local appDataBaseDir="/mnt/disk1/$appName"
      local appLogDir="/mnt/disk1/log/$appName"
      local appServiceFile="/etc/init.d/$appName.service"
      if [ -d "$appHome" ]; then
        log "Removing directory $appHome"
        secDeleteLocal "$appHome"
      fi
      if [ -d "$appDataBaseDir" ]; then
        log "Removing directory $appDataBaseDir"
        secDeleteLocal "$appDataBaseDir"
      fi
      if [ -d "$appLogDir" ]; then
        log "Removing directory $appLogDir"
        secDeleteLocal "$appLogDir"
      fi
      if [ -f "$appServiceFile" ]; then
        log "Removing file $appServiceFile"
        secDeleteLocal "$appServiceFile"
      fi
    done
  fi
}

beginTime=`date +%s`

removeTmpApacheMaven
removeAllAppResources

deployStatus=$([ $? -eq 0 ] && echo "SUCCESS" || echo "FAILURE")
costTime=$[$(echo `date +%s`)-$beginTime]
log "-------------------------------------------------------------------"
log "UNINSTALL $deployStatus"
log "-------------------------------------------------------------------"
log "Total time: ${costTime} sec (Wall Clock)"
log "Finished at: $(date -d today +'%Y-%m-%d %H:%M:%S')"
log "Installing details logs see: $logFile"
log "-------------------------------------------------------------------"
