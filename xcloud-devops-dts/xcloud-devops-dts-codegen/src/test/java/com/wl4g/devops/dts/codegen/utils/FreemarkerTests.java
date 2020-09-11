package com.wl4g.devops.dts.codegen.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.*;

import static com.google.common.base.Charsets.UTF_8;
import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

/**
 * 
 * {@link FreemarkerTests}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @version v1.0 2020-09-07
 * @since
 */
public class FreemarkerTests {

	public static void main(String[] args) throws Exception {
		FreeMarkerConfigurer fmcr = new FreeMarkerConfigurer();
		String basePath = "/ftl/";

		fmcr.setTemplateLoaderPath(basePath);
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

		Template template = fmc.getTemplate("entity.ftl", UTF_8.name());

		// Map<String, Object> model = new HashMap<>();
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

		String renderString = processTemplateIntoString(template, beanMap);
		System.out.println(renderString);

	}

}
