/**
 * Copyright 2017 ~ 2025 the original author or authors<Wanglsir@gmail.com, 983708408@qq.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package utils

import (
	"io/ioutil"
	"os"
)

//ExistsFileOrDir ...
func ExistsFileOrDir(path string) bool {
	_, err := os.Stat(path) // os.Stat获取文件信息
	if err != nil {
		if os.IsExist(err) {
			return true
		}
		return false
	}
	return true
}

// CreateWriteTmpFile Create tmpfile and write content.
func CreateWriteTmpFile(filename string, content string) (string, error) {
	filename = "/tmp/" + filename
	f, err1 := os.Create(filename)
	if err1 != nil {
		return "", err1
	}
	defer f.Close()

	if content != "" {
		err2 := ioutil.WriteFile(filename, []byte(content), os.ModeTemporary)
		if err2 != nil {
			return "", err2
		}
	}

	return filename, nil
}
