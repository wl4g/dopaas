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
package com.wl4g.devops.common.web;

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.devops.common.web.RespBase.RetCode;

public class RespBaseTest {

	static class TestModel {
		private String name;
		private List<TestSubModel> subList = new ArrayList<TestSubModel>() {
			private static final long serialVersionUID = 7144913810891463508L;
			{
				add(new TestSubModel("subModel1"));
				add(new TestSubModel("subModel2"));
			}
		};
		private Map<String, Object> map = new HashMap<String, Object>() {
			private static final long serialVersionUID = 7144913810891463508L;
			{
				put("attr1", "attrValue1");
				put("attr2", "attrValue2");
			}
		};

		public TestModel() {
			super();
		}

		public TestModel(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Map<String, Object> getMap() {
			return map;
		}

		public void setMap(Map<String, Object> map) {
			this.map = map;
		}

		public List<TestSubModel> getSubList() {
			return subList;
		}

		public void setSubList(List<TestSubModel> subList) {
			this.subList = subList;
		}

	}

	static class TestSubModel {
		private String subName;

		public TestSubModel() {
			super();
		}

		public TestSubModel(String subName) {
			super();
			this.subName = subName;
		}

		public String getSubName() {
			return subName;
		}

		public void setSubName(String subName) {
			this.subName = subName;
		}

	}

	public static void main(String[] args) {
		// for controller output(map).
		RespBase<Object> resp11 = new RespBase<>(RetCode.create(4001, "message1"));
		resp11.getData().put("testModel", new TestModel("jack1"));

		String json11 = toJSONString(resp11);
		System.out.println(json11);
		RespBase<TestModel> resp12 = parseJSON(json11, new TypeReference<RespBase<TestModel>>() {
		});
		System.out.println(resp12.getData().get("testModel"));
		System.out.println("-------------------------");

		// for controller output(model).
		RespBase<Object> resp21 = new RespBase<>(RetCode.create(4001, "message2"));
		resp21.setBean(new TestModel("jack2"));

		String json21 = toJSONString(resp21);
		System.out.println(json21);
		RespBase<TestModel> resp22 = parseJSON(json21, new TypeReference<RespBase<TestModel>>() {
		});
		System.out.println(resp22.getData());
		System.out.println("-------------------------");

	}

}
