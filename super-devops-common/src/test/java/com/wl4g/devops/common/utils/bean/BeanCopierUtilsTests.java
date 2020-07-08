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
package com.wl4g.devops.common.utils.bean;

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BeanCopierUtilsTests {

	public static UserBean createUser() {
		UserBean user = new UserBean();
		NameBean nameObj = new NameBean();
		user.setName(nameObj);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		user.setPhone("18576670069");
		user.setSex("1");
		user.setAge(10);
		try {
			user.setBirthdate(dateFormat.parse("1990-10-11"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		user.setMoney(99999l);
		user.setCreateTime(new Date());
		user.setCreateUser("张三");
		user.setModifyTime(new Date());
		user.setModifyUser("李四");
		user.setIsValidate("1");
		user.setComments("2020年 COVID-19");

		try {
			nameObj.setCreateTime(dateFormat.parse("2020-03-06"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		nameObj.setCreateUser("王五");
		nameObj.setModifyTime(new Date());
		nameObj.setModifyUser("赵六");
		nameObj.setIsValidate("1");
		nameObj.setComments("2020年 COVID-19");

		return user;
	}

	public static void main(String[] args) {
		UserBean user = BeanCopierUtilsTests.createUser();

		UserBean user2 = BeanCopierUtils.mapper(user, user.getClass());
		System.out.println("user2: " + user2);
		System.out.println("user2 json: " + toJSONString(user2));

		UserBean user3 = BeanCopierUtils.mapper(user, user.getClass(), userBean -> {
			userBean.getName().setFirstName("川_");
			userBean.getName().setLastName("建国");
			System.out.println("userBean: " + userBean);
			return userBean;
		});
		System.out.println("user3: " + user3);
		System.out.println("user3 json: " + toJSONString(user3));

	}

}
