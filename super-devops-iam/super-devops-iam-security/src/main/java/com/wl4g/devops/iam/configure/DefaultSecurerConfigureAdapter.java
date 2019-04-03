package com.wl4g.devops.iam.configure;

public class DefaultSecurerConfigureAdapter implements SecurerConfigureAdapter {

	@Override
	public SecurerConfig configure() {
		return new SecurerConfig(new String[] { "MD5", "SHA-256", "SHA-384", "SHA-512" }, "IAM", 5, 2 * 60 * 60 * 1000L,
				3 * 60 * 1000L);
	}

}
