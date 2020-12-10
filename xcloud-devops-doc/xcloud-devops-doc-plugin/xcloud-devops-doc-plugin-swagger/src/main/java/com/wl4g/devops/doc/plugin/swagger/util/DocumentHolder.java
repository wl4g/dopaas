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
package com.wl4g.devops.doc.plugin.swagger.util;

import static com.wl4g.components.common.lang.StringUtils2.trimAllWhitespace;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link DocumentHolder}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-10
 * @sine v1.0
 * @see
 */
public class DocumentHolder {

	/**
	 * Maven mojo singleton configuration instance.
	 */
	private static final DocumentHolder holder = new DocumentHolder();

	@SuppressWarnings("serial")
	private List<String> scanBasePackages = new ArrayList<String>() {
		{
			add("com.wl4g");
		}
	};

	private DocumentHolder() {
	}

	public static DocumentHolder get() {
		return holder;
	}

	public List<String> getScanBasePackages() {
		return this.scanBasePackages;
	}

	public void setScanBasePackages(String scanBasePackages) {
		this.scanBasePackages = asList(split(trimAllWhitespace(trimToEmpty(scanBasePackages)), ","));
	}

	public String[] toSpringArgs(DocumentionProvider provider) {
		StringBuffer scans = new StringBuffer();
		scanBasePackages.forEach(s -> scans.append(s).append(","));

		String arg1 = "--" + SCAN_BASE_PACKAGES + "=" + scans;
		String arg2 = "--" + PROPERTY_SWAGGER2 + "=true";
		if (provider == DocumentionProvider.OAS3) {
			arg2 = "--" + PROPERTY_OAS3 + "=true";
		}

		return new String[] { arg1, arg2 };
	}

	public static final String SCAN_BASE_PACKAGES = "scanBasePackages";

	public static final String PROPERTY_SWAGGER2 = "springfox.enable.swagger2";
	public static final String PROPERTY_OAS3 = "springfox.enable.oas3";

	public static enum DocumentionProvider {
		SWAGGER2, OAS3
	}

}
