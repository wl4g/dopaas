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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	private Set<String> numbers = new HashSet<>(4);

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

	public Set<String> getNumbers() {
		return numbers;
	}

	public void setNumbers(Set<String> numbers) {
		this.numbers.addAll(numbers);
	}

	public SmsMessage addNumbers(String... numbers) {
		this.numbers.addAll(Arrays.asList(numbers));
		return this;
	}

	public String getTemplateKey() {
		return templateKey;
	}

	public SmsMessage setTemplateKey(String templateKey) {
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
		this.parameters.put(key, value);
		return this;
	}

}
