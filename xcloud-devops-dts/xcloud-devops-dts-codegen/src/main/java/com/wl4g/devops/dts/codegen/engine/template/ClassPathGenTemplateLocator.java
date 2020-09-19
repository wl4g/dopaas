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
package com.wl4g.devops.dts.codegen.engine.template;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.components.common.io.ByteStreamUtils.readFullyToString;
import static com.wl4g.components.common.jvm.JvmRuntimeKit.isJVMDebugging;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import org.apache.commons.codec.net.URLCodec;

import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.resource.StreamResource;
import com.wl4g.components.common.resource.resolver.ClassPathResourcePatternResolver;
import com.wl4g.devops.dts.codegen.engine.generator.AbstractGeneratorProvider;

import freemarker.template.Template;

/**
 * {@link ClassPathGenTemplateLocator}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-19
 * @sine v1.0.0
 * @see
 */
public class ClassPathGenTemplateLocator implements GenTemplateLocator {

	protected final SmartLogger log = getLogger(getClass());

	@Override
	public List<TemplateWrapper> locate(String provider) throws Exception {
		List<TemplateWrapper> tpls = templatesCache.get(provider);
		if (isJVMDebugging || isNull(tpls)) {
			synchronized (AbstractGeneratorProvider.class) {
				tpls = templatesCache.get(provider);
				if (isJVMDebugging || isNull(tpls)) {
					tpls = new ArrayList<>();

					// Scanning templates resources.
					String[] locations = getResourceLocations(provider).toArray(new String[0]);
					Set<StreamResource> resources = defaultResourceResolver.getResources(locations);

					log.info("Loaded templates resources: {}", resources);
					for (StreamResource res : resources) {
						if (res.isReadable()) {
							tpls.add(wrapTemplate(res, provider));
						}
					}
					templatesCache.put(provider, tpls);
				}
			}
		}
		return tpls;
	}

	@Override
	public boolean cleanAll() {
		templatesCache.clear();
		return true;
	}

	/**
	 * Gets scanning resources locations.
	 * 
	 * @param provider
	 * @return
	 */
	private static List<String> getResourceLocations(@NotBlank String provider) {
		hasTextOf(provider, "provider");

		List<String> locations = new ArrayList<>();
		for (String suffix : LOAD_SUFFIXS) {
			locations.add(format(LOAD_PATTERN, provider, suffix));
		}
		return locations;
	}

	/**
	 * Wrapper {@link Template}
	 *
	 * @param res
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	private static TemplateWrapper wrapTemplate(StreamResource res, String provider) throws Exception {
		/**
		 * e.g: res.getURI().toString()
		 * 
		 * <pre>
		 * jar:file:/opt/apps/acm/dts-manager-package/dts-manager-master-bin/
		 * libs/xcloud-devops-dts-codegen-master.jar!/generate-config/project-
		 * templates/springCloudMvnProvider/%23%7bT(JavaSpecs).lCase(organName)%
		 * 7d-%23%7bT(JavaSpecs).lCase(projectName)%7d/%23%7bT(JavaSpecs).lCase(
		 * organName)%7d-%23%7bT(JavaSpecs).lCase(projectName)%7d-dao/pom.xml.
		 * ftl
		 * </pre>
		 * 
		 * Note: The original path (need decoded) is used here.
		 */
		String path = res.getURI().toString();
		path = new String(URLCodec.decodeUrl(path.getBytes(UTF_8)));
		String projectRootPathPart = TPL_PROJECT_PATH.concat("/").concat(provider).concat("/");
		int i = path.indexOf(projectRootPathPart);
		if (i >= 0) {
			path = path.substring(i + projectRootPathPart.length());
		}
		return new TemplateWrapper(path, res.getFilename(), readFullyToString(res.getInputStream()));
	}

	// Template configuration.
	public static final String TPL_BASEPATH = "generate-config";
	public static final String TPL_PROJECT_PATH = TPL_BASEPATH.concat("/project-templates");

	// Load tpl suffix rules.
	public static final String[] LOAD_SUFFIXS = { DEFAULT_TPL_SUFFIX, ".css", ".js", ".vue", ".ts", ".jpg", ".gif", ".html", ".json",
			".md", ".png", ".svg", ".eot", ".ttf", ".woff", ".woff2" };

	// e.g: classpath:/templates/xxGenProvider/**/*/.ftl
	public static final String LOAD_PATTERN = "classpath:/".concat(TPL_PROJECT_PATH).concat("/%s/**/*%s");

	/**
	 * Global project {@link Template} cache.
	 */
	private static final Map<String, List<TemplateWrapper>> templatesCache = new HashMap<>();

	/**
	 * {@link ClassPathResourcePatternResolver}
	 */
	private static final ClassPathResourcePatternResolver defaultResourceResolver = new ClassPathResourcePatternResolver();

}
