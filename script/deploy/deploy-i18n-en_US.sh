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

[ -z "$currDir" ] && export currDir=$(cd "`dirname $0`"/ ; pwd)

# Deploy i18n(en_US) message definitions.
export confirmServicesRuntimeConfigEnvVarMsg="Option1: The services that will be deploy depends on are configuration in the form of environment variables:"
export confirmServicesRuntimeConfigEnvVarTip1Msg="[y] Confirm to use the above configuration;"
export confirmServicesRuntimeConfigEnvVarTip2Msg="[n] Exit and then customize the reconfiguration;"
export confirmServicesRuntimeConfigEnvVarTip3Msg="Please confirm to (y|n)?"
export confirmServicesRuntimeConfigEnvVarTip4Msg="Please the re-export environment variables, re-execute to:"
export choosingDeployModeMsg="Option2: Choosing deployment mode:"
export choosingDeployModeTip1Msg="[1] If you choose host deploy mode, next the app services is deployed to the remote host;"
export choosingDeployModeTip2Msg="[2] If you choose docker deploy mode, next the app services is deployed to the remote docker;"
export choosingDeployModeTip3Msg="Please choose to (1|2|default:1)?"
export choosingDeployModeTip4Msg="Docker deployment is not supported yet, please look forward to it! Welcome to join us, contact: <wanglsir@gmail.com, 983708408@qq.com>"
export choosingDeployModeTip5Msg="Choosed deployment mode is"
export choosingRuntimeModeMsg="Option3: Choosing apps services runtime mode:"
export choosingRuntimeModeTip1Msg="[1] If you choose standalone runtime mode, it will be deployed to the local host in the smallest mode(Non microservices);"
export choosingRuntimeModeTip2Msg="[2] If you choose cluster runtime mode, it will be deployed to multiple remote hosts as distributed 
microservices, you need to create \"$currDir/deploy-host.csv\" to define the hosts list."
export choosingRuntimeModeTip3Msg="Please choose to (1|2|default:1)?"
