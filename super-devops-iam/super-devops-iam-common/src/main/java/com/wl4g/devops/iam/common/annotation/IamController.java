package com.wl4g.devops.iam.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Wrapper annotation to enable DevOps IAM controller configuration. Note that
 * on the Controller class using the {@link IamController @IamController}
 * annotation, there is no need to use {@link RequestMapping @RequestMapping}
 * again, otherwise spring-MVC will add two mappings.
 * 
 * @since 1.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface IamController {

}
