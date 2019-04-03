package com.wl4g.devops.iam.authc.credential.secure;

/**
 * IAM credentials securer
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月16日
 * @since
 * @see {@link org.apache.shiro.crypto.hash.DefaultHashService#combine()}
 */
public interface IamCredentialsSecurer {

	/**
	 * Encryption credentials
	 * 
	 * @param principal
	 *            principal
	 * @param credentials
	 *            External input credentials
	 * @return
	 */
	String signature(String principal, String credentials);

	/**
	 * Validation credentials
	 * 
	 * @param principal
	 *            principal
	 * @param credentials
	 *            External input credentials
	 * @param storedCredentials
	 *            Database storage credentials
	 * @return
	 */
	boolean validate(String principal, String credentials, String storedCredentials);

	/**
	 * Apply asymmetric algorithm secret public key
	 * 
	 * @param principal
	 * @return
	 */
	String applySecretKey(String principal);

}
