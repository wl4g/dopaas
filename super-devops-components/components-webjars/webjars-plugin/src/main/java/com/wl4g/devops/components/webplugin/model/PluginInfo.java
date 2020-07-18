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
package com.wl4g.devops.components.webplugin.model;

import static com.wl4g.devops.components.tools.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link PluginModuleDependencies}
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年4月22日
 * @since
 */
public class PluginInfo implements Serializable {
	private static final long serialVersionUID = 5131409950359014475L;

	/**
	 * Plug-in name
	 */
	private String pluginName;

	/**
	 * Plug-in version
	 */
	private String version;

	/**
	 * Dependencies
	 */
	private List<Module> modules = new ArrayList<>(8);

	/**
	 * Dependencies
	 */
	private List<Dependency> dependencies = new ArrayList<>(8);

	public String getPluginName() {
		return pluginName;
	}

	public PluginInfo setPluginName(String pluginName) {
		hasTextOf(pluginName, "pluginName");
		this.pluginName = pluginName;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public PluginInfo setVersion(String version) {
		hasTextOf(version, "version");
		this.version = version;
		return this;
	}

	public List<Module> getModules() {
		return modules;
	}

	public PluginInfo setModules(List<Module> modules) {
		if (!isEmpty(modules)) {
			this.modules.addAll(modules);
		}
		return this;
	}

	public PluginInfo addModules(Module... modules) {
		if (!isNull(modules) && modules.length > 0) {
			this.modules.addAll(asList(modules));
		}
		return this;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public PluginInfo setDependencies(List<Dependency> dependencies) {
		if (!isEmpty(dependencies)) {
			this.dependencies = dependencies;
		}
		return this;
	}

	public PluginInfo addDependencies(Dependency... dependencies) {
		if (!isNull(dependencies) && dependencies.length > 0) {
			this.dependencies.addAll(asList(dependencies));
		}
		return this;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " => " + toJSONString(this);
	}

	/**
	 * {@link Module}
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年4月19日 v1.0.0
	 * @see
	 */
	public static class Module implements Serializable {
		private static final long serialVersionUID = 7805276828703966036L;

		/**
		 * Module name.
		 */
		private String modName;

		/**
		 * Stable version js file path.
		 */
		private String stable;

		/**
		 * Grey version js file path.
		 */
		private String grey;

		/**
		 * Stable version css file path.
		 */
		private String css_stable;

		/**
		 * Grey version css file path.
		 */
		private String css_grey;

		/**
		 * Ref module priority level.
		 */
		private int ratio = 100;

		public Module() {
			super();
		}

		public Module(String modName, String stable, String grey, int ratio) {
			setModName(modName);
			setStable(stable);
			setGrey(grey);
			setRatio(ratio);
		}

		public Module(String modName, String stable, String grey, String css_stable, String css_grey, int ratio) {
			setModName(modName);
			setStable(stable);
			setGrey(grey);
			setCss_Stable(css_stable);
			setCss_grey(css_grey);
			setRatio(ratio);
		}

		public String getModName() {
			return modName;
		}

		public Module setModName(String modName) {
			hasTextOf(modName, "modName");
			this.modName = modName;
			return this;
		}

		public String getStable() {
			return stable;
		}

		public Module setStable(String stable) {
			hasTextOf(stable, "stable");
			this.stable = stable;
			return this;
		}

		public String getGrey() {
			return grey;
		}

		public Module setGrey(String grey) {
			hasTextOf(grey, "grey");
			this.grey = grey;
			return this;
		}

		public String getCss_Stable() {
			return css_stable;
		}

		public Module setCss_Stable(String css_stable) {
			// hasTextOf(css_stable, "css_stable");
			this.css_stable = css_stable;
			return this;
		}

		public String getCss_grey() {
			return css_grey;
		}

		public Module setCss_grey(String css_grey) {
			// hasTextOf(css_grey, "css_grey");
			this.css_grey = css_grey;
			return this;
		}

		public int getRatio() {
			return ratio;
		}

		public Module setRatio(int ratio) {
			this.ratio = ratio;
			return this;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + " => " + toJSONString(this);
		}

	}

	/**
	 * {@link Dependency}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年4月22日
	 * @since
	 */
	public static class Dependency implements Serializable {
		private static final long serialVersionUID = -4078154559206854901L;

		/**
		 * Features
		 */
		private Set<String> features = new HashSet<>();

		/**
		 * Module dependencies. (Attention should be paid to the sequence of
		 * application dependent modules)
		 */
		private List<String> depends = new ArrayList<>();

		/**
		 * Sync
		 */
		private boolean sync = true;

		public Set<String> getFeatures() {
			return features;
		}

		public Dependency setFeatures(Set<String> features) {
			if (!isEmpty(features)) {
				this.features.addAll(features);
			}
			return this;
		}

		public Dependency addFeatures(String... features) {
			if (!isNull(features) && features.length > 0) {
				this.features.addAll(asList(features));
			}
			return this;
		}

		public List<String> getDepends() {
			return depends;
		}

		public Dependency setDepends(List<String> depends) {
			if (!isEmpty(depends)) {
				this.depends.addAll(depends);
			}
			return this;
		}

		public Dependency addDepends(String... depends) {
			if (!isNull(depends) && depends.length > 0) {
				this.depends.addAll(asList(depends));
			}
			return this;
		}

		public boolean isSync() {
			return sync;
		}

		public Dependency setSync(boolean sync) {
			this.sync = sync;
			return this;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + " => " + toJSONString(this);
		}

	}

}