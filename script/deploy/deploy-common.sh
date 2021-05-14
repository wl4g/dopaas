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
. $currDir/deploy-base.sh

# Gets OS info and check. return values(centos6_x64,centos7_x64,ubuntu_x64)
function getOsTypeAndCheck() {
  local osType=$([ -n "$(cat /etc/*release|grep -i 'centos linux release 7')" ] && echo centos7)
  if [ -z "$osType" ]; then
    osType=$([ -n "$(cat /etc/*release|grep -i 'centos release 6')" ] && echo centos6)
  fi
  if [ -z "$osType" ]; then
    osType=$([ -n "$(cat /etc/*release|grep -i 'ubuntu')" ] && echo ubuntu)
  fi
  local osArch=$([ -n "$(uname -a|grep -i x86_64)" ] && echo x64)
  echo "${osType}_${osArch}"
}

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

# Check and install infra software.
function checkInstallInfraSoftware() {
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
    log "No command git, auto installing git ..."
    if [ -n "$(command -v yum)" ]; then
      sudo yum install -y git
    elif [ -n "$(command -v apt)" ]; then
      sudo apt install -y git
    elif [ -n "$(command -v apt-get)" ]; then
      sudo apt-get install -y git
    else
      logErr "Failed to auto install git!(currently only the OS is supported: CentOS/Ubuntu), Please manual installation!"; exit -1
    fi
    # Check git installization?
    [ $? -ne 0 ] && logErr "Failed to auto install git! Please manual installation!" && exit -1
  fi

  # Check maven
  local mvnHome="$apacheMvnInstallDir/apache-maven-current"
  if [ -n "$(command -v mvn)" ]; then # Default installed maven.
    export cmdMvn="mvn"
  elif [ ! -d "$mvnHome" ]; then # Need install tmp maven.
    log "No command mvn, auto installing maven ..."
    cd $workspaceDir
    local tmpTarFile="$workspaceDir/apache-maven-current.tar"
    log "Downloading for $apacheMvnDownloadTarUrl"
    curl -o "$tmpTarFile" "$apacheMvnDownloadTarUrl"
    if [ $? -ne 0 ]; then # Fallback
      log "Downloading for $secondaryApacheMvnDownloadTarUrl"
      curl -o "$tmpTarFile" "$secondaryApacheMvnDownloadTarUrl"
    fi
    # Check installization result.
    [ $? -ne 0 ] && logErr "Failed to auto install mvn! Please manual installation!" && exit -1
    mkdir -p $mvnHome
    secDeleteLocal "$mvnHome/*" # Rmove old files(if necessary)
    tar -xf "$tmpTarFile" --strip-components=1 -C "$mvnHome"
    secDeleteLocal "$tmpTarFile" # Cleanup
    # Use china fast maven mirror to settings.xml
    if [ "$isChinaLANNetwork" == "Y" ]; then # see: deploy-boot.sh
      log "Currently in china LAN network, configuring aliyun maven fast mirror ..."
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
    export cmdMvn="$mvnHome/bin/mvn"
  else # Installed tmp maven.
    export cmdMvn="$mvnHome/bin/mvn"
  fi
  log "Use installed maven command: $cmdMvn"

  # Check nodejs
  local nodeHome="$nodejsInstallDir/node-current"
  if [ -n "$(command -v npm)" ]; then # Default installed node.
    export cmdNpm="npm"
  elif [ ! -d "$nodeHome" ]; then # Need install tmp node.
    log "No command nodejs, auto installing nodejs ..."
    cd $workspaceDir
    local tmpTarFile="$workspaceDir/node-current.tar"
    log "Downloading for $nodejsDownloadTarUrl"
    curl -o "$tmpTarFile" "$nodejsDownloadTarUrl"
    if [ $? -ne 0 ]; then # Fallback
      log "Downloading for $secondaryNodejsDownloadTarUrl"
      curl -o "$tmpTarFile" "$secondaryNodejsDownloadTarUrl"
    fi
    # Check installization result.
    [ $? -ne 0 ] && logErr "Failed to auto install nodejs! Please manual installation!" && exit -1
    mkdir -p $nodeHome
    secDeleteLocal "$nodeHome/*" # Rmove old files(if necessary)
    tar -xf "$tmpTarFile" --strip-components=1 -C "$nodeHome"
    secDeleteLocal "$tmpTarFile" # Cleanup
    export cmdNpm="$nodeHome/bin/npm" && sudo ln -snf "$nodeHome/bin/node" /bin/node
  else # Installed tmp nodejs.
    export cmdNpm="$nodeHome/bin/npm"
  fi
  # Use china fast nodejs mirror to config.
  if [ "$isChinaLANNetwork" == "Y" ]; then # see: deploy-boot.sh
    log "Currently in china LAN network, configuring taobao npm fast mirror ..."
    $cmdNpm config set registry https://registry.npm.taobao.org/
  fi
  log "Use installed node(npm) command: $cmdNpm"
}

function checkAndInstallSshpass() {
  local errmsg="Failed to auto install sshpass! Please manual installation, refer to: https://gitee.com/wl4g/sshpass or https://github.com/wl4g/sshpass"
  if [ -z "$(command -v /bin/sshpass)" ]; then
    log "Online installzation sshpass ..."
    if [ -n "$(command -v yum)" ]; then
      sudo yum install -y sshpass
    elif [ -n "$(command -v apt)" ]; then
      sudo apt install -y sshpass
    elif [ -n "$(command -v apt-get)" ]; then
      sudo apt-get install -y sshpass
    else
      logErr "$errmsg"; exit -1
    fi
    # Fallback, online install failure?
    if [ -z "$(command -v /bin/sshpass)" ]; then
      local osType=$(getOsTypeAndCheck)
      if [ "$isChinaLANNetwork" == "Y" ]; then
        if [ "$osType" == "centos6_x64" ]; then
          sudo curl -sLk --connect-timeout 10 -m 20 -o /bin/sshpass "https://gitee.com/wl4g/sshpass/attach_files/690539/download/sshpass_centos6_x64_1.09"; [ $? -ne 0 ] && exit -1
        elif [ "$osType" == "centos7_x64" ]; then
          sudo curl -sLk --connect-timeout 10 -m 20 -o /bin/sshpass "https://gitee.com/wl4g/sshpass/attach_files/690539/download/sshpass_centos7_x64_1.09"; [ $? -ne 0 ] && exit -1
        elif [ "$osType" == "ubuntu_x64" ]; then
          sudo curl -sLk --connect-timeout 10 -m 20 -o /bin/sshpass "https://gitee.com/wl4g/sshpass/attach_files/690539/download/sshpass_ubuntu_x64_1.09"; [ $? -ne 0 ] && exit -1
        fi
      else
        if [ "$osType" == "centos6_x64" ]; then
          sudo curl -sLk --connect-timeout 10 -m 20 -o /bin/sshpass "https://github.com/wl4g/sshpass/releases/download/1.09/sshpass_centos6_x64_1.09"; [ $? -ne 0 ] && exit -1
        elif [ "$osType" == "centos7_x64" ]; then
          sudo curl -sLk --connect-timeout 10 -m 20 -o /bin/sshpass "https://github.com/wl4g/sshpass/releases/download/1.09/sshpass_centos7_x64_1.09"; [ $? -ne 0 ] && exit -1
        elif [ "$osType" == "ubuntu_x64" ]; then
          sudo curl -sLk --connect-timeout 10 -m 20 -o /bin/sshpass "https://github.com/wl4g/sshpass/releases/download/1.09/sshpass_ubuntu20_x64_1.09"; [ $? -ne 0 ] && exit -1
        fi
      fi
      sudo chmod +x /bin/sshpass
    fi
    # Check installization result.
    [ $? -ne 0 ] && logErr "$errmsg" && exit -1
  fi
}

# Exec remote SSH command.
# for testing => doRemoteCmd "root" "mypassword" "localhost" "echo 11" "true"
function doRemoteCmd() {
  local user=$1
  local password=$2
  local host=$3
  local cmd=$4
  local exitOnFail=$5 # (default:false)
  local isOutput=$6 # (default:false) Whether output data needs to be read.
  # Check must args.
  if [[ $# < 4 || "$user" == "" || "$host" == "" || "$cmd" == "" ]]; then
    log "Exec remote command User/Host/Command is required and args should be 4"; exit -1
  fi
  # Check host is locally? (direct exec local command)
  if [[ "$host" == "localhost" || "$host" == "127.0.0.1" ]]; then
    if [ "$isOutput" == "true" ]; then
      local output=$(bash -c "$cmd")
      local exitStatus=$?
      echo "$output"
      return $exitStatus
    else
      bash -c "$cmd"
      return $?
    fi
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
    checkAndInstallSshpass
    if [ "$isOutput" == "true" ]; then
      local output=$(sshpass -p "$password" ssh -o StrictHostKeyChecking=no -p 22 "$user"@"$host" "$cmd")
      local exitStatus=$?
      [[ $exitStatus -ne 0 && "$exitOnFail" == "true" ]] && exit -1
      echo "$output"
      return $exitStatus
    else
      /bin/sshpass -p "$password" ssh -o StrictHostKeyChecking=no -p 22 "$user"@"$host" "$cmd"
      local exitStatus=$?
      [[ $exitStatus -ne 0 && "$exitOnFail" == "true" ]] && exit -1
      return $exitStatus
    fi
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
    checkAndInstallSshpass
    sshpass -p $password scp $localPath $user@$host:$remotePath
    [[ $? -ne 0 && "$exitOnFail" == "true" ]] && exit -1
  fi
}

# Check and install remote services script.
# for testing => checkInstallServiceScript "iam-web" "root" "123456" "10.0.0.160"
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
  if [ "$deployForcedInstallMgtScript" == "false" ]; then
    local hasServiceFile=$(doRemoteCmd "$user" "$password" "$host" "echo $([ -f /etc/init.d/$appName.service ] && echo Y || echo N)" "true")
    [ "$hasServiceFile" == "Y" ] && return 0 # Skip installed
  fi
  log "[$appName/$host] Installing /etc/init.d/${appName}.service script ..."
  local appVersion="master"
  local appMainClass="com.wl4g."$(echo $appName|awk -F '-' '{print toupper(substr($1,1,1))substr($1,2)toupper(substr($2,1,1))substr($2,2)toupper(substr($3,1,1))substr($3,2)}') #eg: udm-manager => UdmManager
  local appInstallDir="${deployAppBaseDir}/${appName}-package"
  local appHome="${appInstallDir}/${appName}-${appVersion}-bin"
  local appClasspath=".:$appHome/conf:${appHome}/ext-lib/*:${appHome}/lib/*" # In case of conflict, custom extension library takes precedence.
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
  local javaExec=$([ -n "$JAVA_HOME" ] && echo "$JAVA_HOME/bin/java" || echo "java")
  #local jvmDebugOpts="-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n"
  #local jvmJmxOpts="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=5000"
  local jvmHeapOpts="-Xms256M -Xmx1G"
  local jvmPerformanceOpts="-XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 \
-XX:+DisableExplicitGC -Djava.awt.headless=true -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${appLogDir}/jvm_dump.hprof \
-XX:-OmitStackTraceInFastThrow"
  local jvmJavaOpts="-Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom"
  local jvmGcLogFile="${appLogDir}/${appName}-gc.log"
  local jvmGcLogOpts="-Xloggc:${jvmGcLogFile} -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps \
-XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"
  # Java 9+
  if [[ "$($JAVA -version 2>&1|sed -E -n 's/.* version \"([^.-]*).*\"/\1/p')" -ge "9" ]] ; then
    jvmGcLogOpts="-Xlog:gc*:file=${jvmGcLogFile}:time,tags:filecount=10,filesize=102400"
  fi
  if [ "$buildPkgType" == "mvnAssTar" ]; then
    local appRunCmd="$javaExec -server $jvmDebugOpts $jvmHeapOpts $jvmPerformanceOpts $jvmGcLogOpts $jvmJmxOpts $jvmJavaOpts -cp $appClasspath $appMainClass $appOpts"
    local appShellRunCmd="$javaExec -client -Dprompt=$appName -Dservname=$appName $shellPort -cp .:$appHome/ext-lib/*:$appHome/lib/* com.wl4g.ShellBootstrap"
  elif [ "$buildPkgType" == "springExecJar" ]; then
    local appRunCmd="$javaExec -server $jvmDebugOpts $jvmHeapOpts $jvmPerformanceOpts $jvmGcLogOpts $jvmJmxOpts $jvmJavaOpts -jar ${appHome}/${appName}-${appVersion}-bin.jar $appOpts"
    # for example using: java -cp myapp.jar -Dloader.main=com.MyApp org.springframework.boot.loader.PropertiesLauncher
    # for example: xcloud-dopaas/xcloud-dopaas-ci/xcloud-dopaas-ci-service-starter-facade/pom.xml#profile.id=springExecJar
    # refer to: https://www.baeldung.com/spring-boot-main-class, https://www.jianshu.com/p/66a101c85485
    local appShellRunCmd="$javaExec -client -Dloader.main=com.wl4g.ShellBootstrap -Dprompt=$appName -Dservname=$appName $shellPort -jar .:$appHome/${appName}-${appVersion}-bin.jar"
  else
    log "Invalid buildPkgType: $buildPkgType" && exit -1
  fi
  # Check make directory.
  if [ "$appGroup" != "root" ]; then
    doRemoteCmd "$user" "$passwd" "$host" "[ -z \"\$(grep '^$appGroup:' /etc/group)\" ] && groupadd $appGroup || exit 0" "true"
  fi
  if [ "$appUser" != "root" ]; then
    doRemoteCmd "$user" "$passwd" "$host" "[ -z \"\$(grep '^$appUser:' /etc/passwd)\" ] && useradd -g $appGroup $appUser || exit 0" "true"
  fi
  doRemoteCmd "$user" "$passwd" "$host" "mkdir -p $appInstallDir" "true"
  doRemoteCmd "$user" "$passwd" "$host" "mkdir -p $appHome" "true"
  doRemoteCmd "$user" "$passwd" "$host" "mkdir -p $appLogDir" "true"
  doRemoteCmd "$user" "$passwd" "$host" "mkdir -p $appDataDir" "true"
  doRemoteCmd "$user" "$passwd" "$host" "chown -R $appUser:$appGroup $appInstallDir" "true"
  doRemoteCmd "$user" "$passwd" "$host" "chown -R $appUser:$appGroup $appLogDir" "true"
  doRemoteCmd "$user" "$passwd" "$host" "chown -R $appUser:$appGroup $appDataDir" "true"
  # Make app services script.
  local appShortNameUpper=$(echo $appName|tr '[a-z]' '[A-Z]'|awk -F '-' '{print $1}') # e.g cmdb-facade => CMDB
  local appShortNameLower=$(echo $appShortNameUpper|tr '[A-Z]' '[a-z]') # e.g cmdb-facade => cmdb
  # Make app runtime environments.
  local runtimeEnvStr=""
  if [ "$appShortNameUpper" == "IAM" ]; then
    runtimeEnvStr="""[ -z \"\$IAM_DB_URL\" ] && export IAM_DB_URL='$IAM_DB_URL'
[ -z \"\$IAM_DB_USER\" ] && export IAM_DB_USER='$IAM_DB_USER'
[ -z \"\$IAM_DB_PASSWD\" ] && export IAM_DB_PASSWD='$IAM_DB_PASSWD'
[ -z \"\$IAM_REDIS_PASSWD\" ] && export IAM_REDIS_PASSWD='$IAM_REDIS_PASSWD'
[ -z \"\$IAM_REDIS_NODES\" ] && export IAM_REDIS_NODES='$IAM_REDIS_NODES'"""
  else
    if [ "$runtimeMode" == "standalone" ]; then
      runtimeEnvStr="""[ -z \"\$STANDALONE_DOPAAS_DB_URL\" ] && export STANDALONE_DOPAAS_DB_URL='$STANDALONE_DOPAAS_DB_URL'
${runtimeEnvStr}[ -z \"\$STANDALONE_DOPAAS_DB_USER\" ] && export STANDALONE_DOPAAS_DB_USER='$STANDALONE_DOPAAS_DB_USER'
${runtimeEnvStr}[ -z \"\$STANDALONE_DOPAAS_DB_PASSWD\" ] && export STANDALONE_DOPAAS_DB_PASSWD='$STANDALONE_DOPAAS_DB_PASSWD'
${runtimeEnvStr}[ -z \"\$STANDALONE_DOPAAS_REDIS_NODES\" ] && export STANDALONE_DOPAAS_REDIS_NODES='$STANDALONE_DOPAAS_REDIS_NODES'
${runtimeEnvStr}[ -z \"\$STANDALONE_DOPAAS_REDIS_PASSWD\" ] && export STANDALONE_DOPAAS_REDIS_PASSWD='$STANDALONE_DOPAAS_REDIS_PASSWD'"""
    elif [ "$runtimeMode" == "cluster" ]; then
      runtimeEnvStr="""[ -z \"\$${appShortNameUpper}_DOPAAS_DB_URL\" ] && export ${appShortNameUpper}_DOPAAS_DB_URL='$(eval echo '$'${appShortNameUpper}_DOPAAS_DB_URL)'
${runtimeEnvStr}[ -z \"\$${appShortNameUpper}_DOPAAS_DB_USER\" ] && export ${appShortNameUpper}_DOPAAS_DB_USER='$(eval echo '$'${appShortNameUpper}_DOPAAS_DB_USER)'
${runtimeEnvStr}[ -z \"\$${appShortNameUpper}_DOPAAS_DB_PASSWD\" ] && export ${appShortNameUpper}_DOPAAS_DB_PASSWD='$(eval echo '$'${appShortNameUpper}_DOPAAS_DB_PASSWD)'
${runtimeEnvStr}[ -z \"\$${appShortNameUpper}_DOPAAS_REDIS_NODES\" ] && export ${appShortNameUpper}_DOPAAS_REDIS_NODES='$(eval echo '$'${appShortNameUpper}_DOPAAS_REDIS_NODES)'
${runtimeEnvStr}[ -z \"\$${appShortNameUpper}_DOPAAS_REDIS_PASSWD\" ] && export ${appShortNameUpper}_DOPAAS_REDIS_PASSWD='$(eval echo '$'${appShortNameUpper}_DOPAAS_REDIS_PASSWD)'"""
    else
      echo "Invalid runtime mode to $runtimeMode"; exit -1
    fi
  fi

  local tmpServiceFile="$workspaceDir/${appName}.service"
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

# Ref external configuration, default settings.
if [ -z "\$SPRING_PROFILES_ACTIVE" ]; then
  export SPRING_PROFILES_ACTIVE="$springProfilesActive" # Use default configuration.
elif [ -n "\$(echo \$SPRING_PROFILES_ACTIVE|grep -i '^None\$')" ]; then
  export SPRING_PROFILES_ACTIVE="" # Use empty configuration.
fi

# '$appName' runtime environment configuration.
$runtimeEnvStr

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
  log "[$appName/$host] Transfer /etc/init.d/$appName.services to remote ..."
  doScp "$user" "$password" "$host" "$tmpServiceFile" "/etc/init.d/${appName}.service" "true"
  doRemoteCmd "$user" "$password" "$host" "chown -R $appUser:$appGroup /etc/init.d/${appName}.service" "true"
  doRemoteCmd "$user" "$password" "$host" "chmod -R 750 /etc/init.d/${appName}.service" "true"
  secDeleteLocal $tmpServiceFile
}

# Load i18n config scripts.
function loadi18n() {
  local isCN="N"
  if [ -n "$isChinaLANNetwork" ]; then
    isCN="$isChinaLANNetwork"
  else
    isCN=$([[ "$LANG" == *"zh_CN"* ]] && echo Y || echo N)
  fi
  if [ "$isCN" == "Y" ]; then
    . $currDir/deploy-i18n-zh_CN.sh
  else
    . $currDir/deploy-i18n-en_US.sh
  fi
}
