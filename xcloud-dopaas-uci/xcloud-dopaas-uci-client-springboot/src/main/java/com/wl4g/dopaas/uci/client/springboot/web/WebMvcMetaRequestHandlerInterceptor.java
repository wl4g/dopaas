/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.dopaas.uci.client.springboot.web;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.component.common.lang.Assert2.isTrue;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.component.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.component.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.commons.lang3.StringUtils.endsWithAny;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wl4g.component.common.codec.Encodes;
import com.wl4g.component.common.io.FileIOUtils;
import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.dopaas.common.bean.uci.model.BuildMetaInfo;
import com.wl4g.dopaas.common.bean.uci.model.BuildMetaInfo.SourceInfo;
import com.wl4g.dopaas.common.constant.UciConstants;
import com.wl4g.dopaas.uci.client.springboot.config.UciClientProperties;

/**
 * {@link WebMvcMetaRequestHandlerInterceptor}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-04-20
 * @sine v1.0
 * @see
 */
public class WebMvcMetaRequestHandlerInterceptor implements HandlerInterceptor {

	protected final SmartLogger log = getLogger(getClass());

	@Autowired
	private UciClientProperties config;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		BuildMetaInfo meta = getBuildMetaInfo();
		if (nonNull(meta)) {
			SourceInfo source = meta.getSourceInfo();
			String value = format("%s:%s", source.getCommitId(), encodeBase64String(source.getComment().getBytes(UTF_8)));
			// To response header.
			response.setHeader(config.getMetaInfoHeaderName(), value);
		}
	}

	/**
	 * Gets UCI build meta information.
	 * 
	 * @return
	 */
	private BuildMetaInfo getBuildMetaInfo() {
		if (nonNull(cacheMetaInfo)) {
			return cacheMetaInfo;
		}
		try {
			if (nonNull(appHomePath)) {
				String metaPath = format("%s%s%s", appHomePath, File.separator, UciConstants.DEFAULT_META_NAME);
				String metaContent = FileIOUtils.readFileToString(new File(metaPath), UTF_8);
				cacheMetaInfo = parseJSON(metaContent, BuildMetaInfo.class);
				log.info("Reading build meta info: {}", toJSONString(cacheMetaInfo));
			}
		} catch (Exception e) {
			log.warn("Unable read UCI build meta file.", e);
		}
		return cacheMetaInfo;
	}

	public static String getAppHomePath() {
		String thisClassPath = ClassUtils.getDefaultClassLoader().getResource("").getPath();
		thisClassPath = trimToEmpty(thisClassPath).replaceAll("\\\\", "/");
		thisClassPath = Encodes.urlDecode(thisClassPath); // Solving-chinese-problems

		if (thisClassPath.contains("/BOOT-INF/classes")) { // SpringBoot jar
			// e.g:/opt/apps/acm/portal-master-bin/portal-master-bin.jar!/BOOT-INF/classes!/
			int index = thisClassPath.indexOf(DEFAULT_SPRING_BOOT_INF_CLASSES);
			isTrue(index > 0, "Unkown spring boot jar class path. %s", thisClassPath);
			String springbootJarPath = thisClassPath.substring(0, index);
			return springbootJarPath.substring(0, springbootJarPath.lastIndexOf("/"));
		}

		// e.g:/home/myuser/safecloud-web-portal/portal-rest/target/classes/
		if (thisClassPath.endsWith("/target/classes")) { // Local IDE
			return null;
		}

		// e.g:/opt/apps/acm/portal-package/portal-master-bin/lib/
		if (endsWithAny(thisClassPath, "/lib", "/libs", "/ext-lib", "/ext-libs")) {
			return thisClassPath.substring(0, thisClassPath.lastIndexOf("/"));
		}

		return null;
	}

	private static BuildMetaInfo cacheMetaInfo;
	private static final String appHomePath = getAppHomePath();
	private static final String DEFAULT_SPRING_BOOT_INF_CLASSES = "!/BOOT-INF/classes!";

}