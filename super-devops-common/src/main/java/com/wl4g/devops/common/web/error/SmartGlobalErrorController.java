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

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import static java.util.Locale.*;
import static java.util.Objects.nonNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.*;
import static org.springframework.util.Assert.notNull;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.springframework.http.MediaType.*;

import static com.google.common.base.Charsets.UTF_8;

import static com.wl4g.devops.common.constants.DevOpsConstants.PARAM_STACK_TRACE;
import static com.wl4g.devops.tool.common.lang.Exceptions.getStackTraceAsString;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.*;
import static com.wl4g.devops.tool.common.web.WebUtils2.isTrue;
import static com.wl4g.devops.tool.common.web.WebUtils2.write;
import static com.wl4g.devops.tool.common.web.WebUtils2.writeJson;
import static com.wl4g.devops.tool.common.web.WebUtils2.ResponseType.*;

import com.wl4g.devops.common.annotation.DevopsErrorController;
import com.wl4g.devops.common.config.ErrorControllerAutoConfiguration.ErrorControllerProperties;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

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
	final private static String DEFAULT_PATH_ERROR = "/error";
	final private static String DEFAULT_REDIRECT_PREFIX = "redirect:";
	final private static String DEFAULT_REDIRECT_KEY = "redirectUrl";

	final private Logger log = LoggerFactory.getLogger(getClass());

	/** Errors configuration properties. */
	final private ErrorControllerProperties config;
	/** Errors configuration adapter. */
	final private CompositeErrorConfiguringAdapter adapter;

	private Template tpl404;
	private Template tpl403;
	private Template tpl50x;

	public SmartGlobalErrorController(ErrorAttributes errorAttributes, ErrorControllerProperties config,
			CompositeErrorConfiguringAdapter adapter) {
		super(errorAttributes);
		notNull(config, "ErrorControllerProperties must not be null.");
		notNull(adapter, "CompositeErrorConfiguringAdapter must not be null.");
		this.config = config;
		this.adapter = adapter;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (log.isInfoEnabled()) {
			log.info("Initializing smart global error controller ...");
		}

		try {
			FreeMarkerConfigurer fmcr = new FreeMarkerConfigurer();
			fmcr.setTemplateLoaderPath(config.getBasePath());
			Properties settings = new Properties();
			settings.setProperty("template_update_delay", "0");
			settings.setProperty("default_encoding", "UTF-8");
			settings.setProperty("number_format", "0.####");
			settings.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
			settings.setProperty("classic_compatible", "true");
			settings.setProperty("template_exception_handler", "ignore");
			fmcr.setFreemarkerSettings(settings);
			fmcr.afterPropertiesSet();

			// Initial errors template.
			Configuration fmc = fmcr.getConfiguration();
			if (!isErrorRedirectURI(config.getNotFountUriOrTpl())) {
				this.tpl404 = fmc.getTemplate(config.getNotFountUriOrTpl(), UTF_8.name());
				notNull(tpl404, "Default 404 view template must not be null");
			}
			if (!isErrorRedirectURI(config.getUnauthorizedUriOrTpl())) {
				this.tpl403 = fmc.getTemplate(config.getUnauthorizedUriOrTpl(), UTF_8.name());
				notNull(tpl403, "Default 403 view template must not be null");
			}
			if (!isErrorRedirectURI(config.getErrorUriOrTpl())) {
				this.tpl50x = fmc.getTemplate(config.getErrorUriOrTpl(), UTF_8.name());
				notNull(tpl50x, "Default 500 view template must not be null");
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
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
			// Obtain errors attributes.
			Map<String, Object> model = getErrorAttributes(request, response, ex);

			// Obtain custom extension response status.
			int status = adapter.getStatus(request, response, model, ex);
			String errmsg = adapter.getRootCause(request, response, model, ex);

			// Get redirectUri or rendering template.
			Object uriOrTpl = getRedirectUriOrRenderErrorView(model, status);

			// If and only if the client is a browser and not an XHR request
			// returns to the page, otherwise it returns to JSON.
			if (isJSONResp(request)) {
				RespBase<Object> resp = new RespBase<>(RetCode.newCode(status, errmsg));
				if (!(uriOrTpl instanceof Template)) {
					resp.forMap().put(DEFAULT_REDIRECT_KEY, uriOrTpl);
				}
				String errJson = toJSONString(resp);
				log.error("Resp err => {}", errJson);
				writeJson(response, errJson);
			}
			// Rendering errors view
			else {
				if (uriOrTpl instanceof Template) {
					log.error("Redirect err view => http({})", status);
					// Merge configuration to model.
					model.putAll(config.asMap());
					// Rendering
					String renderString = processTemplateIntoString((Template) uriOrTpl, model);
					write(response, status, TEXT_HTML_VALUE, renderString.getBytes(UTF_8));
				} else {
					log.error("Redirect err view => [{}]", uriOrTpl);
					response.sendRedirect((String) uriOrTpl);
				}
			}
		} catch (Throwable th) {
			log.error("Failed to global errors for origin causes: \n{} at causes:\n{}", getStackTraceAsString(ex),
					getStackTraceAsString(th));
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
		return isTrue(parameter.toLowerCase(ENGLISH), false);
	}

	/**
	 * Extract error details model
	 * 
	 * @param request
	 * @return
	 */
	private Map<String, Object> getErrorAttributes(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		boolean _stacktrace = isStackTrace(request);
		Map<String, Object> model = super.getErrorAttributes(request, _stacktrace);
		if (_stacktrace) {
			log.error("Origin Errors - {}", model);
		}

		// Replace the exception message that appears to be meaningful.
		model.put("message", adapter.getRootCause(request, response, model, ex));
		return model;
	}

	/**
	 * Get redirectUri rendering errors page view.
	 * 
	 * @param model
	 * @param status
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 */
	private Object getRedirectUriOrRenderErrorView(Map<String, Object> model, int status) throws IOException, TemplateException {
		switch (status) {
		case 404:
			if (nonNull(tpl404)) {
				return tpl404;
			}
			return config.getNotFountUriOrTpl().substring(DEFAULT_REDIRECT_PREFIX.length());
		case 403:
			if (nonNull(tpl403)) {
				return tpl403;
			}
			return config.getUnauthorizedUriOrTpl().substring(DEFAULT_REDIRECT_PREFIX.length());
		default:
			if (nonNull(tpl50x)) {
				return tpl50x;
			}
			return config.getErrorUriOrTpl().substring(DEFAULT_REDIRECT_PREFIX.length());
		}
	}

	/**
	 * Is redirection error URI.
	 * 
	 * @param uriOrTpl
	 * @return
	 */
	private boolean isErrorRedirectURI(String uriOrTpl) {
		return startsWithIgnoreCase(uriOrTpl, DEFAULT_REDIRECT_PREFIX);
	}

}