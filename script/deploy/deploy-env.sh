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
[ -z "$logFile" ] && export logFile="${workspaceDir}/install_"$(date -d today +"%Y-%m-%d_%H%M%S")".log"
[ -z "$asyncDeploy" ] && export asyncDeploy="true" # true|false

# ----------------------- Maven environment variables. -----------------------------------------
[ -z "$apacheMvnDownloadTarUrl" ] && export apacheMvnDownloadTarUrl="https://mirrors.bfsu.edu.cn/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz"
[ -z "$secondaryApacheMvnDownloadTarUrl" ] && export secondaryApacheMvnDownloadTarUrl="https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz"
[ -z "$apacheMvnInstallDir" ] && export apacheMvnInstallDir="/opt/apps"
# You can customize the overlay. (for example: mvn -Dmaven.repo.local=$HOME/.m2/repository/ -f $HOME/myproject_dir/pom.xml clean install)
[ -z "$apacheMvnLocalRepoDir" ] && export apacheMvnLocalRepoDir="$HOME/.m2/repository/"

# ----------------------- Deployment environment variables. ------------------------------------

# Git definition.
[ -z "$gitBaseUri" ] && export gitBaseUri="https://github.com/wl4g" # for example options: https://github.com/wl4g or https://gitee.com/wl4g
[ -z "$gitXCloudComponentUrl" ] && export gitXCloudComponentUrl="${gitBaseUri}/xcloud-component"
[ -z "$gitXCloudIamUrl" ] && export gitXCloudIamUrl="${gitBaseUri}/xcloud-iam"
[ -z "$gitXCloudDevOpsUrl" ] && export gitXCloudDevOpsUrl="${gitBaseUri}/xcloud-devops"

# Build definition.
[ -z "$buildPkgType" ] && export buildPkgType="mvnAssTar" # Options: mvnAssTar|springExecJar
[ -z "$buildPkgVersion" ] && export buildPkgVersion="master"
[ -z "$rebuildOfGitPullAlreadyUpToDate" ] && export rebuildOfGitPullAlreadyUpToDate="true"

# Deploy common definition.
[ -z "$runtimeMode" ] && export runtimeMode="cluster" # Options: standalone|cluster
[ -z "$deployBaseDir" ] && export deployBaseDir="/opt/apps/acm"

# Delopy(standalone) modules definition.
export deployStandaloneBuildTargets=(
  "${currDir}/xcloud-iam/xcloud-iam-service-starter-all/target"
  "${currDir}/xcloud-devops/xcloud-devops-all-starter/target"
)

# Deploy(cluster) definition.
export deployClusterNodesConfigPath="$currDir/deploy-host.csv"
export deployClusterBuildTargets=(
  "${currDir}/xcloud-iam/xcloud-iam-service-starter-data/target"
  "${currDir}/xcloud-iam/xcloud-iam-service-starter-facade/target"
  "${currDir}/xcloud-iam/xcloud-iam-service-starter-web/target"
  "${currDir}/xcloud-devops/xcloud-devops-ci/xcloud-devops-ci-service-starter-facade/target"
  "${currDir}/xcloud-devops/xcloud-devops-ci/xcloud-devops-ci-service-starter-server/target"
  "${currDir}/xcloud-devops/xcloud-devops-doc/xcloud-devops-doc-service-starter-facade/target"
  "${currDir}/xcloud-devops/xcloud-devops-doc/xcloud-devops-doc-service-starter-manager/target"
  "${currDir}/xcloud-devops/xcloud-devops-dts/xcloud-devops-dts-service-starter-facade/target"
  "${currDir}/xcloud-devops/xcloud-devops-dts/xcloud-devops-dts-service-starter-manager/target"
  #"${currDir}/xcloud-devops/xcloud-devops-scm/xcloud-devops-scm-service-starter-facade/target"
  #"${currDir}/xcloud-devops/xcloud-devops-scm/xcloud-devops-scm-service-starter-server/target"
  #"${currDir}/xcloud-devops/xcloud-devops-umc/xcloud-devops-umc-service-starter-facade/target"
  #"${currDir}/xcloud-devops/xcloud-devops-umc/xcloud-devops-umc-service-starter-manager/target"
  #"${currDir}/xcloud-devops/xcloud-devops-umc/xcloud-devops-umc-service-starter-receiver/target"
  "${currDir}/xcloud-devops/xcloud-devops-vcs/xcloud-devops-vcs-service-starter-facade/target"
  "${currDir}/xcloud-devops/xcloud-devops-vcs/xcloud-devops-vcs-service-starter-manager/target"
)

