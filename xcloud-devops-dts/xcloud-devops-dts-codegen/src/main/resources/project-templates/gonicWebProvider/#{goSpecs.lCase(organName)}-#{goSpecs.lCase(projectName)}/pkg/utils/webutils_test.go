/**
 * Copyright 2017 ~ 2025 the original author or author<Wanglsir@gmail.com, 983708408@qq.com>.
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
)

func TestWebUtilsAll(t *testing.T) {
	fmt.Println("-------------1111-------------------")
	fmt.Println(ExtractWildcardEndpoint("http://*.aaa.anjiancloud.test/API/v2"))

	fmt.Println("-------------2222-------------------")
	fmt.Println(IsSameWildcardOrigin("http://*.aa.domain.com/api/v2", "http://bb.aa.domain.com/api/v2", true))
	fmt.Println(IsSameWildcardOrigin("http://*.aa.domain.com/api/v2", "https://bb.aa.domain.com/api/v2", true))
	fmt.Println(IsSameWildcardOrigin("http://*.aa.domain.com/api/v2/", "http://bb.aa.domain.com/API/v2", true))
	fmt.Println(IsSameWildcardOrigin("http://bb.*.domain.com", "https://bb.aa.domain.com", false))
	fmt.Println(IsSameWildcardOrigin("http://*.aa.domain.com", "https://bb.aa.domain.com", true))
	fmt.Println(IsSameWildcardOrigin("http://*.aa.domain.com:8080", "http://bb.aa.domain.com:8080/", true))
	fmt.Println(IsSameWildcardOrigin("http://*.aa.domain.com:*", "http://bb.aa.domain.com:8080/api/v2/xx", true))
	fmt.Println(IsSameWildcardOrigin("http://*.aa.domain.com:8080", "http://bb.aa.domain.com:8443/v2/xx", true))
	fmt.Println(IsSameWildcardOrigin("http://*.aa.domain.com:*", "http://bb.aa.domain.com:8080/v2/xx", true))
	fmt.Println(IsSameWildcardOrigin("*://*.aa.domain.com:*", "http://bb.aa.domain.com:8080/v2/xx", true))

}
