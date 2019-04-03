package com.wl4g.devops.iam.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wl4g.devops.iam.client.authc.aop.SecondAuthenticateHandler;

/**
 * Safety reinforcement for inspection of secondary certification.
 * 
 * @author wangl.sir
 * @version v1.0 2019年2月28日
 * @since
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface SecondAuthenticate {

	/**
	 * Function ID, not duplicated
	 * 
	 * @return
	 */
	String funcId();

	/**
	 * Handler class
	 * 
	 * @return
	 */
	Class<? extends SecondAuthenticateHandler> handleClass();

}
