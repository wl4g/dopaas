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

# ----------------------- Basic environment configuration. ---------------------------------------------------
[ -z "$workspaceDir" ] && export workspaceDir="${HOME}/.deploy-workspace" && mkdir -p $workspaceDir
currDate=$(date -d today +"%Y-%m-%d_%H%M%S")
[ -z "$logFile" ] && export logFile="${workspaceDir}/install_${currDate}.log" && touch $logFile
[ -z "$deployDebug" ] && export deployDebug="false" # true|false
[ -z "$deployAsync" ] && export deployAsync="true" # true|false

# ----------------------- Sources(Git) environment configuration. --------------------------------------------

# Git clone URLs definition.
[ -z "$gitBaseUri" ] && export gitBaseUri=$([ "$isGFWNetwork" == "Y" ] && echo "https://gitee.com" || echo "https://github.com") # For speed-up, fuck gfw!
[ -z "$gitXCloudComponentUrl" ] && export gitXCloudComponentUrl="${gitBaseUri}/wl4g/xcloud-component"
[ -z "$gitXCloudIamUrl" ] && export gitXCloudIamUrl="${gitBaseUri}/wl4g/xcloud-iam"
[ -z "$gitXCloudDoPaaSUrl" ] && export gitXCloudDoPaaSUrl="${gitBaseUri}/wl4g/xcloud-dopaas"
[ -z "$gitXCloudDoPaaSFrontendUrl" ] && export gitXCloudDoPaaSFrontendUrl="${gitBaseUri}/wl4g/xcloud-dopaas-view"
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
