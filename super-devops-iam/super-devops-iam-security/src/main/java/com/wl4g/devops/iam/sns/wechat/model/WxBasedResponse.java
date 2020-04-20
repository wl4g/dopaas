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
package com.wl4g.devops.iam.sns.wechat.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.tool.common.serialize.JacksonUtils;

/**
 * WeChat based response message model
 *
 * @author wangl.sir
 * @version v1.0 2019年2月19日
 * @since
 */
public abstract class WxBasedResponse implements Serializable {
	private static final long serialVersionUID = -7472798322235907609L;

	final public static int DEFAULT_WX_OK = 0;

	@JsonProperty("errcode")
	private Integer errcode = DEFAULT_WX_OK;
	@JsonProperty("errmsg")
	private String errmsg = "ok";

	public Integer getErrcode() {
		return errcode;
	}

	public void setErrcode(Integer errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

}