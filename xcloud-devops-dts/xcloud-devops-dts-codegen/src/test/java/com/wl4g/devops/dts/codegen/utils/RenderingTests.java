/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.dts.codegen.utils;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import static java.util.Collections.singletonList;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wl4g.components.common.view.Freemarkers;
import com.wl4g.components.core.utils.expression.SpelExpressions;

/**
 * {@link RenderingTests}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-11
 * @since
 */
public class RenderingTests {

	static String tplContent = "Freemarker rendering for entityName: ${entityName}, SPEL rendering for entityName: #{entityName}";

	public static void main(String[] args) throws Exception {
		String result = tplContent;

		//
		// Note: After testing, we found that we should use spel to render
		// first, otherwise FreeMarker will report an error.
		//

		// Rendering with SPEL
		result = SpelExpressions.create().resolve(result, testModel);
		System.out.println("----- SPEL rendering result: -----\n" + result);

		// Rendering with freemarker
		Template template = new Template("tpl1", new StringReader(result), configurer);
		result = Freemarkers.renderingTemplateToString(template, testModel);
		System.out.println("-----Freemarker rendering result: -----\n" + result);

	}

	static Map<String, Object> testModel;
	static Configuration configurer = Freemarkers.createDefault().withVersion(Configuration.VERSION_2_3_27)
			.withTemplateLoaders(singletonList(new StringTemplateLoader())).build();

	static {
		testModel = new HashMap<>();
		testModel.put("entityName", "User");
		List<Map<String, String>> attrs = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			Map<String, String> attr = new HashMap<>();
			attr.put("fieldNote", "fieldNote" + i);
			attr.put("fieldType", "String");
			attr.put("fieldName", "fieldName" + i);
			attrs.add(attr);
		}
		testModel.put("attributes", attrs);
	}

}