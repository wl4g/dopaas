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
package com.wl4g.devops.dts.codegen.utils;

import static com.wl4g.components.common.lang.Assert2.isNull;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.nonNull;

import java.util.HashMap;
import java.util.Map;

import com.wl4g.components.common.log.SmartLogger;

/**
 * {@link RenderableModelMap}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-20
 * @sine v1.0.0
 * @see
 */
public class RenderableModelMap extends HashMap<String, Object> {
	private static final long serialVersionUID = -3984465155412592192L;

	protected final SmartLogger log = getLogger(getClass());

	/**
	 * Is it allowed to override the key of the data model.
	 */
	protected final boolean overridable;

	public RenderableModelMap(boolean overridable) {
		this.overridable = overridable;
	}

	@Override
	public Object put(String key, Object value) {
		log.debug("Put rendering data model of key: {}, value: {}", key, value);
		if (!overridable) {
			isNull(super.put(key, value), CannotOverrideRenderingModelException.class,
					"Cannot override rendering data model of key: %s", key);
		}
		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		if (nonNull(m)) {
			m.forEach((key, value) -> put(key, value));
		}
	}

	@Override
	public Object putIfAbsent(String key, Object value) {
		return put(key, value);
	}

	@Override
	public Object remove(Object key) {
		log.debug("Removing rendering data model of key: {}", key);
		return super.remove(key);
	}

	@Override
	public boolean remove(Object key, Object value) {
		log.debug("Removing rendering data model of key: {}, value: {}", key, value);
		return super.remove(key, value);
	}

	@Override
	public RenderableModelMap clone() {
		return (RenderableModelMap) super.clone();
	}

}
