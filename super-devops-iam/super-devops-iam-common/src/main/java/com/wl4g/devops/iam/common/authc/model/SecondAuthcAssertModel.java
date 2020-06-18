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
package com.wl4g.devops.iam.common.authc.model;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.wl4g.devops.components.tools.common.serialize.JacksonUtils;

/**
 * Secondary authentication assertion.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @Long 2018年11月22日
 * @since
 */
public final class SecondAuthcAssertModel implements Serializable {

	private static final long serialVersionUID = 5483716885382988025L;

	/** The application for which this assertion is valid for. */
	@NotNull
	private String application;

	/**
	 * social networking service provider ID
	 */
	private String provider;

	/** The principal for which this assertion is valid for. */
	@NotNull
	private String principal;

	/** Business Function ID. */
	@NotNull
	private String functionId;

	/** The Long from which the assertion is valid(start Long). */
	@NotNull
	private Date validFromDate;

	/**
	 * Assertion result status.
	 */
	private Status status = Status.Authenticated;

	/**
	 * Assertion result description.
	 */
	private String errdesc = "Second authenticated";

	public SecondAuthcAssertModel() {
		super();
	}

	public SecondAuthcAssertModel(Status status) {
		super();
		this.status = status;
	}

	public SecondAuthcAssertModel(String application, String provider, String functionId) {
		this(application, provider, null, functionId, null);
	}

	public SecondAuthcAssertModel(String application, String provider, String principal, String functionId, Date validFromDate) {
		this.application = application;
		this.provider = provider;
		this.principal = principal;
		this.functionId = functionId;
		this.validFromDate = validFromDate;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public Date getValidFromDate() {
		return validFromDate;
	}

	public void setValidFromDate(Date validFromDate) {
		this.validFromDate = validFromDate;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getErrdesc() {
		return errdesc;
	}

	public void setErrdesc(String description) {
		this.errdesc = description;
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

	/**
	 * Secondary authentication status
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年3月1日
	 * @since
	 */
	public static enum Status {

		/**
		 * Successful certified.
		 */
		Authenticated,

		/**
		 * Illegal authorizer.
		 */
		IllegalAuthorizer,

		/**
		 * Invalid authorizer.
		 */
		InvalidAuthorizer,

		/**
		 * Expired authorized information.
		 */
		ExpiredAuthorized;
	}

}