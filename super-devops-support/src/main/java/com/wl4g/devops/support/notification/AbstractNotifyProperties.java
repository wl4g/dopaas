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

import javax.validation.constraints.NotEmpty;

import com.wl4g.devops.components.tools.common.log.SmartLogger;

import java.util.Map;
import java.util.Properties;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Abstract Notify configuration properties.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月25日
 * @since
 */
public abstract class AbstractNotifyProperties implements NotifyProperties {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Case sensitive when parsing template parameters.
	 */
	private boolean caseSensitive = false;

	/**
	 * Notification message template IDS.
	 * 
	 * @see http://www.bejson.com/convert/unicode_chinese/
	 */
	@NotEmpty
	private Properties templates = new Properties();

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public Properties getTemplates() {
		return templates;
	}

	public void setTemplates(Properties templates) {
		if (!isEmpty(templates)) {
			// Check and sets
			this.templates.putAll(templates.entrySet().stream().map(e -> {
				if (isNull(e.getKey()) || isNull(e.getValue()) || isBlank((String) e.getKey())) {
					throw new IllegalArgumentException(
							format("Cannot configure empty template, key: %s, value: %s", e.getKey(), e.getValue()));
				}
				return e;
			}).collect(toMap(e -> e.getKey(), e -> e.getValue())));
		}
	}

	/**
	 * Gets resolved template message.
	 * 
	 * @param templateKey
	 * @param parameters
	 * @return
	 */
	public String getResolvedMessage(String templateKey, Map<String, Object> parameters) {
		hasTextOf(templateKey, "notifyTemplateKey");
		notNullOf(parameters, "notifyTemplateParameters");

		// Gets template content
		String tplContent = getTemplates().getProperty(templateKey);
		hasTextOf(tplContent, format("No such notification template content key of: %s", templateKey));

		// Resolving template message
		StringBuffer template = new StringBuffer(tplContent);
		parameters.forEach((k, v) -> {
			String template0 = template.toString();
			template.setLength(0);
			String key = "${" + k + "}";
			if (isCaseSensitive()) {
				template.append(replace(template0, key, valueOf(v)));
			} else {
				template.append(replaceIgnoreCase(template0, key, valueOf(v)));
			}
		});

		// Check full resolved
		String message = template.toString();
		checkHasFullyResolved(message);
		return message;
	}

	/**
	 * Check if the specified template exists.
	 * 
	 * @param templateKey
	 * @return
	 */
	public boolean hasTemplateKey(String templateKey) {
		return getTemplates().containsKey(templateKey);
	}

	/**
	 * Check that the template message has been fully parsed, that is, there is
	 * no: ${VAR1} variable in the body of the message
	 * 
	 * @param tplMessage
	 * @return
	 * @throws NotificationMessageParseException
	 */
	public void checkHasFullyResolved(String tplMessage) throws NotificationMessageParseException {
		int firstStart = tplMessage.indexOf("$");
		if (firstStart > -1) {
			int firstEnd = tplMessage.indexOf("}");
			isTrue(firstEnd > -1, NotificationMessageParseException.class,
					"Notification message template syntax error, No ending '}' after start index: %s", firstStart);

			String firstVar = EMPTY;
			if ((firstEnd + 1) == tplMessage.length()) {
				firstVar = tplMessage.substring(firstStart);
			} else {
				firstVar = tplMessage.substring(firstStart, firstEnd + 1);
			}
			throw new NotificationMessageParseException(format(
					"Notification template parsing error, and unresolved symbol variable: %s, => %s", firstVar, tplMessage));
		}

	}

	@Override
	public void validate() {

	}

}