package com.wl4g.devops.coss.common.auth;

/**
 * Abstract credentials provider that maintains only one user credentials. Users
 * can switch to other valid credentials with
 * {@link OSS#switchCredentials(com.aliyun.oss.common.auth.Credentials)} Note
 * that <b>implementations of this interface must be thread-safe.</b>
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月1日
 * @since
 */
public interface CredentialsProvider {

	/**
	 * Gets credentials.
	 * 
	 * @return
	 */
	public Credentials getCredentials();

}
