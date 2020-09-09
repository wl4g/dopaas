package com.wl4g.devops.dts.codegen.utils;

import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.log.SmartLoggerFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.util.*;

import static com.google.common.base.Charsets.UTF_8;
import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

/**
 * @author vjay
 * @date 2020-09-07 10:57:00
 */
public class CodeGenUtils {

	private static SmartLogger log = SmartLoggerFactory.getLogger(CodeGenUtils.class);

	private final static Configuration fmc;

	private final static String ftlBasePath = "/ftl/";

	static {
		FreeMarkerConfigurer fmcr = new FreeMarkerConfigurer();
		fmcr.setTemplateLoaderPath(ftlBasePath);
		Properties settings = new Properties();
		settings.setProperty("template_update_delay", "0");
		settings.setProperty("default_encoding", "UTF-8");
		settings.setProperty("number_format", "0.####");
		settings.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
		settings.setProperty("classic_compatible", "true");
		settings.setProperty("template_exception_handler", "ignore");
		fmcr.setFreemarkerSettings(settings);
		try {
			fmcr.afterPropertiesSet();
		} catch (IOException | TemplateException e) {
			log.error("create FreeMarkerConfigurer fail", e);
		}
		fmc = fmcr.getConfiguration();
	}

	public static String gen(String templatePath, Map<String, Object> model) throws IOException, TemplateException {
		Template template = fmc.getTemplate(templatePath, UTF_8.name());
		return processTemplateIntoString(template, model);
	}

	public static void main(String[] args) throws IOException, TemplateException {

		Map<String, Object> beanMap = new HashMap<String, Object>();
		beanMap.put("beanName", "User");// 实体类名
		beanMap.put("interfaceName", "User");// 接口名
		List<Map<String, String>> paramsList = new ArrayList<Map<String, String>>();
		for (int i = 0; i < 4; i++) {
			Map<String, String> tmpParamMap = new HashMap<String, String>();
			tmpParamMap.put("fieldNote", "fieldNote" + i);
			tmpParamMap.put("fieldType", "String");
			tmpParamMap.put("fieldName", "fieldName" + i);
			paramsList.add(tmpParamMap);
		}
		beanMap.put("params", paramsList);

		String renderString = gen("entity.ftl", beanMap);
		System.out.println(renderString);
	}

}
