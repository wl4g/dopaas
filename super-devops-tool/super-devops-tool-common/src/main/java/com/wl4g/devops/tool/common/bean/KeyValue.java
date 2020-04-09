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
package com.wl4g.devops.tool.common.bean;

import java.io.Serializable;

/**
 * A generic and simple key value object storage bean.</br>
 * {@link KeyValue}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月28日 v1.0.0
 * @see
 */
public class KeyValue implements Serializable {

	private static final long serialVersionUID = -6651344183217701756L;

	/**
	 * Object key.
	 */
	private Object key;

	/**
	 * Object value.
	 */
	private Object value;

	public KeyValue() {
	}

	public KeyValue(Object key, Object value) {
		setKey(key);
		setValue(value);
	}

	@SuppressWarnings("unchecked")
	public <K> K getKey() {
		return (K) key;
	}

	public <K> KeyValue setKey(K key) {
		this.key = key;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <V> V getValue() {
		return (V) value;
	}

	public <V> KeyValue setValue(V value) {
		this.value = value;
		return this;
	}

	@Override
	public String toString() {
		return "KeyValue [key=" + key + ", value=" + value + "]";
	}

}
