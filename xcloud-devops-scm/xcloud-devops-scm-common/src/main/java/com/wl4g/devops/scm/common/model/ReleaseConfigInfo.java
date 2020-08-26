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
package com.wl4g.devops.scm.common.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ReleaseConfigInfo}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018-08-11
 * @since
 */
public class ReleaseConfigInfo extends GenericConfigInfo {
	final private static long serialVersionUID = -4016863811283064989L;

	/**
	 * Pulish configuration to nodes.
	 */
	@NotNull
	@NotEmpty
	private List<ConfigNode> nodes = new ArrayList<>();

	/** {@link ReleaseConfigSource} */
	@NotNull
	@NotEmpty
	private List<ReleaseConfigSource> releaseSources = new ArrayList<>();

	public ReleaseConfigInfo() {
		super();
	}

	public ReleaseConfigInfo(String cluster, List<String> namespaces, ConfigMeta meta, List<ConfigNode> nodes) {
		super(cluster, namespaces, meta);
		setNodes(nodes);
	}

	public List<ConfigNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<ConfigNode> nodes) {
		this.nodes = nodes;
	}

	public List<ReleaseConfigSource> getReleaseSources() {
		return releaseSources;
	}

	public void setReleaseSources(List<ReleaseConfigSource> propertySources) {
		if (propertySources != null) {
			this.releaseSources = propertySources;
		}
	}

	@Override
	public void validate(boolean versionValidate, boolean releaseValidate) {
		super.validate(versionValidate, releaseValidate);
		notNull(getReleaseSources(), "Invalid empty propertySources");
		getReleaseSources().stream().forEach((rs) -> {
			notNull(rs, "Invalid release property source");
			rs.validate();
		});
	}

	/**
	 * {@link ReleaseConfigSource}
	 *
	 * @since
	 */
	public static class ReleaseConfigSource {

		/**
		 * Configuration property source format
		 */
		private String sourceType;

		/**
		 * Configuration property source content text.
		 */
		private String sourceContent;

		public ReleaseConfigSource(String sourceType, String sourceContent) {
			setSourceType(sourceType);
			setSourceContent(sourceContent);
		}

		public String getSourceType() {
			return sourceType;
		}

		public void setSourceType(String sourceType) {
			hasTextOf(sourceType, "sourceType");
			this.sourceType = sourceType;
		}

		public String getSourceContent() {
			return sourceContent;
		}

		public void setSourceContent(String sourceContent) {
			hasTextOf(sourceContent, "sourceContent");
			this.sourceContent = sourceContent;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
		}

		public void validate() {
			hasTextOf(sourceType, "sourceType");
			hasTextOf(sourceContent, "sourceContent");
		}

	}

}