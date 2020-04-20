/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.iam.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.wl4g.devops.iam.config.IamServerApiV1AutoConfiguration;
import com.wl4g.devops.iam.config.JssdkWebappsAutoConfiguration;
import com.wl4g.devops.iam.config.LoginAutoConfiguration;
import com.wl4g.devops.iam.config.SimpleRcmAutoConfiguration;
import com.wl4g.devops.iam.config.IamAutoConfiguration;
import com.wl4g.devops.iam.config.BasedConfigAutoConfiguration;
import com.wl4g.devops.iam.config.SnsAutoConfiguration;
import com.wl4g.devops.iam.config.VerifyAutoConfiguration;

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
@Import({ BasedConfigAutoConfiguration.class, IamAutoConfiguration.class, LoginAutoConfiguration.class,
		VerifyAutoConfiguration.class, SimpleRcmAutoConfiguration.class, SnsAutoConfiguration.class,
		IamServerApiV1AutoConfiguration.class, JssdkWebappsAutoConfiguration.class })
public @interface EnableIamServer {

}