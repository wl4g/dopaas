package com.wl4g.devops.iam.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link com.wl4g.devops.iam.common.config.AbstractIamConfiguration#shiroFilter}
 * {@link com.wl4g.devops.iam.common.core.IamPathMatchingFilterChainResolver#getChain}
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月3日
 * @since
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface IamFilter {

}
