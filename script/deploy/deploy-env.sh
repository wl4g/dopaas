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

# Deploy base environments.
export springProfilesActive='fat' # options: dev|fat|uat|pro

# Deploy for standalone mode.(And cluster mode are mutually exclusive)
export STANDALONE_DOPAAS_DB_URL='jdbc:mysql://owner-node2:3306/dopaas_standalone?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true'
export STANDALONE_DOPAAS_DB_USER='dopaas'
export STANDALONE_DOPAAS_DB_PASSWD='12345678'
export STANDALONE_DOPAAS_REDIS_PASSWD='zzx!@#$%'
export STANDALONE_DOPAAS_REDIS_NODES='owner-node2:6379,owner-node2:6380,owner-node2:6381,owner-node2:7379,owner-node2:7380,owner-node2:7381'

# Deploy for cluster mode.(And standalone mode are mutually exclusive)
export IAM_DB_URL='jdbc:mysql://owner-node2:3306/dopaas_iam?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&autoReconnect=true'
export IAM_DB_USER='dopaas'
export IAM_DB_PASSWD='12345678'
export IAM_REDIS_PASSWD='zzx!@#$%'
export IAM_REDIS_NODES='owner-node2:6379,owner-node2:6380,owner-node2:6381,owner-node2:7379,owner-node2:7380,owner-node2:7381'
export CMDB_DOPAAS_DB_URL='jdbc:mysql://owner-node2:3306/dopaas_cmdb?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
export CMDB_DOPAAS_DB_USER='dopaas'
export CMDB_DOPAAS_DB_PASSWD='12345678'
export CMDB_DOPAAS_REDIS_PASSWD='zzx!@#$%'
export CMDB_DOPAAS_REDIS_NODES='owner-node2:6379,owner-node2:6380,owner-node2:6381,owner-node2:7379,owner-node2:7380,owner-node2:7381'
export UCI_DOPAAS_DB_URL='jdbc:mysql://owner-node2:3306/dopaas_uci?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
export UCI_DOPAAS_DB_USER='dopaas'
export UCI_DOPAAS_DB_PASSWD='12345678'
export UCI_DOPAAS_REDIS_PASSWD='zzx!@#$%'
export UCI_DOPAAS_REDIS_NODES='owner-node2:6379,owner-node2:6380,owner-node2:6381,owner-node2:7379,owner-node2:7380,owner-node2:7381'
export UDM_DOPAAS_DB_URL='jdbc:mysql://owner-node2:3306/dopaas_udm?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
export UDM_DOPAAS_DB_USER='dopaas'
export UDM_DOPAAS_DB_PASSWD='12345678'
export UDM_DOPAAS_REDIS_PASSWD='zzx!@#$%'
export UDM_DOPAAS_REDIS_NODES='owner-node2:6379,owner-node2:6380,owner-node2:6381,owner-node2:7379,owner-node2:7380,owner-node2:7381'
export HOME_DOPAAS_DB_URL='jdbc:mysql://owner-node2:3306/dopaas_home?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
export HOME_DOPAAS_DB_USER='dopaas'
export HOME_DOPAAS_DB_PASSWD='12345678'
export HOME_DOPAAS_REDIS_PASSWD='zzx!@#$%'
export HOME_DOPAAS_REDIS_NODES='owner-node2:6379,owner-node2:6380,owner-node2:6381,owner-node2:7379,owner-node2:7380,owner-node2:7381'
export LCDP_DOPAAS_DB_URL='jdbc:mysql://owner-node2:3306/dopaas_lcdp?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
export LCDP_DOPAAS_DB_USER='dopaas'
export LCDP_DOPAAS_DB_PASSWD='12345678'
export LCDP_DOPAAS_REDIS_PASSWD='zzx!@#$%'
export LCDP_DOPAAS_REDIS_NODES='owner-node2:6379,owner-node2:6380,owner-node2:6381,owner-node2:7379,owner-node2:7380,owner-node2:7381'
export UCM_DOPAAS_DB_URL='jdbc:mysql://owner-node2:3306/dopaas_ucm?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
export UCM_DOPAAS_DB_USER='dopaas'
export UCM_DOPAAS_DB_PASSWD='12345678'
export UCM_DOPAAS_REDIS_PASSWD='zzx!@#$%'
export UCM_DOPAAS_REDIS_NODES='owner-node2:6379,owner-node2:6380,owner-node2:6381,owner-node2:7379,owner-node2:7380,owner-node2:7381'
export UDS_DOPAAS_DB_URL='jdbc:mysql://owner-node2:3306/dopaas_uds?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
export UDS_DOPAAS_DB_USER='dopaas'
export UDS_DOPAAS_DB_PASSWD='12345678'
export UDS_DOPAAS_REDIS_PASSWD='zzx!@#$%'
export UDS_DOPAAS_REDIS_NODES='owner-node2:6379,owner-node2:6380,owner-node2:6381,owner-node2:7379,owner-node2:7380,owner-node2:7381'
export UMC_DOPAAS_DB_URL='jdbc:mysql://owner-node2:3306/dopaas_umc?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
export UMC_DOPAAS_DB_USER='dopaas'
export UMC_DOPAAS_DB_PASSWD='12345678'
export UMC_DOPAAS_REDIS_PASSWD='zzx!@#$%'
export UMC_DOPAAS_REDIS_NODES='owner-node2:6379,owner-node2:6380,owner-node2:6381,owner-node2:7379,owner-node2:7380,owner-node2:7381'
export URM_DOPAAS_DB_URL='jdbc:mysql://owner-node2:3306/dopaas_urm?useunicode=true&servertimezone=asia/shanghai&characterencoding=utf-8&usessl=false&allowmultiqueries=true&autoreconnect=true'
export URM_DOPAAS_DB_USER='dopaas'
export URM_DOPAAS_DB_PASSWD='12345678'
export URM_DOPAAS_REDIS_PASSWD='zzx!@#$%'
export URM_DOPAAS_REDIS_NODES='owner-node2:6379,owner-node2:6380,owner-node2:6381,owner-node2:7379,owner-node2:7380,owner-node2:7381'

# Others deployer configuration.
#export deployFrontendSkip=true # options: true|false
#export deployDebug=true # options: true|false

#export gitBaseUri='https://gitee.com/wl4g' # options: https://gitee.com/wl4g | https://github.com/wl4g

#export gitDefaultBranch=master # e.g: master | 2.0.0-RC3-jobs
#export gitComponentBranch="${gitDefaultBranch}"
#export gitIamBranch="${gitDefaultBranch}"
#export gitDoPaaSBranch="${gitDefaultBranch}"
#export gitDoPaaSViewBranch="${gitDefaultBranch}"

#export buildForcedOnPullUpToDate=true # options: true|false
