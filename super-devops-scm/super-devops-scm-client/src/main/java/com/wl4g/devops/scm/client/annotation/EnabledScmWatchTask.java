package com.wl4g.devops.scm.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Wrapper annotation to enable DevOps watcher task configuration.
 *
 * @since 1.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@ConditionalOnProperty(value = "spring.cloud.devops.scm.client.watcher.enabled", matchIfMissing = false)
public @interface EnabledScmWatchTask {

}
