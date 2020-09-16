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
package com.wl4g.devops.dts.codegen.utils;

import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link FreemarkerUtils2Tests}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-11
 * @since
 */
public class FreemarkerUtils2Tests {

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

		/*String renderString = FreemarkerUtils2.gen("entity.ftl", beanMap);
		System.out.println(renderString);*/
	}

}
