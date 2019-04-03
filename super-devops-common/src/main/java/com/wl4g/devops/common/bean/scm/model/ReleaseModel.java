package com.wl4g.devops.common.bean.scm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.Assert;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;

public class ReleaseModel extends GetReleaseModel {
	final private static long serialVersionUID = -4016863811283064989L;

	private List<ReleasePropertySource> propertySources = new ArrayList<>();

	public ReleaseModel() {
		super();
	}

	public ReleaseModel(String application, String profile, ReleaseInstance instance) {
		super(application, profile, instance);
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
	public void validation(boolean validVersion, boolean validReleaseId) {
		super.validation(validVersion, validReleaseId);
		Assert.notEmpty(getPropertySources(), "`propertySources` is not allowed to be null.");
		getPropertySources().stream().forEach((ps) -> {
			Assert.notNull(ps, "`releasePropertySource` is not allowed to be null.");
			ps.validation();
		});
	}

	public CompositePropertySource convertCompositePropertySource(String sourceName) {
		CompositePropertySource composite = new CompositePropertySource(sourceName);
		for (ReleasePropertySource ps : this.getPropertySources()) {
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
			return new MapPropertySource(this.getName(), this.getSource());
		}

		public static ReleasePropertySource build(MapPropertySource mapSource) {
			if (mapSource == null) {
				return null;
			}
			return new ReleasePropertySource(mapSource.getName(), mapSource.getSource());
		}

	}

}
