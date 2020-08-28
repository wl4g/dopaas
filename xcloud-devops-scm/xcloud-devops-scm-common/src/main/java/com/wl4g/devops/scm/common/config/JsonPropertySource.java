package com.wl4g.devops.scm.common.config;

import java.util.HashMap;
import java.util.Map;

import static com.wl4g.components.common.serialize.JacksonUtils.parseJSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.devops.scm.common.model.AbstractConfigInfo.ConfigProfile;

import lombok.Getter;

/**
 * {@link JsonPropertySource}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
public class JsonPropertySource extends GenericPropertySource {
	private static final long serialVersionUID = 5937417582294678642L;

	/**
	 * Resolved JSON property source.
	 */
	private Map<Object, Object> resolved;

	@Override
	public void doRead(ConfigProfile profile, String sourceContent) {
		this.resolved = parseJSON(sourceContent, DEFAULT_REFTYPE);

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

	}

	private static final TypeReference<HashMap<Object, Object>> DEFAULT_REFTYPE = new TypeReference<HashMap<Object, Object>>() {
	};

}
