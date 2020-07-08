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
package com.wl4g.devops.iam.common.cache;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static com.wl4g.devops.components.tools.common.lang.TypeConverts.safeLongToInt;
import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;
import com.wl4g.devops.components.tools.common.serialize.ProtostuffUtils;
import com.wl4g.devops.components.tools.common.serialize.JdkSerializeUtils;

/**
 * Support for automatic expiration support, which can be used for login failure
 * retry restrictions
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @time 2017年6月20日
 * @since
 */
public class CacheKey implements Serializable {
	private static final long serialVersionUID = 3452072504066624385L;

	private String key;
	private Integer expire; // 0 means never expired.
	private Class<?> valueClass;
	private Serializer serializer = PB_SERIALIZER;

	public CacheKey(Serializable key) {
		this(key, -1); // -1:not overdue
	}

	public CacheKey(byte[] key) {
		this.key = new String(key);
		this.expire = 0;
	}

	public CacheKey(Serializable key, Class<?> valueClass) {
		notNull(key, "'key' must not be null");
		notNull(valueClass, "'valueClass' must not be null");
		this.key = getRealTypeKeyToString(key);
		this.valueClass = valueClass;
	}

	public CacheKey(Serializable key, long expireMs) {
		this(key, safeLongToInt(MILLISECONDS.toSeconds(expireMs)));
	}

	public CacheKey(Serializable key, int expireSec) {
		notNull(key, "'key' must not be null");
		this.key = getRealTypeKeyToString(key);
		this.expire = expireSec;
	}

	public byte[] getKey(String prefix) {
		return toKeyBytes(prefix, key);
	}

	public byte[] getKey() {
		return getKey(null);
	}

	public boolean hasExpire() {
		return (!isNull(getExpire()) && getExpire() >= 0);
	}

	public Integer getExpire() {
		return expire;
	}

	public Long getExpireMs() {
		if (hasExpire()) {
			return TimeUnit.SECONDS.toMillis(getExpire());
		}
		return null;
	}

	public Class<?> getValueClass() {
		return valueClass;
	}

	public CacheKey serializer(Serializer serializer) {
		notNull(serializer, "'serializer' must not be null");
		this.serializer = serializer;
		return this;
	}

	public Serializer getSerializer() {
		return serializer;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + (key != null ? "key=" + key + ", " : "")
				+ (expire != null ? "expire=" + expire + ", " : "") + (valueClass != null ? "valueClass=" + valueClass : "")
				+ "]";
	}

	/**
	 * Gets real type key to string.
	 * 
	 * @param key
	 * @return
	 */
	public static String getRealTypeKeyToString(Serializable key) {
		if (key instanceof byte[]) {
			return new String((byte[]) key);
		} else if (key instanceof Byte[]) {
			Byte[] _key = (Byte[]) key;
			byte[] __key = new byte[_key.length];
			for (int i = 0; i < _key.length; i++) {
				__key[i] = _key[i];
			}
			return new String(__key);
		}
		return key.toString();
	}

	/**
	 * To key bytes.
	 * 
	 * @param key
	 * @return
	 */
	public static byte[] toKeyBytes(String key) {
		return toKeyBytes(null, key);
	}

	/**
	 * To key bytes.
	 * 
	 * @param prefix
	 * @param key
	 * @return
	 */
	public static byte[] toKeyBytes(String prefix, String key) {
		notNull(key, "'key' must not be null");
		return ((prefix == null ? "" : prefix) + key).getBytes(Charsets.UTF_8);
	}

	/**
	 * Serializer
	 *
	 * @author wangl.sir
	 * @version v1.0 2019年1月22日
	 * @since
	 */
	public static interface Serializer {

		/**
		 * Serialization
		 * 
		 * @param bean
		 * @return
		 */
		<T> byte[] serialize(T bean);

		/**
		 * Deserialization
		 * 
		 * @param data
		 * @param clazz
		 * @return
		 */
		<T> T deserialize(byte[] data, Class<T> clazz);
	}

	/**
	 * Jdk object serializer
	 */
	final public static Serializer JDK_SERIALIZER = new Serializer() {
		@Override
		public <T> byte[] serialize(T bean) {
			return JdkSerializeUtils.serialize(bean);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T deserialize(byte[] data, Class<T> clazz) {
			return (T) JdkSerializeUtils.unserialize(data);
		}
	};

	/**
	 * Protostuff object serializer
	 */
	final public static Serializer PB_SERIALIZER = new Serializer() {
		@Override
		public <T> byte[] serialize(T bean) {
			return ProtostuffUtils.serialize(bean);
		}

		@Override
		public <T> T deserialize(byte[] data, Class<T> clazz) {
			return ProtostuffUtils.deserialize(data, clazz);
		}
	};

}