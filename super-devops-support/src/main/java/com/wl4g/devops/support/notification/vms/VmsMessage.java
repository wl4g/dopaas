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
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.wl4g.devops.support.notification.NotifyMessage;

public abstract class VmsMessage implements NotifyMessage {
	private static final long serialVersionUID = 1303039928183495028L;

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

	/**
	 * The ID reserved for the caller will eventually be brought back to the
	 * caller through the receipt message. String type, 1-15 bytes in length.
	 */
	private String callbackId;

	public VmsMessage(String calledNumber, String templateKey) {
		this(calledNumber, templateKey, null);
	}

	public VmsMessage(String calledNumber, String templateKey, String callbackId) {
		hasTextOf(calledNumber, "calledNumber");
		hasTextOf(templateKey, "templateKey");
		this.calledNumber = calledNumber;
		this.templateKey = templateKey;
		setCallbackId(callbackId);
	}

	public String getCalledNumber() {
		return calledNumber;
	}

	public String getTemplateKey() {
		return templateKey;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public VmsMessage setParameters(Map<String, String> parameters) {
		this.parameters.putAll(parameters);
		return this;
	}

	public VmsMessage addParameter(String key, String value) {
		hasTextOf(key, "vmsParameterKey");
		hasTextOf(value, "vmsParameterValue");
		this.parameters.put(key, value);
		return this;
	}

	public String getCallbackId() {
		return callbackId;
	}

	public VmsMessage setCallbackId(String callbackId) {
		if (!isBlank(callbackId)) {
			this.callbackId = callbackId;
		}
		return this;
	}

}
