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
import static com.wl4g.components.common.lang.Assert2.isTrue;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.nonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.wl4g.components.common.log.SmartLogger;

/**
 * {@link RenderableMapModel}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-20
 * @sine v1.0.0
 * @see
 */
public class RenderableMapModel implements Map<String, Object>, Cloneable {

	protected final SmartLogger log = getLogger(getClass());

	/**
	 * Is it allowed to override the key of the data model.
	 */
	protected final boolean overridable;

	/**
	 * Data model saved orig.
	 */
	protected final Map<String, Object> orig = new HashMap<>(16);

	/**
	 * Is it allowed to modifiy elements.
	 */
	protected AtomicBoolean isModifiable = new AtomicBoolean(true);

	/**
	 * Construtors.
	 * 
	 * @param overridable
	 */
	public RenderableMapModel(boolean overridable) {
		this.overridable = overridable;
	}

	@Override
	public Object put(String key, Object value) {
		checkModifiable();

		log.debug("Put rendering data model of key: {}, value: {}", key, value);
		if (!overridable) {
			isNull(orig.put(key, value), CannotOverrideRenderingModelException.class,
					"Cannot override rendering data model of key: %s", key);
		}
		return orig.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		checkModifiable();

		if (nonNull(m)) {
			m.forEach((key, value) -> put(key, value));
		}
	}

	@Override
	public Object putIfAbsent(String key, Object value) {
		checkModifiable();
		return put(key, value);
	}

	@Override
	public Object remove(Object key) {
		checkModifiable();

		log.debug("Removing rendering data model of key: {}", key);
		return orig.remove(key);
	}

	@Override
	public boolean remove(Object key, Object value) {
		checkModifiable();

		log.debug("Removing rendering data model of key: {}, value: {}", key, value);
		return orig.remove(key, value);
	}

	@Override
	public int size() {
		return orig.size();
	}

	@Override
	public boolean isEmpty() {
		return orig.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return orig.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return orig.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return orig.get(key);
	}

	@Override
	public void clear() {
		checkModifiable();
		this.orig.clear();
	}

	@Override
	public Set<String> keySet() {
		return orig.keySet();
	}

	@Override
	public Collection<Object> values() {
		return orig.values();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return orig.entrySet();
	}

	/**
	 * As converint to readonly map.
	 * 
	 * @return
	 */
	@Override
	public final RenderableMapModel clone() {
		RenderableMapModel clone = new RenderableMapModel(overridable);
		clone.orig.putAll(this.orig);
		return clone;
	}

	// /**
	// * Sets allowed modifiable to elements.
	// *
	// * @return
	// */
	// public final RenderableMapModel enableModifiable() {
	// this.isModifiable.set(true);
	// return this;
	// }

	/**
	 * Sets not allowed modifiable to elements.
	 * 
	 * @return
	 */
	public final RenderableMapModel disableModifiable() {
		this.isModifiable.set(false);
		return this;
	}

	/**
	 * Check whether modification of rendering model elements is allowed.
	 */
	protected final void checkModifiable() {
		isTrue(isModifiable.get(), () -> "Rendering data model modifiable state is currently disabled");
	}

}
