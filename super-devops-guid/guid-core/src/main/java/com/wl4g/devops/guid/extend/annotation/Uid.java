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
package com.wl4g.devops.guid.extend.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @类名称 Uid.java
 * @类描述
 * 
 *      <pre>
 *      uid生成注解
 *      </pre>
 * 
 * @作者 庄梦蝶殇 linhuaichuan1989@126.com
 * @创建时间 2018年8月31日 下午4:18:51
 * @版本 1.00
 *
 * @修改记录
 * 
 *       <pre>
 *     版本                       修改人 		修改日期 		 修改内容描述
 *     ----------------------------------------------
 *     1.00 	庄梦蝶殇 	2018年8月31日             
 *     ----------------------------------------------
 *       </pre>
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Uid {
	/**
	 * uid 生成模式
	 * 
	 * @方法名称 model
	 * @功能描述
	 * 
	 *       <pre>
	 * uid 生成模式
	 *       </pre>
	 */
	UidModel model() default UidModel.step;

	/**
	 * 前缀
	 * 
	 * @方法名称 prefix
	 * @功能描述
	 * 
	 *       <pre>
	 *       前缀
	 *       </pre>
	 */
	String prefix() default "";

	/**
	 * 是否数字类型
	 * 
	 * @方法名称 isNum
	 * @功能描述
	 * 
	 *       <pre>
	 *       是否数字类型
	 *       </pre>
	 */
	boolean isNum() default true;

}