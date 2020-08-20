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
import com.wl4g.devops.scm.common.command.ReleaseConfigInfo.IniPropertySource;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notEmpty;
import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
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
	private List<IniPropertySource> propertySources = new ArrayList<>();

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

	public List<IniPropertySource> getPropertySources() {
		return propertySources;
	}

	public void setPropertySources(List<IniPropertySource> propertySources) {
		if (propertySources != null) {
			this.propertySources = propertySources;
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
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
		for (IniPropertySource ps : getPropertySources()) {
			// See:org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration
			composite.addFirstPropertySource(ps.convertMapPropertySource());
		}
		return composite;
	}

	/**
	 * Origin property source typeof {@link PlaintextPropertySource}
	 * 
	 * @see
	 */
	public static class PlaintextPropertySource {

		/** Release configuration profile.(like spring.profiles) */
		private String profile;

		/** Release configuration plaintext content string. */
		private String content;

		public PlaintextPropertySource() {
			super();
		}

		public PlaintextPropertySource(String profile, String content) {
			hasTextOf(profile, "profile");
			hasTextOf(content, "content");
			this.profile = profile;
			this.content = content;
		}

		public String getProfile() {
			return profile;
		}

		public void setProfile(String profile) {
			this.profile = profile;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
		}

	}

	/**
	 * {@link PropertiesPropertySource}
	 * 
	 * @see
	 */
	@Getter
	public static class PropertiesPropertySource extends PlaintextPropertySource {

		public PropertiesPropertySource(String profile, String content) {
			super(profile, content);
		}

	}

	/**
	 * {@link IniPropertySource}
	 * 
	 * @see
	 */
	public static class IniPropertySource extends PlaintextPropertySource {

		/** Configuration source typeof map */
		private final Map<String, Object> source;

		public IniPropertySource() {
			super();
		}

		public IniPropertySource(String profile, String content) {
			super(profile, content);
			this.source = null; // TODO
		}

		public Map<String, Object> getSource() {
			return source;
		}

	}

	/**
	 * {@link XmlPropertySource}
	 * 
	 * @see
	 */
	@Getter
	public static class XmlPropertySource extends PlaintextPropertySource {

		/** Configuration source typeof map */
		private final XmlNode root;

		public XmlPropertySource() {
			super();
		}

		public XmlPropertySource(String profile, String content) {
			super(profile, content);
			this.root = null; // TODO
		}

		/**
		 * {@link XmlNode}
		 * 
		 * @see
		 */
		@Getter
		@Setter
		public static class XmlNode {

			/** Xml node name. */
			private String name;

			/** Xml node attributes. */
			private Map<String, String> attributes;

			/** Xml children nodes. */
			private List<XmlNode> children;

			@Override
			public String toString() {
				return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
			}

		}

	}

	/**
	 * {@link JsonPropertySource}
	 * 
	 * @see
	 */
	@Getter
	public static class JsonPropertySource extends PlaintextPropertySource {

	}

	/**
	 * {@link HoconPropertySource}
	 * 
	 * @see
	 */
	@Getter
	public static class HoconPropertySource extends PlaintextPropertySource {

		public HoconPropertySource(String profile, String content) {
			super(profile, content);
		}

	}

	/**
	 * {@link YamlMapPropertySource}
	 * 
	 * @see
	 */
	@Getter
	public static class YamlMapPropertySource extends PlaintextPropertySource {

		public YamlMapPropertySource(String profile, String content) {
			super(profile, content);
		}

	}

}