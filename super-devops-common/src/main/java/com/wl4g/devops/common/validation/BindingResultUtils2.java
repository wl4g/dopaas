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
package com.wl4g.devops.common.validation;

import org.springframework.validation.BindingResult;
import org.springframework.validation.BindingResultUtils;
import org.springframework.validation.FieldError;

/**
 * BindingResult tool
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月28日
 * @since
 */
public abstract class BindingResultUtils2 extends BindingResultUtils {

	/**
	 * BindResult field errors to string.<br/>
	 * EG:<br/>
	 * <code>
	 * &#64;GetMapping("/user/modify")<br/>
	 * public RespBase&lt;User&gt; modifyUser(@Validated User user, BindingResult bind) {<br/>
	 * 	&nbsp;&nbsp;RespBase&lt;User&gt; resp = new RespBase<>();<br/>
		&nbsp;&nbsp;if (bind.hasErrors()) {<br/>
			&nbsp;&nbsp;&nbsp;&nbsp;resp.setCode(RetCode.param_err);<br/>
			&nbsp;&nbsp;&nbsp;&nbsp;resp.setMessage(BindingResultUtils.toBindErrString(bind));<br/>
			&nbsp;&nbsp;&nbsp;&nbsp;return resp;<br/>
		&nbsp;&nbsp;}<br/>
		&nbsp;&nbsp;// Other logical processing...
	 * <br/>}
	 * </code>
	 * 
	 * @param bind
	 * @return
	 */
	public static String toBindErrString(BindingResult bind) {
		StringBuffer errs = new StringBuffer();
		bind.getAllErrors().forEach(err -> {
			FieldError fErr = (FieldError) err;
			errs.append(fErr.getObjectName()).append(".").append(fErr.getField());
			errs.append(":").append(fErr.getDefaultMessage());
		});
		return errs.toString();
	}

}