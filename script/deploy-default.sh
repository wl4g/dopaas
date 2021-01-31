#!/bin/bash

#/*
# * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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

echo ""
echo " Welcome to XCloud DevSecOps Deploying Script(Physical Host) ! "
echo ""
echo " Wiki: https://github.com/wl4g/xcloud-devops/blob/master/README.md or https://gitee.com/wl4g/xcloud-devops/blob/master/README_CN.md"
echo " Authors: <Wanglsir@gmail.com, 983708408@qq.com>"
echo " Version: 2.2.2"
echo " Time: "$(date -d today +"%Y-%m-%d %H:%M:%S")
echo ""


# Global definitions
CURR_DIR="$(cd "`dirname "$0"`"/..; pwd)" && cd $CURR_DIR
DEPLOY_MODE="cluster" # Options: standalone|cluster
DEPLOY_BASE_DIR="/opt/apps/acm"
BUILD_PKG_TYPE="mvnAssTar" # Options: mvnAssTar|springExecJar
GIT_BASE_URL="https://github.com/wl4g" # Options(e.g): https://github.com/wl4g|https://gitee.com/wl4g
GIT_XCLOUD_COMPONENT_URL="${GIT_BASE_URL}/xcloud-component"
GIT_XCLOUD_IAM_URL="${GIT_BASE_URL}/xcloud-iam"
GIT_XCLOUD_DEVOPS_URL="${GIT_BASE_URL}/xcloud-devops"
APACHE_MVN_DL_TAR_URL="https://mirror.bit.edu.cn/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz"
APACHE_MVN_INSTALL_DIR="/opt/apps/"

MVN="$(command -v mvn)"

# Check pre dependencies
if [ ! -n "$(command -v git)" ]; then 
  echo "No git, auto installing git ..."
  if [ -n "$(command -v yum)" ]; then
    yum install -y git
  elif [ -n "$(command -v apt)" ]; then
    apt install -y git
  else
    echo "Please install git!"
    exit -1
  fi
fi

if [ ! -n "$(command -v mvn)" ]; then
  echo "No mvn, auto installing maven ..."
  wget $APACHE_MVN_DL_TAR_URL
  tar -xf apache-maven-*-bin.tar.gz
  mv apache-maven-* $APACHE_MVN_INSTALL_DIR
  MVN="$APACHE_MVN_INSTALL_DIR/bin/mvn"
fi


# Pull and compile
function pullAndCompile() {
  projectName=$1 # e.g xcloud-devops
  cloneUrl=$2
  if [ ! -d "$projectName" ]; then
    echo "Git clone $projectName from $cloneUrl ..."
    git clone $cloneUrl
    echo "Compiling $projectName ..."
    $MVN clean install -DskipTests -T 2C -U -P $BUILD_PKG_TYPE
  else
    echo "Git pull $projectName..."
    cd $CURR_DIR/$projectName
    git pull
    echo "Compiling $projectName ..."
    $MVN clean install -DskipTests -T 2C -U -P $BUILD_PKG_TYPE
  fi
}

# Pull & compile all
pullAndCompile "xcloud-component" $GIT_XCLOUD_COMPONENT_URL
pullAndCompile "xcloud-iam" $GIT_XCLOUD_IAM_URL
pullAndCompile "xcloud-devops" $GIT_XCLOUD_DEVOPS_URL

# Deploy & startup all

# The 'standalone' mode is only deployed to the local host
if [ "$DEPLOY_MODE" == "stadalone" ]; then
  echo "Deploying all for standalone ..."

  # TODO

  echo "Deploy to local completion !"
  exit 0
fi

# The 'cluster' mode is deployed to the remote nodes.

# TODO

echo "Deploy to remote nodes completion !"
exit 0

