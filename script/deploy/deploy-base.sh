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

# ----------------------- Initialization. --------------------------------------------------------------------
[ -z "$currDir" ] && export currDir=$(cd "`dirname $0`"/ ; pwd)
. $currDir/deploy-env.sh
. $currDir/deploy-logging.sh

# ----------------------- Basic environment configuration. ---------------------------------------------------

[ -z "$workspaceDir" ] && export workspaceDir="${HOME}/.deploy-workspace" && mkdir -p $workspaceDir
currDate=$(date -d today +"%Y-%m-%d_%H%M%S")
[ -z "$logFile" ] && export logFile="${workspaceDir}/install_${currDate}.log" && touch $logFile
[ -z "$deployDebug" ] && export deployDebug="false" # true|false
[ -z "$deployAsync" ] && export deployAsync="true" # true|false

# ----------------------- Sources(Git) environment configuration. --------------------------------------------

# Git clone URLs definition.
[ -z "$gitBaseUri" ] && export gitBaseUri=$([ "$isChinaLANNetwork" == "Y" ] && echo "https://gitee.com/wl4g" || echo "https://github.com/wl4g") # For speed-up, fuck!
[ -z "$gitXCloudComponentUrl" ] && export gitXCloudComponentUrl="${gitBaseUri}/xcloud-component"
[ -z "$gitXCloudIamUrl" ] && export gitXCloudIamUrl="${gitBaseUri}/xcloud-iam"
[ -z "$gitXCloudDoPaaSUrl" ] && export gitXCloudDoPaaSUrl="${gitBaseUri}/xcloud-dopaas"
[ -z "$gitXCloudDoPaaSFrontendUrl" ] && export gitXCloudDoPaaSFrontendUrl="${gitBaseUri}/xcloud-dopaas-view"
# Git pull branchs definition.
[ -z "$gitDefaultBranch" ] && export gitDefaultBranch="master"
[ -z "$gitComponentBranch" ] && export gitComponentBranch="${gitDefaultBranch}"
[ -z "$gitIamBranch" ] && export gitIamBranch="${gitDefaultBranch}"
[ -z "$gitDoPaaSBranch" ] && export gitDoPaaSBranch="${gitDefaultBranch}"
[ -z "$gitDoPaaSFrontendBranch" ] && export gitDoPaaSFrontendBranch="${gitDefaultBranch}"

# ----------------------- Deployment(common) environment configuration. --------------------------------------

# Common build definition.
[ -z "$buildForcedOnPullUpToDate" ] && export buildForcedOnPullUpToDate="false"

# ----------------------- Deployment(backend) environment configuration. -------------------------------------

# Maven environment.
[ -z "$apacheMvnDownloadTarUrl" ] && export apacheMvnDownloadTarUrl="https://mirrors.bfsu.edu.cn/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz"
[ -z "$secondaryApacheMvnDownloadTarUrl" ] && export secondaryApacheMvnDownloadTarUrl="https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz"
[ -z "$apacheMvnInstallDir" ] && export apacheMvnInstallDir="$workspaceDir"
# You can customize the overlay. (e.g: mvn -Dmaven.repo.local=$HOME/.m2/repository/ -f $HOME/myproject_dir/pom.xml clean install)
[ -z "$apacheMvnLocalRepoDir" ] && export apacheMvnLocalRepoDir="$HOME/.m2/repository/"

# Maven build definition.
[ -z "$buildPkgType" ] && export buildPkgType="mvnAssTar" # Options: mvnAssTar|springExecJar
[ -z "$buildPkgVersion" ] && export buildPkgVersion="master"

# Deploy backend common definition.
[ -z "$runtimeMode" ] && export runtimeMode="cluster" # Options: standalone|cluster
[ -z "$deployAppBaseDir" ] && export deployAppBaseDir="/opt/apps/acm"
[ -z "$deployAppDataBaseDir" ] && export deployAppDataBaseDir="/mnt/disk1"
[ -z "$deployAppLogBaseDir" ] && export deployAppLogBaseDir="${deployAppDataBaseDir}/log"
[ -z "$deployForcedInstallMgtScript" ] && export deployForcedInstallMgtScript="true" # e.g: Forced installing to '/etc/init.d/iam-web.service'

# Deploy(eureka) backend modules defintion.
export deployEurekaBuildModule="eureka-server,${currDir}/xcloud-component/xcloud-component-integration/xcloud-component-integration-regcenter-eureka-server/target"

# Delopy(standalone mode) backend modules definition.
export deployStandaloneBuildModules=(
  "standalone-iam,${currDir}/xcloud-iam/xcloud-iam-service-starter-all/target"
  "standalone-dopaas,${currDir}/xcloud-dopaas/xcloud-dopaas-all-starter/target"
)

# Deploy(cluster mode) backend modules definition.
export deployClusterNodesConfigPath="$currDir/deploy-host.csv"
# for example: "{appName},{buildAssetsDir}"
export deployClusterBuildModules=(
  "iam-data,${currDir}/xcloud-iam/xcloud-iam-service-starter-data/target"
  "iam-facade,${currDir}/xcloud-iam/xcloud-iam-service-starter-facade/target"
  "iam-web,${currDir}/xcloud-iam/xcloud-iam-service-starter-web/target"
  "cmdb-facade,${currDir}/xcloud-dopaas/xcloud-dopaas-cmdb/xcloud-dopaas-cmdb-service-starter-facade/target"
  "cmdb-manager,${currDir}/xcloud-dopaas/xcloud-dopaas-cmdb/xcloud-dopaas-cmdb-service-starter-manager/target"
  "uci-facade,${currDir}/xcloud-dopaas/xcloud-dopaas-uci/xcloud-dopaas-uci-service-starter-facade/target"
  "uci-server,${currDir}/xcloud-dopaas/xcloud-dopaas-uci/xcloud-dopaas-uci-service-starter-server/target"
  "udm-facade,${currDir}/xcloud-dopaas/xcloud-dopaas-udm/xcloud-dopaas-udm-service-starter-facade/target"
  "udm-manager,${currDir}/xcloud-dopaas/xcloud-dopaas-udm/xcloud-dopaas-udm-service-starter-manager/target"
  "home-facade,${currDir}/xcloud-dopaas/xcloud-dopaas-home/xcloud-dopaas-home-service-starter-facade/target"
  "home-manager,${currDir}/xcloud-dopaas/xcloud-dopaas-home/xcloud-dopaas-home-service-starter-manager/target"
  "lcdp-facade,${currDir}/xcloud-dopaas/xcloud-dopaas-lcdp/xcloud-dopaas-lcdp-service-starter-facade/target"
  "lcdp-manager,${currDir}/xcloud-dopaas/xcloud-dopaas-lcdp/xcloud-dopaas-lcdp-service-starter-manager/target"
  #"ucm-facade,${currDir}/xcloud-dopaas/xcloud-dopaas-ucm/xcloud-dopaas-ucm-service-starter-facade/target"
  #"ucm-server,${currDir}/xcloud-dopaas/xcloud-dopaas-ucm/xcloud-dopaas-ucm-service-starter-server/target"
  "uds-facade,${currDir}/xcloud-dopaas/xcloud-dopaas-uds/xcloud-dopaas-uds-service-starter-facade/target"
  "uds-manager,${currDir}/xcloud-dopaas/xcloud-dopaas-uds/xcloud-dopaas-uds-service-starter-manager/target"
  "umc-collector,${currDir}/xcloud-dopaas/xcloud-dopaas-umc/xcloud-dopaas-umc-service-starter-collector/target"
  "umc-tracker,${currDir}/xcloud-dopaas/xcloud-dopaas-umc/xcloud-dopaas-umc-service-starter-tracker/target"
  "umc-facade,${currDir}/xcloud-dopaas/xcloud-dopaas-umc/xcloud-dopaas-umc-service-starter-facade/target"
  "umc-manager,${currDir}/xcloud-dopaas/xcloud-dopaas-umc/xcloud-dopaas-umc-service-starter-manager/target"
  "urm-facade,${currDir}/xcloud-dopaas/xcloud-dopaas-urm/xcloud-dopaas-urm-service-starter-facade/target"
  "urm-manager,${currDir}/xcloud-dopaas/xcloud-dopaas-urm/xcloud-dopaas-urm-service-starter-manager/target"
)

# ----------------------- Deployment(frontend) environment configuration. ------------------------------------

# NodeJS environment definition.
[ -z "$nodejsDownloadTarUrl" ] && export nodejsDownloadTarUrl="https://nodejs.org/dist/v14.16.1/node-v14.16.1-linux-x64.tar.xz"
[ -z "$secondaryNodejsDownloadTarUrl" ] && export secondaryNodejsDownloadTarUrl="https://nodejs.org/dist/v14.16.1/node-v14.16.1-linux-x64.tar.xz"
[ -z "$nodejsInstallDir" ] && export nodejsInstallDir="$workspaceDir"

# Deploy frontend definition.
[ -z "$deployFrontendSkip" ] && export deployFrontendSkip="false"
[ -z "$deployFrontendAppBaseDir" ] && export deployFrontendAppBaseDir="/usr/share/nginx/html/xcloud-dopaas-view-package/xcloud-dopaas-view-master-bin"

# ----------------------- Runtime environment configuration. ------------------------------------

globalExportedEnvStr=""
# Initilization and merge the services configuration that the runtime depends. ---------------------------------
function initRuntimeEnvConfig() {
  # Common environment configuration.
  [ -z "$springProfilesActive" ] && export springProfilesActive="pro"
  globalExportedEnvStr="${globalExportedEnvStr}\nexport springProfilesActive='${springProfilesActive}'"

  # (IAM) environment configuration.
  [ -z "$IAM_DB_URL" ] && export IAM_DB_URL="jdbc:mysql://localhost:3306/iam?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true"
  [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: IAM_DB_URL='$(eval echo $IAM_DB_URL)'"

  [ -z "$IAM_DB_USER" ] && export IAM_DB_USER="iam"
  [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: IAM_DB_USER='$(eval echo $IAM_DB_USER)'"

  [ -z "$IAM_DB_PASSWD" ] && export IAM_DB_PASSWD="123456"
  [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: IAM_DB_PASSWD='$(eval echo $IAM_DB_PASSWD)'"

  [ -z "$IAM_REDIS_PASSWD" ] && export IAM_REDIS_PASSWD="123456"
  [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: IAM_REDIS_PASSWD='$(eval echo $IAM_REDIS_PASSWD)'"

  [ -z "$IAM_REDIS_NODES" ] && export IAM_REDIS_NODES="localhost:6379"
  [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: IAM_REDIS_NODES='$(eval echo $IAM_REDIS_NODES)'"

  globalExportedEnvStr="${globalExportedEnvStr}\nexport IAM_DB_URL='${IAM_DB_URL}'"
  globalExportedEnvStr="${globalExportedEnvStr}\nexport IAM_DB_USER='${IAM_DB_USER}'"
  globalExportedEnvStr="${globalExportedEnvStr}\nexport IAM_DB_PASSWD='${IAM_DB_PASSWD}'"
  globalExportedEnvStr="${globalExportedEnvStr}\nexport IAM_REDIS_PASSWD='${IAM_REDIS_PASSWD}'"
  globalExportedEnvStr="${globalExportedEnvStr}\nexport IAM_REDIS_NODES='${IAM_REDIS_NODES}'"

  # (DoPaaS) environment configuration.
  if [ "$runtimeMode" == "standalone" ]; then
    [ -z "$STANDALONE_DOPAAS_DB_URL" ] && export STANDALONE_DOPAAS_DB_URL='jdbc:mysql://localhost:3306/dopaas_standalone?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true'
    [ -z "$STANDALONE_DOPAAS_DB_USER" ] && export STANDALONE_DOPAAS_DB_USER='dopaas_standalone'
    [ -z "$STANDALONE_DOPAAS_DB_PASSWD" ] && export STANDALONE_DOPAAS_DB_PASSWD='123456'
    [ -z "$STANDALONE_DOPAAS_REDIS_PASSWD" ] && export STANDALONE_DOPAAS_REDIS_PASSWD='123456'
    [ -z "$STANDALONE_DOPAAS_REDIS_NODES" ] && export STANDALONE_DOPAAS_REDIS_NODES='localhost:6379'
    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_DB_URL='${STANDALONE_DOPAAS_DB_URL}'"
    [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: STANDALONE_DOPAAS_DB_URL='$(eval echo $STANDALONE_DOPAAS_DB_URL)'"

    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_DB_USER='${STANDALONE_DOPAAS_DB_USER}'"
    [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: STANDALONE_DOPAAS_DB_USER='$(eval echo $STANDALONE_DOPAAS_DB_USER)'"

    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_DB_PASSWD='${STANDALONE_DOPAAS_DB_PASSWD}'"
    [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: STANDALONE_DOPAAS_DB_PASSWD='$(eval echo $STANDALONE_DOPAAS_DB_PASSWD)'"

    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_REDIS_PASSWD='${STANDALONE_DOPAAS_REDIS_PASSWD}'"
    [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: STANDALONE_DOPAAS_REDIS_PASSWD='$(eval echo $STANDALONE_DOPAAS_REDIS_PASSWD)'"

    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_REDIS_NODES='${STANDALONE_DOPAAS_REDIS_NODES}'"
    [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: STANDALONE_DOPAAS_REDIS_NODES='$(eval echo $STANDALONE_DOPAAS_REDIS_NODES)'"
  elif [ "$runtimeMode" == "cluster" ]; then
    local knownModuleNames=()
    for ((i=0;i<${#deployClusterBuildModules[@]};i++)) do
      local buildModule=${deployClusterBuildModules[i]}
      local appName=$(echo "$buildModule"|awk -F ',' '{print $1}')
      local appShortNameUpper=$(echo $appName|tr '[a-z]' '[A-Z]'|awk -F '-' '{print $1}') # e.g cmdb-facade => CMDB
      local appShortNameLower=$(echo $appShortNameUpper|tr '[A-Z]' '[a-z]') # e.g cmdb-facade => cmdb
      if [[ "$appShortNameUpper" == "IAM" || "${knownModuleNames[@]}"  =~ "${appShortNameUpper}" ]]; then # Skip and remove duplicate.
        continue
      fi
      knownModuleNames[${#knownModuleNames[@]}]="$appShortNameUpper"
      # For example: export CMDB_DOPAAS_DB_USER="dopaas_cmdb"
      local key1="${appShortNameUpper}_DOPAAS_DB_URL"
      [ -z "$(eval echo '$'$key1)" ] && export "$key1"="jdbc:mysql://localhost:3306/dopaas_$appShortNameLower?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true"
      local key2="${appShortNameUpper}_DOPAAS_DB_USER"
      [ -z "$(eval echo '$'$key2)" ] && export "$key2"="dopaas_${appShortNameLower}"
      local key3="${appShortNameUpper}_DOPAAS_DB_PASSWD"
      [ -z "$(eval echo '$'$key3)" ] && export "$key3"='123456'
      local key4="${appShortNameUpper}_DOPAAS_REDIS_PASSWD"
      [ -z "$(eval echo '$'$key4)" ] && export "$key4"='123456'
      local key5="${appShortNameUpper}_DOPAAS_REDIS_NODES"
      [ -z "$(eval echo '$'$key5)" ] && export "$key5"='localhost:6379'

      [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: $key1='$(eval echo '$'$key1)'"
      [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: $key2='$(eval echo '$'$key2)'"
      [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: $key3='$(eval echo '$'$key3)'"
      [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: $key4='$(eval echo '$'$key4)'"
      [ "$deployDebug" == "true" ] && log "DEBUG" "Exported env: $key5='$(eval echo '$'$key5)'"

      globalExportedEnvStr="${globalExportedEnvStr}\nexport $key1='$(eval echo '$'$key1)'"
      globalExportedEnvStr="${globalExportedEnvStr}\nexport $key2='$(eval echo '$'$key2)'"
      globalExportedEnvStr="${globalExportedEnvStr}\nexport $key3='$(eval echo '$'$key3)'"
      globalExportedEnvStr="${globalExportedEnvStr}\nexport $key4='$(eval echo '$'$key4)'"
      globalExportedEnvStr="${globalExportedEnvStr}\nexport $key5='$(eval echo '$'$key5)'"
    done
  else
    echo "Invalid runtime mode to $runtimeMode"; exit -1
  fi
}
initRuntimeEnvConfig
