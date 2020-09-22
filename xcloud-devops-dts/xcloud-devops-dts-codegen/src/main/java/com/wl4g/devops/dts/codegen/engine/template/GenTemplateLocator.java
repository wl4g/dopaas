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

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.wl4g.components.common.annotation.Nullable;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * {@link GenTemplateLocator}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-19
 * @sine v1.0.0
 * @see
 */
public interface GenTemplateLocator {

	/**
	 * Locate generate templates.
	 * 
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	List<RenderingResourceWrapper> locate(String provider) throws Exception;

	/**
	 * Cleanup located templates cache all.
	 * 
	 * @return
	 */
	boolean cleanAll();

	/**
	 * Generate rendering resource wrapper.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @author Vjay
	 * @version v1.0 2020-09-16
	 * @since
	 */
	public static class RenderingResourceWrapper implements Serializable {
		private static final long serialVersionUID = 4336198329362479594L;

		// Template class path.
		private final String path;
		private final String name;
		private final String content;
		private final boolean isTemplate;
		private final boolean isForeachTemplate;

		protected RenderingResourceWrapper(@NotBlank String path, @NotBlank String name, @Nullable String content) {
			hasTextOf(path, "path");
			hasTextOf(name, "name");
			// hasTextOf(content, "content");
			this.path = path;
			this.name = name;
			this.content = isBlank(content) ? EMPTY : content; // By default
			this.isTemplate = name.endsWith(DEFAULT_TPL_SUFFIX);
			this.isForeachTemplate = name.contains(DEFAULT_VARIABLE_ENTITY_NAME);
		}

		public String getPath() {
			return path;
		}

		public String getName() {
			return name;
		}

		public String getContent() {
			return content;
		}

		public boolean isTemplate() {
			return isTemplate;
		}

		public boolean isForeachTemplate() {
			return isForeachTemplate;
		}

		public void validate() {
			hasTextOf(path, "tplPath");
			hasTextOf(name, "fileName");
			hasTextOf(content, "fileContent");
		}

	}

	// Default load template suffix.
	public static final String DEFAULT_TPL_SUFFIX = ".ftl";

	// Default need foreach rendering variable.
	public static final String DEFAULT_VARIABLE_ENTITY_NAME = "entityName";

}
