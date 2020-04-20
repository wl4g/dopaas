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
package com.wl4g.devops.iam.common.security.cipher;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.wl4g.devops.tool.common.collection.Collections2.isEmptyArray;
import static com.wl4g.devops.tool.common.lang.Assert2.isTrue;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import static com.wl4g.devops.iam.common.config.AbstractIamProperties.CipherProperties.*;

import com.wl4g.devops.common.exception.iam.UnableDecryptionCipherParameterException;
import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Abstract ciper parameters {@link HttpServletRequestWrapper} implements.
 *
 * @author wangl.sir
 * @version v1.0 2019年4月26日
 * @since
 */
public abstract class CipherRequestWrapper extends HttpServletRequestWrapper {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * {@link AbstractIamProperties}
	 */
	final private AbstractIamProperties<? extends ParamProperties> config;

	public CipherRequestWrapper(AbstractIamProperties<? extends ParamProperties> config, HttpServletRequest request) {
		super(request);
		notNullOf(config, "config");
		this.config = config;
	}

	@Override
	public String getParameter(String name) {
		return doDecrypting(name, super.getParameter(name));
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] paramValues = super.getParameterValues(name);
		if (isEmptyArray(paramValues)) {
			return null;
		}

		for (int i = 0; i < paramValues.length; i++) {
			paramValues[i] = doDecrypting(name, paramValues[i]);
		}

		return paramValues;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> paramMapAll = new LinkedHashMap<>();

		Map<String, String[]> tmpMap = super.getParameterMap();
		for (String key : tmpMap.keySet()) {
			String[] values = tmpMap.get(key);
			for (int i = 0; i < values.length; i++) {
				values[i] = doDecrypting(key, values[i]);
			}
			paramMapAll.put(key, values);
		}

		return paramMapAll;
	}

	@SuppressWarnings("unchecked")
	protected <O, I> O doDecrypting(String paramName, I paramValue) {
		if (!isNull(paramValue) && paramValue instanceof String && !isBlank((String) paramValue)) {
			try {
				for (String defineParam : config.getCipher().getCipherParameterHeader()) {
					if (matchParameter(defineParam, paramName)) {
						log.debug("Decrypting cipher parameter name: {}, origin value: {}", paramName, paramValue);
						return (O) doDecryptParameterValue((String) paramValue);
					}
				}
			} catch (Exception ex) {
				throw new UnableDecryptionCipherParameterException(
						format("Unable decrypting cipher parameter failure of: %s ", paramValue), ex);
			}
		}

		return (O) paramValue;
	}

	/**
	 * Matching define parameter name and request parameter name.
	 * 
	 * @param defineParam
	 * @param param
	 * @return
	 */
	protected boolean matchParameter(String defineParam, String param) {
		isTrue(defineParam.contains(CIPHER_HEADER_PREFIX),
				"Illegal define cipher headerEncryptParameter: %s, Shouldn't be here. Please check the configuration? ",
				defineParam);

		// Cleanup defineParameter of header prefix.
		defineParam = defineParam.substring(defineParam.indexOf(CIPHER_HEADER_PREFIX) + CIPHER_HEADER_PREFIX.length());

		// Matching header parameter.
		return (config.getCipher().isCaseSensitive() && defineParam.equals(param))
				|| (!config.getCipher().isCaseSensitive() && defineParam.equalsIgnoreCase(param));
	}

	/**
	 * Do eecryption cipher parameter.
	 * 
	 * @param value
	 * @return
	 */
	protected abstract String doDecryptParameterValue(String value);

}