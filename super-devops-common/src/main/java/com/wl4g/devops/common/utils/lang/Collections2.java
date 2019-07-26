package com.wl4g.devops.common.utils.lang;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Collection2 utility.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月26日
 * @since
 */
public abstract class Collections2 {

	/**
	 * Safe collection list.
	 * 
	 * @param collection
	 * @return
	 */
	public static <T> Collection<T> safeList(Collection<T> collection) {
		return isEmpty(collection) ? Collections.emptyList() : collection;
	}

	/**
	 * Safe collection set.
	 * 
	 * @param collection
	 * @return
	 */
	public static <T> Collection<T> safeSet(Collection<T> collection) {
		return isEmpty(collection) ? Collections.emptySet() : collection;
	}

	/**
	 * Safe collection map.
	 * 
	 * @param collection
	 * @return
	 */
	public static <K, V> Map<K, V> safeMap(Map<K, V> collection) {
		return isEmpty(collection) ? Collections.emptyMap() : collection;
	}

}
