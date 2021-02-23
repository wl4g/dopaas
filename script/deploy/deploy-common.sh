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
  local logLevel="INFO "
  local logContent=$1
  if [[ $# > 1 ]]; then
    logLevel=$(echo "$1" | tr 'a-z' 'A-Z') # To UpperCase
    logContent=$2
  fi
  local logMsg="[$logLevel] "$(date -d today +'%y%m%d/%H:%M:%S')" - [$(getCurrPid)] $logContent"
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
  local javaVersion=$(java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | awk '{print $3}')
  local numJavaVersion=$(echo $javaVersion|sed 's/\.//g'|sed 's/_//g')
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
    # Check installization
    [ $? -ne 0 ] && logErr "Failed to auto install git! Please manual installation!" && exit -1
  fi
  # Check maven
  if [ ! -n "$(command -v mvn)" ]; then
    log "No such command mvn, auto installing maven ..."
    cd $currDir
    curl -O "$apacheMvnDownloadTarUrl"
    if [ $? -ne 0 ]; then # Fallback
      curl -O "$secondaryApacheMvnDownloadTarUrl"
    fi
    # Check installization result.
    [ $? -ne 0 ] && logErr "Failed to auto install mvn! Please manual installation!" && exit -1
    local mvnHome="$apacheMvnInstallDir/apache-maven-current"
    mkdir -p $mvnHome
    \rm -rf $mvnHome/* # Cleanup older
    tar -zxf apache-maven-*-bin.tar.gz -C $mvnHome
    cmdMvn="$mvnHome/bin/mvn"
  fi
  log "Use installed maven command: $cmdMvn"
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
      logErr "Failed to auto install sshpass! Please manual installation!"; exit -1
    fi
    # Check installization result.
    [ $? -ne 0 ] && logErr "Failed to auto install sshpass! Please manual installation!" && exit -1
  fi
}

# Exec remote SSH command.
# for testing => doRemoteCmd "root" "mypassword" "localhost" "echo 11" "true"
function doRemoteCmd() {
  local user=$1
  local password=$2
  local host=$3
  local cmd=$4
  local exitOnFail=$5
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
# for testing => doScp "root" "mypassword" "localhost" "/root/aa.txt" "/home/myuser/aa.txt" "true"
function doScp() {
  local user=$1
  local password=$2
  local host=$3
  local localPath=$4
  local remotePath=$5
  local exitOnFail=$6
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

# Installization app services scripts.
# for testing => checkInstallService "iam-data" "root" "cn#!7%7^^" "10.0.0.160"
function checkInstallService() {
  local appName=$1
  local user=$2
  local password=$3
  local host=$4
  if [[ $# < 4 || "$appName" == "" || "$user" == "" || "$host" == "" ]]; then
    logErr "Cannot installization app services, args appName/user/host is required !"
    return -1
  fi
  # Check & install app services(if necessary)
  local hasServiceFile=$(doRemoteCmd "$user" "$password" "$host" "echo $([ -f /etc/init.d/$appName.service ] && echo Y || echo N)" "true")
  [ "$hasServiceFile" == "Y" ] && return 0 # Skip installed

  log "[$appName/$host] Not detected /etc/init.d/$appName.services script, installing ..."
  local appVersion="master"
  local appMainClass="com.wl4g."$(echo $appName|awk -F '-' '{print toupper(substr($1,1,1))substr($1,2)toupper(substr($2,1,1))substr($2,2)}') #eg: doc-manager => DocManager
  local appInstallDir="${deployBaseDir}/${appName}-package"
  local appHome="$appInstallDir/${appName}-${appVersion}-bin"
  local appClasspath="$appHome/libs"
  local appLogDir="/mnt/disk1/log/$appName"
  local appLogFile="$appLogDir/$appName.log"
  local appLogStdoutFile="$appLogDir/$appName.stdout"
  local appDataDir="/mnt/disk1/$appName"
  local appUser="$appName"
  local appGroup="$appUser"
  local appOpts="$appOpts --spring.application.name=${appName}"
  local appOpts="$appOpts --spring.profiles.active=pro"
  local appOpts="$appOpts --server.tomcat.basedir=${appDataDir}"
  local appOpts="$appOpts --logging.file=${appLogFile}"

  local javaExec="java"
  #local jvmDebugOpts="-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n"
  #local jvmJmxOpts="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=5000"
  local jvmHeapOpts="-Xms256M -Xmx1G"
  local jvmPerformanceOpts="-XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:+DisableExplicitGC -Djava.awt.headless=true"
  local jvmJavaOpts="-Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom"
  local jvmGcLogFile="${appLogDir}/${appName}-gc.log"
  local jvmGcLogOpts="-Xloggc:${jvmGcLogFile} -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps \
-XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"
  # Java 9+
  if [[ "$($JAVA -version 2>&1 | sed -E -n 's/.* version "([^.-]*).*"/\1/p')" -ge "9" ]] ; then
    jvmGcLogOpts="-Xlog:gc*:file=${jvmGcLogFile}:time,tags:filecount=10,filesize=102400"
  fi
  if [ "$buildPkgType" == "mvnAssTar" ]; then
    local appRunCmd="java -server $jvmDebugOpts $jvmHeapOpts $jvmPerformanceOpts $jvmGcLogOpts $jvmJmxOpts $jvmJavaOpts -cp $appClasspath $appMainClass $appOpts"
    local appShellRunCmd="$javaExec -client -Dprompt=$appName -Dservname=$appName $shellPort -cp .:$appHome/libs/* com.wl4g.ShellBootstrap"
  elif [ "$buildPkgType" == "springExecJar" ]; then
    local appRunCmd="java -server $jvmDebugOpts $jvmHeapOpts $jvmPerformanceOpts $jvmGcLogOpts $jvmJmxOpts $jvmJavaOpts -jar ${appName}-${appVersion}-bin.jar $appOpts"
    # TODO, The mainclass of jar cannot be executed as specified by classpath. 
    #local appShellRunCmd="$javaExec -client -Dprompt=$appName -Dservname=$appName $shellPort -cp .:$appHome/${appName}-${appVersion}-bin.jar com.wl4g.ShellBootstrap"
  fi

  mkdir -p $appInstallDir
  mkdir -p $appHome
  mkdir -p $appLogDir
  mkdir -p $appDataDir
  touch $appLogFile
  touch $appLogStdoutFile
  if [ "$appGroup" != "root" ]; then
    if [ -z "$(grep "^$appGroup" /etc/group)" ]; then
      groupadd $appGroup
    fi
  fi
  if [ "$appUser" != "root" ]; then
    if [ -z "$(grep "^$appUser" /etc/passwd)" ]; then
      useradd -g $appGroup $appUser
    fi
  fi
  chown -R $appUser:$appGroup $appInstallDir
  chown -R $appUser:$appGroup $appLogDir
  chown -R $appUser:$appGroup $appDataDir

  # Make app service script.
  local tmpServiceFile=$workspaceDir/${appName}.service
cat<<EOF>$tmpServiceFile
#!/bin/bash
# chkconfig: - 85 15
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

[ -f /etc/sysconfig/network ] && . /etc/sysconfig/network
[ "\$NETWORKING" = "no" ] && exit 0

# Load the user environment, e.g: get the secret key of decrypting database password.
[ -f "/etc/profile" ] && . /etc/profile
[ -f "/etc/bashrc" ] && . /etc/bashrc
[ -f "/etc/bash.bashrc" ] && . /etc/bash.bashrc # e.g ubuntu19+
if [ "$appUser" != "root" ]; then
  [ -f "/home/$appUser/.bash_profile" ] && . /home/$appUser/.bash_profile
  [ -f "/home/$appUser/.bashrc" ] && . /home/$appUser/.bashrc
fi
if [ "\$USER" == "root" ]; then
  . "/root/.bash_profile"
  . "/root/.bashrc"
fi

function start() {
  local pids=\$(getPids)
  if [ -z "\$pids" ]; then
    nohup $appRunCmd > $appLogStdoutFile 2>&1 < /dev/null &

    echo -n "Starting $appName ..."
    while true
    do
      pids=\$(getPids)
      if [ "\$pids" == "" ]; then
        echo -n ".";
        sleep 0.8;
      else
        echo \$pids >"${appDataDir}/${appName}.pid"
        break;
      fi
    done
    echo -e "\nStarted $appName on "\$pids
    \rm -rf $appLogStdoutFile
  else
    echo "$appName process is running "\$pids
  fi
}

function stop() {
  local pids=\$(getPids)
  if [ -z "\$pids" ]; then
    echo "$appName not running!"
  else
    echo -n "Stopping $appName for \$pids ..."
    kill -s TERM \$pids
    while true
    do
      pids=\$(getPids)
      if [ "\$pids" == "" ]; then
        \rm -f ${appDataDir}/${appName}.pid
        break;
      else
        echo -n ".";
        sleep 0.8;
      fi
    done
    echo -e "\nStopped $appName !"
  fi
}

function status() {
  ps -ef | grep -v grep | grep $appHome
}

function console() {
  exec $appShellRunCmd
}

function getPids() {
  local pids=\$(ps ax | grep java | grep -i $appHome | grep -v grep | awk '{print \$1}')
  echo \$pids # Output execution result value.
  return 0 # Return the execution result code.
}

# --- Main call. ---
CMD=\$1
case \$CMD in
  status)
    status
    ;;
  start)
    start
    ;;
  stop)
    stop
    ;;
  restart)
    stop
    start
    ;;
  shell)
    console
    ;;
    *)
  echo \$"Usage: {start|stop|restart|status|shell}"
  exit 2
esac
EOF

  # Transfer services script to remote.
  doScp "$user" "$password" "$host" "$tmpServiceFile" "/etc/init.d/${appName}.service" "true"
  doRemoteCmd "$user" "$password" "$host" "chmod 750 /etc/init.d/${appName}.service" "true"
  \rm -rf $tmpServiceFile
}

