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
package com.wl4g.devops.support.notification;

import com.wl4g.devops.common.web.RespBase.DataMap;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wl4g.devops.components.tools.common.lang.Assert2.hasTextOf;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * {@link GenericNotifyMessage}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月14日 v1.0.0
 * @see
 */
@Validated
public class GenericNotifyMessage implements NotifyMessage {

	private static final long serialVersionUID = 7422435702403504747L;

	/**
	 * The notification target objects.
	 */
	@NotEmpty
	private List<String> toObjects = new ArrayList<>(4);

	/**
	 * Notification message template key-name.
	 */
	@NotBlank
	private String templateKey;

	/**
	 * The value list of the notification message content placeholder parameter.
	 */
	@NotNull
	private DataMap<Object> parameters = new DataMap<Object>(4) {
		private static final long serialVersionUID = 1299361493607274200L;

		@Override
		public Object get(Object key) {
			return super.get(key);
		}

	};

	/**
	 * The notification receipt ID, which can be used to process reliable
	 * message confirmation application (optional), string type, 1-15 bytes
	 * long.
	 */
	private String callbackId;

	public GenericNotifyMessage() {
	}

	public GenericNotifyMessage(@NotBlank String singleToObject, @NotBlank String templateKey) {
		addToObjects(singleToObject);
		setTemplateKey(templateKey);
	}

	@Override
	public List<String> getToObjects() {
		return toObjects;
	}

	/**
	 * Sets notification target objects.
	 * 
	 * @param toObjects
	 * @return
	 */
	public GenericNotifyMessage setToObjects(@NotEmpty List<String> toObjects) {
		this.toObjects = toObjects;
		return this;
	}

	/**
	 * Add notification target objects.
	 * 
	 * @param toObjectArray
	 * @return
	 */
	public GenericNotifyMessage addToObjects(@NotEmpty String... toObjectArray) {
		if (!isNull(toObjectArray) && toObjectArray.length > 0) {
			for(String s : toObjectArray){
				hasTextOf(s,"toObjects");
			}
			toObjects.addAll(asList(toObjectArray).stream().filter(t -> !isNull(t)).collect(toList()));
		}
		return this;
	}

	@Override
	public String getTemplateKey() {
		return templateKey;
	}

	/**
	 * Sets notification message content template ID
	 * 
	 * @param templateKey
	 * @return
	 */
	public GenericNotifyMessage setTemplateKey(@NotBlank String templateKey) {
		hasTextOf(templateKey, "templateKey");
		this.templateKey = templateKey;
		return this;
	}

	@Override
	public DataMap<Object> getParameters() {
		return parameters;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getParameter(@NotBlank String key, Object defaultValue) {
		hasTextOf(key, "parameterKey");
		return (T) getParameters().getOrDefault(key, defaultValue);
	}

	@Override
	public String getParameterAsString(@NotBlank String key, Object defaultValue) {
		hasTextOf(key, "parameterKey");
		Object value = getParameters().getOrDefault(key, defaultValue);
		return isNull(value) ? null : value.toString();
	}

	/**
	 * Add the value list of the notification message content placeholder
	 * parameter.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public GenericNotifyMessage addParameter(@NotBlank String key, Object value) {
		hasTextOf(key, "parameterKey");
		// notNullOf(value, "parameterValue");
		parameters.put(key, value);
		return this;
	}

	/**
	 * Add the value list of the notification message content placeholder
	 * parameter.
	 * 
	 * @param parameters
	 * @return
	 */
	public GenericNotifyMessage addParameters(Map<String, Object> parameters) {
		if (!isNull(parameters) && !parameters.isEmpty()) {
			parameters.putAll(parameters.entrySet().stream().filter(e -> !isNull(e.getKey()))
					.collect(toMap(e -> e.getKey(), e -> e.getValue())));
		}
		return this;
	}

	@Override
	public String getCallbackId() {
		return callbackId;
	}

	/**
	 * Sets notification receipt ID, which can be used to process reliable
	 * message confirmation application (optional), string type, 1-15 bytes
	 * long.
	 * 
	 * @param callbackId
	 * @return
	 */
	public GenericNotifyMessage setCallbackId(@NotBlank String callbackId) {
		this.callbackId = callbackId;
		return this;
	}

}