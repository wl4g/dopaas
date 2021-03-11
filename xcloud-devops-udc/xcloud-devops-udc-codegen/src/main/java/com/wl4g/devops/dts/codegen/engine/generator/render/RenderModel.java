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
package com.wl4g.devops.dts.codegen.engine.generator.render;

import static com.wl4g.component.common.lang.Assert2.hasTextOf;
import static com.wl4g.component.common.lang.Assert2.isNull;
import static com.wl4g.component.common.lang.Assert2.isTrue;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.nonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.validation.constraints.NotBlank;

import javax.annotation.Nullable;
import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.devops.dts.codegen.exception.CannotOverridePropertyException;

/**
 * {@link RenderModel}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-20
 * @sine v1.0.0
 * @see
 */
public class RenderModel implements Map<String, Object>, Cloneable {

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
	 * Is it readonly elements.
	 */
	protected AtomicBoolean readonly = new AtomicBoolean(false);

	/**
	 * Construtors.
	 * 
	 * @param overridable
	 */
	public RenderModel(boolean overridable) {
		this.overridable = overridable;
	}

	@Override
	public Object put(String key, Object value) {
		checkReadonly();

		log.debug("Put rendering data model of key: {}, value: {}", key, value);
		if (!overridable) {
			isNull(orig.put(key, value), CannotOverridePropertyException.class, "Cannot override rendering data model of key: %s",
					key);
		}
		return orig.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		checkReadonly();

		if (nonNull(m)) {
			m.forEach((key, value) -> put(key, value));
		}
	}

	@Override
	public Object putIfAbsent(String key, Object value) {
		checkReadonly();
		return put(key, value);
	}

	@Override
	public Object remove(Object key) {
		checkReadonly();

		log.debug("Removing rendering data model of key: {}", key);
		return orig.remove(key);
	}

	@Override
	public boolean remove(Object key, Object value) {
		checkReadonly();

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
		checkReadonly();
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

	@Override
	public String toString() {
		return getClass().getSimpleName().concat("-")
				.concat("[overridable=" + overridable + ", readonly=" + readonly + ", orig=" + orig + "]");
	}

	//
	// --- Funcation's. ---
	//

	/**
	 * Gets model attribute of key.
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getElement(final @NotBlank String key) {
		return (T) get(hasTextOf(key, "key"));
	}

	/**
	 * Gets model attribute of key.
	 * 
	 * @param <T>
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getElement(final @NotBlank String key, final @Nullable Object defaultValue) {
		return (T) getOrDefault(hasTextOf(key, "key"), defaultValue);
	}

	/**
	 * As converint to readonly map.
	 * 
	 * @return
	 */
	@Override
	public final RenderModel clone() {
		RenderModel clone = new RenderModel(overridable);
		clone.orig.putAll(this.orig);
		clone.readonly.set(false);
		return clone;
	}

	/**
	 * Sets not allowed modifiable to elements.
	 * 
	 * @return
	 */
	public final RenderModel readonly() {
		this.readonly.set(true);
		return this;
	}

	/**
	 * Check whether modification of rendering model elements is allowed.
	 */
	protected final void checkReadonly() {
		isTrue(!readonly.get(), () -> "Rendering data model modifiable state is currently disabled");
	}

}