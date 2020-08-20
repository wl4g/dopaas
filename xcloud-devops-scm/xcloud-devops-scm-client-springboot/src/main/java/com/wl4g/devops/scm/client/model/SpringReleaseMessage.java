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
package com.wl4g.devops.scm.client.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.wl4g.devops.scm.common.command.CompositePropertySource;
import com.wl4g.devops.scm.common.command.ReleaseConfigInfo;
import com.wl4g.devops.scm.common.command.MapPropertySource;
import com.wl4g.devops.scm.common.command.ReleaseConfigInfo.ReleasePropertySource;

/**
 * {@link SpringReleaseMessage}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-11
 * @since
 */
public class SpringReleaseMessage extends ReleaseConfigInfo {
	private static final long serialVersionUID = 2325498697253289001L;

	@Override
	public void validate(boolean versionValidate, boolean releaseValidate) {
		super.validate(versionValidate, releaseValidate);
		Assert.notEmpty(getPropertySources(), "Invalid empty propertySources");
		getPropertySources().stream().forEach((ps) -> {
			Assert.notNull(ps, "Invalid release propertySources");
			ps.validate();
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
	 * {@link IniPropertySource} that reads keys and values from a {@code Map}
	 * object.
	 *
	 * @author Chris Beams
	 * @author Juergen Hoeller
	 * @since 3.1
	 * @see IniPropertySource
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

		public IniPropertySource convertMapPropertySource() {
			return new IniPropertySource(getName(), getSource());
		}

		public static ReleasePropertySource build(IniPropertySource mapSource) {
			if (mapSource == null) {
				return null;
			}
			return new ReleasePropertySource(mapSource.getName(), mapSource.getSource());
		}

	}

}
