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
package com.wl4g.devops.common.bean.scm.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.Assert;

import com.wl4g.devops.components.tools.common.serialize.JacksonUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReleaseMessage extends GetRelease {
	final private static long serialVersionUID = -4016863811283064989L;

	@NotNull
	@NotEmpty
	private List<ReleasePropertySource> propertySources = new ArrayList<>();

	public ReleaseMessage() {
		super();
	}

	public ReleaseMessage(String cluster, List<String> namespaces, ReleaseMeta meta, ReleaseInstance instance) {
		super(cluster, namespaces, meta, instance);
	}

	public List<ReleasePropertySource> getPropertySources() {
		return propertySources;
	}

	public void setPropertySources(List<ReleasePropertySource> propertySources) {
		if (propertySources != null) {
			this.propertySources = propertySources;
		}
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

	@Override
	public void validation(boolean versionValidate, boolean releaseValidate) {
		super.validation(versionValidate, releaseValidate);
		Assert.notEmpty(getPropertySources(), "Invalid empty propertySources");
		getPropertySources().stream().forEach((ps) -> {
			Assert.notNull(ps, "Invalid release propertySources");
			ps.validation();
		});
	}

	public CompositePropertySource convertCompositePropertySource(String sourceName) {
		CompositePropertySource composite = new CompositePropertySource(sourceName);
		for (ReleasePropertySource ps : getPropertySources()) {
			// See:org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration
			composite.addFirstPropertySource(ps.convertMapPropertySource());
		}
		return composite;
	}

	/**
	 * {@link MapPropertySource} that reads keys and values from a {@code Map}
	 * object.
	 *
	 * @author Chris Beams
	 * @author Juergen Hoeller
	 * @since 3.1
	 * @see MapPropertySource
	 */
	public static class ReleasePropertySource {
		private String name;
		private Map<String, Object> source = new HashMap<>();

		public ReleasePropertySource() {
			super();
		}

		public ReleasePropertySource(String name, Map<String, Object> source) {
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

		public void validation() {
			Assert.notNull(getName(), "PropertySource-Name is not allowed to be null.");
			Assert.notEmpty(getSource(), "PropertySource-Properties is not allowed to be empty.");
		}

		public MapPropertySource convertMapPropertySource() {
			return new MapPropertySource(getName(), getSource());
		}

		public static ReleasePropertySource build(MapPropertySource mapSource) {
			if (mapSource == null) {
				return null;
			}
			return new ReleasePropertySource(mapSource.getName(), mapSource.getSource());
		}

	}

}