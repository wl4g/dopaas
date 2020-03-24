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
package com.wl4g.devops.tool.common.collection;

import static java.util.Collections.synchronizedList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

import com.wl4g.devops.tool.common.lang.Assert2;

/**
 * The {@link List} is applicable to the registration of framework instances.
 * Its feature is that after the registration is successful, it cannot be
 * modified.
 * 
 * Note: it uses synchronous lock to control thread safety. It is not applicable
 * to high concurrency scenarios, but more applicable to registration
 * configuration when starting server.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月1日
 * @since
 */
public class RegisteredUnmodifiableList<E> implements List<E> {

	/**
	 * Read only list.
	 */
	final private List<E> readOnlyList;

	/**
	 * One-time modifiable map status marker.
	 */
	final private AtomicBoolean modified = new AtomicBoolean(false);

	public RegisteredUnmodifiableList(List<E> readOnlyList) {
		Assert2.state(null != readOnlyList, "Once modifiable read only list must not be null.");
		this.readOnlyList = synchronizedList(readOnlyList);
	}

	@Override
	public int size() {
		return readOnlyList.size();
	}

	@Override
	public boolean isEmpty() {
		return readOnlyList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return readOnlyList.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return readOnlyList.iterator();
	}

	@Override
	public Object[] toArray() {
		return readOnlyList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return readOnlyList.toArray(a);
	}

	@Override
	public boolean add(E e) {
		Assert2.notNull(e, "Once modifiable final list value must not be null.");
		if (modified.compareAndSet(false, true)) {
			return readOnlyList.add(e);
		}
		throw new UnsupportedOperationException("A modifiable list does not support multiple modifications.");
	}

	@Override
	public boolean remove(Object o) {
		Assert2.notNull(o, "Once modifiable final list value must not be null.");
		if (modified.compareAndSet(false, true)) {
			return readOnlyList.remove(o);
		}
		throw new UnsupportedOperationException("A modifiable list does not support multiple modifications.");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return readOnlyList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		Assert2.notNull(c, "Once modifiable final list value must not be null.");
		if (modified.compareAndSet(false, true)) {
			return readOnlyList.addAll(c);
		}
		throw new UnsupportedOperationException("A modifiable list does not support multiple modifications.");
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		Assert2.notNull(c, "Once modifiable final list value must not be null.");
		if (modified.compareAndSet(false, true)) {
			return readOnlyList.addAll(index, c);
		}
		throw new UnsupportedOperationException("A modifiable list does not support multiple modifications.");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Assert2.notNull(c, "Once modifiable final list value must not be null.");
		if (modified.compareAndSet(false, true)) {
			return readOnlyList.remove(c);
		}
		throw new UnsupportedOperationException("A modifiable list does not support multiple modifications.");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Assert2.notNull(c, "Once modifiable final list value must not be null.");
		if (modified.compareAndSet(false, true)) {
			return readOnlyList.retainAll(c);
		}
		throw new UnsupportedOperationException("A modifiable list does not support multiple modifications.");
	}

	@Override
	public void clear() {
		readOnlyList.clear();
	}

	@Override
	public E get(int index) {
		return readOnlyList.get(index);
	}

	@Override
	public E set(int index, E element) {
		Assert2.notNull(index, "Once modifiable final list index must not be null.");
		Assert2.notNull(element, "Once modifiable final list element must not be null.");
		if (modified.compareAndSet(false, true)) {
			return readOnlyList.set(index, element);
		}
		throw new UnsupportedOperationException("A modifiable list does not support multiple modifications.");
	}

	@Override
	public void add(int index, E element) {
		Assert2.notNull(index, "Once modifiable final list index must not be null.");
		Assert2.notNull(element, "Once modifiable final list element must not be null.");
		if (modified.compareAndSet(false, true)) {
			readOnlyList.add(index, element);
		}
		throw new UnsupportedOperationException("A modifiable list does not support multiple modifications.");
	}

	@Override
	public E remove(int index) {
		if (modified.compareAndSet(false, true)) {
			return readOnlyList.remove(index);
		}
		throw new UnsupportedOperationException("A modifiable list does not support multiple modifications.");
	}

	@Override
	public int indexOf(Object o) {
		return readOnlyList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return readOnlyList.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return readOnlyList.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return readOnlyList.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return readOnlyList.subList(fromIndex, toIndex);
	}

}