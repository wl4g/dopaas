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
package com.wl4g.devops.doc.plugin.swagger.jaxrs2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import org.apache.maven.plugin.logging.Log;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

/**
 * Scan for classes with {@link Path} annotation or {@link OpenAPIDefinition}
 * annotation, and for {@link Application} instances.
 */
class JaxRSScanner {

	private final Log log;

	private final Set<String> resourcePackages;

	private final boolean useResourcePackagesChildren;

	public JaxRSScanner(Log log, Set<String> resourcePackages, Boolean useResourcePackagesChildren) {
		this.log = log;
		this.resourcePackages = resourcePackages == null ? Collections.emptySet() : new HashSet<>(resourcePackages);
		this.useResourcePackagesChildren = useResourcePackagesChildren != null && useResourcePackagesChildren;
	}

	Application applicationInstance() {
		ConfigurationBuilder config = ConfigurationBuilder.build(resourcePackages).setScanners(new ResourcesScanner(),
				new TypeAnnotationsScanner(), new SubTypesScanner());
		Reflections reflections = new Reflections(config);
		Set<Class<? extends Application>> applicationClasses = reflections.getSubTypesOf(Application.class).stream()
				.filter(this::filterClassByResourcePackages).collect(Collectors.toSet());
		if (applicationClasses.isEmpty()) {
			return null;
		}
		if (applicationClasses.size() > 1) {
			log.warn("More than one javax.ws.rs.core.Application classes found on the classpath, skipping");
			return null;
		}
		return ClassUtils.createInstance(applicationClasses.iterator().next());
	}

	Set<Class<?>> classes() {
		ConfigurationBuilder config = ConfigurationBuilder.build(resourcePackages).setScanners(new ResourcesScanner(),
				new TypeAnnotationsScanner(), new SubTypesScanner());
		Reflections reflections = new Reflections(config);
		Stream<Class<?>> apiClasses = reflections.getTypesAnnotatedWith(Path.class).stream()
				.filter(this::filterClassByResourcePackages);
		Stream<Class<?>> defClasses = reflections.getTypesAnnotatedWith(OpenAPIDefinition.class).stream()
				.filter(this::filterClassByResourcePackages);
		return Stream.concat(apiClasses, defClasses).collect(Collectors.toSet());
	}

	private boolean filterClassByResourcePackages(Class<?> cls) {
		return resourcePackages.isEmpty() || resourcePackages.contains(cls.getPackage().getName()) || (useResourcePackagesChildren
				&& resourcePackages.stream().anyMatch(p -> cls.getPackage().getName().startsWith(p)));
	}

}