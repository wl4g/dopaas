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
. $currDir/deploy-base.sh

# Gets OS info and check. return values(centos6_x64/centos7_x64/centos8_x64/ubuntu_x64/unknown)
function getOsTypeAndCheck() {
  local osType=$([ -n "$(cat /etc/*release|grep -i 'centos release 6')" ] && echo centos6)
  if [ -z "$osType" ]; then
    osType=$([ -n "$(cat /etc/*release|grep -i 'centos linux release 7')" ] && echo centos7)
  fi
  if [ -z "$osType" ]; then
    osType=$([ -n "$(cat /etc/*release|grep -i 'centos linux release 8')" ] && echo centos8)
  fi
  if [ -z "$osType" ]; then
    osType=$([ -n "$(cat /etc/*release|grep -i 'ubuntu')" ] && echo ubuntu)
  fi
  local osArch=$([ -n "$(uname -a|grep -i x86_64)" ] && echo x64)
  [[ -n "$osType" && -n "$osArch" ]] && echo "${osType}_${osArch}" || echo "unknown"
}

# Security delete local object.
function secDeleteLocal() {
  local targetPath=$1
  local result=""
  # Simple filtered sys's file or directory.
  if [[ "$targetPath" != ""
        && "$targetPath" != "/"
        && "$targetPath" != "/*"
        && "$targetPath" != "/bin"*
        && "$targetPath" != "/bin/"*
        && "$targetPath" != "/sbin"*
        && "$targetPath" != "/usr/bin/"*
        && "$targetPath" != "/usr/sbin"*
        && "$targetPath" != "/usr/lib"*
        && "$targetPath" != "/usr/lib64"*
        && "$targetPath" != "/usr/libexec"*
        && "$targetPath" != "/boot"*
        && "$targetPath" != "/proc"*
        && "$targetPath" != "/sys"*
        && "$targetPath" != "/dev"*
        && "$targetPath" != "/lib"*
        && "$targetPath" != "/lib64"*
        && "$targetPath" != "/var"
        && "$targetPath" != "/var/"
        && "$targetPath" != "/mnt"
        && "$targetPath" != "/mnt/"
        && "$targetPath" != "/etc"
        && "$targetPath" != "/etc/"
        && "$targetPath" != "/usr"
        && "$targetPath" != "/usr/"
        && "$targetPath" != "/usr/local"
        && "$targetPath" != "/usr/local/"
        && "$targetPath" != "/root"
        && "$targetPath" != "/root/" ]]; then
    unalias rm >/dev/null 2>&1
    result=$(\rm -rf $targetPath)
  else
    logErr "Cannot remove sys's file or directory. targetPath: $targetPath"; exit -1
  fi
  local delStatus="$?"
  [ "$delStatus" -ne 0 ] && echo "Deletion can't seem to success. target: $targetPath, causeBy: $result"
  return $delStatus
}

# Check and install infra software.
function checkInstallInfraSoftware() {
  log "Checking basic software pre dependencies ..."
  local osType=$(getOsTypeAndCheck)

  # [Check install jdk]
  local javaHome="$jdkInstallDir"
  if [[ -n "$(command -v java)" && -n "$(command -v javac)" ]]; then
    # check existing jdk version.
    local javaVersion=$(java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | awk '{print $3}')
    local numJavaVersion=$(echo $javaVersion|sed 's/\.//g'|sed 's/_//g')
    if [[ ${numJavaVersion} -lt 18 ]]; then # must is jdk1.8+
      log "Installed java version: ${javaVersionJDK}, must be jdk8+, please reinstallation!"; exit -1
    fi
    export cmdJava="java"
  else
    log "Not detected java and javac, installing jdk ..."
    if [ "$deployNetworkMode" == "extranet" ]; then
      if [ -n "$(command -v yum)" ]; then
        sudo yum install -y "$jdk8YumX64PkgName"
      elif [ -n "$(command -v apt)" ]; then
        sudo apt install -y "$jdk8AptX64PkgName"
      elif [ -n "$(command -v apt-get)" ]; then
        sudo apt-get install -y "$jdk8AptX64PkgName"
      else
        logErr "Failed to install JDK!"; exit -1
      fi
    elif [ "$deployNetworkMode" == "intranet" ]; then
      local tmpJdkTarFile="$workspaceDir/jdk8-linux-x64.tar.gz"
      downloadFile "$localJdk8DownloadUrl" "$tmpJdkTarFile" "240"
      mkdir -p $javaHome
      secDeleteLocal "$javaHome/*" # Rmove old files(if necessary)
      tar -zxf "$tmpJdkTarFile" --strip-components=1 -C "$javaHome"
      secDeleteLocal "$tmpJdkTarFile" # Cleanup
      export cmdJava="$javaHome/bin/java" && sudo ln -snf "$javaHome/bin/java" /bin/java && sudo chmod +x /bin/java
    else
      logErr "Invalid deployNetworkMode is '$deployNetworkMode' !"; exit -1
    fi
  fi
  log "Use installed java command: $cmdJava"

  # [Check install sshpass]
  local sshpassFile="$workspaceDir/sshpass"
  if [ -z "$(command -v /bin/sshpass)" ]; then
    if [ "$deployNetworkMode" == "extranet" ]; then
      if [ "$isChinaLANNetwork" == "Y" ]; then
        if [ "$osType" == "centos6_x64" ]; then
          downloadFile "$sshpassForCentos6x64" "$sshpassFile"
        elif [ "$osType" == "centos7_x64" ]; then
          downloadFile "$sshpassForCentos7x64" "$sshpassFile"
        elif [ "$osType" == "centos8_x64" ]; then
          downloadFile "$sshpassForCentos8x64" "$sshpassFile"
        elif [ "$osType" == "ubuntu_x64" ]; then
          downloadFile "sshpassForUbuntu20x64" "$sshpassFile"
        fi
      else
        if [ "$osType" == "centos6_x64" ]; then
          downloadFile "$secondarySshpassForCentos6x64" "$sshpassFile"
        elif [ "$osType" == "centos7_x64" ]; then
          downloadFile "$secondarySshpassForCentos7x64" "$sshpassFile"
        elif [ "$osType" == "centos8_x64" ]; then
          downloadFile "$secondarySshpassForCentos8x64" "$sshpassFile"
        elif [ "$osType" == "ubuntu_x64" ]; then
          downloadFile "$secondarySshpassForUbuntu20x64" "$sshpassFile"
        fi
      fi
    elif [ "$deployNetworkMode" == "intranet" ]; then
      if [ "$osType" == "centos6_x64" ]; then
        downloadFile "$localSshpassForCentos6x64" "$sshpassFile"
      elif [ "$osType" == "centos7_x64" ]; then
        downloadFile "$localSshpassForCentos7x64" "$sshpassFile"
      elif [ "$osType" == "centos8_x64" ]; then
        downloadFile "$localSshpassForCentos8x64" "$sshpassFile"
      elif [ "$osType" == "ubuntu_x64" ]; then
        downloadFile "$localSshpassForUbuntu20x64" "$sshpassFile"
      fi
    fi
    export cmdSshpass="$sshpassFile" && sudo ln -snf $sshpassFile /bin/sshpass && sudo chmod +x /bin/sshpass
  fi
  # Check installization result.
  [ $? -ne 0 ] && logErr "Failed to auto install sshpass! Please manual installation, refer to: https://gitee.com/wl4g/sshpass or https://github.com/wl4g/sshpass" && exit -1
  log "Use installed sshpass command: /bin/sshpass -> $sshpassFile"

  # [Check install git]
  local gitHome="$gitInstallDir"
  if [ -n "$(command -v git)" ]; then # Default installed git.
    export cmdGit="git"
  elif [ ! -d "$gitHome" ]; then # Need install tmp git.
    log "No command git, auto installing git ..."
    cd $workspaceDir
    local tmpGitTarFile="$workspaceDir/git-current.tar.gz"
    if [ "$deployNetworkMode" == "extranet" ]; then
      if [ "$isChinaLANNetwork" == "N" ]; then
        if [ "$osType" == "centos6_x64" ]; then
          downloadFile "$gitDownloadUrlForCentos6x64" "$tmpGitTarFile" "240"
        elif [ "$osType" == "centos7_x64" ]; then
          downloadFile "$gitDownloadUrlForCentos7x64" "$tmpGitTarFile" "240"
        elif [ "$osType" == "centos8_x64" ]; then
          downloadFile "$gitDownloadUrlForCentos8x64" "$tmpGitTarFile" "240"
        elif [ "$osType" == "ubuntu_x64" ]; then
          downloadFile "gitDownloadUrlForUbuntu20x64" "$tmpGitTarFile" "240"
        fi
      else
        if [ "$osType" == "centos6_x64" ]; then
          downloadFile "$secondaryGitDownloadUrlForCentos6x64" "$tmpGitTarFile" "240"
        elif [ "$osType" == "centos7_x64" ]; then
          downloadFile "$secondaryGitDownloadUrlForCentos7x64" "$tmpGitTarFile" "240"
        elif [ "$osType" == "centos8_x64" ]; then
          downloadFile "$secondaryGitDownloadUrlForCentos8x64" "$tmpGitTarFile" "240"
        elif [ "$osType" == "ubuntu_x64" ]; then
          downloadFile "$secondaryGitDownloadUrlForUbuntu20x64" "$tmpGitTarFile" "240"
        fi
      fi
    elif [ "$deployNetworkMode" == "intranet" ]; then
      if [ "$osType" == "centos6_x64" ]; then
        downloadFile "$localGitDownloadUrlForCentos6x64" "$tmpGitTarFile"
      elif [ "$osType" == "centos7_x64" ]; then
        downloadFile "$localGitDownloadUrlForCentos7x64" "$tmpGitTarFile"
      elif [ "$osType" == "centos8_x64" ]; then
        downloadFile "$localGitDownloadUrlForCentos8x64" "$tmpGitTarFile"
      elif [ "$osType" == "ubuntu_x64" ]; then
        downloadFile "$localGitDownloadUrlForUbuntu20x64" "$tmpGitTarFile"
      fi
    else
      logErr "Invalid deployNetworkMode is '$deployNetworkMode' !"; exit -1
    fi
    mkdir -p $gitHome
    secDeleteLocal "$gitHome/*" # Rmove old files(if necessary)
    tar -zxf "$tmpGitTarFile" --strip-components=1 -C "$gitHome"
    secDeleteLocal "$tmpGitTarFile" # Cleanup
    export cmdGit="$gitHome/bin/git" && sudo ln -snf "$gitHome/bin/git" /bin/git && sudo chmod +x /bin/git
  else # Installed tmp git.
    export cmdGit="$gitHome/bin/git"
  fi
  log "Use installed git command: $cmdGit"

  # [Check install maven]
  local mvnHome="$apacheMvnInstallDir/apache-maven-current"
  if [ -n "$(command -v mvn)" ]; then # Default installed maven.
    export cmdMvn="mvn"
  elif [ ! -d "$mvnHome" ]; then # Need install tmp maven.
    log "No command mvn, auto installing maven ..."
    cd $workspaceDir
    local tmpMvnTarFile="$workspaceDir/apache-maven-current.tar.gz"
    if [ "$deployNetworkMode" == "extranet" ]; then
      downloadFile "$apacheMvnDownloadTarUrl" "$tmpMvnTarFile" "" "false"
      if [ $? -ne 0 ]; then # Fallback
        downloadFile "$secondaryApacheMvnDownloadTarUrl" "$tmpMvnTarFile"
      fi
    elif [ "$deployNetworkMode" == "intranet" ]; then
      downloadFile "$localApacheMvnDownloadTarUrl" "$tmpMvnTarFile"
    else
      logErr "Invalid deployNetworkMode is '$deployNetworkMode' !"; exit -1
    fi
    # Check installization result.
    [ $? -ne 0 ] && logErr "Failed to auto install mvn! Please manual installation!" && exit -1
    mkdir -p $mvnHome
    secDeleteLocal "$mvnHome/*" # Rmove old files(if necessary)
    tar -zxf "$tmpMvnTarFile" --strip-components=1 -C "$mvnHome"
    secDeleteLocal "$tmpMvnTarFile" # Cleanup
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
    export cmdMvn="$mvnHome/bin/mvn" && sudo ln -snf "$mvnHome/bin/mvn" /bin/mvn && sudo chmod +x /bin/mvn
  else # Installed tmp maven.
    export cmdMvn="$mvnHome/bin/mvn"
  fi
  log "Use installed maven command: $cmdMvn"

  # [Check install nodejs]
  local nodeHome="$nodejsInstallDir/node-current"
  if [ -n "$(command -v npm)" ]; then # Default installed node.
    export cmdNpm="npm"
  elif [ ! -d "$nodeHome" ]; then # Need install tmp node.
    log "No command nodejs, auto installing nodejs ..."
    cd $workspaceDir
    local tmpNodeTarFile="$workspaceDir/node-current.tar"
    if [ "$deployNetworkMode" == "extranet" ]; then
      downloadFile "$nodejsDownloadTarUrl" "$tmpNodeTarFile" "" "false"
      if [ $? -ne 0 ]; then # Fallback
        downloadFile "$secondaryNodejsDownloadTarUrl" "$tmpNodeTarFile"
      fi
    elif [ "$deployNetworkMode" == "intranet" ]; then
      downloadFile "$localNodejsDownloadTarUrl" "$tmpNodeTarFile"
    else
      logErr "Invalid deployNetworkMode is '$deployNetworkMode' !"; exit -1
    fi
    mkdir -p $nodeHome
    secDeleteLocal "$nodeHome/*" # Rmove old files(if necessary)
    tar -xf "$tmpNodeTarFile" --strip-components=1 -C "$nodeHome"
    secDeleteLocal "$tmpNodeTarFile" # Cleanup
    export cmdNpm="$nodeHome/bin/npm"
    sudo ln -snf "$nodeHome/bin/node" /bin/node && sudo chmod +x /bin/node
    sudo ln -snf "$nodeHome/bin/npm" /bin/npm && sudo chmod +x /bin/npm
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

# Exec to remote SSH commands.
# for example: doRemoteCmd "root" "mypassword" "localhost" "echo 123" "true"
# Notes: Multiple consecutive commands should be combined and executed at one time to prevent 
#  requent concurrent connects result in 'ssh_ exchange_ identification: Connection closed by remote host'
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
      [ -n "$output" ] && echo "$output"
      return $exitStatus
    else
      bash -c "$cmd"
      return $?
    fi
  fi
  # Check whether it is login passwordless.(When the password is empty)
  if [ "$password" == "" ]; then
    # If need to enter a password, it will timeout.
    local hasPasswordless=$(timeout 3 /bin/sshpass -p "$password" /bin/ssh $user@$host echo "YES" || echo "NO")
    # Direct exec remote cmd.
    if [ "$hasPasswordless" == "YES" ]; then
      /bin/sshpass -p "$password" /bin/ssh -o StrictHostKeyChecking=no -p 22 -i $HOME/.ssh/id_rsa.pub $user@$host $cmd
      [[ $? -ne 0 && "$exitOnFail" == "true" ]] && exit -1
    else
      logErr "Failed to exec remote, bacause not ssh-passwordless authorized! $hasPasswordless"; exit -1
    fi
  else # Exec remote by sshpass
    if [ "$isOutput" == "true" ]; then
      local output=$(/bin/sshpass -p "$password" ssh -o StrictHostKeyChecking=no -p 22 "$user"@"$host" "$cmd")
      local exitStatus=$?
      [[ $exitStatus -ne 0 && "$exitOnFail" == "true" ]] && exit -1
      [ -n "$output" ] && echo "$output"
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
    unalias cp >/dev/null 2>&1
    cp -Rf "$localPath" "$remotePath"
    return $?
  fi
  # Check whether it is login passwordless.(When the password is empty)
  if [ "$password" == "" ]; then
    # If need to enter a password, it will timeout.
    local hasPasswordless=$(timeout 3 /bin/sshpass -p "$password" /bin/ssh $user@$host echo "YES" || echo "NO")
    # Direct exec remote cmd.
    if [ "$hasPasswordless" == "YES" ]; then
      /bin/sshpass -p "$password" /bin/scp $localPath $user@$host:$remotePath
      [[ $? -ne 0 && "$exitOnFail" == "true" ]] && exit -1
    else
      logErr "Failed to scp '$localPath' to remote, bacause not ssh-passwordless authorized!"; exit -1
    fi
  else # Exec remote by sshpass
    /bin/sshpass -p "$password" scp $localPath $user@$host:$remotePath
    [[ $? -ne 0 && "$exitOnFail" == "true" ]] && exit -1
  fi
}

# Check and install remote services script.
# for testing => checkInstallServiceScript "iam-web" "root" "123456" "10.0.0.160" "fat"
function checkInstallServiceScript() {
  local appName=$1
  local user=$2
  local password=$3
  local host=$4
  local springProfilesActive=$5
  local buildVersion=$6
  if [[ $# < 6 || "$appName" == "" || "$user" == "" || "$host" == "" ]]; then
    logErr "[$appName/$host] Cannot installization app services, args appName/user/host is required and args should be 6 !"; exit -1
  fi
  # Check installed service script?
  if [ "$deployForcedInstallMgtScript" == "false" ]; then
    local hasServiceFile=$(doRemoteCmd "$user" "$password" "$host" "echo $([ -f /etc/init.d/$appName.service ] && echo Y || echo N)" "true")
    [ "$hasServiceFile" == "Y" ] && return 0 # Skip installed
  fi
  log "[$appName/$host] Installing /etc/init.d/${appName}.service ..."
  local appVersion="${buildVersion}"
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

  # Install init.d service.
  local tmpServiceFile="$workspaceDir/init.d/${appName}.service" && mkdir -p "$workspaceDir/init.d/"
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

# Load boot external environment.
[ -f "$appDataDir/environment" ] && source $appDataDir/environment

# Export 'SPRING_PROFILES_ACTIVE'
if [ -z "\$SPRING_PROFILES_ACTIVE" ]; then
  export SPRING_PROFILES_ACTIVE="$springProfilesActive" # Use default configuration.
elif [ -n "\$(echo \$SPRING_PROFILES_ACTIVE|grep -i '^None\$')" ]; then
  export SPRING_PROFILES_ACTIVE="" # Use empty configuration.
fi

# Export runtime environment.
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
  # Installing init.d service script to remote.
  log "[$appName/$host] Installing init.d '/etc/init.d/${appName}.service' to remote ..."
  doScp "$user" "$password" "$host" "$tmpServiceFile" "/etc/init.d/${appName}.service" "true"
  # Generate app user/group/directory/scripts.
  [ "$appGroup" != "root" ] && local addUserScript="[ -z \"\$(grep '^$appGroup:' /etc/group)\" ] && groupadd $appGroup"
  [ "$appUser" != "root" ] && local addGroupScript="[ -z \"\$(grep '^$appUser:' /etc/passwd)\" ] && useradd -g $appGroup $appUser"
  doRemoteCmd "$user" "$password" "$host" "
$addUserScript
$addGroupScript
mkdir -p $appInstallDir
mkdir -p $appHome
mkdir -p $appLogDir
mkdir -p $appDataDir
chown -R $appUser:$appGroup $appInstallDir
chown -R $appUser:$appGroup $appLogDir
chown -R $appUser:$appGroup $appDataDir
touch $appDataDir/environment
chown -R $appUser:$appGroup /etc/init.d/${appName}.service
chmod -R 750 /etc/init.d/${appName}.service" "true"
  #secDeleteLocal $tmpServiceFile

  # Install systemctl service.(if necessary)
  if [ -n "$(command -v systemctl)" ]; then
    local tmpCtlServiceFile="$workspaceDir/systemd/${appName}.service" && mkdir -p "$workspaceDir/systemd/"
cat<<EOF>$tmpCtlServiceFile
# See:http://www.ruanyifeng.com/blog/2016/03/systemd-tutorial-commands.html
[Unit]
Description=${appName} - lightweight high availability service based on spring cloud
After=network.target remote-fs.target nss-lookup.target

[Service]
Type=simple
PIDFile=${appDataDir}/${appName}.pid
#EnvironmentFile=${appDataDir}/environment
#Environment=SPRING_PROFILES_ACTIVE=${springProfilesActive}
ExecStartPre=/bin/rm -f ${appDataDir}/${appName}.pid
ExecStart=/bin/bash -c "/etc/init.d/${appName}.service start"
ExecStartPost=/bin/bash -c "/bin/mkdir -p ${appDataDir} && /bin/echo \$MAINPID >${appDataDir}/${appName}.pid"
#ExecReload=/bin/kill -s HUP \$MAINPID
ExecReload=/bin/bash -c "/etc/init.d/${appName}.service restart"
ExecStop=/bin/bash -c "/etc/init.d/${appName}.service stop"

# Will it cause 'Restart=on-abnormal' to be invalid?
#ExecStop=/bin/kill -s TERM \$MAINPID
#StandardOutput=null
StandardError=journal
LimitNOFILE=1048576
LimitNPROC=1048576
LimitCORE=infinity
TimeoutStartSec=5
Restart=on-abnormal
KillMode=process
#KillSignal=SIGQUIT
#PrivateTmp=true # Will move the JVM default hsperfdata file
User=${appName}
Group=${appName}
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
EOF
    # Installing systemd service script to remote.
    log "[$appName/$host] Installing systemd '/lib/systemd/system/${appName}.service' to remote ..."
    doScp "$user" "$password" "$host" "$tmpCtlServiceFile" "/lib/systemd/system/${appName}.service" "true"
    doRemoteCmd "$user" "$password" "$host" "sudo chmod -R 750 /lib/systemd/system/${appName}.service && sudo systemctl daemon-reload && systemctl enable ${appName}.service" "true"
    #secDeleteLocal $tmpCtlServiceFile
  fi
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

# Downloading install package file.
function downloadFile() {
  local downloadUrl=$1
  local filename=$2
  local readTimeout=$3 # Default: 60s
  local exitOnFail=$4 # Default: true
  [[ -z "$downloadUrl" || -z "$filename" ]] && echo "Invalid 'downloadUrl'=arg1 and 'filename'=args2 is required!" && exit -1
  [ -z "$readTimeout" ] && readTimeout=60
  # Check local file is exists. (offline mode)
  if [[ -n "$(echo $downloadUrl|grep '^file:')" && ! -f "$(echo $downloadUrl|sed 's/file://g')" ]]; then
    log "Cannot download from '$downloadUrl', because it not found!"; exit -1
  fi
  log "Downloading package '$filename' from '$downloadUrl' ..."
  sudo curl -sLk --connect-timeout 10 -m $readTimeout -o $filename $downloadUrl
  local retval=$?
  if [ "$exitOnFail" == "false" ]; then
    return $retval
  else
    [ $retval -ne 0 ] && log "ERROR" "Failed to download package, exitCode=$retval, readTimeout=${readTimeout}s from '$downloadUrl' !" && exit -1
  fi
}

# Gets frontend project build version.
function getFrontendBuildVersion() {
  local fProjectDir="$currDir/$gitXCloudDoPaaSViewProjectName"
  # Extract npm project version from package.json
  local fBuildVersion=$(cat "$fProjectDir/package.json"|grep -E "\"version\":(\s)*"|awk -F ':' '{print $2}'|sed -r "s/\"//g"|sed 's/,//g'|sed 's/ //g')
  echo "$fBuildVersion"
}

# Make nginx conf of dopaas.
# for example:
# nodeArr=("10.0.0.100" "10.0.0.200")
# makeNginxConf "/tmp/dopaas.conf" "dev" "${nodeArr[*]}"
function makeNginxConf() {
  local tmpNgxDopaasConfFile=$1
  local springProfilesActive=$2
  local nodeArr=$3

  local deployBuildModulesSize=0
  if [ "$runtimeMode" == "standalone" ]; then
    local deployBuildModules=("${deployStandaloneBuildModules[@]}") # Copy build targets array
  elif [ "$runtimeMode" == "cluster" ]; then # The 'cluster' mode is deploy to the remote hosts
    local deployBuildModules=("${deployClusterBuildModules[@]}") # Copy build targets array
  else
    logErr "Invalid config runtime mode: $runtimeMode"; exit -1
  fi
  local deployBuildModulesSize=${#deployBuildModules[@]}
  local fBuildVersion=$(getFrontendBuildVersion)
  if [ $deployBuildModulesSize -gt 0 ]; then
    local configStr="
# Auto Generated by DoPaaS deployer.
#
# Copyright (c) 2017 ~ 2025, the original author wangl.sir individual Inc,
# All rights reserved. Contact us <Wanglsir@gmail.com, 983708408@qq.com>
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an \"AS IS\" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# XCloud DoPaaS Servers Forward configuration.
#   (Notes: this deployment structure is only for routine deployment, It is
#    recommended to modify the production environment, for example: ELB/SLB/HAProxy/LVS)

# ----------------------------------------------------------------------------

# Forced rewrite HTTPS. (if necessary)
#server {
#   listen 80;
#   #listen 443 ssl;
#   server_name localhost;
#   root html;
#   index index.html index.htm;
#   rewrite ^(.*)$  https://$host$1 permanent;
#}

# ----------------------------------- Frontend -----------------------------------
server {
    listen 80;
    #listen 443 ssl;
    server_name dopaas.wl4g.${springProfilesActive};
    include /etc/nginx/default.d/*.conf;
    location / {
       root /usr/share/nginx/html/xcloud-dopaas-view-package/xcloud-dopaas-view-${fBuildVersion}-bin;
       index index.html;
    }
}

# ----------------------------------- Backtend -----------------------------------"
    for ((i=0;i<${#deployBuildModules[@]};i++)); do
      local buildModule=${deployBuildModules[i]}
      local appName=$(echo "$buildModule"|awk -F ',' '{print $1}')
      local appPort=$(echo "$buildModule"|awk -F ',' '{print $2}')
      local appType=$(echo "$buildModule"|awk -F ',' '{print $3}')
      [ -z "$appName" ] && logErr "Failed to deploy, appName is required! all args: '$@'" && exit -1
      [ -z "$appPort" ] && logErr "Failed to deploy, appPort is required! all args: '$@'" && exit -1
      [ -z "$appType" ] && logErr "Failed to deploy, appType is required! all args: '$@'" && exit -1
      if [ "$appType" == "external" ]; then
        local appNameUnderline=$(echo "$appName"|sed 's/-/_/g')
        local appPrefix=$(echo "$appName"|awk -F '-' '{print $1}')
        # Join upstream config string.
        local appUpstreamStr="upstream ${appNameUnderline}_nodes {" # upstream conf.
        for node in ${nodeArr[@]}; do
          local host=$(echo $node|awk -F 'ξ' '{print $1}')
          appUpstreamStr="${appUpstreamStr}\n\tserver $host:$appPort max_fails=5 fail_timeout=60s weight=50;"
        done
        appUpstreamStr="$appUpstreamStr\n}"
        configStr="$configStr\n$appUpstreamStr"
        # Join listen config string.
        local appListenStr="server {
\tlisten 80;
\t#listen 443 ssl;
\tserver_name ${appPrefix}.wl4g.${springProfilesActive};
\tinclude /etc/nginx/default.d/*.conf;
\tlocation / {
\t\tproxy_pass http://${appNameUnderline}_nodes; break;
\t}
}"
        configStr="$configStr\n$appListenStr"
      fi
    done
    echo -e "$configStr" > "$tmpNgxDopaasConfFile"
  fi
}

