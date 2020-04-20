/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.iam.web;

import static com.wl4g.devops.iam.web.SimpleRcmEvaluatorEndpoint.*;

import java.util.Map;

public class SimpleRcmEvaluatorControllerTests {

	public static void main(String[] args) {
		String umdata = "177!KAcEgP3RkyXgfqzvXFobSofgHMkhTEkQx6fnieP1WHsuhkY3iwLai4vWJQm98HxAgmbg2tE79WepDCw61QgT2SpyG7GfX64a4WF7xX1nbEuoenEgazzfzFFrWZAnBW9oz19jrF18yRhRNnBwYZb1DMYzJokheriSyd7h92pVbWyrPsSevvtskeTNo4bnT3jo4GNwQK9rjv5G6mE7hRpJV63W3aJnSj4WEVszXAHmVoqM5oi1m9W2XcjtpdzV76zP2FnDM1wtVJkTcCG34m9ca2gHEZZYtkDG95pcQXRHbNbswQ4f4oxrg5R597AY6PACQmo8nTS4sMun4EGd";
		Map<String, String> umParam = parseUmdataParameters(umdata);
		System.out.println(umParam);

	}

}
