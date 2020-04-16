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
package com.wl4g.devops.iam.controller;

import static com.wl4g.devops.tool.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.common.web.model.SessionAttributeModel;
import com.wl4g.devops.iam.common.web.model.SessionAttributeModel.CursorIndex;
import com.wl4g.devops.iam.common.web.model.SessionAttributeModel.IamSessionInfo;

public class IamManagerApiV1ControllerTest {

	public static void main(String[] args) {
		// for controller output(model).
		RespBase<SessionAttributeModel> resp11 = new RespBase<>(RetCode.newCode(4001, "message2"));
		resp11.forMap().put("testKey", newSessionAttributeModel());

		String json11 = toJSONString(resp11);
		System.out.println(json11);
		RespBase<SessionAttributeModel> resp12 = parseJSON(json11, new TypeReference<RespBase<SessionAttributeModel>>() {
		});
		// SessionAttributeModel sam1 = resp12.forMap().get("testKey");
		SessionAttributeModel sam1 = resp12.getData();
		System.out.println(sam1);
		System.out.println("===================================================");

		// for controller output(model).
		RespBase<SessionAttributeModel> resp21 = new RespBase<>(RetCode.newCode(4001, "message2"));
		resp21.setData(newSessionAttributeModel());

		String json21 = toJSONString(resp21);
		System.out.println(json21);
		RespBase<SessionAttributeModel> resp22 = parseJSON(json21, new TypeReference<RespBase<SessionAttributeModel>>() {
		});
		Object sam2 = resp22.getData();
		System.out.println(sam2);
		System.out.println("===================================================");
	}

	static SessionAttributeModel newSessionAttributeModel() {
		SessionAttributeModel sam = new SessionAttributeModel();
		sam.setIndex(new CursorIndex("0@5", false));
		IamSessionInfo sa1 = new IamSessionInfo();
		sa1.setId("1111");
		sa1.setAuthenticated(false);
		sa1.setExpired(false);
		sa1.setHost("0.0.0.01");
		sa1.setPrincipal("root");
		sa1.getGrants().add("umc-manager");
		sam.getSessions().add(sa1);
		return sam;
	}

}