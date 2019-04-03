package com.wl4g.devops.iam.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.wl4g.devops.iam.config.DefaultViewConfiguration;
import com.wl4g.devops.iam.config.ExtraConfiguration;
import com.wl4g.devops.iam.config.IamConfiguration;
import com.wl4g.devops.iam.config.BasedContextConfiguration;
import com.wl4g.devops.iam.config.SnsConfiguration;
//import com.wl4g.devops.iam.config.WechatMpSnsConfiguration;

/**
 * Controls whether IAM servers are enabled
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月27日
 * @since
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Import({ BasedContextConfiguration.class, IamConfiguration.class, ExtraConfiguration.class, SnsConfiguration.class,
		DefaultViewConfiguration.class })
public @interface EnableIamServer {

}
