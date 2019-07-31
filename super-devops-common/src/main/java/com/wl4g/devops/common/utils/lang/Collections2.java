package com.wl4g.devops.common.utils.lang;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public static <T> List<T> safeList(List<T> collection) {
		return isEmpty(collection) ? Collections.emptyList() : collection;
	}

	/**
	 * Safe collection set.
	 * 
	 * @param collection
	 * @return
	 */
	public static <T> Set<T> safeSet(Set<T> collection) {
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
