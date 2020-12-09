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
package com.wl4g.devops.doc.plugin.swagger.export.enhance;

import static java.util.Objects.isNull;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Sorter for the contents of an OpenAPI specification.
 * <p>
 * Sorting the OpenAPI specification solves problems when the output of this
 * plugin is committed in a version control system.
 * <p>
 * The swagger-core library generates non-deterministic output, because
 * reflection operations on scanned Resource classes are non-deterministic in
 * the order of methods and fields.
 * <p>
 * This class and its functionality may be removed if the generation of
 * deterministic output is solved in swagger-core.
 * <p>
 * See https://github.com/swagger-api/swagger-core/issues/3475 See
 * https://github.com/swagger-api/swagger-core/issues/2775 See
 * https://github.com/swagger-api/swagger-core/issues/2828
 */
@SuppressWarnings("rawtypes")
public class OpenAPISorter {

	private OpenAPISorter() {
		// No instances
	}

	/**
	 * Sort all the paths and components of the OpenAPI specification, in place.
	 * 
	 * @param swagger
	 *            OpenAPI specification to apply sorting to
	 * @return the sorted version of the specification
	 */
	public static OpenAPI sort(OpenAPI swagger) {
		swagger.setPaths(sortPaths(swagger.getPaths()));
		sortComponents(swagger.getComponents());
		return swagger;
	}

	/**
	 * Sort all the elements of Paths.
	 */
	private static Paths sortPaths(Paths paths) {
		if (isNull(paths)) {
			return null;
		}
		TreeMap<String, PathItem> sorted = new TreeMap<>(paths);
		paths.clear();
		paths.putAll(sorted);
		return paths;
	}

	/**
	 * Sort all the elements of Components.
	 */
	private static void sortComponents(Components components) {
		if (isNull(components)) {
			return;
		}

		components.setSchemas(sortSchemas(components.getSchemas()));
		components.setResponses(createSorted(components.getResponses()));
		components.setParameters(createSorted(components.getParameters()));
		components.setExamples(createSorted(components.getExamples()));
		components.setRequestBodies(createSorted(components.getRequestBodies()));
		components.setHeaders(createSorted(components.getHeaders()));
		components.setSecuritySchemes(createSorted(components.getSecuritySchemes()));
		components.setLinks(createSorted(components.getLinks()));
		components.setCallbacks(createSorted(components.getCallbacks()));
		components.setExtensions(createSorted(components.getExtensions()));
	}

	/**
	 * Recursively sort all the schemas in the Map.
	 */
	private static SortedMap<String, Schema> sortSchemas(Map<String, Schema> schemas) {
		if (schemas == null) {
			return null;
		}

		TreeMap<String, Schema> sorted = new TreeMap<>();
		schemas.entrySet().forEach(entry -> {
			Schema<?> schema = entry.getValue();
			schema.setProperties(sortSchemas(schema.getProperties()));
			sorted.put(entry.getKey(), schema);
		});

		return sorted;
	}

	/**
	 * Created sorted map based on natural key order.
	 */
	private static <T> SortedMap<String, T> createSorted(Map<String, T> map) {
		return map == null ? null : new TreeMap<>(map);
	}

}