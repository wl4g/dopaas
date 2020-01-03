package com.wl4g.devops.ci.pmplatform.anno;

import com.wl4g.devops.ci.pmplatform.constant.PlatformEnum;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface PmPlatform {
    PlatformEnum value();
}
