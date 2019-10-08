package com.wl4g.devops.ci.anno;

import com.wl4g.devops.ci.constant.DeployTypeEnum;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface DeployType {
    DeployTypeEnum value();
}
