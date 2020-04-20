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

import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static java.util.Collections.synchronizedList;
import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * This is a list implementation suitable for registration, which can avoid
 * duplication. See setup functions. When the element of add() is repeated, you
 * can perform the override or skip logic according to the configuration. Note:
 * it uses synchronous lock to control thread safety. It is not applicable to
 * high concurrency scenarios, but more applicable to registration configuration
 * when starting server.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月1日
 * @since
 * @see {@link List}
 * @see {@link Set}
 */
public class RegisteredSetList<E> implements List<E> {

	/**
	 * Origin list object.
	 */
	final private List<E> orig;

	/**
	 * Skip or overwrite when adding duplicate elements, default: false
	 */
	final private boolean overlay;

	public RegisteredSetList(List<E> orig) {
		this(orig, false);
	}

	public RegisteredSetList(List<E> orig, boolean overlay) {
		notNullOf(orig, "origList");
		this.orig = synchronizedList(orig);
		this.overlay = overlay;
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
	public boolean contains(Object o) {
		return orig.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return orig.iterator();
	}

	@Override
	public Object[] toArray() {
		return orig.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return orig.toArray(a);
	}

	@Override
	public boolean add(E e) {
		if (isNull(e))
			return false;

		if (orig.contains(e)) {
			if (overlay) {
				if (!orig.remove(e)) { // Remove failed?
					if (!orig.isEmpty()) // Attempt remove by last index.
						orig.remove(orig.size() - 1);
				}
				return orig.add(e);
			}
			// Skip add
			return true;
		}
		return orig.add(e);
	}

	@Override
	public boolean remove(Object o) {
		notNullOf(o, "removeElementValue");
		return orig.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return orig.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		notNullOf(c, "addElementValues");
		boolean modified = false;
		for (E e : c) {
			add(e);
			modified = true;
		}
		return modified;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		orig.clear();
	}

	@Override
	public E get(int index) {
		return orig.get(index);
	}

	@Override
	public E set(int index, E element) {
		if (isNull(element))
			return null;

		if (orig.contains(element)) {
			if (overlay) {
				orig.remove((Object) element); // Remove duplicate
			}
			// Skip set
			return element;
		}

		// if (index < orig.size()) {
		// // Tail sub list
		// List<E> tailSubList = new ArrayList<>();
		// for (int i = 0; i < orig.size(); i++) {
		// if (i >= index) {
		// tailSubList.add(orig.get(i));
		// }
		// }
		// // Sets insert element
		// E result = orig.set(index, element);
		// // Cover tail subList append to origList
		// int startCoverIndex = index + 1;
		// for (int i = 0; i < tailSubList.size(); i++) {
		// int coverIndex = startCoverIndex + i;
		// if (coverIndex >= orig.size()) {
		// orig.add(tailSubList.get(i));
		// } else {
		// orig.set(coverIndex, tailSubList.get(i));
		// }
		// }
		// return result;
		// }
		//
		// return orig.set(index, element);

		return orig.add(element) ? element : null;
	}

	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E remove(int index) {
		notNullOf(index, "removeElementIndex");
		return orig.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return orig.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return orig.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return orig.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return orig.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return orig.subList(fromIndex, toIndex);
	}

	@Override
	public String toString() {
		Iterator<E> it = iterator();
		if (!it.hasNext())
			return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (;;) {
			E e = it.next();
			sb.append(e == this ? "(this Collection)" : e);
			if (!it.hasNext())
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}

}