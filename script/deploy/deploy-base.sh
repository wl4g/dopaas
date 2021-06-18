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

[ -z "$currDir" ] && export currDir=$(cd "`dirname $0`"/ ; pwd)
. $currDir/deploy-env.sh

# --------------------------- Basic environment definition. ---------------------------------------------------

[ -z "$workspaceDir" ] && export workspaceDir="${HOME}/.deploy-workspace"; mkdir -p $workspaceDir
[ -z "$pkgRepoLocalDir" ] && export pkgRepoLocalDir="${workspaceDir}/pkg"; mkdir -p $pkgRepoLocalDir
currDate=$(date -d today +"%Y-%m-%d_%H%M%S")
[ -z "$logFile" ] && export logFile="${workspaceDir}/install_${currDate}.log"; touch $logFile
[ -z "$deployDebug" ] && export deployDebug="false" # true|false
[ -z "$deployNetworkMode" ] && export deployNetworkMode="extranet" # extranet|intranet
# 部署并发数,用于限制并行将多个应用分别异步部署到多个实例的进程数(注:若设置过大会引发大量ssh频繁建立连接导致被sshd拒绝:ssh_exchange_identification: read: Connection reset by peer)
# 好在deploy-host.sh#configureRemoteSshd函数已经对远端sshd配置修改, 此处还限制并发数是为了防止某些系统下修改未生效.
[ -z "$deployConcurrent" ] && export deployConcurrent=5

# --------------------------- Jdk environment definition. -----------------------------------------------------

[ -z "$jdkInstallDir" ] && export jdkInstallDir="/usr/local/jdk/"
[ -z "$jdk8YumX64PkgName" ] && export jdk8YumX64PkgName="java-1.8.0-openjdk-devel.x86_64"
[ -z "$jdk8AptX64PkgName" ] && export jdk8AptX64PkgName="openjdk-8-jdk"
# @see: http://jdk.java.net/archive/ or http://jdk.java.net/java-se-ri/8-MR3
[ -z "$localJdk8DownloadUrl" ] && export localJdk8DownloadUrl="file://${pkgRepoLocalDir}/openjdk-8u41-b04-linux-x64-14_jan_2020.tar.gz"

# --------------------------- Sshpass environment definition. -------------------------------------------------

[ -z "$sshpassForCentos6x64" ] && export sshpassForCentos6x64="https://gitee.com/wl4g-collect/sshpass/attach_files/690539/download/sshpass_centos6_x64_1.09"
[ -z "$sshpassForCentos7x64" ] && export sshpassForCentos7x64="https://gitee.com/wl4g-collect/sshpass/attach_files/690540/download/sshpass_centos7_x64_1.09"
[ -z "$sshpassForCentos8x64" ] && export sshpassForCentos8x64="https://gitee.com/wl4g-collect/sshpass/attach_files/711793/download/sshpass_centos8_x64_1.09"
[ -z "$sshpassForUbuntu20x64" ] && export sshpassForUbuntu20x64="https://gitee.com/wl4g-collect/sshpass/attach_files/690541/download/sshpass_ubuntu20_x64_1.09"
[ -z "$secondarySshpassForCentos6x64" ] && export secondarySshpassForCentos6x64="https://github.com/wl4g-collect/sshpass/releases/download/1.09/sshpass_centos6_x64_1.09"
[ -z "$secondarySshpassForCentos7x64" ] && export secondarySshpassForCentos7x64="https://github.com/wl4g-collect/sshpass/releases/download/1.09/sshpass_centos7_x64_1.09"
[ -z "$secondarySshpassForCentos8x64" ] && export secondarySshpassForCentos8x64="https://github.com/wl4g-collect/sshpass/releases/download/1.09/sshpass_centos8_x64_1.09"
[ -z "$secondarySshpassForUbuntu20x64" ] && export secondarySshpassForUbuntu20x64="https://github.com/wl4g-collect/sshpass/releases/download/1.09/sshpass_ubuntu20_x64_1.09"
[ -z "$localSshpassForCentos6x64" ] && export localSshpassForCentos6x64="file://${pkgRepoLocalDir}/sshpass_centos6_x64_1.09"
[ -z "$localSshpassForCentos7x64" ] && export localSshpassForCentos7x64="file://${pkgRepoLocalDir}/sshpass_centos7_x64_1.09"
[ -z "$localSshpassForCentos8x64" ] && export localSshpassForCentos8x64="file://${pkgRepoLocalDir}/sshpass_centos8_x64_1.09"
[ -z "$localSshpassForUbuntu20x64" ] && export localSshpassForUbuntu20x64="file://${pkgRepoLocalDir}/sshpass_ubuntu20_x64_1.09"

# --------------------------- Git environment definition. -----------------------------------------------------

# Git install
[ -z "$gitInstallDir" ] && export gitInstallDir="/usr/local/git-2.27.0/"
[ -z "$gitDownloadUrlForCentos6x64" ] && export gitDownloadUrlForCentos6x64="https://github.com/wl4g-collect/git-2.27.0/releases/download/2.27.0/git-2.27.0-centos6-x64-bin.tar.gz"
[ -z "$gitDownloadUrlForCentos7x64" ] && export gitDownloadUrlForCentos7x64="https://github.com/wl4g-collect/git-2.27.0/releases/download/2.27.0/git-2.27.0-centos7-x64-bin.tar.gz"
[ -z "$gitDownloadUrlForCentos8x64" ] && export gitDownloadUrlForCentos8x64="https://github.com/wl4g-collect/git-2.27.0/releases/download/2.27.0/git-2.27.0-centos8-x64-bin.tar.gz"
[ -z "$gitDownloadUrlForUbuntu20x64" ] && export gitDownloadUrlForUbuntu20x64="https://github.com/wl4g-collect/git-2.27.0/releases/download/2.27.0/git-2.27.0-ubuntu20-x64-bin.tar.gz"
[ -z "$secondaryGitDownloadUrlForCentos6x64" ] && export secondaryGitDownloadUrlForCentos6x64="https://gitee.com/wl4g-collect/git-2.27.0/attach_files/711189/download/git-2.27.0-centos6-x64-bin.tar.gz"
[ -z "$secondaryGitDownloadUrlForCentos7x64" ] && export secondaryGitDownloadUrlForCentos7x64="https://gitee.com/wl4g-collect/git-2.27.0/attach_files/711194/download/git-2.27.0-centos7-x64-bin.tar.gz"
[ -z "$secondaryGitDownloadUrlForCentos8x64" ] && export secondaryGitDownloadUrlForCentos8x64="https://gitee.com/wl4g-collect/git-2.27.0/attach_files/711195/download/git-2.27.0-centos8-x64-bin.tar.gz"
[ -z "$secondaryGitDownloadUrlForUbuntu20x64" ] && export secondaryGitDownloadUrlForUbuntu20x64="https://gitee.com/wl4g-collect/git-2.27.0/attach_files/711196/download/git-2.27.0-ubuntu20-x64-bin.tar.gz"
[ -z "$localGitDownloadUrlForCentos6x64" ] && export localGitDownloadUrlForCentos6x64="file://${pkgRepoLocalDir}/git-2.27.0-centos6-x64-bin.tar.gz"
[ -z "$localGitDownloadUrlForCentos7x64" ] && export localGitDownloadUrlForCentos7x64="file://${pkgRepoLocalDir}/git-2.27.0-centos7-x64-bin.tar.gz"
[ -z "$localGitDownloadUrlForCentos8x64" ] && export localGitDownloadUrlForCentos8x64="file://${pkgRepoLocalDir}/git-2.27.0-centos8-x64-bin.tar.gz"
[ -z "$localGitDownloadUrlForUbuntu20x64" ] && export localGitDownloadUrlForUbuntu20x64="file://${pkgRepoLocalDir}/git-2.27.0-ubuntu20-x64-bin.tar.gz"
# Clone URLs.
[ -z "$gitBaseUri" ] && export gitBaseUri=$([ "$isChinaLANNetwork" == "Y" ] && echo "https://gitee.com/wl4g" || echo "https://github.com/wl4g") # For speed-up, fuck!
[ -z "$gitXCloudComponentUrl" ] && export gitXCloudComponentUrl="${gitBaseUri}/xcloud-component"
[ -z "$gitXCloudIamUrl" ] && export gitXCloudIamUrl="${gitBaseUri}/xcloud-iam"
[ -z "$gitXCloudDoPaaSUrl" ] && export gitXCloudDoPaaSUrl="${gitBaseUri}/xcloud-dopaas"
[ -z "$gitXCloudDoPaaSViewUrl" ] && export gitXCloudDoPaaSViewUrl="${gitBaseUri}/xcloud-dopaas-view"
# Git project name of URLs.
[ -z "$gitXCloudComponentProjectName" ] && export gitXCloudComponentProjectName="$(echo `basename $gitXCloudComponentUrl`|sed s/.git//g)"
[ -z "$gitXCloudIamProjectName" ] && export gitXCloudIamProjectName="$(echo `basename $gitXCloudIamUrl`|sed s/.git//g)"
[ -z "$gitXCloudDoPaaSProjectName" ] && export gitXCloudDoPaaSProjectName="$(echo `basename $gitXCloudDoPaaSUrl`|sed s/.git//g)"
[ -z "$gitXCloudDoPaaSViewProjectName" ] && export gitXCloudDoPaaSViewProjectName="$(echo `basename $gitXCloudDoPaaSViewUrl`|sed s/.git//g)"
# pull branchs.
[ -z "$gitDefaultBranch" ] && export gitDefaultBranch="master"
[ -z "$gitComponentBranch" ] && export gitComponentBranch="${gitDefaultBranch}"
[ -z "$gitIamBranch" ] && export gitIamBranch="${gitDefaultBranch}"
[ -z "$gitDoPaaSBranch" ] && export gitDoPaaSBranch="${gitDefaultBranch}"
[ -z "$gitDoPaaSViewBranch" ] && export gitDoPaaSViewBranch="${gitDefaultBranch}"

# ----------------------- Backend environment definition. -----------------------------------------

[ -z "$buildForcedOnPullUpToDate" ] && export buildForcedOnPullUpToDate="false"
# Maven install.
[ -z "$apacheMvnDownloadTarUrl" ] && export apacheMvnDownloadTarUrl="https://mirrors.bfsu.edu.cn/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz"
[ -z "$secondaryApacheMvnDownloadTarUrl" ] && export secondaryApacheMvnDownloadTarUrl="https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz"
[ -z "$localApacheMvnDownloadTarUrl" ] && export localApacheMvnDownloadTarUrl="file://${pkgRepoLocalDir}/apache-maven-3.6.3-bin.tar.gz"
[ -z "$apacheMvnInstallDir" ] && export apacheMvnInstallDir="$workspaceDir"
# You can customize the overlay. (e.g: mvn -Dmaven.repo.local=$HOME/.m2/repository/ -f $HOME/myproject_dir/pom.xml clean install)
[ -z "$apacheMvnLocalRepoDir" ] && export apacheMvnLocalRepoDir="$HOME/.m2/repository/"
# Maven build.
[ -z "$buildPkgType" ] && export buildPkgType="mvnAssTar" # Options: mvnAssTar|springExecJar
[ -z "$buildPkgVersion" ] && export buildPkgVersion="master"
# Deploy.
[ -z "$runtimeMode" ] && export runtimeMode="cluster" # Options: standalone|cluster
[ -z "$deployAppBaseDir" ] && export deployAppBaseDir="/opt/apps/ecm"
[ -z "$deployAppDataBaseDir" ] && export deployAppDataBaseDir="/mnt/disk1"
[ -z "$deployAppLogBaseDir" ] && export deployAppLogBaseDir="${deployAppDataBaseDir}/log"
[ -z "$deployForcedInstallMgtScript" ] && export deployForcedInstallMgtScript="true" # e.g: Forced installing to '/etc/init.d/iam-web.service'
[ -z "$deployBackendSkip" ] && export deployBackendSkip="false"

# Deploy(eureka).
export deployEurekaBuildModule="eureka-server,8761,internal,${currDir}/xcloud-component/xcloud-component-integration/xcloud-component-integration-regcenter-eureka-server/target"

# Deploy(zookeeper).(https://www.apache.org/dyn/closer.lua/zookeeper/zookeeper-3.6.3/apache-zookeeper-3.6.3-bin.tar.gz)
[ -z "$zkHome" ] && export zkHome="$deployAppBaseDir/zookeeper-current/"
[ -z "$zkDownloadUrl" ] && export zkDownloadUrl="https://downloads.apache.org/zookeeper/zookeeper-3.6.3/apache-zookeeper-3.6.3-bin.tar.gz"
[ -z "$secondaryZkDownloadUrl" ] && export secondaryZkDownloadUrl="https://mirrors.sonic.net/apache/zookeeper/zookeeper-3.6.3/apache-zookeeper-3.6.3-bin.tar.gz"
[ -z "$localZkDownloadUrl" ] && export localZkDownloadUrl="file://${pkgRepoLocalDir}/apache-zookeeper-3.6.3-bin.tar.gz"

# Deploy(nginx)
[ -z "$nginxDownloadUrlForCentos6x64" ] && export nginxDownloadUrlForCentos6x64="https://gitee.com/wl4g-collect/nginx/attach_files/714981/download/nginx-1.18.0-centos6-x64-bin.tar.gz"
[ -z "$nginxDownloadUrlForCentos7x64" ] && export nginxDownloadUrlForCentos7x64="https://gitee.com/wl4g-collect/nginx/attach_files/714980/download/nginx-1.20.0-centos7-x64-bin.tar.gz"
[ -z "$nginxDownloadUrlForCentos8x64" ] && export nginxDownloadUrlForCentos8x64="https://gitee.com/wl4g-collect/nginx/attach_files/714979/download/nginx-1.21.0-centos8-x64-bin.tar.gz"
[ -z "$nginxDownloadUrlForUbuntu20x64" ] && export nginxDownloadUrlForUbuntu20x64="https://gitee.com/wl4g-collect/nginx/attach_files/714982/download/nginx-1.18.0-ubuntu20-x64-bin.tar.gz"
#[ -z "$secondaryNgxDownloadUrlForCentos6x64" ] && export secondaryNgxDownloadUrlForCentos6x64="https://github.com/wl4g-collect/nginx/releases/download/release-1.18.0/nginx-1.18.0-centos6-x64-bin.tar.gz"
#[ -z "$secondaryNgxDownloadUrlForCentos7x64" ] && export secondaryNgxDownloadUrlForCentos7x64="https://github.com/wl4g-collect/nginx/releases/download/release-1.20.0/nginx-1.20.0-centos7-x64-bin.tar.gz"
#[ -z "$secondaryNgxDownloadUrlForCentos8x64" ] && export secondaryNgxDownloadUrlForCentos8x64="https://github.com/wl4g-collect/nginx/releases/download/release-1.21.0/nginx-1.21.0-centos8-x64-bin.tar.gz"
#[ -z "$secondaryNgxDownloadUrlForUbuntu20x64" ] && export secondaryNgxDownloadUrlForUbuntu20x64="https://github.com/wl4g-collect/nginx/releases/download/release-1.18.0/nginx-1.18.0-ubuntu20-x64-bin.tar.gz"
[ -z "$localNgxDownloadUrlForCentos6x64" ] && export localNgxDownloadUrlForCentos6x64="file://${pkgRepoLocalDir}/nginx-1.18.0-centos6-x64-bin.tar"
[ -z "$localNgxDownloadUrlForCentos7x64" ] && export localNgxDownloadUrlForCentos7x64="file://${pkgRepoLocalDir}/nginx-1.20.0-centos7-x64-bin.tar.gz"
[ -z "$localNgxDownloadUrlForCentos8x64" ] && export localNgxDownloadUrlForCentos8x64="file://${pkgRepoLocalDir}/nginx-1.21.0-centos8-x64-bin.tar.gz"
[ -z "$localNgxDownloadUrlForUbuntu20x64" ] && export localNgxDownloadUrlForUbuntu20x64="file://${pkgRepoLocalDir}/nginx-1.18.0-ubuntu20-x64-bin.tar.gz"

# Delopy(standalone mode).
export deployStandaloneBuildModules=(
  "standalone-iam,18080,external,${currDir}/xcloud-iam/xcloud-iam-service-starter-all/target"
  "standalone-dopaas,20000,external,${currDir}/xcloud-dopaas/xcloud-dopaas-all-starter/target"
)

# Deploy(cluster mode).
export deployClusterNodesConfigPath="$currDir/deploy-host.csv"
# Format: {appName},{appPort},{appType},{buildAssetsDir}
export deployClusterBuildModules=(
  "iam-data,18082,internal,${currDir}/xcloud-iam/xcloud-iam-service-starter-data/target"
  "iam-facade,18081,internal,${currDir}/xcloud-iam/xcloud-iam-service-starter-facade/target"
  "iam-web,18080,external,${currDir}/xcloud-iam/xcloud-iam-service-starter-web/target"
  "home-facade,17001,internal,${currDir}/xcloud-dopaas/xcloud-dopaas-home/xcloud-dopaas-home-service-starter-facade/target"
  "home-manager,17000,external,${currDir}/xcloud-dopaas/xcloud-dopaas-home/xcloud-dopaas-home-service-starter-manager/target"
  "cmdb-facade,17011,internal,${currDir}/xcloud-dopaas/xcloud-dopaas-cmdb/xcloud-dopaas-cmdb-service-starter-facade/target"
  "cmdb-manager,17010,external,${currDir}/xcloud-dopaas/xcloud-dopaas-cmdb/xcloud-dopaas-cmdb-service-starter-manager/target"
  "uci-facade,17021,internal,${currDir}/xcloud-dopaas/xcloud-dopaas-uci/xcloud-dopaas-uci-service-starter-facade/target"
  "uci-server,17020,external,${currDir}/xcloud-dopaas/xcloud-dopaas-uci/xcloud-dopaas-uci-service-starter-server/target"
  #"ucm-facade,17031,internal,${currDir}/xcloud-dopaas/xcloud-dopaas-ucm/xcloud-dopaas-ucm-service-starter-facade/target"
  #"ucm-server,17030,external,${currDir}/xcloud-dopaas/xcloud-dopaas-ucm/xcloud-dopaas-ucm-service-starter-server/target"
  "lcdp-facade,17041,internal,${currDir}/xcloud-dopaas/xcloud-dopaas-lcdp/xcloud-dopaas-lcdp-service-starter-facade/target"
  "lcdp-manager,17040,external,${currDir}/xcloud-dopaas/xcloud-dopaas-lcdp/xcloud-dopaas-lcdp-service-starter-manager/target"
  "udm-facade,17051,internal,${currDir}/xcloud-dopaas/xcloud-dopaas-udm/xcloud-dopaas-udm-service-starter-facade/target"
  "udm-manager,17050,external,${currDir}/xcloud-dopaas/xcloud-dopaas-udm/xcloud-dopaas-udm-service-starter-manager/target"
  "umc-collector,17063,external,${currDir}/xcloud-dopaas/xcloud-dopaas-umc/xcloud-dopaas-umc-service-starter-collector/target"
  "umc-tracker,17062,external,${currDir}/xcloud-dopaas/xcloud-dopaas-umc/xcloud-dopaas-umc-service-starter-tracker/target"
  "umc-facade,17061,internal,${currDir}/xcloud-dopaas/xcloud-dopaas-umc/xcloud-dopaas-umc-service-starter-facade/target"
  "umc-manager,17060,external,${currDir}/xcloud-dopaas/xcloud-dopaas-umc/xcloud-dopaas-umc-service-starter-manager/target"
  "urm-facade,17071,internal,${currDir}/xcloud-dopaas/xcloud-dopaas-urm/xcloud-dopaas-urm-service-starter-facade/target"
  "urm-manager,17070,external,${currDir}/xcloud-dopaas/xcloud-dopaas-urm/xcloud-dopaas-urm-service-starter-manager/target"
  "uds-facade,17081,internal,${currDir}/xcloud-dopaas/xcloud-dopaas-uds/xcloud-dopaas-uds-service-starter-facade/target"
  "uds-manager,17080,external,${currDir}/xcloud-dopaas/xcloud-dopaas-uds/xcloud-dopaas-uds-service-starter-manager/target"
  #"uos-facade,17091,internal,${currDir}/xcloud-dopaas/xcloud-dopaas-uos/xcloud-dopaas-uos-service-starter-facade/target"
  #"uos-manager,17090,external,${currDir}/xcloud-dopaas/xcloud-dopaas-uos/xcloud-dopaas-uos-service-starter-manager/target"
)

# ----------------------- Frontend environment definition. ----------------------------------------

# NodeJS install.
[ -z "$nodejsDownloadTarUrl" ] && export nodejsDownloadTarUrl="https://nodejs.org/dist/v14.16.1/node-v14.16.1-linux-x64.tar.xz"
[ -z "$secondaryNodejsDownloadTarUrl" ] && export secondaryNodejsDownloadTarUrl="https://nodejs.org/dist/v14.16.1/node-v14.16.1-linux-x64.tar.xz"
[ -z "$localNodejsDownloadTarUrl" ] && export localNodejsDownloadTarUrl="file://${pkgRepoLocalDir}/node-v14.16.1-linux-x64.tar.xz"
[ -z "$nodejsInstallDir" ] && export nodejsInstallDir="$workspaceDir"
# Deploy frontend.
[ -z "$deployFrontendSkip" ] && export deployFrontendSkip="false"
[ -z "$deployFrontendAppBaseDir" ] && export deployFrontendAppBaseDir="/usr/share/nginx/html"

# ----------------------- APPs runtime environment definition. ------------------------------------------------

globalExportedEnvStr=""
# Init and merge the depends services configuration.
function initRuntimeEnvConfig() {
  # Common environment configuration.
  [ -z "$springProfilesActive" ] && export springProfilesActive="pro"
  globalExportedEnvStr="${globalExportedEnvStr}\nexport springProfilesActive='${springProfilesActive}'"

  # (IAM) environment configuration.
  [ -z "$IAM_DB_URL" ] && export IAM_DB_URL="jdbc:mysql://localhost:3306/iam?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true"
  [ "$deployDebug" == "true" ] && logDebug "Exported env: IAM_DB_URL='$(eval echo $IAM_DB_URL)'"

  [ -z "$IAM_DB_USER" ] && export IAM_DB_USER="iam"
  [ "$deployDebug" == "true" ] && logDebug "Exported env: IAM_DB_USER='$(eval echo $IAM_DB_USER)'"

  [ -z "$IAM_DB_PASSWD" ] && export IAM_DB_PASSWD="123456"
  [ "$deployDebug" == "true" ] && logDebug "Exported env: IAM_DB_PASSWD='$(eval echo $IAM_DB_PASSWD)'"

  [ -z "$IAM_REDIS_PASSWD" ] && export IAM_REDIS_PASSWD="123456"
  [ "$deployDebug" == "true" ] && logDebug "Exported env: IAM_REDIS_PASSWD='$(eval echo $IAM_REDIS_PASSWD)'"

  [ -z "$IAM_REDIS_NODES" ] && export IAM_REDIS_NODES="localhost:6379"
  [ "$deployDebug" == "true" ] && logDebug "Exported env: IAM_REDIS_NODES='$(eval echo $IAM_REDIS_NODES)'"

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
    [ "$deployDebug" == "true" ] && logDebug "Exported env: STANDALONE_DOPAAS_DB_URL='$(eval echo $STANDALONE_DOPAAS_DB_URL)'"

    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_DB_USER='${STANDALONE_DOPAAS_DB_USER}'"
    [ "$deployDebug" == "true" ] && logDebug "Exported env: STANDALONE_DOPAAS_DB_USER='$(eval echo $STANDALONE_DOPAAS_DB_USER)'"

    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_DB_PASSWD='${STANDALONE_DOPAAS_DB_PASSWD}'"
    [ "$deployDebug" == "true" ] && logDebug "Exported env: STANDALONE_DOPAAS_DB_PASSWD='$(eval echo $STANDALONE_DOPAAS_DB_PASSWD)'"

    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_REDIS_PASSWD='${STANDALONE_DOPAAS_REDIS_PASSWD}'"
    [ "$deployDebug" == "true" ] && logDebug "Exported env: STANDALONE_DOPAAS_REDIS_PASSWD='$(eval echo $STANDALONE_DOPAAS_REDIS_PASSWD)'"

    globalExportedEnvStr="${globalExportedEnvStr}\nexport STANDALONE_DOPAAS_REDIS_NODES='${STANDALONE_DOPAAS_REDIS_NODES}'"
    [ "$deployDebug" == "true" ] && logDebug "Exported env: STANDALONE_DOPAAS_REDIS_NODES='$(eval echo $STANDALONE_DOPAAS_REDIS_NODES)'"
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

      [ "$deployDebug" == "true" ] && logDebug "Exported env: $key1='$(eval echo '$'$key1)'"
      [ "$deployDebug" == "true" ] && logDebug "Exported env: $key2='$(eval echo '$'$key2)'"
      [ "$deployDebug" == "true" ] && logDebug "Exported env: $key3='$(eval echo '$'$key3)'"
      [ "$deployDebug" == "true" ] && logDebug "Exported env: $key4='$(eval echo '$'$key4)'"
      [ "$deployDebug" == "true" ] && logDebug "Exported env: $key5='$(eval echo '$'$key5)'"

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

# --------------------------------------- Utility definition. -------------------------------------------------

function getCurrPid() {
  local pid=$!
  if [ "$pid" == "" ]; then
    echo "main"
    return 0
  fi
  echo "pid/$pid"
  return 0
}

# Core logging.
# e.g1: log "error" "Failed to xxx"
# e.g2: log "xxx complete!"
function log() {
  local logLevel="\033[33mINFO\033[0m "
  local logContent=$1
  if [[ $# > 1 ]]; then
    logLevel=$1
    logContent=$2
  fi
  local logMsg="[$logLevel] $(date '+%Y-%m-%d %H:%M:%S') - [$(getCurrPid)] $logContent"
  echo -e "$logMsg"
  echo -e "$logMsg" >> ${logFile}
}

# Debug logging.
function logDebug() {
  log "\033[38mDEBUG\033[0m" "$@"
}

# Error logging.
function logErr() {
  log "\033[31mERROR\033[0m" "$@"
}

initRuntimeEnvConfig
