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
export confirmDeployerNetworkModeEnvVarMsg="Option1: The deployment program is about to be executed, several necessary dependency packages will be downloaded. Please select the deployment network mode to use:"
export confirmDeployerNetworkModeEnvVarTip1Msg="[1] Extranet mode (This mode is recommended when the host can connect to the public network);"
export confirmDeployerNetworkModeEnvVarTip2Msg="[2] Intranet mode (If the host is unable to connect to the public network, this mode is recommended. You need to download the installation package to the local directory: '$pkgRepoLocalDir');"
export confirmDeployerNetworkModeEnvVarTip3Msg="Please input to(1|2|default:1)?"
export confirmDeployerNetworkModeEnvVarTip4Msg="The local repo package directory '$pkgRepoLocalDir' is empty, Please manually download the installation package to the local directory in advance according to the following tips!"
export confirmDeployerNetworkModeEnvVarTip5Msg="Choosed deploy network mode is"
export confirmServicesRuntimeConfigEnvVarMsg="Option2: The services that will be deploy depends on are configuration in the form of environment variables:"
export confirmServicesRuntimeConfigEnvVarTip1Msg="[y] Confirm to use the above configuration;"
export confirmServicesRuntimeConfigEnvVarTip2Msg="[n] Exit first, modify the '$curDir/deploy-env.sh' configuration items to override the default value, and then execute again;"
export confirmServicesRuntimeConfigEnvVarTip3Msg="Please confirm to (y|n)?"
export confirmServicesRuntimeConfigEnvVarTip4Msg="Please modify the '$currDir/deploy-env.sh' configuration items !"
export choosingDeployModeMsg="Option3: Choosing deployment mode:"
export choosingDeployModeTip1Msg="[1] Host deployment mode (deploying the deployer service to the remote host);"
export choosingDeployModeTip2Msg="[2] Docker deployment mode (deployer service will be deployed to remote docker);"
export choosingDeployModeTip3Msg="Please choose to (1|2|default:1)?"
export choosingDeployModeTip4Msg="Docker deployment is not supported yet, please look forward to it! Welcome to join us, contact: <wanglsir@gmail.com, 983708408@qq.com>"
export choosingDeployModeTip5Msg="Choosed deployment mode is"
export choosingRuntimeModeMsg="Option4: Choosing apps services runtime mode:"
export choosingRuntimeModeTip1Msg="[1] If you choose standalone runtime mode, it will be deployed to the local host in the smallest mode(Monomer application);"
export choosingRuntimeModeTip2Msg="[2] If you choose cluster runtime mode, it will be deployed to multiple remote hosts as distributed 
microservices, you need to create \"$currDir/deploy-host.csv\" to define the hosts list."
export choosingRuntimeModeTip3Msg="Please choose to (1|2|default:1)?"
export choosingRuntimeModeTip4Msg="Choosed apps services runtime mode is"
