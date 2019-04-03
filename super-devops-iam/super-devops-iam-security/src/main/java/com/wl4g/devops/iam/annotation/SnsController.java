package com.wl4g.devops.iam.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enabled SNS oauth2 controller configuration.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年9月17日
 * @since
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface SnsController {

}
