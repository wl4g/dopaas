package com.wl4g.devops.common.utils.lang;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
	 * Is empty array.
	 * 
	 * @param collection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean isEmptyArray(T... array) {
		return null == array || array.length <= 0;
	}

	/**
	 * Safe collection list.
	 * 
	 * @param collection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] safeArray(Class<T> componentType, T... array) {
		return null == array ? (T[]) Array.newInstance(componentType, 0) : array;
	}

	/**
	 * Ensure that the default is at least an ArrayList instance (when the
	 * parameter is empty)
	 * 
	 * @param list
	 * @return
	 */
	public static <T> List<T> ensureList(List<T> list) {
		return isEmpty(list) ? new ArrayList<T>() : list;
	}

	/**
	 * Ensure that the default is at least an ArrayList instance (when the
	 * parameter is empty)
	 * 
	 * @param list
	 * @param fallback
	 * @return
	 */
	public static <T> List<T> ensureList(List<T> list, List<T> fallback) {
		return isEmpty(list) ? fallback : list;
	}

	/**
	 * Safe collection list.
	 * 
	 * @param list
	 * @return
	 */
	public static <T> List<T> safeList(List<T> list) {
		return isEmpty(list) ? Collections.emptyList() : list;
	}

	/**
	 * Safe array to list.
	 * 
	 * @param array
	 * @return
	 */
	public static <T> List<T> safeToList(Class<T> componentType, T[] array) {
		return Arrays.asList(safeArray(componentType, array));
	}

	/**
	 * Safe collection set.
	 * 
	 * @param set
	 * @return
	 */
	public static <T> Set<T> safeSet(Set<T> set) {
		return isEmpty(set) ? Collections.emptySet() : set;
	}

	/**
	 * Safe collection map.
	 * 
	 * @param map
	 * @return
	 */
	public static <K, V> Map<K, V> safeMap(Map<K, V> map) {
		return isEmpty(map) ? Collections.emptyMap() : map;
	}

}
