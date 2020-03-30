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

import static com.wl4g.devops.tool.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.devops.common.web.RespBase.ErrorPromptMessageBuilder;
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

	static class TestGenericModel<T> {
		private T value;

		public TestGenericModel() {
			super();
		}

		public TestGenericModel(T value) {
			super();
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}

	}

	public static void main(String[] args) {
		respBasePromptTest1();
		System.out.println("----------11111----------------");

		// for controller output(map).
		RespBase<TestModel> resp11 = new RespBase<>(RetCode.newCode(4001, "message1"));
		resp11.forMap().put("testModel11", new TestModel("jack11"));
		resp11.forMap().put("testModel12", new TestModel("jack12"));
		resp11.forMapNode("testNode1").put("k11", "v11");
		resp11.forMapNode("testNode2").put("k21", "v21");
		System.out.println(">>As Map>>" + resp11.asMap());

		String json11 = toJSONString(resp11);
		System.out.println(json11);
		RespBase<TestModel> resp12 = parseJSON(json11, new TypeReference<RespBase<TestModel>>() {
		});
		// for testing unsupported
		// TestModel tm1 = (TestModel) resp12.forMap().get("testModel");
		TestModel tm1 = (TestModel) resp12.getData();
		System.out.println(tm1);
		System.out.println("----------22222---------------");

		// for controller output(model).
		RespBase<TestModel> resp21 = new RespBase<>(RetCode.newCode(4001, "message2"));
		resp21.setData(new TestModel("jack2"));
		System.out.println(">>As Map>>" + resp21.asMap());
		// for testing unsupported
		// resp21.forNode("testNode2").put("node2", "n22");

		String json21 = toJSONString(resp21);
		System.out.println(json21);
		RespBase<TestModel> resp22 = parseJSON(json21, new TypeReference<RespBase<TestModel>>() {
		});
		Object tm2 = resp22.getData();
		System.out.println(tm2);
		System.out.println("----------33333---------------");

		// for generic types.
		RespBase<TestGenericModel<TestSubModel>> resp31 = new RespBase<>(RetCode.newCode(4001, "message3"));
		resp31.setData(new TestGenericModel<TestSubModel>(new TestSubModel("jack3")));
		System.out.println(">>As Map>>" + resp31.asMap());

		String json31 = toJSONString(resp31);
		System.out.println(json31);
		RespBase<TestGenericModel<TestSubModel>> resp32 = parseJSON(json31,
				new TypeReference<RespBase<TestGenericModel<TestSubModel>>>() {
				});
		Object tm3 = resp32.getData();
		System.out.println(tm3);
		System.out.println("-----------44444--------------");

	}

	public static void respBasePromptTest1() {
		String errmsg = ErrorPromptMessageBuilder.build(RetCode.PARAM_ERR, "参数错误");
		System.out.println(errmsg);
		System.out.println(ErrorPromptMessageBuilder.build(RetCode.SYS_ERR, errmsg));
	}

}