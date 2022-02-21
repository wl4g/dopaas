/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.dopaas.udm.plugin.swagger.springfox.plugin;

import java.lang.annotation.*;

import org.springframework.stereotype.Indexed;

/**
 * Swagger enhanced extension documentions configuration annotation.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-14
 * @sine v1.0
 * @see
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface ApiOperationExtension {

	/***
	 * Sort Fields
	 * 
	 * @return 排序
	 */
	int order() default 0;

	/***
	 * author
	 * 
	 * @return 开发者
	 */
	String author() default "";

	/**
	 * 请求忽略参数数组
	 * 
	 * @since 1.9.5
	 *        <ul>
	 *        <li>例如新增接口时,某实体类不需要显示Id,即可使用该属性对参数进行忽略.ignoreParameters={"id"}</li>
	 *        <li>如果存在多个层次的参数过滤,则使用名称.属性的方式,例如
	 *        ignoreParameters={"uptModel.id","uptModel.uptPo.id"},其中uptModel是实体对象参数名称,id为其属性,uptPo为实体类,作为uptModel类的属性名称</li>
	 *        <li>如果参数层级只是一级的情况下,并且参数是实体类的情况下,不需要设置参数名称,直接给定属性值名称即可.</li>
	 *        </ul>
	 *        例如一级参数情况下： <code>
	 *     {@code @ApiOperationSupport(ignoreParameters={"id"}) }
	 *     public void api(UptModel uptModel){
	 *
	 *     }
	 * </code>
	 * @return 过滤参数数组
	 */
	String[] ignoreParameters() default {};

}
