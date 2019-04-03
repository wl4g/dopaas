package org.springframework.social.wechat.api;

import org.springframework.social.ApiBinding;
import org.springframework.web.client.RestOperations;

/**
 * spring-social-wechat
 * 
 * @author <a href="mailto:larry7696@gmail.com">Larry</a>
 * @version 18.6.27
 */
public interface Wechat extends ApiBinding {

	UserOperations userOperations();

	RestOperations restOperations();

}
