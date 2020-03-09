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
package com.wl4g.devops.support.notification.sms;

import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.wl4g.devops.support.notification.NotifyMessage;

/**
 * {@link SmsMessage}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月9日 v1.0.0
 * @see
 */
public class SmsMessage implements NotifyMessage {
	private static final long serialVersionUID = 1303039928183495028L;

	/**
	 * Sms sent target number
	 */
	@NotEmpty
	private List<String> numbers = new ArrayList<>(4);

	/**
	 * Sms message template key-name.
	 */
	@NotBlank
	private String templateKey;

	/**
	 * Sms placeholder message parameters.
	 */
	@NotEmpty
	private Map<String, String> parameters = new HashMap<>();

	/**
	 * The ID reserved for the caller will eventually be brought back to the
	 * caller through the receipt message. String type, 1-15 bytes in length.
	 */
	private String callbackId;

	public SmsMessage(@NotBlank String templateKey) {
		this(templateKey, null, null);
	}

	public SmsMessage(@NotBlank String templateKey, @NotEmpty List<String> numbers) {
		this(templateKey, null, numbers);
	}

	public SmsMessage(@NotBlank String templateKey, String callbackId, @NotEmpty List<String> numbers) {
		setTemplateKey(templateKey);
		setCallbackId(callbackId);
		setNumbers(numbers);
	}

	public List<String> getNumbers() {
		return numbers;
	}

	public SmsMessage setNumbers(List<String> numbers) {
		if (!isNull(numbers)) {
			this.numbers = numbers;
		}
		return this;
	}

	public SmsMessage addNumbers(String... numbers) {
		this.numbers.addAll(Arrays.asList(numbers));
		return this;
	}

	public String getTemplateKey() {
		return templateKey;
	}

	public SmsMessage setTemplateKey(String templateKey) {
		hasTextOf(templateKey, "vmsTemplateKey");
		this.templateKey = templateKey;
		return this;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters.putAll(parameters);
	}

	public SmsMessage addParameter(String key, String value) {
		hasTextOf(key, "smsParameterKey");
		hasTextOf(value, "smsParameterValue");
		this.parameters.put(key, value);
		return this;
	}

	public String getCallbackId() {
		return callbackId;
	}

	public SmsMessage setCallbackId(String callbackId) {
		if (!isBlank(callbackId)) {
			this.callbackId = callbackId;
		}
		return this;
	}

}
