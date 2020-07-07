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
package com.wl4g.devops.common.utils.bean;

import org.springframework.cglib.beans.BeanCopier;
import static org.springframework.cglib.beans.BeanCopier.create;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * {@link BeanCopierUtils}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年7月8日 v1.0.0
 * @see
 */
public abstract class BeanCopierUtils {

	/***
	 * 通过类复制属性（同一个类其实就是克隆自己） 属性相同才能复制
	 * 
	 * @param source
	 *            需要复制的对象
	 * @param target
	 *            目标类
	 * @param <O>
	 * @param <T>
	 * @return
	 */
	public static <O, T> T mapper(O source, Class<T> target) {
		return baseMapper(source, target);
	}

	/**
	 * 通过类复制属性（同一个类其实就是克隆自己） 属性相同才能复制
	 * 
	 * @param source
	 *            需要复制的对象
	 * @param target
	 *            目标类
	 * @param action
	 *            支持lambda操作
	 * @param <O>
	 * @param <T>
	 * @return
	 */
	public static <O, T> T mapper(O source, Class<T> target, Consumer<T> action) {
		T instance = mapper(source, target);
		action.accept(instance);
		return instance;
	}

	/**
	 * 通过类复制属性（同一个类其实就是克隆自己） 属性相同才能复制
	 * 
	 * @param source
	 *            需要复制的对象
	 * @param target
	 *            目标类
	 * @param action
	 *            支持lambda操作
	 * @param <O>
	 * @param <T>
	 * @return
	 */
	public static <O, T> T mapper(O source, Class<T> target, UnaryOperator<T> action) {
		T instance = mapper(source, target);
		return action.apply(instance);
	}

	/***
	 * 通过对象复制属性（同一个类其实就是克隆自己） 属性相同才能复制
	 * 
	 * @param source
	 * @param target
	 * @param <O>
	 * @param <T>
	 * @return
	 */
	public static <O, T> T mapperObject(O source, T target) {
		String baseKey = generateKey(source.getClass(), target.getClass());
		BeanCopier copier = null;
		if (!mapCaches.containsKey(baseKey)) {
			mapCaches.put(baseKey, (copier = create(source.getClass(), target.getClass(), false)));
		} else {
			copier = mapCaches.get(baseKey);
		}
		copier.copy(source, target, null);
		return target;
	}

	public static <O, T> T mapperObject(O source, T target, Consumer<T> action) {
		mapperObject(source, target);
		action.accept(target);
		return target;
	}

	public static <O, T> T mapperObject(O source, T target, UnaryOperator<T> action) {
		mapperObject(source, target);
		return action.apply(target);
	}

	private static <O, T> T baseMapper(O source, Class<T> target) {
		String baseKey = generateKey(source.getClass(), target);
		BeanCopier copier;
		if (!mapCaches.containsKey(baseKey)) {
			copier = BeanCopier.create(source.getClass(), target, false);
			mapCaches.put(baseKey, copier);
		} else {
			copier = mapCaches.get(baseKey);
		}
		T instance = null;
		try {
			// The object to be copied must have a parameterless constructor
			instance = target.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		copier.copy(source, instance, null);
		return instance;
	}

	/**
	 * Generate Key
	 * 
	 * @param class1
	 * @param class2
	 * @return
	 */
	private static String generateKey(Class<?> class1, Class<?> class2) {
		return class1.toString() + class2.toString();
	}

	/**
	 * Use cache to improve efficiency
	 */
	final private static ConcurrentHashMap<String, BeanCopier> mapCaches = new ConcurrentHashMap<>();

}
