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
set -e

echo ""
echo " Welcome to XCloud DevSecOps Deployer (for Docker) ! "
echo ""
echo " Wiki: https://github.com/wl4g/xcloud-devops/blob/master/README.md or https://gitee.com/wl4g/xcloud-devops/blob/master/README_CN.md"
echo " Authors: <Wanglsir@gmail.com, 983708408@qq.com>"
echo " Version: 2.0.0"
echo " Time: "$(date -d today +"%Y-%m-%d %H:%M:%S")
echo ""

# Macro definitions.
currDir="$(cd "`dirname "$0"`"/..; pwd)" && cd $currDir
source ${currDir}/deploy-variable.sh

# TODO

