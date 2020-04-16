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
package com.wl4g.devops.iam.common.session;

import org.apache.commons.lang3.SerializationUtils;

import com.wl4g.devops.common.bean.iam.SocialAuthorizeInfo;
import com.wl4g.devops.common.utils.serialize.ProtostuffUtils;

public class IamSessionTests {

	public static void main(String[] args) {
		IamSession session = new IamSession();
		session.setHost("127.0.0.1");
		SocialAuthorizeInfo info = new SocialAuthorizeInfo("provideraaaa", "openIdbbbb");
		info.getUserProfile().put("a1", 11);
		info.getUserProfile().put("b2", "2222");
		session.setAttribute("userProfile", info);

		byte[] res = ProtostuffUtils.serialize(session);
		IamSession s2 = ProtostuffUtils.deserialize(res, IamSession.class);
		System.out.println(s2.getAttribute("userProfile"));

		System.out.println("-----------------");

		byte[] data = SerializationUtils.serialize(new IamSession("111"));
		IamSession s = SerializationUtils.deserialize(data);
		System.out.println(s);

	}

}
