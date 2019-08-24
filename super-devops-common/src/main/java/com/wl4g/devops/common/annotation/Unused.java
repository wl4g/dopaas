package com.wl4g.devops.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods, classes, parameters, etc., annotated by @Unused, indicate code
 * snippets that are not currently used but are likely to be used in the future
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月4日
 * @since
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
@Documented
public @interface Unused {
}