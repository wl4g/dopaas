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
set -e

echo ""
echo " Welcome to XCloud DevSecOps Deploying (for Host) ! "
echo ""
echo " Wiki: https://github.com/wl4g/xcloud-devops/blob/master/README.md or https://gitee.com/wl4g/xcloud-devops/blob/master/README_CN.md"
echo " Authors: <Wanglsir@gmail.com, 983708408@qq.com>"
echo " Version: 2.0.0"
echo " Time: "$(date -d today +"%Y-%m-%d %H:%M:%S")
echo ""


# Macro definitions.
currDir="$(cd "`dirname "$0"`"/; pwd)" && mkdir -p $currDir/.deploy-workspace/ && cd $currDir/.deploy-workspace/
. ${currDir}/deploy-common.sh
confPath="$currDir/deploy-default-host.txt"
deployMode="standalone" # Options: standalone|cluster
deployBaseDir="/opt/apps/acm"
buildPkgType="mvnAssTar" # Options: mvnAssTar|springExecJar
buildPkgVersion="master"
gitBaseUri="https://github.com/wl4g" # Options(e.g): https://github.com/wl4g|https://gitee.com/wl4g
gitXCloudComponentUrl="${gitBaseUri}/xcloud-component"
gitXCloudIamUrl="${gitBaseUri}/xcloud-iam"
gitXCloudDevOpsUrl="${gitBaseUri}/xcloud-devops"
apacheMvnDownloadTarUrl="https://mirror.bit.edu.cn/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz"
apacheMvninstallDir="/opt/apps/"
# Deploy services definition.
deployStandaloneProperties=(
  "${currDir}/xcloud-iam/xcloud-iam-service-starter-data/target/iam-data-${buildPkgVersion}-bin.tar@/etc/init.d/iam-data.service restart"
  "${currDir}/xcloud-iam-service-starter-facade/target/iam-facade-${buildPkgVersion}-bin.tar@/etc/init.d/iam-data.service restart"
  "${currDir}/xcloud-iam-service-starter-web/target/iam-web-${buildPkgVersion}-bin.tar@/etc/init.d/iam-data.service restart"
  "${currDir}/xcloud-devops/xcloud-devops-ci-service-starter-facade/target/ci-facade-${buildPkgVersion}-bin.tar@/etc/init.d/ci-facade.service restart"
  "${currDir}/xcloud-devops/xcloud-devops-ci-service-starter-server/target/ci-server-${buildPkgVersion}-bin.tar@/etc/init.d/ci-server.service restart"
  "${currDir}/xcloud-devops/xcloud-devops-doc-service-starter-facade/target/doc-facade-${buildPkgVersion}-bin.tar@/etc/init.d/doc-facade.service restart"
  "${currDir}/xcloud-devops/xcloud-devops-doc-service-starter-manager/target/doc-manager-${buildPkgVersion}-bin.tar@/etc/init.d/doc-manager.service restart"
  "${currDir}/xcloud-devops/xcloud-devops-dts-service-starter-facade/target/dts-facade-${buildPkgVersion}-bin.tar@/etc/init.d/dts-facade.service restart"
  "${currDir}/xcloud-devops/xcloud-devops-dts-service-starter-manager/target/dts-manager-${buildPkgVersion}-bin.tar@/etc/init.d/dts-manager.service restart"
  #"${currDir}/xcloud-devops/xcloud-devops-scm-service-starter-facade/target/scm-facade-${buildPkgVersion}-bin.tar@/etc/init.d/scm-facade.service restart"
  #"${currDir}/xcloud-devops/xcloud-devops-scm-service-starter-server/target/scm-server-${buildPkgVersion}-bin.tar@/etc/init.d/scm-server.service restart"
  #"${currDir}/xcloud-devops/xcloud-devops-umc-service-starter-facade/target/umc-facade-${buildPkgVersion}-bin.tar@/etc/init.d/umc-facade.service restart"
  #"${currDir}/xcloud-devops/xcloud-devops-umc-service-starter-manager/target/umc-manager-${buildPkgVersion}-bin.tar@/etc/init.d/umc-manager.service restart"
  #"${currDir}/xcloud-devops/xcloud-devops-umc-service-starter-receiver/target/umc-receiver-${buildPkgVersion}-bin.tar@/etc/init.d/umc-receiver.service restart"
  "${currDir}/xcloud-devops/xcloud-devops-vcs-service-starter-facade/target/vcs-facade-${buildPkgVersion}-bin.tar@/etc/init.d/vcs-facade.service restart"
  "${currDir}/xcloud-devops/xcloud-devops-vcs-service-starter-manager/target/vcs-manager-${buildPkgVersion}-bin.tar@/etc/init.d/vcs-manager.service restart"
)
deployClusterProperties=(
  "${currDir}/xcloud-iam/xcloud-iam-service-starter-all/target/iam-server-${buildPkgVersion}-bin.tar@/etc/init.d/iam-server.service restart"
  "${currDir}/xcloud-devops/xcloud-devops-all-starter/target/devops-server-all-${buildPkgVersion}-bin.tar@/etc/init.d/devops-server.service restart"
)


# Pull and compile.
function pullAndCompile() {
  projectName=$1 # e.g xcloud-devops
  cloneUrl=$2
  projectDir="$currDir/$projectName"
  if [ ! -d "$projectDir" ]; then
    echo "Git clone $projectName from $cloneUrl ..."
    git clone $cloneUrl && cd $projectDir
    echo "Compiling $projectName ..."
    $cmdMvn clean install -DskipTests -T 2C -U -P $buildPkgType
  else
    echo "Git pull $projectName..."
    cd $projectDir && git pull
    echo "Compiling $projectName ..."
    $cmdMvn clean install -DskipTests -T 2C -U -P $buildPkgType
  fi
}

# Deploy & startup all(standalone).
function deployAndStartupAllWithStandalone() {
  buildPkgFilePath=$1
  cmdRestart=$2
  appName=$3
  appInstallDir=${deployBaseDir}/${appName}-package
  echo "Cleanup $appInstallDir/* ..."
  rm -rf $appInstallDir/*
  if [ "$buildPkgType" == "mvnAssTar" ]; then
    echo "Uncompress $buildPkgFilePath to $(cd "`dirname "$0"`"/; pwd)/ ..."
    tar -xf $buildPkgFilePath -C .
    echo "Move $(cd "`dirname "$0"`"/; pwd)/${appName}-${buildPkgVersion}-bin to $appInstallDir/* ..."
    mv ${appName}-${buildPkgVersion}-bin $appInstallDir/
  elif [ "$buildPkgType" == "springExecJar" ]; then
    echo "Move $(cd "`dirname "$0"`"/; pwd)/${appName}-${buildPkgVersion}-bin.jar to $appInstallDir/* ..."
    mv ${appName}-${buildPkgVersion}-bin.jar $appInstallDir/
  else
    echo "Illegal buildPkgType: $buildPkgType"
    exit -1
  fi
  exec $cmdRestart
}

# Deploy & startup all(cluster).
function deployAndStartupAllWithCluster() {
  echo "TODO"
}

# Deploy & startup all.
function deployAndStartupAll() {
  if [ "$deployMode" == "standalone" ]; then
    deployProperties=$deployStandaloneProperties
  elif [ "$deployMode" == "cluster" ]; then # The 'cluster' mode is deployed to the remote hosts
    deployProperties=$deployClusterProperties
  else
    echo "Illegal deployMode: $deployMode"
    exit -1
  fi
  # Call deploying
  deployPropertiesLen=${#deployProperties[@]}
  if [ $deployPropertiesLen -gt 0 ]; then
    for((i=0;i<${#deployProperties[@]};i++)) do
      buildPkgFilePath=$(echo ${deployProperties[i]}|awk -F '@' '{print $1}')
      cmdRestart=$(echo ${deployProperties[i]}|awk -F '@' '{print $2}')
      appName=$(echo "$(basename $buildPkgFilePath)"|awk -F "-${buildPkgVersion}-bin.tar" '{print $1}')
      if [ "$deployMode" == "standalone" ]; then # The 'standalone' mode is only deployed to the local host
        echo "Deploying $appName to local with standalone ..."
        deployAndStartupAllWithStandalone "$buildPkgFilePath" "$cmdRestart" "$appName"
        echo "Deployed $appName to local completion !"
      elif [ "$deployMode" == "cluster" ]; then # The 'cluster' mode is deployed to the remote hosts
        echo "Deploying $appName to cluster hosts with cluster ..."
        deployAndStartupAllWithCluster "$buildPkgFilePath" "$cmdRestart" "$appName"
        echo "Deploy $appName to remote cluster hosts completion !"
      fi
    done;
  fi
}

# --- Entrypoint exec. ---
checkPreDependencies
pullAndCompile "xcloud-component" $gitXCloudComponentUrl
pullAndCompile "xcloud-iam" $gitXCloudIamUrl
pullAndCompile "xcloud-devops" $gitXCloudDevOpsUrl
deployAndStartupAll

