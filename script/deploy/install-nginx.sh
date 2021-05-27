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
# @see: http://nginx.org/en/linux_packages.html#RHEL-CentOS

function installLocalNginx() {
  if [[ "$(echo groups)" == "root" ]]; then
    echo "Please execute the installing nginx script as a user with root privileges !"; exit -1
  fi

  # Online installing for nginx.
  if [ -n "$(cat /etc/*release|grep -i 'centos')" ]; then
  sudo mkdir -p /etc/yum.repos.d/
cat<<EOF>/etc/yum.repos.d/nginx.repo
[nginx-stable]
name=nginx stable repo
baseurl=http://nginx.org/packages/centos/\$releasever/\$basearch/
gpgcheck=1
enabled=1
gpgkey=https://nginx.org/keys/nginx_signing.key
module_hotfixes=true

[nginx-mainline]
name=nginx mainline repo
baseurl=http://nginx.org/packages/mainline/centos/\$releasever/\$basearch/
gpgcheck=1
enabled=0
gpgkey=https://nginx.org/keys/nginx_signing.key
module_hotfixes=true
EOF
    sudo yum-config-manager --enable nginx-mainline
    sudo yum install -y nginx
  elif [ -n "$(cat /etc/*release|grep -i 'ubuntu')" ]; then
    sudo apt install curl gnupg2 ca-certificates lsb-release
    sudo mkdir -p /etc/apt/sources.list.d/
    echo "deb http://nginx.org/packages/ubuntu `lsb_release -cs` nginx" | sudo tee /etc/apt/sources.list.d/nginx.list
    if [ -n "$(command -v apt)" ]; then
      sudo apt update
      sudo apt install -y nginx
    elif [ -n "$(command -v apt-get)" ]; then
      sudo apt-get update
      sudo apt-get install -y nginx
    fi
  elif [ -n "$(cat /etc/*release|grep -i 'alpine')" ]; then
    sudo apk add openssl curl ca-certificates
    printf "%s%s%s%s\n" "@nginx " "http://nginx.org/packages/alpine/v" \
      `egrep -o '^[0-9]+\.[0-9]+' /etc/alpine-release` "/main" | sudo tee -a /etc/apk/repositories
    curl -o /tmp/nginx_signing.rsa.pub https://nginx.org/keys/nginx_signing.rsa.pub
    openssl rsa -pubin -in /tmp/nginx_signing.rsa.pub -text -noout
    sudo mv /tmp/nginx_signing.rsa.pub /etc/apk/keys/
    sudo apk add nginx@nginx
    sudo apk add nginx-module-image-filter@nginx nginx-module-njs@nginx
  else
    echo "Failed to auto install nginx!(currently only the OS is supported: CentOS/Ubuntu/Alpine), Please manual installation!"
  fi
  [ $? -ne 0 ] && exit -1
}
installLocalNginx
