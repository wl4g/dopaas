package com.wl4g.devops.common.validation;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * BindingResult tool
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月28日
 * @since
 */
public abstract class BindingResultUtils extends org.springframework.validation.BindingResultUtils {

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
