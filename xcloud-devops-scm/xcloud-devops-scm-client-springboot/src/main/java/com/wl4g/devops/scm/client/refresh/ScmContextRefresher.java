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
package com.wl4g.devops.scm.client.refresh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.bootstrap.BootstrapApplicationListener;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;

import static org.springframework.core.env.CommandLinePropertySource.*;
import static org.springframework.core.env.StandardEnvironment.*;
import static org.springframework.web.context.support.StandardServletEnvironment.*;

/**
 * Implementing SCM dynamic configuration refresher, which is almost consistent
 * with the origin {@link ContextRefresher} logic, mainly changes the details
 * related to SCM-server communication.</br>
 * See:{@link RefreshAutoConfiguration#contextRefresher()}
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月30日
 * @since
 */
public class ScmContextRefresher extends ContextRefresher {

	final public static String SCM_REFRESH_PROPERTY_SOURCE = "_DevOpsScmPropertySource_";

	/**
	 * Order matters, cli args aren't first, things get messy
	 */
	final private static String[] DEFAULT_PROPERTY_SOURCES = new String[] { COMMAND_LINE_PROPERTY_SOURCE_NAME,
			"defaultProperties" };

	/**
	 * Replacement of Spring boot built-in configuration source is not allowed.
	 */
	final private static List<String> STANDARD_SOURCES = Arrays.asList(SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME,
			SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, JNDI_PROPERTY_SOURCE_NAME, SERVLET_CONFIG_PROPERTY_SOURCE_NAME,
			SERVLET_CONTEXT_PROPERTY_SOURCE_NAME);

	/**
	 * Required based application listeners.
	 */
	final private static List<ApplicationListener<?>> REQUIRE_LISTENERS = Arrays.asList(new BootstrapApplicationListener(),
			new ConfigFileApplicationListener());

	final private ConfigurableApplicationContext context;
	final private RefreshScope scope;

	public ScmContextRefresher(ConfigurableApplicationContext context, RefreshScope scope) {
		super(context, scope);
		Assert.notNull(context, "ApplicationContext must not be null");
		Assert.notNull(scope, "RefreshScope must not be null");
		this.context = context;
		this.scope = scope;
	}

	@Override
	public synchronized Set<String> refresh() {
		Map<String, Object> before = extract(context.getEnvironment().getPropertySources());
		addScmConfigToEnvironment();
		Set<String> keys = changes(before, extract(context.getEnvironment().getPropertySources())).keySet();
		context.publishEvent(new EnvironmentChangeEvent(context, keys));
		scope.refreshAll();
		return keys;
	}

	private ConfigurableApplicationContext addScmConfigToEnvironment() {
		ConfigurableApplicationContext capture = null;
		try {
			StandardEnvironment environment = copyEnvironment(context.getEnvironment());

			// Temporarily create a simple non-Web application context
			SpringApplicationBuilder builder = new SpringApplicationBuilder(Empty.class).bannerMode(Mode.OFF)
					.web(WebApplicationType.NONE).environment(environment);
			// Just the listeners that affect the environment (e.g. excluding
			// logging listener because it has side effects)
			builder.application().setListeners(REQUIRE_LISTENERS);
			capture = builder.run();
			if (environment.getPropertySources().contains(SCM_REFRESH_PROPERTY_SOURCE)) {
				environment.getPropertySources().remove(SCM_REFRESH_PROPERTY_SOURCE);
			}

			MutablePropertySources target = context.getEnvironment().getPropertySources();
			for (PropertySource<?> source : environment.getPropertySources()) {
				String targetName = null;
				String name = source.getName();
				if (target.contains(name)) {
					targetName = name;
				}
				if (!STANDARD_SOURCES.contains(name)) {
					if (target.contains(name)) {
						target.replace(name, source);
					} else {
						if (targetName != null) {
							target.addAfter(targetName, source);
						}
						// targetName was null so we are at the start of the
						// list.
						else {
							target.addFirst(source);
							targetName = name;
						}
					}
				}
			}
		} finally {
			ConfigurableApplicationContext closeable = capture;
			while (closeable != null) {
				try {
					closeable.close();
				} catch (Exception e) {
					// Ignore;
				}
				if (closeable.getParent() instanceof ConfigurableApplicationContext) {
					closeable = (ConfigurableApplicationContext) closeable.getParent();
				} else {
					break;
				}
			}
		}
		return capture;
	}

	/**
	 * Don't use ConfigurableEnvironment.merge() in case there are clashes with
	 * property source names
	 * 
	 * @param input
	 * @return
	 */
	private StandardEnvironment copyEnvironment(ConfigurableEnvironment input) {
		StandardEnvironment environment = new StandardEnvironment();
		MutablePropertySources capturedPropertySources = environment.getPropertySources();

		// Only copy the default property source(s) and the profiles over from
		// the main environment (everything else should be pristine, just like
		// it was on startup).
		for (String name : DEFAULT_PROPERTY_SOURCES) {
			if (input.getPropertySources().contains(name)) {
				if (capturedPropertySources.contains(name)) {
					capturedPropertySources.replace(name, input.getPropertySources().get(name));
				} else {
					capturedPropertySources.addLast(input.getPropertySources().get(name));
				}
			}
		}
		environment.setActiveProfiles(input.getActiveProfiles());
		environment.setDefaultProfiles(input.getDefaultProfiles());

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("spring.jmx.enabled", false);
		map.put("spring.main.sources", "");
		capturedPropertySources.addFirst(new MapPropertySource(SCM_REFRESH_PROPERTY_SOURCE, map));
		return environment;
	}

	private Map<String, Object> changes(Map<String, Object> before, Map<String, Object> after) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (String key : before.keySet()) {
			if (!after.containsKey(key)) {
				result.put(key, null);
			} else if (!equal(before.get(key), after.get(key))) {
				result.put(key, after.get(key));
			}
		}
		for (String key : after.keySet()) {
			if (!before.containsKey(key)) {
				result.put(key, after.get(key));
			}
		}
		return result;
	}

	private boolean equal(Object one, Object two) {
		if (one == null && two == null) {
			return true;
		}
		if (one == null || two == null) {
			return false;
		}
		return one.equals(two);
	}

	private Map<String, Object> extract(MutablePropertySources propertySources) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<PropertySource<?>> sources = new ArrayList<PropertySource<?>>();
		for (PropertySource<?> source : propertySources) {
			sources.add(0, source);
		}
		for (PropertySource<?> source : sources) {
			if (!STANDARD_SOURCES.contains(source.getName())) {
				extract(source, result);
			}
		}
		return result;
	}

	private void extract(PropertySource<?> parent, Map<String, Object> result) {
		if (parent instanceof CompositePropertySource) {
			try {
				List<PropertySource<?>> sources = new ArrayList<PropertySource<?>>();
				for (PropertySource<?> source : ((CompositePropertySource) parent).getPropertySources()) {
					sources.add(0, source);
				}
				for (PropertySource<?> source : sources) {
					extract(source, result);
				}
			} catch (Exception e) {
				return;
			}
		} else if (parent instanceof EnumerablePropertySource) {
			for (String key : ((EnumerablePropertySource<?>) parent).getPropertyNames()) {
				result.put(key, parent.getProperty(key));
			}
		}
	}

}