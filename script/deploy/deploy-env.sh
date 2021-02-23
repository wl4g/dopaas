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

# ----------------------- Initialization. ------------------------------------------------------
currDir=$([ "$currDir" == "" ] && echo "$(cd "`dirname "$0"`"/; pwd)" || echo $currDir)

# ----------------------- Base environment variables. ------------------------------------------
workspaceDir="/tmp/.deploy-workspace" && mkdir -p $workspaceDir && cd $workspaceDir
logFile="${workspaceDir}/install_"$(date -d today +"%Y-%m-%d_%H%M%S")".log"
asyncDeploy="true" # true|false

# ----------------------- Maven environment variables. -----------------------------------------
apacheMvnDownloadTarUrl="https://mirror.bit.edu.cn/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz"
apacheMvnInstallDir="/opt/apps/"
# You can customize the overlay. (for example: mvn -Dmaven.repo.local=$HOME/.m2/repository/ -f $HOME/myproject_dir/pom.xml clean install)
#apacheMvnLocalRepoDir="$HOME/.m2/repository/"
apacheMvnLocalRepoDir="/home/ci-server/.m2/repository/"

# ----------------------- Deployment environment variables. ------------------------------------

# Git definition.
if [ "$gitBaseUri" == "" ]; then
  gitBaseUri="https://github.com/wl4g" # for example options: https://github.com/wl4g or https://gitee.com/wl4g
fi
gitXCloudComponentUrl="${gitBaseUri}/xcloud-component"
gitXCloudIamUrl="${gitBaseUri}/xcloud-iam"
gitXCloudDevOpsUrl="${gitBaseUri}/xcloud-devops"

# Build definition.
buildPkgType="mvnAssTar" # Options: mvnAssTar|springExecJar
buildPkgVersion="master"
rebuildOfGitPullAlreadyUpToDate="true"

# Deploy common definition.
deployMode="cluster" # Options: standalone|cluster
deployBaseDir="/opt/apps/acm"

# Delopy(standalone) modules definition.
deployStandaloneBuildTargets=(
  "${currDir}/xcloud-iam/xcloud-iam-service-starter-all/target"
  "${currDir}/xcloud-devops/xcloud-devops-all-starter/target"
)

# Deploy(cluster) definition.
deployClusterNodesConfigPath="$currDir/deploy-host.csv"
deployClusterBuildTargets=(
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

