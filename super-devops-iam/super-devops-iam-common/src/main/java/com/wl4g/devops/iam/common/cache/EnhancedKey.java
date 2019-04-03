package com.wl4g.devops.iam.common.cache;

import java.io.Serializable;

import org.springframework.util.Assert;

import com.google.common.base.Charsets;

/**
 * Support for automatic expiration support, which can be used for login failure
 * retry restrictions
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @time 2017年6月20日
 * @since
 */
public class EnhancedKey {

	private String key;
	/**
	 * Effective when greater than or equal to 0
	 */
	private Integer expire; // 0 means never expired.
	private Class<?> valueClass;
	private Serializer serializer;
	private Deserializer deserializer;

	public EnhancedKey(Serializable key) {
		this(key, -1); // -1:not overdue
	}

	public EnhancedKey(byte[] key) {
		this.key = new String(key);
		this.expire = 0;
	}

	public EnhancedKey(Serializable key, Class<?> valueClass) {
		Assert.notNull(key, "'key' must not be null");
		Assert.notNull(valueClass, "'valueClass' must not be null");
		this.key = key.toString();
		this.valueClass = valueClass;
	}

	public EnhancedKey(Serializable key, long expire) {
		this(key, (int) (expire / 1000));
	}

	public EnhancedKey(Serializable key, int expire) {
		Assert.notNull(key, "'key' must not be null");
		this.key = key.toString();
		this.expire = expire;
	}

	public byte[] getKey(String prefix) {
		return toKeyBytes(prefix, key);
	}

	public byte[] getKey() {
		return getKey(null);
	}

	public boolean isExpire() {
		return (getExpire() != null && getExpire() >= 0);
	}

	public Integer getExpire() {
		return expire;
	}

	public Class<?> getValueClass() {
		return valueClass;
	}

	public EnhancedKey serializer(Serializer serializer) {
		Assert.notNull(serializer, "'serializer' must not be null");
		this.serializer = serializer;
		return this;
	}

	public Serializer getSerializer() {
		return serializer;
	}

	public EnhancedKey deserializer(Deserializer deserializer) {
		Assert.notNull(deserializer, "'deserializer' must not be null");
		this.deserializer = deserializer;
		return this;
	}

	public Deserializer getDeserializer() {
		return deserializer;
	}

	@Override
	public String toString() {
		return "EnhancedKey [" + (key != null ? "key=" + key + ", " : "") + (expire != null ? "expire=" + expire + ", " : "")
				+ (valueClass != null ? "valueClass=" + valueClass : "") + "]";
	}

	public static byte[] toKeyBytes(String prefix, String key) {
		Assert.notNull(key, "'key' must not be null");
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
		<T> byte[] serialize(T bean);
	}

	/**
	 * Deserializer
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年1月22日
	 * @since
	 */
	public static interface Deserializer {
		<T> T deserialize(byte[] data, Class<T> clazz);
	}

}
