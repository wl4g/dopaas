package com.wl4g.devops.common.web;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import static com.wl4g.devops.common.constants.DevOpsConstants.PARAM_STACK_TRACE;

import com.google.common.base.Charsets;
import com.wl4g.devops.common.annotation.DevOpsErrorController;
import com.wl4g.devops.common.config.AbstractOptionalControllerConfiguration;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.common.utils.web.WebUtils2.ResponseType;
import com.wl4g.devops.common.web.RespBase.RetCode;

import freemarker.template.Template;

/**
 * Smart global error controller
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月10日
 * @since
 */
@DevOpsErrorController
public class SuperErrorsController extends AbstractErrorController implements InitializingBean {

	final private static String DEFAULT_DIR_VIEW = "/default-error-view/";
	final private static String DEFAULT_PATH_ERROR = "/error";

	final private Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${spring.cloud.devops.error.enabled:true}")
	private boolean enabled;
	@Value("${spring.cloud.devops.error.base-path:" + DEFAULT_DIR_VIEW + "}")
	private String basePath;
	@Value("${spring.cloud.devops.error.404:404.html}")
	private String ftl404Name;
	@Value("${spring.cloud.devops.error.500:500.html}")
	private String ftl500Name;

	private Template template404;
	private Template template500;

	public SuperErrorsController(ErrorAttributes errorAttributes) {
		super(errorAttributes);
	}

	/**
	 * Default template view initial
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (!enabled) {
			log.warn("Disabled global error handing.");
			return;
		}
		if (log.isInfoEnabled()) {
			log.info("Enabled global error handling ...");
		}

		FreeMarkerConfigurer config = new FreeMarkerConfigurer();
		config.setTemplateLoaderPath(this.basePath);
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
			this.template404 = config.getConfiguration().getTemplate(this.ftl404Name, "UTF-8");
			this.template500 = config.getConfiguration().getTemplate(this.ftl500Name, "UTF-8");
		} catch (Exception e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
		Assert.notNull(template404, "Default 404 view template must not be null");
		Assert.notNull(template500, "Default 500 view template must not be null");
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
	 * Execution handle global error
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = DEFAULT_PATH_ERROR)
	public void doHandleError(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String, Object> model = this.getErrorInformation(request);
			if (log.isErrorEnabled()) {
				log.error("=> Global error handling:{}", model);
			}

			/*
			 * If and only if the client is a browser and not an XHR request
			 * returns to the page, otherwise it returns to JSON
			 */
			if (ResponseType.isJSONResponse(ResponseType.auto, request)) {
				String errmsg = JacksonUtils.toJSONString(new RespBase<>(RetCode.SYS_ERR, (String) model.get("message"), null));
				WebUtils2.writeJson(response, errmsg);
			} else {
				WebUtils2.write(response, getStatus(request).value(), MediaType.TEXT_HTML_VALUE,
						reader(model, request).getBytes(Charsets.UTF_8));
			}
		} catch (IOException e) {
			log.error("\n===========>> Global unified error response failure <<===========\n", e);
		}
	}

	/**
	 * Get error HTTP status
	 * 
	 * @param request
	 * @return
	 */
	@Override
	protected HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		try {
			return HttpStatus.valueOf(statusCode);
		} catch (Exception ex) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

	/**
	 * Whether error stack information is enabled
	 * 
	 * @param request
	 * @return
	 */
	private boolean isStackTrace(HttpServletRequest request) {
		String parameter = request.getParameter(PARAM_STACK_TRACE);
		if (parameter == null) {
			return false;
		}
		return Boolean.valueOf(parameter.toLowerCase(Locale.ENGLISH));
	}

	/**
	 * Error information
	 * 
	 * @param request
	 * @return
	 */
	private Map<String, Object> getErrorInformation(HttpServletRequest request) {
		// Error information
		return Collections.unmodifiableMap(getErrorAttributes(request, isStackTrace(request)));
	}

	/**
	 * Reader text HTML
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	private String reader(Map<String, Object> model, HttpServletRequest request) {
		String renderedString = null;
		Template template = null;

		// Error HTTP status
		HttpStatus status = this.getStatus(request);
		switch (status) {
		case NOT_FOUND:
			template = this.template404;
			break;
		default:
			template = this.template500;
		}

		// Reader
		try {
			renderedString = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		} catch (Exception e) {
			ReflectionUtils.rethrowRuntimeException(e);
		}
		return renderedString;
	}

	/**
	 * Smart global error controller configuration
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年1月10日
	 * @since
	 */
	@Configuration
	@ConditionalOnProperty(value = "spring.cloud.devops.error.enabled", matchIfMissing = true)
	public static class SuperErrorsControllerConfiguration extends AbstractOptionalControllerConfiguration {

		@Bean
		public SuperErrorsController superErrorsController(ErrorAttributes errorAttributes) {
			return new SuperErrorsController(errorAttributes);
		}

		@Bean
		public PrefixHandlerMapping errorControllerPrefixHandlerMapping() {
			return super.createPrefixHandlerMapping();
		}

		@Override
		protected String getMappingPrefix() {
			return "/"; // Fixed to Spring-MVC default: /
		}

		@Override
		protected Class<? extends Annotation> annotationClass() {
			return DevOpsErrorController.class;
		}

	}

}
