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
package com.wl4g.devops.scm.client.locator;

import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;

import java.util.List;

import com.wl4g.components.common.codec.CodecSource;
import com.wl4g.components.common.crypto.symmetric.AES128ECBPKCS5;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.devops.scm.exception.ScmException;
import com.wl4g.devops.scm.model.ReleaseMessage;
import com.wl4g.devops.scm.model.ReleaseMessage.ReleasePropertySource;

/**
 * {@link AbstractConfigSourceLocator}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-11
 * @since
 */
public abstract class AbstractConfigSourceLocator implements ConfigSourceLocator {

	protected final SmartLogger log = getLogger(getClass());

	/**
	 * Resolver cipher configuration source.
	 * 
	 * @param release
	 */
	protected void resolvesCipherSource(ReleaseMessage release) {
		log.debug("Resolver cipher configuration propertySource ...");

		for (ReleasePropertySource ps : release.getPropertySources()) {
			ps.getSource().forEach((key, value) -> {
				String cipher = String.valueOf(value);
				if (cipher.startsWith(CIPHER_PREFIX)) {
					try {
						// TODO using dynamic cipherKey??
						byte[] cipherKey = AES128ECBPKCS5.getEnvCipherKey("DEVOPS_CIPHER_KEY");
						String cipherText = cipher.substring(CIPHER_PREFIX.length());
						// TODO fromHex()??
						String plain = new AES128ECBPKCS5().decrypt(cipherKey, CodecSource.fromHex(cipherText)).toString();
						ps.getSource().put(key, plain);

						log.debug("Decryption property key: {}, cipherText: {}, plainText: {}", key, cipher, plain);
					} catch (Exception e) {
						throw new ScmException("Cipher decryption error.", e);
					}
				}
			});
		}

	}

	/**
	 * Prints property sources.
	 * 
	 * @param release
	 */
	protected void printfSources(ReleaseMessage release) {
		log.info("Fetched from scm config <= group({}), namespace({}), release meta({})", release.getCluster(),
				release.getNamespaces(), release.getMeta());

		if (log.isDebugEnabled()) {
			List<ReleasePropertySource> propertySources = release.getPropertySources();
			if (propertySources != null) {
				int propertyCount = 0;
				for (ReleasePropertySource ps : propertySources) {
					propertyCount += ps.getSource().size();
				}
				log.debug(String.format("Environment has %d property sources with %d properties.", propertySources.size(),
						propertyCount));
			}
		}
	}

	/** SCM encrypted field identification prefix */
	final private static String CIPHER_PREFIX = "{cipher}";

}
