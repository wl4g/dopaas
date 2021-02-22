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

# Initialization.
currDir=$([ "$currDir" == "" ] && echo "$(cd "`dirname "$0"`"/; pwd)" || echo $currDir)
. ${currDir}/deploy-env.sh

# Common variables.
cmdMvn="$(command -v mvn)"
if [ "$(echo $apacheMvnLocalRepoDir|cut -c 1-5)" == "/root" ]; then
  apacheMvnLocalRepoDirOfUser="root"
elif [ "$(echo $apacheMvnLocalRepoDir|cut -c 1-5)" == "/home" ]; then
  apacheMvnLocalRepoDirOfUser="$(echo $apacheMvnLocalRepoDir|awk -F '/' '{print $3}')"
else
  logErr "Invalid maven local repository path. for example: \$USER/.m2/repository"
  exit -1
fi

function getCurrPid() {
  pid=$!
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
  logLevel="INFO "
  logContent=$1
  if [[ $# > 1 ]]; then
    logLevel=$(echo "$1" | tr 'a-z' 'A-Z') # To UpperCase
    logContent=$2
  fi
  logMsg="[$logLevel] "$(date -d today +'%y%m%d/%H:%M:%S')" - [$(getCurrPid)] $logContent"
  echo -e $logMsg
  echo -e $logMsg >> ${logFile}
}
# Error logging.
function logErr() {
  log "ERROR" "$@"
}

# Check pre dependencies.
function checkPreDependencies() {
  log "Checking OS software pre dependencies ..."
  # Check java
  if [ ! -n "$(command -v java)" ]; then
    log "JDK package not detected, please install at least jdk8+ first!"
    exit -1
  fi
  javaVersion=$(java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | awk '{print $3}')
  numJavaVersion=$(echo $javaVersion|sed 's/\.//g'|sed 's/_//g')
  if [[ ${numJavaVersion} -lt 18 ]]; then # must is jdk1.8+
    log "Installed java version: ${javaVersionJDK}, must be jdk8+, please reinstallation!"
    exit -1
  fi
  # Check git
  if [ ! -n "$(command -v git)" ]; then
    log "No such command git, auto installing git ..."
    if [ -n "$(command -v yum)" ]; then
      sudo yum install -y git
    elif [ -n "$(command -v apt)" ]; then
      sudo apt install -y git
    elif [ -n "$(command -v apt-get)" ]; then
      sudo apt-get install -y git
    else
      logErr "Failed to auto install git! Please manual installation!"
      exit -1
    fi
  fi
  # Check maven
  if [ ! -n "$(command -v mvn)" ]; then
    log "No such command mvn, auto installing maven ..."
    if [ ! -n "$(command -v wget)" ]; then
      wget $apacheMvnDownloadTarUrl
    else
      curl -O $apacheMvnDownloadTarUrl
    fi
    tar -xf apache-maven-*-bin.tar.gz
    mv apache-maven-* $apacheMvnInstallDir
    cmdMvn="$apacheMvnInstallDir/bin/mvn"
  fi
}

function installSshpass() {
  if [ -z "$(command -v sshpass)" ]; then
    log "Installzation sshpass ..."
    if [ -n "$(command -v yum)" ]; then
      sudo yum install -y sshpass
    elif [ -n "$(command -v apt)" ]; then
      sudo apt install -y sshpass
    elif [ -n "$(command -v apt-get)" ]; then
      sudo apt-get install -y sshpass
    else
      logErr "Failed to auto install sshpass! Please manual installation!"
      exit -1
    fi
  fi
}

# Exec remote SSH command.
# for testing => doRemoteCmd "root" "mypassword" "localhost" "echo 11" "true"
function doRemoteCmd() {
  user=$1
  password=$2
  host=$3
  cmd=$4
  exitOnFail=$5
  # Check args.
  if [[ $# < 5 || "$user" == "" || "$host" == "" ]]; then
    log "ssh-passwordless authorization User/Host/Command must required or args should be at least 4"
    exit -1
  fi
  # Check whether it is login passwordless.(When the password is empty)
  if [ "$password" == "" ]; then
    # If need to enter a password, it will timeout.
    hasPasswordless=$(timeout 3 ssh $user@$host echo "YES" || echo "NO")
    # Direct exec remote cmd.
    if [ "$hasPasswordless" == "YES" ]; then
      ssh -o StrictHostKeyChecking=no -p 22 -i $HOME/.ssh/id_rsa.pub $user@$host $cmd
      [[ $? -ne 0 && "$exitOnFail" == "true" ]] && exit -1
    else
      logErr "Failed to exec remote, bacause not ssh-passwordless authorized!"
      exit -1
    fi
  else # Exec remote by sshpass
    installSshpass
    sshpass -p $password ssh -o StrictHostKeyChecking=no -p 22 $user@$host $cmd
    [[ $? -ne 0 && "$exitOnFail" == "true" ]] && exit -1
  fi
}

# SCP files to remote.
# for testing => doScp "root" "mypassword" "localhost" "/root/aa.txt" "/home/myuser/aa.txt"
function doScp() {
  user=$1
  password=$2
  host=$3
  localPath=$4
  remotePath=$5
  exitOnFail=$6
  # Check args.
  if [[ $# < 6 || "$user" == "" || "$host" == "" ]]; then
    log "ssh-passwordless authorization User/Host/LocalPath/RemotePath must required or args should be at least 5"; exit -1
  fi
  # Check whether it is login passwordless.(When the password is empty)
  if [ "$password" == "" ]; then
    # If need to enter a password, it will timeout.
    hasPasswordless=$(timeout 3 ssh $user@$host echo "YES" || echo "NO")
    # Direct exec remote cmd.
    if [ "$hasPasswordless" == "YES" ]; then
      scp $localPath $user@$host:$remotePath
      [[ $? -ne 0 && "$exitOnFail" == "true" ]] && exit -1
    else
      logErr "Failed to scp files to remote, bacause not ssh-passwordless authorized!"; exit -1
    fi
  else # Exec remote by sshpass
    installSshpass
    sshpass -p $password scp $localPath $user@$host:$remotePath
    [[ $? -ne 0 && "$exitOnFail" == "true" ]] && exit -1
  fi
}

# Make services scripts.
function makeServicesScript() {
  echo "" # TODO
}
