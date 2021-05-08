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

# Deploy i18n(zh_CN) message definitions.
export confirmServicesRuntimeConfigEnvVarMsg="选项1: 您即将部署的服务需依赖以下环境变量形式的配置:"
export confirmServicesRuntimeConfigEnvVarTip1Msg="[y] 确认使用以上配置;"
export confirmServicesRuntimeConfigEnvVarTip2Msg="[n] 先退出, 然后在当前shell终端重新执行export自定义值来覆盖这些默认配置;"
export confirmServicesRuntimeConfigEnvVarTip3Msg="请输入(y|n)?"
export confirmServicesRuntimeConfigEnvVarTip4Msg="请在当前shell终端重新执行export自定义值来覆盖这些默认配置, 然后重新执行"
export choosingDeployModeMsg="选项2: 请选择即将要部署模式:"
export choosingDeployModeTip1Msg="[1] 如果选择主机部署模式, 则下一步就会将程序服务部署到远程宿主机上;"
export choosingDeployModeTip2Msg="[2] 如果选择docker部署模式, 那么接下来就会将程序服务将部署到远程docker上;"
export choosingDeployModeTip3Msg="请输入(1|2|默认:1)?"
export choosingDeployModeTip4Msg="目前还不支持Docker模式部署, 敬请期待！也欢迎加入我们, 请联系: <wanglsir@gmail.com, 983708408@qq.com>"
export choosingDeployModeTip5Msg="已选择要部署方式为"
export choosingRuntimeModeMsg="选项3: 请选择即将要部署服务的运行模式:"
export choosingRuntimeModeTip1Msg="[1] 如果您选择单机运行时模式, 则将以最小的规模部署到本地主机运行(非微服务);"
export choosingRuntimeModeTip2Msg="[2] 如果您选择集群运行时模式, 则将以分布式架构部署到多个远程主机上(微服务)
, 同时您还需要创建 '$currDir/deploy-host.csv' 文件来配置要发布的主机列表."
export choosingRuntimeModeTip3Msg="请输入 (1|2|默认:1)?"
export choosingRuntimeModeTip4Msg="已选择即将要部署的程序服务运行时模式为"
