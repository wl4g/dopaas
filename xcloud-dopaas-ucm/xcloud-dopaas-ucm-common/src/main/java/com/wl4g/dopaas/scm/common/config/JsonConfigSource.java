/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.scm.common.config;

import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wl4g.component.common.reflect.ReflectionUtils2.isCompatibleType;
import static com.wl4g.component.common.serialize.JacksonUtils.parseJSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.dopaas.scm.common.model.AbstractConfigInfo.ConfigProfile;

import lombok.Getter;

/**
 * {@link JsonConfigSource}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
public class JsonConfigSource extends AbstractConfigSource {
	private static final long serialVersionUID = 5937417582294678642L;

	/**
	 * Resolved JSON property source.
	 */
	private Map<String, Object> resolved;

	@Override
	public void doRead(ConfigProfile profile, String sourceContent) {
		this.resolved = parseJSON(sourceContent, DEFAULT_REFTYPE);

		// Resolving cipher all property.
		resolvingHierarchyCipherProperty(resolved);
	}

	/**
	 * Resolving hierarchy cipher all properties.
	 * 
	 * @param source
	 */
	@SuppressWarnings({ "unchecked" })
	private void resolvingHierarchyCipherProperty(Map<String, Object> source) {
		source.forEach((key, value) -> {
			if (isNull(value)) {
				return;
			}
			Class<?> cls = value.getClass();
			if (isCompatibleType(cls, String.class)) {
				resolveCipherProperty(key, (String) value);
			} else if (isCompatibleType(cls, Map.class)) {
				resolvingHierarchyCipherProperty((Map<String, Object>) value);
			} else if (isCompatibleType(cls, List.class)) {
				((List<Object>) value).forEach(e -> {
					if (isCompatibleType(e.getClass(), Map.class)) {
						resolvingHierarchyCipherProperty((Map<String, Object>) e);
					} else {
						resolveCipherProperty(key, (String) e);
					}
				});
			} else if (cls.isArray()) {
				for (Object val : ((Object[]) value)) {
					if (isCompatibleType(cls, String.class)) {
						resolveCipherProperty(key, (String) val);
					}
				}
			} else {
				resolvingHierarchyCipherProperty((Map<String, Object>) value);
			}
		});

	}

	private static final TypeReference<HashMap<String, Object>> DEFAULT_REFTYPE = new TypeReference<HashMap<String, Object>>() {
	};

}