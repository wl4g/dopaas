/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.common.bean.scm;

/**
 * 对应表：cf_release_history
 * 
 * @author zzh
 * @Description: TODO
 * @date 2018年9月26日
 */
public class Version extends BaseBean {

	private String sign; // 签名字符串
	private String signtype; // 签名类型
	private Integer tag; // 版本标记

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSigntype() {
		return signtype;
	}

	public void setSigntype(String signtype) {
		this.signtype = signtype;
	}

	public Integer getTag() {
		return tag;
	}

	public void setTag(Integer tag) {
		this.tag = tag;
	}
}