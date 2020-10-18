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
	"fmt"
	"testing"

	jsoniter "github.com/json-iterator/go"
)

type Inner struct {
	A int
	B float64
	C bool
	D string
}
type Mid struct {
	I  Inner
	Ip *Inner
	D  int
}
type Outer struct {
	Inner
	M    Mid
	Mp   *Mid
	Mids map[int]*Mid
	Ins  []*Inner
}

func TestCopyProperties1(t *testing.T) {
	fmt.Printf("Testing %s starting ...\n", "TestCopyProperties1")

	dst := &Outer{
		Inner: Inner{
			A: 111, // 按同名 field 覆盖，不影响其他 field
			B: 33.122,
			C: true,
			D: "abc",
		},
		M: Mid{
			I: Inner{
				A: 11, // 嵌套的 struct 也是按 field 覆盖
				C: true,
			},
		},
		Mp: &Mid{
			I: Inner{
				A: 11, // 指针指向的 struct 内部 field 同理
				C: true,
			},
		},
		Mids: map[int]*Mid{
			0: &Mid{ // map elem 被整体覆盖
				I: Inner{
					C: true, // 被覆盖为默认值
				},
				Ip: &Inner{
					B: 333, // 被覆盖为默认值
				},
				D: 444, // 被覆盖为默认值
			},
			1: &Mid{ // 但是不影响 map 的其他 elem，值被完整保留
				I: Inner{
					A: 555,
				},
			},
		},
		Ins: []*Inner{
			{ // slice elem 内部被按 field 覆盖
				A: 666,
				B: 77.7,
			},
			{ // 同上
				A: 888,
				B: 99.9,
			},
			{ // 但其他 elem 反而丢失了
				A: 1010,
				B: 11.11,
			},
		},
	}

	src := &Outer{
		Inner: Inner{
			A: 222, // 按同名 field 覆盖，不影响其他 field
			B: 33.1,
			C: false,
			D: "bbb",
		},
		M: Mid{
			I: Inner{
				A: 11, // 嵌套的 struct 也是按 field 覆盖
				C: true,
			},
		},
		Mp: &Mid{
			I: Inner{
				A: 11, // 指针指向的 struct 内部 field 同理
				C: true,
			},
		},
	}

	dstData, _ := jsoniter.Marshal(dst)
	fmt.Println("before.dst:", string(dstData))

	srcData, _ := jsoniter.Marshal(src)
	fmt.Println("before.src:", string(srcData))

	CopyProperties(src, dst)
	dstData, _ = jsoniter.Marshal(dst)
	fmt.Println("after.dst:", string(dstData))
}

func TestCopyProperties2(t *testing.T) {
	fmt.Printf("Testing %s starting ...\n", "TestCopyProperties2")

	dst := &Outer{
		Inner: Inner{
			A: 111, // 按同名 field 覆盖，不影响其他 field
			B: 33.11,
			C: true,
			D: "abc",
		},
		M: Mid{
			I: Inner{
				A: 11, // 嵌套的 struct 也是按 field 覆盖
				C: true,
			},
		},
		Mp: &Mid{
			I: Inner{
				A: 11, // 指针指向的 struct 内部 field 同理
				C: true,
			},
		},
		Mids: map[int]*Mid{
			0: &Mid{ // map elem 被整体覆盖
				I: Inner{
					C: true, // 被覆盖为默认值
				},
				Ip: &Inner{
					B: 333, // 被覆盖为默认值
				},
				D: 444, // 被覆盖为默认值
			},
			1: &Mid{ // 但是不影响 map 的其他 elem，值被完整保留
				I: Inner{
					A: 555,
				},
			},
		},
		Ins: []*Inner{
			{ // slice elem 内部被按 field 覆盖
				A: 666,
				B: 77.7,
			},
			{ // 同上
				A: 888,
				B: 99.9,
			},
			{ // 但其他 elem 反而丢失了
				A: 1010,
				B: 11.11,
			},
		},
	}

	srcData := []byte(`
			{
			   "A": 123,
			   "C": false,
			   "M": {
				   "I": {
					   "B": 222
				   }
			   },
			   "Mp": {
				   "I": {
					   "B": 222,
						"C": false
				   }
			   },
			   "Mids": {
				   "0": {
					   "I": {
						   "A": 234
					   },
					   "Ip": {
						   "A": 777
					   }
				   }
			   },
			}
			`)

	dstData, _ := jsoniter.Marshal(dst)
	fmt.Println("before.dst:", string(dstData))

	fmt.Println("before.src:", string(srcData))

	ParseJSONObject(srcData, dst)
	dstData, _ = jsoniter.Marshal(dst)
	fmt.Println("after.dst:", string(dstData))
}
