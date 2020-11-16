/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.scm.common.config;

import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;

import javax.validation.constraints.NotNull;

import com.wl4g.components.common.codec.CodecSource;
import com.wl4g.components.common.crypto.symmetric.AES128ECBPKCS5;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.devops.scm.common.exception.ScmException;
import com.wl4g.devops.scm.common.model.AbstractConfigInfo.ConfigProfile;
import static com.wl4g.devops.scm.common.SCMConstants.KEY_CIPHER_PREFIX;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * Generic origin base property source.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
@Setter
public abstract class AbstractConfigSource implements ScmConfigSource {
	private static final long serialVersionUID = -5037062685017411482L;

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Configuration files. (like spring.profiles)
	 */
	@NotNull
	private ConfigProfile profile;

	public AbstractConfigSource() {
		super();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
	}

	@Override
	public void read(ConfigProfile profile, String sourceContent) {
		log.debug("Resolving release cipher configuration source ...");
		setProfile(profile);
	}

	/**
	 * DO read & resolving property source.
	 * 
	 * @param profile
	 * @param sourceContent
	 */
	protected abstract void doRead(ConfigProfile profile, String sourceContent);

	/**
	 * Resolving cipher release source property value.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	protected String resolveCipherProperty(String key, String value) {
		if (!isBlank(value) && value.startsWith(KEY_CIPHER_PREFIX)) {
			try {
				// TODO using dynamic cipherKey??
				byte[] cipherKey = AES128ECBPKCS5.getEnvCipherKey("DEVOPS_CIPHER_KEY");
				String cipherText = value.substring(KEY_CIPHER_PREFIX.length());

				// TODO fromHex()??
				String plainVal = new AES128ECBPKCS5().decrypt(cipherKey, CodecSource.fromHex(cipherText)).toString();
				log.debug("Decryption property key: {}, cipherText: {}, plainText: {}", key, value, plainVal);

				return plainVal;
			} catch (Exception e) {
				throw new ScmException(format("Cannot decrypt cipher property. '%s' -> '%s'", key, value), e);
			}
		}
		return value;
	}

}