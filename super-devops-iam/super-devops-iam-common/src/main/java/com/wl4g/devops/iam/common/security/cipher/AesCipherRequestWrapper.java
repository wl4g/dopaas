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

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_DATA_CIPHER;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.getBindValue;
import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.tool.common.crypto.CrypticSource;
import com.wl4g.devops.tool.common.crypto.symmetric.AESCryptor;

/**
 * Default AES cipher parameters {@link HttpServletRequestWrapper} implements.
 *
 * @author wangl.sir
 * @version v1.0 2019年4月26日
 * @since
 */
public class AesCipherRequestWrapper extends CipherRequestWrapper {

	public AesCipherRequestWrapper(AbstractIamProperties<? extends ParamProperties> config, HttpServletRequest request) {
		super(config, request);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String doDecryptParameterValue(String value) {
		// Gets data cipherKey.
		String dataCipherKey = getBindValue(KEY_DATA_CIPHER);
		hasTextOf(dataCipherKey, "dataCipherKey");

		// Decryption data ciphertext.
		CrypticSource res = new AESCryptor().decrypt(dataCipherKey.getBytes(UTF_8), new CrypticSource(value));
		log.debug("Decryption data cipherValue: {}, plainValue: {}", () -> value, () -> res.toString());

		return res.toString();
	}

}