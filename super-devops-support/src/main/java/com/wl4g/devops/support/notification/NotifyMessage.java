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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

/**
 * {@link NotifyMessage}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月9日 v1.0.0
 * @see
 */
public interface NotifyMessage extends Serializable {

	/**
	 * Gets notification target objects.
	 * 
	 * @return
	 */
	List<String> getToObjects();

	/**
	 * Gets notification message content template ID
	 * 
	 * @return
	 */
	String getTemplateKey();

	/**
	 * Gets the value list of the notification message content placeholder
	 * parameter.
	 * 
	 * @return
	 */
	Map<String, Object> getParameters();

	/**
	 * Gets parameter value by key.
	 * 
	 * @param key
	 * @return
	 */
	default <T> T getParameter(@NotBlank String key) {
		return getParameter(key, null);
	}

	/**
	 * Gets parameter value by key.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	<T> T getParameter(@NotBlank String key, Object defaultValue);

	/**
	 * Gets parameter value by key to string.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	String getParameterAsString(@NotBlank String key, Object defaultValue);

	/**
	 * Gets notification receipt ID, which can be used to process reliable
	 * message confirmation application (optional), string type, 1-15 bytes
	 * long.
	 * 
	 * @return
	 */
	String getCallbackId();

}