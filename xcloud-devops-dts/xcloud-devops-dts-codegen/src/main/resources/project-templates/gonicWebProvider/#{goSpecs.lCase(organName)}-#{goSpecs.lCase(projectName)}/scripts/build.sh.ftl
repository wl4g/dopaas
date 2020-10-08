# ${watermark}

# Copyright 2017 ~ 2025 the original author or authors<Wanglsir@gmail.com, 983708408@qq.com>.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# -------------------------------------------------------------------------
# --- Compiling Mac and Windows executable programs under Linux. ---
# -------------------------------------------------------------------------
set -e

BASE_DIR=$(dirname $0)
cd $BASE_DIR/..

export CGO_ENABLED=0
export GOARCH=amd64
export GOOS=windows # linux|darwin|windows

if [ "$GOOS" == "windows" ]; then
  SUFFIX=".exe"
fi

go build -v -a -ldflags '-s -w' \
-gcflags="all=-trimpath=${"r${"}BASE_DIR}" \
-asmflags="all=-trimpath=${"r${"}BASE_DIR}" \
-o ./bin/${projectName?lower_case}_${"r${"}GOOS}_${"r${"}GOARCH}${"r${"}SUFFIX} ./pkg/
