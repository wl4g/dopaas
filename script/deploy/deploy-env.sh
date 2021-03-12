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

# ----------------------- Initialization. ------------------------------------------------------
[ -z "$currDir" ] && export currDir=$(cd "`dirname $0`"/ ; pwd)

# ----------------------- Base environment variables. ------------------------------------------
[ -z "$workspaceDir" ] && export workspaceDir="/tmp/.deploy-workspace" && mkdir -p $workspaceDir
currDate=$(date -d today +"%Y-%m-%d_%H%M%S")
[ -z "$logFile" ] && export logFile="${workspaceDir}/install_${currDate}.log" && touch $logFile
[ -z "$deployDebug" ] && export deployDebug="false" # true|false
[ -z "$asyncDeploy" ] && export asyncDeploy="true" # true|false

# ----------------------- Maven environment variables. -----------------------------------------
[ -z "$apacheMvnDownloadTarUrl" ] && export apacheMvnDownloadTarUrl="https://mirrors.bfsu.edu.cn/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz"
[ -z "$secondaryApacheMvnDownloadTarUrl" ] && export secondaryApacheMvnDownloadTarUrl="https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz"
[ -z "$apacheMvnInstallDir" ] && export apacheMvnInstallDir="$workspaceDir"
# You can customize the overlay. (for example: mvn -Dmaven.repo.local=$HOME/.m2/repository/ -f $HOME/myproject_dir/pom.xml clean install)
[ -z "$apacheMvnLocalRepoDir" ] && export apacheMvnLocalRepoDir="$HOME/.m2/repository/"

# ----------------------- Deployment environment variables. ------------------------------------

# Git URLs definition.
[ -z "$gitBaseUri" ] && export gitBaseUri="https://gitee.com/wl4g" # for example options: https://github.com/wl4g or https://gitee.com/wl4g
[ -z "$gitXCloudComponentUrl" ] && export gitXCloudComponentUrl="${gitBaseUri}/xcloud-component"
[ -z "$gitXCloudIamUrl" ] && export gitXCloudIamUrl="${gitBaseUri}/xcloud-iam"
[ -z "$gitXCloudDevOpsUrl" ] && export gitXCloudDevOpsUrl="${gitBaseUri}/xcloud-devops"
# Git pull branchs.
[ -z "$defaultGitBranch" ] && export defaultGitBranch="master"
[ -z "$xcloudComponentGitBranch" ] && export xcloudComponentGitBranch="${defaultGitBranch}"
[ -z "$xcloudIamGitBranch" ] && export xcloudIamGitBranch="${defaultGitBranch}"
[ -z "$xcloudDevOpsGitBranch" ] && export xcloudDevOpsGitBranch="${defaultGitBranch}"

# Build definition.
[ -z "$buildPkgType" ] && export buildPkgType="mvnAssTar" # Options: mvnAssTar|springExecJar
[ -z "$buildPkgVersion" ] && export buildPkgVersion="master"
[ -z "$rebuildOfGitPullAlreadyUpToDate" ] && export rebuildOfGitPullAlreadyUpToDate="false"

# Deploy common definition.
[ -z "$runtimeMode" ] && export runtimeMode="cluster" # Options: standalone|cluster
[ -z "$deployAppBaseDir" ] && export deployAppBaseDir="/opt/apps/acm"
[ -z "$deployAppDataBaseDir" ] && export deployAppDataBaseDir="/mnt/disk1"
[ -z "$deployAppLogBaseDir" ] && export deployAppLogBaseDir="${deployAppDataBaseDir}/log"
[ -z "$forceInstallServiceScript" ] && export forceInstallServiceScript="true"

# Delopy(standalone) modules definition.
export deployStandaloneBuildModules=(
  "standalone-iam,${currDir}/xcloud-iam/xcloud-iam-service-starter-all/target"
  "standalone-devops,${currDir}/xcloud-devops/xcloud-devops-all-starter/target"
)

# Deploy(cluster) modules definition.
export deployClusterNodesConfigPath="$currDir/deploy-host.csv"
export deployClusterBuildModules=(
  "iam-data,${currDir}/xcloud-iam/xcloud-iam-service-starter-data/target"
  "iam-facade,${currDir}/xcloud-iam/xcloud-iam-service-starter-facade/target"
  "iam-web,${currDir}/xcloud-iam/xcloud-iam-service-starter-web/target"
  "cmdb-facade,${currDir}/xcloud-devops/xcloud-devops-cmdb/xcloud-devops-cmdb-service-starter-facade/target"
  "cmdb-manager,${currDir}/xcloud-devops/xcloud-devops-cmdb/xcloud-devops-cmdb-service-starter-manager/target"
  "uci-facade,${currDir}/xcloud-devops/xcloud-devops-uci/xcloud-devops-uci-service-starter-facade/target"
  "uci-server,${currDir}/xcloud-devops/xcloud-devops-uci/xcloud-devops-uci-service-starter-server/target"
  "udm-facade,${currDir}/xcloud-devops/xcloud-devops-udm/xcloud-devops-udm-service-starter-facade/target"
  "udm-manager,${currDir}/xcloud-devops/xcloud-devops-udm/xcloud-devops-udm-service-starter-manager/target"
  "udc-facade,${currDir}/xcloud-devops/xcloud-devops-udc/xcloud-devops-udc-service-starter-facade/target"
  "udc-manager,${currDir}/xcloud-devops/xcloud-devops-udc/xcloud-devops-udc-service-starter-manager/target"
  #"ucm-facade,${currDir}/xcloud-devops/xcloud-devops-ucm/xcloud-devops-ucm-service-starter-facade/target"
  #"ucm-server,${currDir}/xcloud-devops/xcloud-devops-ucm/xcloud-devops-ucm-service-starter-server/target"
  "umc-facade,${currDir}/xcloud-devops/xcloud-devops-umc/xcloud-devops-umc-service-starter-facade/target"
  "umc-manager,${currDir}/xcloud-devops/xcloud-devops-umc/xcloud-devops-umc-service-starter-manager/target"
  "umc-collector,${currDir}/xcloud-devops/xcloud-devops-umc/xcloud-devops-umc-service-starter-collector/target"
  "urm-facade,${currDir}/xcloud-devops/xcloud-devops-urm/xcloud-devops-urm-service-starter-facade/target"
  "urm-manager,${currDir}/xcloud-devops/xcloud-devops-urm/xcloud-devops-urm-service-starter-manager/target"
)
# Eureka build module info.
export deployEurekaBuildModule="eureka-server,${currDir}/xcloud-component/xcloud-component-rpc/xcloud-component-rpc-springcloud-eureka-server/target"

# Runtime dependency external services configuration.
[ -z "$runtimeMysqlUrl" ] && export runtimeMysqlUrl="jdbc:mysql://localhost:3306/devops?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false"
[ -z "$runtimeMysqlUser" ] && export runtimeMysqlUser="root"
[ -z "$runtimeMysqlPassword" ] && export runtimeMysqlPassword="123456"
[ -z "$runtimeRedisNodes" ] && export runtimeRedisNodes="localhost:6379"
[ -z "$runtimeRedisPassword" ] && export runtimeRedisPassword="123456"
[ -z "$runtimeAppSpringProfilesActive" ] && export runtimeAppSpringProfilesActive="pro"
