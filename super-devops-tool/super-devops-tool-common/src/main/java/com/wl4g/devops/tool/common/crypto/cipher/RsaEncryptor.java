package com.wl4g.devops.tool.common.crypto.cipher;

import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * RivestShamirAdleman algorithm
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月20日
 * @since
 */
public final class RsaEncryptor extends AsymmetricEncryptor<RsaEncryptor> {

	static {
		AsymeetricRegistrarFactory.register(new RsaEncryptor());
	}

	@Override
	protected String getAlgorithmPrimary() {
		return "RSA";
	}

	@Override
	protected int getKeyBit() {
		return 1024;
	}

	@Override
	protected Class<? extends KeySpec> getPublicKeySpecClass() {
		return RSAPublicKeySpec.class;
	}

	@Override
	public Class<? extends KeySpec> getPrivateKeySpecClass() {
		return RSAPrivateCrtKeySpec.class;
	}

	@Override
	protected String getPadAlgorithm() {
		return "RSA/ECB/PKCS1Padding";
	}

	@Override
	protected KeySpecEntity newKeySpecEntity(String algorithm, KeySpec pubKeySpec, KeySpec keySpec) {
		return new RsaKeySpecEntity(algorithm, pubKeySpec, keySpec);
	}

}
