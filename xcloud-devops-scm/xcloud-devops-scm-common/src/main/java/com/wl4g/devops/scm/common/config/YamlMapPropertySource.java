package com.wl4g.devops.scm.common.config;

import static java.lang.String.valueOf;

import java.util.function.Function;

import com.wl4g.components.common.codec.CodecSource;
import com.wl4g.components.common.crypto.symmetric.AES128ECBPKCS5;
import com.wl4g.devops.scm.common.exception.ScmException;

import lombok.Getter;

/**
 * {@link YamlMapPropertySource}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
public class YamlMapPropertySource extends GenericPropertySource {
	private static final long serialVersionUID = -4793468560178245882L;

	@Override
	protected ScmPropertySource doResolved(Function<String, Object> cipherResolver) {
		log.debug("Resolver cipher configuration propertySource ...");

		getRelease().forEach(rs -> {
			String cipher = valueOf(value);
			if (cipher.startsWith(CIPHER_PREFIX)) {
				try {
					// TODO using dynamic cipherKey??
					byte[] cipherKey = AES128ECBPKCS5.getEnvCipherKey("DEVOPS_CIPHER_KEY");
					String cipherText = cipher.substring(CIPHER_PREFIX.length());
					// TODO fromHex()??
					String plain = new AES128ECBPKCS5().decrypt(cipherKey, CodecSource.fromHex(cipherText)).toString();
					rs.getSource().put(key, plain);

					log.debug("Decryption property key: {}, cipherText: {}, plainText: {}", key, cipher, plain);
				} catch (Exception e) {
					throw new ScmException("Cipher decryption error.", e);
				}
			}
		});

		return null;
	}

}
