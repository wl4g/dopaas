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

# [NOTES]:
#   It is recommended that each distributed service (such as independent database/redis, etc)
#   be configured independently in the production environment to achieve physical isolation.

# Deploy common environments.
export runtimeMode='cluster' # standalone | cluster
export springProfilesActive='pro' # dev | fat | uat | pro

# Deploy for standalone mode.
if [ "$runtimeMode" == "standalone" ]; then
  export STANDALONE_DOPAAS_DB_URL='jdbc:mysql://localhost:3306/dopaas_standalone?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true'
  export STANDALONE_DOPAAS_DB_USER='dopaas_standalone'
  export STANDALONE_DOPAAS_DB_PASSWD='123456'
  export STANDALONE_DOPAAS_REDIS_PASSWD='123456'
  export STANDALONE_DOPAAS_REDIS_NODES='localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381'
# Deploy for cluster mode.
elif [ "$runtimeMode" == "cluster" ]; then
  export IAM_DB_URL='jdbc:mysql://localhost:3306/iam?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true'
  export IAM_DB_USER='iam'
  export IAM_DB_PASSWD='123456'
  export IAM_REDIS_PASSWD='123456'
  export IAM_REDIS_NODES='localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381'
  export CMDB_DOPAAS_DB_URL='jdbc:mysql://localhost:3306/dopaas_cmdb?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
  export CMDB_DOPAAS_DB_USER='dopaas_cmdb'
  export CMDB_DOPAAS_DB_PASSWD='123456'
  export CMDB_DOPAAS_REDIS_PASSWD='123456'
  export CMDB_DOPAAS_REDIS_NODES='localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381'
  export UCI_DOPAAS_DB_URL='jdbc:mysql://localhost:3306/dopaas_uci?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
  export UCI_DOPAAS_DB_USER='dopaas_uci'
  export UCI_DOPAAS_DB_PASSWD='123456'
  export UCI_DOPAAS_REDIS_PASSWD='123456'
  export UCI_DOPAAS_REDIS_NODES='localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381'
  export UDM_DOPAAS_DB_URL='jdbc:mysql://localhost:3306/dopaas_udm?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
  export UDM_DOPAAS_DB_USER='dopaas_udm'
  export UDM_DOPAAS_DB_PASSWD='123456'
  export UDM_DOPAAS_REDIS_PASSWD='123456'
  export UDM_DOPAAS_REDIS_NODES='localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381'
  export HOME_DOPAAS_DB_URL='jdbc:mysql://localhost:3306/dopaas_home?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
  export HOME_DOPAAS_DB_USER='dopaas_home'
  export HOME_DOPAAS_DB_PASSWD='123456'
  export HOME_DOPAAS_REDIS_PASSWD='123456'
  export HOME_DOPAAS_REDIS_NODES='localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381'
  export LCDP_DOPAAS_DB_URL='jdbc:mysql://localhost:3306/dopaas_lcdp?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
  export LCDP_DOPAAS_DB_USER='dopaas_lcdp'
  export LCDP_DOPAAS_DB_PASSWD='123456'
  export LCDP_DOPAAS_REDIS_PASSWD='123456'
  export LCDP_DOPAAS_REDIS_NODES='localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381'
  export UCM_DOPAAS_DB_URL='jdbc:mysql://localhost:3306/dopaas_ucm?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
  export UCM_DOPAAS_DB_USER='dopaas_ucm'
  export UCM_DOPAAS_DB_PASSWD='123456'
  export UCM_DOPAAS_REDIS_PASSWD='123456'
  export UCM_DOPAAS_REDIS_NODES='localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381'
  export UDS_DOPAAS_DB_URL='jdbc:mysql://localhost:3306/dopaas_uds?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
  export UDS_DOPAAS_DB_USER='dopaas_uds'
  export UDS_DOPAAS_DB_PASSWD='123456'
  export UDS_DOPAAS_REDIS_PASSWD='123456'
  export UDS_DOPAAS_REDIS_NODES='localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381'
  export UMC_DOPAAS_DB_URL='jdbc:mysql://localhost:3306/dopaas_umc?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
  export UMC_DOPAAS_DB_USER='dopaas_umc'
  export UMC_DOPAAS_DB_PASSWD='123456'
  export UMC_DOPAAS_REDIS_PASSWD='123456'
  export UMC_DOPAAS_REDIS_NODES='localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381'
  export URM_DOPAAS_DB_URL='jdbc:mysql://localhost:3306/dopaas_urm?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
  export URM_DOPAAS_DB_USER='dopaas_urm'
  export URM_DOPAAS_DB_PASSWD='123456'
  export URM_DOPAAS_REDIS_PASSWD='123456'
  export URM_DOPAAS_REDIS_NODES='localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381'
fi

# Deployer other configuration.
#export deployFrontendSkip=true # true | false
#export gitBaseUri='https://gitee.com/wl4g' # https://gitee.com/wl4g | https://github.com/wl4g
#export gitDefaultBranch=2.0.0-RC3-jobs # master | 2.0.0-RC3-jobs
#export buildForcedOnPullUpToDate=true # true | false
#export deployDebug=true # true | false
