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

import static com.wl4g.components.common.lang.Assert2.hasTextOf;

import java.io.Serializable;
import java.util.List;

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
	List<TemplateWrapper> locate(String provider) throws Exception;

	/**
	 * Cleanup located templates cache all.
	 * 
	 * @return
	 */
	boolean cleanAll();

	/**
	 * Generate template wrapper.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @author Vjay
	 * @version v1.0 2020-09-16
	 * @since
	 */
	public static class TemplateWrapper implements Serializable {
		private static final long serialVersionUID = 4336198329362479594L;

		// Template class path.
		private final String tplPath;
		private final String fileName;
		private final String fileContent;
		private final boolean isTpl;
		private final boolean isForeachTpl;

		protected TemplateWrapper(String tplPath, String filename, String fileContent) {
			hasTextOf(tplPath, "tplPath");
			hasTextOf(filename, "filename");
			hasTextOf(fileContent, "fileContent");
			this.tplPath = tplPath;
			this.fileName = filename;
			this.fileContent = fileContent;
			this.isTpl = filename.endsWith(DEFAULT_TPL_SUFFIX);
			this.isForeachTpl = filename.contains(DEFAULT_VARIABLE_ENTITY_NAME);
		}

		public String getTplPath() {
			return tplPath;
		}

		public String getFileName() {
			return fileName;
		}

		public String getFileContent() {
			return fileContent;
		}

		public boolean isTpl() {
			return isTpl;
		}

		public boolean isForeachTpl() {
			return isForeachTpl;
		}

	}

	// Default load template suffix.
	public static final String DEFAULT_TPL_SUFFIX = ".ftl";

	// Default need foreach rendering variable.
	public static final String DEFAULT_VARIABLE_ENTITY_NAME = "entityName";

}
