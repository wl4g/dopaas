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
package com.wl4g.devops.scm.common.session;
///*
// * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.devops.scm.session;
//
//import static com.wl4g.component.common.codec.CodecSource.*;
//import static com.wl4g.component.common.lang.Assert2.notNullOf;
//import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
//
//import java.security.spec.KeySpec;
//
//import com.wl4g.component.common.codec.CodecSource;
//import com.wl4g.component.common.crypto.asymmetric.AsymmetricCryptor;
//import com.wl4g.component.common.crypto.asymmetric.spec.KeyPairSpec;
//import com.wl4g.component.common.crypto.symmetric.AES128ECBPKCS5;
//import com.wl4g.component.common.log.SmartLogger;
//
///**
// * Abstract scm asymmetric security manager support.
// *
// * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
// * @version v1.0 2020年5月26日
// * @since
// * @param <K>
// */
//public abstract class ConfigSecurityManagerSupport {
//
//	final protected SmartLogger log = getLogger(getClass());
//
//	/**
//	 * Asymmetric asymmeCryptor
//	 */
//	final protected AsymmetricCryptor asymmeCryptor;
//
//	/**
//	 * Symmetric asymmeCryptor
//	 */
//	final protected AES128ECBPKCS5 symmeCryptor;
//
//	@Autowired
//	protected Environment env;
//
//	/**
//	 * Spring application name.
//	 */
//	protected String appName;
//
//	protected ConfigSecurityManagerSupport(AsymmetricCryptor asymmeCryptor, AES128ECBPKCS5 symmeCryptor) {
//		notNullOf(asymmeCryptor, "asymmeCryptor");
//		notNullOf(symmeCryptor, "symmeCryptor");
//		this.asymmeCryptor = asymmeCryptor;
//		this.symmeCryptor = symmeCryptor;
//	}
//
//	@Override
//	public void afterPropertiesSet() throws Exception {
//		this.appName = env.getProperty("spring.application.name");
//	}
//
//	//
//	// --- Asymmetric.---
//	//
//
//	/**
//	 * Encryption with hex plain.
//	 *
//	 * @param keySpec
//	 * @param plaintext
//	 * @return
//	 */
//	protected String encrypt(KeySpec keySpec, String plaintext) {
//		return asymmeCryptor.encrypt(keySpec, new CodecSource(plaintext)).toHex();
//	}
//
//	/**
//	 * Decryption with hex cipher.
//	 *
//	 * @param keySpec
//	 * @param hexCiphertext
//	 * @return
//	 */
//	protected String decrypt(KeySpec keySpec, String hexCiphertext) {
//		return asymmeCryptor.decrypt(keySpec, fromHex(hexCiphertext)).toString();
//	}
//
//	/**
//	 * Generate keyPairSpec by publicKey and privateKey.
//	 * 
//	 * @param hexPublicKey
//	 * @param hexPublicKey
//	 * @return
//	 */
//	protected KeyPairSpec generateKeyPair(String hexPublicKey, String hexPrivateKey) {
//		return asymmeCryptor.generateKeyPair(fromHex(hexPublicKey).getBytes(), fromHex(hexPrivateKey).getBytes());
//	}
//
//	/**
//	 * Deserialization generate private KeySpec.
//	 * 
//	 * @param hexPublicKey
//	 * @return
//	 */
//	protected KeySpec generatePubKeySpec(String hexPublicKey) {
//		return asymmeCryptor.generatePubKeySpec(fromHex(hexPublicKey).getBytes());
//	}
//
//	/**
//	 * Deserialization generate private KeySpec.
//	 * 
//	 * @param hexPrivateKey
//	 * @return
//	 */
//	protected KeySpec generateKeySpec(String hexPrivateKey) {
//		return asymmeCryptor.generateKeySpec(fromHex(hexPrivateKey).getBytes());
//	}
//
//	//
//	// --- Symmetric.---
//	//
//
//	/**
//	 * Encryption data with hex plain.
//	 *
//	 * @param cipherKey
//	 * @param plaintext
//	 * @return
//	 */
//	protected String encryptData(byte[] cipherKey, String plaintext) {
//		return symmeCryptor.encrypt(cipherKey, new CodecSource(plaintext)).toHex();
//	}
//
//	/**
//	 * Decryption data with hex cipher.
//	 *
//	 * @param cipherKey
//	 * @param hexCiphertext
//	 * @return
//	 */
//	protected String decryptData(byte[] cipherKey, String hexCiphertext) {
//		return symmeCryptor.decrypt(cipherKey, fromHex(hexCiphertext)).toHex();
//	}
//
//	/**
//	 * Generate symmetric algorithm key, return byte array, default length is
//	 * 128 bits (16 bytes)
//	 * 
//	 * @return
//	 */
//	protected CodecSource generateKey() {
//		return symmeCryptor.generateKey(1024);
//	}
//
//}