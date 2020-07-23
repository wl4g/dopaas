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
package com.wl4g.devops.gateway.controller;

import com.wl4g.devops.components.tools.common.log.SmartLogger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

/**
 * {@link GatewayExampleController}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-07-20
 * @since
 */
@RestController
@RequestMapping("example")
public class GatewayExampleController {

	final protected SmartLogger log = getLogger(getClass());

	@RequestMapping("hello")
	public String hello(HttpServletRequest request) {
		String token = request.getHeader("token");
		log.info("helloworld... token: {}", token);

		StringBuffer responseContent = new StringBuffer(200);
		responseContent.append("</br></hr></br>");
		responseContent.append("----- Http Request Headers: -----</br>");

		// Request headers
		Enumeration<String> en = request.getHeaderNames();
		while (en.hasMoreElements()) {
			String headerName = en.nextElement();
			responseContent.append(headerName);
			responseContent.append(": ");
			responseContent.append(request.getHeader(headerName));
			responseContent.append("</br>");
		}
		responseContent.append("</br></hr></br>");

		// Request body parameters
		responseContent.append("----- Http Request Parameters: -----</br>");
		Map<String, String[]> parameterMap = request.getParameterMap();
		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String key = entry.getKey();
			String[] value = entry.getValue();
			StringBuilder valueStr = new StringBuilder();
			for (int i = 0; i < value.length; i++) {
				valueStr.append(value[i]);
				if (i < (value.length - 1)) {
					valueStr.append(", ");
				}
				responseContent.append(key);
				responseContent.append(": ");
				responseContent.append(valueStr);
			}
			log.info(key + ":" + valueStr);
		}

		// Request statistics
		responseContent.append("</br>RequestURL: ");
		responseContent.append(request.getRequestURL());
		responseContent.append("</br>LocalAddr: ");
		responseContent.append(request.getLocalAddr());
		responseContent.append("</br>LocalName: ");
		responseContent.append(request.getLocalName());
		responseContent.append("</br>LocalPort: ");
		responseContent.append(request.getLocalPort());
		responseContent.append("</br>RemoteAddr: ");
		responseContent.append(request.getRemoteAddr());
		responseContent.append("</br>RemoteHost: ");
		responseContent.append(request.getRemoteHost());
		responseContent.append("</br>Locale: ");
		responseContent.append(request.getLocale());

		return "Testing Successful!".concat(responseContent.toString());
	}

}