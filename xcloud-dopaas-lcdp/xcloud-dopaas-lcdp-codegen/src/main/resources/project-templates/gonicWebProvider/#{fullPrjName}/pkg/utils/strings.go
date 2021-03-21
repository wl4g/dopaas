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
	"bytes"
	"strings"
)

// StringsContains Check whether the string contains
func StringsContains(array []string, val string) bool {
	if array == nil {
		return false
	}
	for i := 0; i < len(array); i++ {
		if strings.TrimSpace(array[i]) == strings.TrimSpace(val) {
			return true
		}
	}
	return false
}

// IsEmpty ...
func IsEmpty(str string) bool {
	return str == "" || len(str) <= 0
}

func JoinAll(array []string, separator string) string {
	startIndex := 0
	endIndex := len(array)
	return JoinString(array, separator, startIndex, endIndex)
}

// JoinString Joins the elements of the provided array into a single String
// containing the provided list of elements.
//
// <p>No delimiter is added before or after the list.
// A {@code null} separator is the same as an empty String ("").
// Null objects or empty strings within the array are represented by
// empty strings.</p>
//
// <pre>
// JoinString(null, *, *, *)                = null
// JoinString([], *, *, *)                  = ""
// JoinString([null], *, *, *)              = ""
// JoinString(["a", "b", "c"], "--", 0, 3)  = "a--b--c"
// JoinString(["a", "b", "c"], "--", 1, 3)  = "b--c"
// JoinString(["a", "b", "c"], "--", 2, 3)  = "c"
// JoinString(["a", "b", "c"], "--", 2, 2)  = ""
// JoinString(["a", "b", "c"], null, 0, 3)  = "abc"
// JoinString(["a", "b", "c"], "", 0, 3)    = "abc"
// JoinString([null, "", "a"], ',', 0, 3)   = ",,a"
// </pre>
//
// @param array  the array of values to join together, may be null
// @param separator  the separator character to use, null treated as ""
// @param startIndex the first index to start joining from.
// @param endIndex the index to stop joining from (exclusive).
// @return the joined String, {@code null} if null array input; or the empty string
// if endIndex - startIndex <= 0 , The number of joined entries is given by
// endIndex - startIndex
// startIndex < 0} or
// startIndex >= array.length()} or
// endIndex < 0} or
// endIndex > array.length()
// @see commons-lang-{version}.jar org.apache.commons.lang3.StringUtils
func JoinString(array []string, separator string, startIndex int, endIndex int) string {
	if array == nil {
		return ""
	}
	if separator == "" {
		separator = ""
	}

	// endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
	//           (Assuming that all Strings are roughly equally long)
	noOfItems := endIndex - startIndex
	if noOfItems <= 0 {
		return ""
	}

	var buffer bytes.Buffer
	for i := startIndex; i < endIndex; i++ {
		if i > startIndex {
			buffer.WriteString(separator)
		}
		if array[i] != "" {
			buffer.WriteString(array[i])
		}
	}
	return buffer.String()
}
