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
	jsoniter "github.com/json-iterator/go"
)

// ToJSONString ...
func ToJSONString(v interface{}) (string, error) {
	str, err := jsoniter.MarshalToString(v)
	if err != nil {
		// fmt.Printf("Marshal data error! %s", err)
		return "", err
	}
	return str, nil
}

// DeepCopy Deep objects copy.
func DeepCopy(value interface{}) interface{} {
	if valueMap, ok := value.(map[string]interface{}); ok {
		newMap := make(map[string]interface{})
		for k, v := range valueMap {
			newMap[k] = DeepCopy(v)
		}
		return newMap
	} else if valueSlice, ok := value.([]interface{}); ok {
		newSlice := make([]interface{}, len(valueSlice))
		for k, v := range valueSlice {
			newSlice[k] = DeepCopy(v)
		}
		return newSlice
	}
	return value
}

// CopyProperties Copy src to dst properties.
func CopyProperties(src interface{}, dst interface{}) error {
	srcData, err := jsoniter.Marshal(src)
	if err != nil {
		return err
	}
	return jsoniter.Unmarshal(srcData, dst)
}

// ParseJSONObject Copy src properties to object.
func ParseJSONObject(srcData []byte, dst interface{}) error {
	return jsoniter.Unmarshal(srcData, dst)
}
