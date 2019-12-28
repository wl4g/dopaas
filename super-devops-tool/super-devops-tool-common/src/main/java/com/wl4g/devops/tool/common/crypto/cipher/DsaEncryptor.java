package com.wl4g.devops.tool.common.crypto.cipher;

import java.security.spec.KeySpec;

public final class DsaEncryptor extends AsymmetricEncryptor<KeySpecEntity> {

	@Override
	protected String getAlgorithmPrimary() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected String getPadAlgorithm() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected int getKeyBit() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Class<? extends KeySpec> getPublicKeySpecClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Class<? extends KeySpec> getPrivateKeySpecClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected KeySpecEntity newKeySpecEntity(String algorithm, KeySpec pubKeySpec, KeySpec keySpec) {
		throw new UnsupportedOperationException();
	}

}
