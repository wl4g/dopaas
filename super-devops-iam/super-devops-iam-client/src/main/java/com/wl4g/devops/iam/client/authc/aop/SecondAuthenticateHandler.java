package com.wl4g.devops.iam.client.authc.aop;

/**
 * Secondary authentication handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年3月9日
 * @since
 */
public interface SecondAuthenticateHandler {

	String[] doGetAuthorizers(String funcId);

}
