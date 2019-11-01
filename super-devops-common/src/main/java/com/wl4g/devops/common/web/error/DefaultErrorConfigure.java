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
package com.wl4g.devops.common.web.error;

import static com.wl4g.devops.common.web.RespBase.RetCode.SYS_ERR;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;

/**
 * Default error configure.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月1日
 * @since
 */
public class DefaultErrorConfigure implements ErrorConfigure {

	@Override
	public HttpStatus getStatus(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model,
			Exception ex) {
		Integer statusCode = (Integer) model.getOrDefault("status", SYS_ERR.getErrcode());

		if (isNull(statusCode)) {
			RetCode retCode = RespBase.getRestfulCode(ex);
			if (nonNull(retCode)) {
				statusCode = retCode.getErrcode();
			} else {
				statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
			}
		}

		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		try {
			return HttpStatus.valueOf(statusCode);
		} catch (Exception e) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

	@Override
	public String getCause(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model, Exception ex) {
		return extractMeaningfulErrorsMessage(model);
	}

	/**
	 * Extract meaningful errors messages.
	 * 
	 * @param model
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String extractMeaningfulErrorsMessage(Map<String, Object> model) {
		StringBuffer errmsg = new StringBuffer();
		Object message = model.get("message");
		if (message != null) {
			errmsg.append(message);
		}

		Object errors = model.get("errors"); // @NotNull?
		if (errors != null) {
			errmsg.setLength(0); // Print only errors information
			if (errors instanceof Collection) {
				// Used to remove duplication
				List<String> fieldErrs = new ArrayList<>(8);

				Collection<Object> _errors = (Collection) errors;
				Iterator<Object> it = _errors.iterator();
				while (it.hasNext()) {
					Object err = it.next();
					if (err instanceof FieldError) {
						FieldError ferr = (FieldError) err;
						/*
						 * Remove duplicate field validation errors,
						 * e.g. @NotNull and @NotEmpty
						 */
						String fieldErr = ferr.getField();
						if (!fieldErrs.contains(fieldErr)) {
							errmsg.append("'");
							errmsg.append(fieldErr);
							errmsg.append("'");
							errmsg.append(ferr.getDefaultMessage());
							errmsg.append(", ");
						}
						fieldErrs.add(fieldErr);
					} else {
						errmsg.append(err.toString());
						errmsg.append(", ");
					}
				}
			} else {
				errmsg.append(errors.toString());
			}
		}

		return errmsg.toString();
	}

}
