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
package com.wl4g.devops.support.notification.vms;

import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.wl4g.devops.support.notification.NotifyMessage;

public abstract class VmsMessage implements NotifyMessage {
	private static final long serialVersionUID = 1303039928183495028L;

	/**
	 * Called show number
	 */
	@NotBlank
	private String calledShowNumber;

	/**
	 * Called number
	 */
	@NotBlank
	private String calledNumber;

	/**
	 * Vms message template key-name.
	 */
	@NotBlank
	private String templateKey;

	/**
	 * Vms placeholder message parameters.
	 */
	@NotEmpty
	private Map<String, String> parameters = new HashMap<>();

	public VmsMessage(String calledNumber, String templateKey) {
		this(calledNumber, calledNumber, templateKey);
	}

	public VmsMessage(String calledShowNumber, String calledNumber, String templateKey) {
		setCalledShowNumber(calledShowNumber);
		setCalledNumber(calledNumber);
		setTemplateKey(templateKey);
	}

	public String getCalledShowNumber() {
		return calledShowNumber;
	}

	public VmsMessage setCalledShowNumber(String calledShowNumber) {
		hasTextOf(calledShowNumber, "calledShowNumber");
		this.calledShowNumber = calledShowNumber;
		return this;
	}

	public String getCalledNumber() {
		return calledNumber;
	}

	public VmsMessage setCalledNumber(String calledNumber) {
		hasTextOf(calledNumber, "calledNumber");
		this.calledNumber = calledNumber;
		return this;
	}

	public String getTemplateKey() {
		return templateKey;
	}

	public VmsMessage setTemplateKey(String templateKey) {
		hasTextOf(templateKey, "vmsTemplateKey");
		this.templateKey = templateKey;
		return this;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public VmsMessage setParameters(Map<String, String> parameters) {
		this.parameters.putAll(parameters);
		return this;
	}

	public VmsMessage addParameter(String key, String value) {
		this.parameters.put(key, value);
		return this;
	}

}
