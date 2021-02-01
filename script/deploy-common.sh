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

# Macro definitions.
if [ -z "$currDir" ]; then
  currDir="$(cd "`dirname "$0"`"/; pwd)"
fi
cmdMvn="$(command -v mvn)"

# Check pre dependencies.
function checkPreDependencies() {
  # Check java
  if [ ! -n "$(command -v java)" ]; then
    echo "JDK package not detected, please install at least jdk8 first+"
    exit -1
  fi
  javaVersion=$(java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | awk '{print $3}')
  numJavaVersion=$(echo $javaVersion|sed 's/\.//g'|sed 's/_//g')
  if [[ ${numJavaVersion} -lt 18 ]]; then # must is jdk1.8+
    echo "Current java version: ${javaVersionJDK}, must be jdk8+, Please re-installation"
    exit -1
  fi
  # Check git
  if [ ! -n "$(command -v git)" ]; then
    echo "No git, auto installing git ..."
    if [ -n "$(command -v yum)" ]; then
      sudo yum install -y git
    elif [ -n "$(command -v apt)" ]; then
      sudo apt install -y git
    else
      echo "Please install git!"
      exit -1
    fi
  fi
  # Check maven
  if [ ! -n "$(command -v mvn)" ]; then
    echo "No mvn, auto installing maven ..."
    wget $apacheMvnDownloadTarUrl
    tar -xf apache-maven-*-bin.tar.gz
    mv apache-maven-* $apacheMvninstallDir
    cmdMvn="$apacheMvninstallDir/bin/mvn"
  fi
}

