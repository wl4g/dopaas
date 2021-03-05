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

# Init.
[ -z "$currDir" ] && export currDir=$(cd "`dirname $0`"/ ; pwd)
. ${currDir}/deploy-env.sh

# Common variables.
export cmdMvn="$(command -v mvn)"
if [ "$(echo $apacheMvnLocalRepoDir|cut -c 1-5)" == "/root" ]; then
  apacheMvnLocalRepoDirOfUser="root"
elif [ "$(echo $apacheMvnLocalRepoDir|cut -c 1-5)" == "/home" ]; then
  apacheMvnLocalRepoDirOfUser="$(echo $apacheMvnLocalRepoDir|awk -F '/' '{print $3}')"
else
  logErr "Invalid maven local repository path. for example: \$USER/.m2/repository"; exit -1
fi

# Security delete local object.
function secDeleteLocal() {
  local targetPath=$1
  local result=""
  if [[ "$targetPath" != "" && "$targetPath" != "/" && "$targetPath" != "/bin"* && "$targetPath" != "/sbin"* ]]; then
    unalias -a rm
    result=$(\rm -rf $targetPath)
  fi
  local delStatus="$?"
  [ "$delStatus" -ne 0 ] && echo "Deletion can't seem to success. target: $targetPath, causeBy: $result"
  return $delStatus
}

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

# Check and install basic software.
function checkInstallBasicSoftware() {
  log "Checking basic software pre dependencies ..."
  # Check java/javac
  if [[ "$(command -v java)" == "" || "$(command -v javac)" == "" ]]; then
    log "Not detected java and javac, please install at least jdk8+, note not just JRE !"; exit -1
  fi
  local javaVersion=$(java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | awk '{print $3}')
  local numJavaVersion=$(echo $javaVersion|sed 's/\.//g'|sed 's/_//g')
  if [[ ${numJavaVersion} -lt 18 ]]; then # must is jdk1.8+
    log "Installed java version: ${javaVersionJDK}, must be jdk8+, please reinstallation!"; exit -1
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
      logErr "Failed to auto install git! Please manual installation!"; exit -1
    fi
    # Check installization
    [ $? -ne 0 ] && logErr "Failed to auto install git! Please manual installation!" && exit -1
  fi
  # Check maven
  local mvnHome="$apacheMvnInstallDir/apache-maven-current"
  export cmdMvn="$mvnHome/bin/mvn"
  if [[ ! ("$(command -v mvn)" != "" || -d "$mvnHome") ]]; then
    log "No such command mvn, auto installing maven ..."
    cd $workspaceDir
    local tmpTarFile="$workspaceDir/apache-maven-current.tar"
    curl -o "$tmpTarFile" "$apacheMvnDownloadTarUrl"
    if [ $? -ne 0 ]; then # Fallback
      curl -o "$tmpTarFile" "$secondaryApacheMvnDownloadTarUrl"
    fi
    # Check installization result.
    [ $? -ne 0 ] && logErr "Failed to auto install mvn! Please manual installation!" && exit -1
    mkdir -p $mvnHome
    secDeleteLocal "$mvnHome/*" # Rmove old files(if necessary)
    tar -xf "$tmpTarFile" --strip-components=1 -C "$mvnHome"
    secDeleteLocal "$tmpTarFile" # Cleanup
    # Use china fast maven mirror to settings.xml
    if [ "$isNetworkInGfwWall" == "Y" ]; then # see: deploy-boot.sh
      log "Currently in china gfw network, configuring aliyun maven fast mirror ..."
cat<<EOF>$mvnHome/conf/settings.xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>nexus-aliyun</id>
      <mirrorOf>central</mirrorOf>
      <name>Nexus aliyun</name>
      <url>http://maven.aliyun.com/nexus/content/groups/public</url>
    </mirror>
  </mirrors>
</settings>
EOF
    fi
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
  # Check must args.
  if [[ $# < 4 || "$user" == "" || "$host" == "" || "$cmd" == "" ]]; then
    log "Exec remote command User/Host/Command is required and args should be 4"; exit -1
  fi
  # Check host is locally? (direct exec local command)
  if [[ "$host" == "localhost" || "$host" == "127.0.0.1" ]]; then
    bash -c "$cmd"
    return $?
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
      logErr "Failed to exec remote, bacause not ssh-passwordless authorized!"; exit -1
    fi
  else # Exec remote by sshpass
    installSshpass
    sshpass -p "$password" ssh -o StrictHostKeyChecking=no -p 22 "$user"@"$host" "$cmd"
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
  local exitOnFail=$6 # optional
  # Check args.
  if [[ $# < 5 || "$user" == "" || "$host" == "" || "$localPath" == "" || "$remotePath" == "" ]]; then
    log "ssh-passwordless authorization User/Host/LocalPath/RemotePath is required and args should be 5"; exit -1
  fi
  # Check host is locally? (direct exec local command)
  if [[ "$host" == "localhost" || "$host" == "127.0.0.1" ]]; then
    unalias -a cp
    cp -Rf "$localPath" "$remotePath"
    return $?
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
      logErr "Failed to scp \"$localPath\" to remote, bacause not ssh-passwordless authorized!"; exit -1
    fi
  else # Exec remote by sshpass
    installSshpass
    sshpass -p $password scp $localPath $user@$host:$remotePath
    [[ $? -ne 0 && "$exitOnFail" == "true" ]] && exit -1
  fi
}

# Check and install remote services script.
# for testing => checkInstallServiceScript "iam-data" "root" "cn#!7%7^^" "10.0.0.160"
function checkInstallServiceScript() {
  local appName=$1
  local user=$2
  local password=$3
  local host=$4
  local springProfilesActive=$5
  local isCheckInstalled=$6
  if [[ $# < 4 || "$appName" == "" || "$user" == "" || "$host" == "" ]]; then
    logErr "[$appName/$host] Cannot installization app services, args appName/user/host is required and args should be 4 !"; exit -1
  fi
  # Check installed service script?
  if [ "$isCheckInstalled" == "true" ]; then
    local hasServiceFile=$(doRemoteCmd "$user" "$password" "$host" "echo $([ -f /etc/init.d/$appName.service ] && echo Y || echo N)" "true")
    [ "$hasServiceFile" == "Y" ] && return 0 # Skip installed
  fi
  log "[$appName/$host] Not detected /etc/init.d/$appName.services script, installing ..."
  local appVersion="master"
  local appMainClass="com.wl4g."$(echo $appName|awk -F '-' '{print toupper(substr($1,1,1))substr($1,2)toupper(substr($2,1,1))substr($2,2)toupper(substr($3,1,1))substr($3,2)}') #eg: doc-manager => DocManager
  local appInstallDir="${deployAppBaseDir}/${appName}-package"
  local appHome="${appInstallDir}/${appName}-${appVersion}-bin"
  local appClasspath=".:$appHome/conf:${appHome}/libs/*"
  local appDataDir="${deployAppDataBaseDir}/${appName}"
  local appLogDir="${deployAppLogBaseDir}/${appName}"
  local appLogFile="${appLogDir}/${appName}_\${SPRING_PROFILES_ACTIVE}.log"
  local appLogStdoutFile="${appLogDir}/${appName}.stdout"
  local appUser="$appName"
  local appGroup="$appUser"
  local appOpts="$appOpts --server.tomcat.basedir=${appDataDir}"
  local appOpts="$appOpts --logging.file.name=${appLogFile}"
  local appOpts="$appOpts --spring.application.name=${appName}"
  local appOpts="$appOpts --spring.profiles.active=\${SPRING_PROFILES_ACTIVE}"

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
    local appRunCmd="java -server $jvmDebugOpts $jvmHeapOpts $jvmPerformanceOpts $jvmGcLogOpts $jvmJmxOpts $jvmJavaOpts -jar ${appHome}/${appName}-${appVersion}-bin.jar $appOpts"
    # for example using: java -cp myapp.jar -Dloader.main=com.MyApp org.springframework.boot.loader.PropertiesLauncher
    # see: xcloud-devops/xcloud-devops-ci/xcloud-devops-ci-service-starter-facade/pom.xml#profile.id=springExecJar
    # refer to: https://www.baeldung.com/spring-boot-main-class, https://www.jianshu.com/p/66a101c85485
    local appShellRunCmd="$javaExec -client -Dloader.main=com.wl4g.ShellBootstrap -Dprompt=$appName -Dservname=$appName $shellPort -jar .:$appHome/${appName}-${appVersion}-bin.jar"
  fi

  mkdir -p $appInstallDir
  mkdir -p $appHome
  mkdir -p $appLogDir
  mkdir -p $appDataDir
  if [ "$appGroup" != "root" ]; then
    if [ -z "$(grep "^$appGroup:" /etc/group)" ]; then
      groupadd $appGroup
    fi
  fi
  if [ "$appUser" != "root" ]; then
    if [ -z "$(grep "^$appUser:" /etc/passwd)" ]; then
      useradd -g $appGroup $appUser
    fi
  fi
  chown -R $appUser:$appGroup $appInstallDir
  chown -R $appUser:$appGroup $appLogDir
  chown -R $appUser:$appGroup $appDataDir

  # Make app services script.
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

# Ref external configuration, fallback use defaults.
[ -z "\$SPRING_PROFILES_ACTIVE" ] && export SPRING_PROFILES_ACTIVE="$springProfilesActive"
[ -z "\$DEVOPS_DB_URL" ] && export DEVOPS_DB_URL="$runtimeMysqlUrl"
[ -z "\$DEVOPS_DB_USER" ] && export DEVOPS_DB_USER="$runtimeMysqlUser"
[ -z "\$DEVOPS_DB_PASSWD" ] && export DEVOPS_DB_PASSWD="$runtimeMysqlPassword"
[ -z "\$DEVOPS_REDIS_NODES" ] && export DEVOPS_REDIS_NODES="$runtimeRedisNodes"
[ -z "\$DEVOPS_REDIS_PASSWD" ] && export DEVOPS_REDIS_PASSWD="$runtimeRedisPassword"

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
        break
      fi
    done
    echo -e "\nStarted $appName on "\$pids
    [[ "$appLogStdoutFile" != "" && "$appLogStdoutFile" != "/" ]] && \rm -rf "$appLogStdoutFile"
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
        break
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
  local pids=\$(ps ax | grep java | grep -i "${appHome}" | grep "spring.profiles.active=\${SPRING_PROFILES_ACTIVE}" | grep -v grep | awk '{print \$1}')
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
  secDeleteLocal $tmpServiceFile
}

# Load i18n config scripts.
function loadi18n() {
  local isCN="N"
  if [ -n "$isNetworkInGfwWall" ]; then
    isCN="$isNetworkInGfwWall"
  else
    isCN=$([[ "$LANG" == *"zh_CN"* ]] && echo Y || echo N)
  fi
  if [ "$isCN" == "Y" ]; then
    . $currDir/deploy-i18n-zh_CN.sh
  else
    . $currDir/deploy-i18n-en_US.sh
  fi
}
