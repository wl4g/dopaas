package com.wl4g.devops.iam.authc.credential.secure;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.util.Assert;

import com.wl4g.devops.common.utils.CheckSums;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.configure.SecurerConfig;

/**
 * Default credentials securer
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月16日
 * @since
 */
public class DefaultCredentialsSecurer extends AbstractCredentialsSecurerAdapter {

	public DefaultCredentialsSecurer(SecurerConfig config, JedisCacheManager cacheManager) {
		super(config, cacheManager);
	}

	@Override
	protected ByteSource merge(ByteSource privateSalt, ByteSource publicSalt) {
		return ByteSource.Util.bytes(crossCombined(privateSalt.getBytes(), publicSalt.getBytes()));
	}

	@Override
	protected ByteSource getPublicSalt(String principal) {
		return ByteSource.Util.bytes(principal);
	}

	/**
	 * Cross combined of bytes
	 * 
	 * @param privateSalt
	 * @param publicSalt
	 * @return
	 */
	private static byte[] crossCombined(byte[] privateSalt, byte[] publicSalt) {
		Assert.notNull(privateSalt, "'privateSalt' must not be null");
		Assert.notNull(publicSalt, "'publicSalt' must not be null");
		int privateSaltLength = privateSalt != null ? privateSalt.length : 0;
		int publicSaltLength = publicSalt != null ? publicSalt.length : 0;

		int length = privateSaltLength + publicSaltLength;
		if (length <= 0) {
			return null;
		}

		byte[] combined = new byte[length];
		int i = 0;
		for (int j = 0, k = 0; j < privateSaltLength || k < publicSaltLength; j++, k++) {
			if (j < privateSaltLength) {
				combined[i++] = privateSalt[j];
			}
			if (k < publicSaltLength) {
				combined[i++] = publicSalt[k];
			}
		}
		return combined;
	}

	public static void main(String[] args) {
		// Get hashing public salt
		ByteSource publicSalt = ByteSource.Util.bytes("admin");
		// Merge salt
		// String privateSalt = "IAM";
		String privateSalt = "safecloud";
		ByteSource salt = ByteSource.Util
				.bytes(crossCombined(ByteSource.Util.bytes(privateSalt).getBytes(), publicSalt.getBytes()));

		final String[] hashAlgorithms = new String[] { "MD5", "SHA-256", "SHA-384", "SHA-512" };
		final int size = hashAlgorithms.length;
		final long index = CheckSums.crc32(salt.getBytes()) % size & (size - 1);
		final String algorithm = hashAlgorithms[(int) index];
		final int hashIterations = (int) (Integer.MAX_VALUE % (index + 1)) + 1;
		System.out.println(">>>>>>>>>>");
		System.out.print(new SimpleHash(algorithm, ByteSource.Util.bytes("123456"), salt, hashIterations).toHex());
		System.out.print("\n<<<<<<<<<<");
	}

}
