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
package com.wl4g.devops.scm.common.command;

import org.apache.commons.lang3.StringUtils;

import com.wl4g.components.common.serialize.JacksonUtils;
import com.wl4g.devops.scm.common.command.GenericConfigInfo.ConfigNode;
import com.wl4g.devops.scm.common.command.ReleaseConfigInfo.ConfigPropertySource;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.wl4g.components.common.lang.Assert2.notEmpty;
import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/** {@link ConfigPropertySources} */
	@NotNull
	@NotEmpty
	private List<ConfigPropertySource> propertySources = new ArrayList<>();

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

	public List<ConfigPropertySource> getPropertySources() {
		return propertySources;
	}

	public void setPropertySources(List<ConfigPropertySource> propertySources) {
		if (propertySources != null) {
			this.propertySources = propertySources;
		}
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

	@Override
	public void validate(boolean versionValidate, boolean releaseValidate) {
		super.validate(versionValidate, releaseValidate);
		notNull(getPropertySources(), "Invalid empty propertySources");
		getPropertySources().stream().forEach((ps) -> {
			notNull(ps, "Invalid release propertySources");
			ps.validate();
		});
	}

	public CompositePropertySource convertCompositePropertySource(String sourceName) {
		CompositePropertySource composite = new CompositePropertySource(sourceName);
		for (ConfigPropertySource ps : getPropertySources()) {
			// See:org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration
			composite.addFirstPropertySource(ps.convertMapPropertySource());
		}
		return composite;
	}

	/**
	 * {@link ConfigPropertySource}
	 * 
	 * @see
	 */
	public static class ConfigPropertySource {

		private String name;
		private Map<String, Object> source = new HashMap<>();

		public ConfigPropertySource() {
			super();
		}

		public ConfigPropertySource(String name, Map<String, Object> source) {
			super();
			this.name = name;
			this.source = source;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			if (!StringUtils.isEmpty(name) && !"NULL".equalsIgnoreCase(name)) {
				this.name = name;
			}
		}

		public Map<String, Object> getSource() {
			return source;
		}

		public void setSource(Map<String, Object> source) {
			if (source != null) {
				this.source = source;
			}
		}

		public void validate() {
			notNull(getName(), "PropertySource-Name is not allowed to be null.");
			notEmpty(getSource(), "PropertySource-Properties is not allowed to be empty.");
		}

		public MapPropertySource convertMapPropertySource() {
			return new MapPropertySource(getName(), getSource());
		}

		public static ConfigPropertySource build(MapPropertySource mapSource) {
			if (mapSource == null) {
				return null;
			}
			return new ConfigPropertySource(mapSource.getName(), mapSource.getSource());
		}

	}

}