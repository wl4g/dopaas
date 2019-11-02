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

import java.util.Map;
import java.util.Properties;
import static java.util.Locale.*;
import static java.util.Objects.isNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.*;
import static org.springframework.util.ReflectionUtils.rethrowRuntimeException;

import static com.google.common.base.Charsets.UTF_8;

import static com.wl4g.devops.common.constants.DevOpsConstants.PARAM_STACK_TRACE;
import static com.wl4g.devops.common.utils.web.WebUtils2.write;
import static com.wl4g.devops.common.utils.web.WebUtils2.writeJson;
import static com.wl4g.devops.common.utils.web.WebUtils2.ResponseType.*;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.*;
import com.wl4g.devops.common.annotation.DevopsErrorController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;

import freemarker.template.Template;

/**
 * Smart global error controller.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月10日
 * @since
 */
@DevopsErrorController
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class SmartGlobalErrorController extends AbstractErrorController implements InitializingBean {
	final private static String DEFAULT_DIR_VIEW = "/default-error-view/";
	final private static String DEFAULT_PATH_ERROR = "/error";
	final private static Logger log = LoggerFactory.getLogger(SmartGlobalErrorController.class);

	/** Errors configuration adapter. */
	@Autowired
	private CompositeErrorConfiguringAdapter adapter;

	@Value("${spring.cloud.devops.error.enabled:true}")
	private boolean enabled;
	@Value("${spring.cloud.devops.error.base-path:" + DEFAULT_DIR_VIEW + "}")
	private String basePath;
	@Value("${spring.cloud.devops.error.404:404.html}")
	private String tpl404Name;
	@Value("${spring.cloud.devops.error.403:403.html}")
	private String tpl403Name;
	@Value("${spring.cloud.devops.error.500:500.html}")
	private String tpl500Name;

	private Template tpl404;
	private Template tpl403;
	private Template tpl50x;

	public SmartGlobalErrorController(ErrorAttributes errorAttributes) {
		super(errorAttributes);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!enabled) {
			log.warn("Disabled global error handler");
			return;
		}
		if (log.isInfoEnabled()) {
			log.info("Enabled global error handler ...");
		}

		FreeMarkerConfigurer config = new FreeMarkerConfigurer();
		config.setTemplateLoaderPath(basePath);
		Properties settings = new Properties();
		settings.setProperty("template_update_delay", "0");
		settings.setProperty("default_encoding", "UTF-8");
		settings.setProperty("number_format", "0.####");
		settings.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
		settings.setProperty("classic_compatible", "true");
		settings.setProperty("template_exception_handler", "ignore");
		config.setFreemarkerSettings(settings);
		try {
			config.afterPropertiesSet();
			this.tpl404 = config.getConfiguration().getTemplate(tpl404Name, "UTF-8");
			this.tpl403 = config.getConfiguration().getTemplate(tpl403Name, "UTF-8");
			this.tpl50x = config.getConfiguration().getTemplate(tpl500Name, "UTF-8");
			Assert.notNull(tpl404, "Default 404 view template must not be null");
			Assert.notNull(tpl403, "Default 403 view template must not be null");
			Assert.notNull(tpl50x, "Default 500 view template must not be null");
		} catch (Exception e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
	}

	/**
	 * Returns the path of the error page.
	 *
	 * @return the error path
	 */
	@Override
	public String getErrorPath() {
		return DEFAULT_PATH_ERROR;
	}

	/**
	 * Any exception error handle.
	 * 
	 * @param request
	 * @param response
	 * @param ex
	 * @return
	 */
	@RequestMapping(DEFAULT_PATH_ERROR)
	@ExceptionHandler({ Exception.class/* ,Throwable.class */ })
	public void doAnyHandleError(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		try {
			Map<String, Object> model = getErrorAttributes(request);
			if (log.isErrorEnabled()) {
				log.error("=> Global error handling - {}", model);
			}

			// Obtain custom extension response status.
			HttpStatus status = adapter.getStatus(request, response, model, ex);
			String errmsg = adapter.getRootCause(request, response, model, ex);
			// Fallback.
			status = !isNull(status) ? status : SERVICE_UNAVAILABLE;
			errmsg = !isBlank(errmsg) ? errmsg : "Unknown error";

			/*
			 * If and only if the client is a browser and not an XHR request
			 * returns to the page, otherwise it returns to JSON
			 */
			if (isJSONResponse(request)) {
				writeJson(response, toJSONString(new RespBase<>(RetCode.create(status.value(), errmsg))));
			} else {
				String renderString = renderErrorPage(model, request, response, ex);
				write(response, status.value(), TEXT_HTML_VALUE, renderString.getBytes(UTF_8));
			}
		} catch (Throwable th) {
			log.error("\n=======>>> Basic global errors handling failure!\n", th);
		}
	}

	/**
	 * Whether error stack information is enabled
	 * 
	 * @param request
	 * @return
	 */
	private boolean isStackTrace(HttpServletRequest request) {
		if (log.isDebugEnabled()) {
			return true;
		}

		String parameter = request.getParameter(PARAM_STACK_TRACE);
		if (parameter == null) {
			return false;
		}
		return Boolean.valueOf(parameter.toLowerCase(ENGLISH));
	}

	/**
	 * Extract error details model
	 * 
	 * @param request
	 * @return
	 */
	private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
		return super.getErrorAttributes(request, isStackTrace(request));
	}

	/**
	 * Render errors page
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	private String renderErrorPage(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response,
			Exception ex) {
		// Replace the exception message that appears to be meaningful.
		model.put("message", adapter.getRootCause(request, response, model, ex));

		Template tpl = this.tpl50x;
		switch (getStatus(request)) {
		case NOT_FOUND:
			tpl = tpl404;
			break;
		case FORBIDDEN:
			tpl = tpl403;
			break;
		default:
			tpl = tpl50x;
		}

		// Readering
		String renderString = null;
		try {
			renderString = processTemplateIntoString(tpl, model);
		} catch (Exception e) {
			rethrowRuntimeException(e);
		}
		return renderString;
	}

}