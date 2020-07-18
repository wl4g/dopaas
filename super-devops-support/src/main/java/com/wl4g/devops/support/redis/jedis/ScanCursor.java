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
package com.wl4g.devops.support.redis.jedis;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static com.wl4g.devops.components.tools.common.serialize.ProtostuffUtils.*;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Charsets;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

/**
 * Redis client agnostic {@link CursorWrapper} implementation continuously
 * loading additional results from Redis server until reaching its starting
 * point {@code zero}. <br />
 * <strong>Note:</strong> Please note that the {@link ScanCursor} has to be
 * initialized ({@link #start()} prior to usage.
 * 
 * <font color=red> Note: redis scan is reverse binary iteration, not sequential
 * pointer iteration. </font> See: <a href=
 * "https://www.jianshu.com/p/2f31881bf847">https://www.jianshu.com/p/2f31881bf847</a>
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年11月9日
 * @since
 * @param <E>
 */
public abstract class ScanCursor<E> implements Iterator<E> {
	final public static String REPLICATION = "Replication";
	final public static String ROLE_MASTER = "role:master";
	final public static ScanParams NONE_PARAMS = new ScanParams();

	final private Logger log = LoggerFactory.getLogger(getClass());
	final private ScanParams params;
	final private Class<?> valueType;
	final private CompositeJedisOperatorsAdapter adapter;
	final private List<JedisPool> nodePools;

	private CursorWrapper cursor;
	private CursorState state;
	private ScanIterable<byte[]> iter; // Values

	/**
	 * Crates new {@link ScanCursor} with {@code id=0} and
	 * {@link ScanParams#NONE}
	 */
	public ScanCursor(CompositeJedisOperatorsAdapter adapter, Class<?> valueType) {
		this(adapter, valueType, NONE_PARAMS);
	}

	/**
	 * Crates new {@link ScanCursor} with {@code id=0}.
	 * 
	 * @param params
	 */
	public ScanCursor(CompositeJedisOperatorsAdapter adapter, ScanParams params) {
		this(adapter, new CursorWrapper(), null, params);
	}

	/**
	 * Crates new {@link ScanCursor} with {@code id=0}.
	 * 
	 * @param params
	 */
	public ScanCursor(CompositeJedisOperatorsAdapter adapter, Class<?> valueType, ScanParams params) {
		this(adapter, new CursorWrapper(), valueType, params);
	}

	/**
	 * Crates new {@link ScanCursor} with {@link ScanParams#NONE}
	 * 
	 * @param cursor
	 */
	public ScanCursor(CompositeJedisOperatorsAdapter adapter, CursorWrapper cursor, Class<?> valueType) {
		this(adapter, cursor, valueType, NONE_PARAMS);
	}

	/**
	 * Crates new {@link ScanCursor}
	 * 
	 * @param adapter
	 *            JedisCluster
	 * @param cursor
	 * @param param
	 *            Defaulted to {@link ScanParams#NONE} if nulled.
	 */
	public ScanCursor(CompositeJedisOperatorsAdapter adapter, CursorWrapper cursor, Class<?> valueType, ScanParams param) {
		notNull(adapter, "jedisCluster must not be null");
		if (isNull(valueType)) {
			valueType = ResolvableType.forClass(getClass()).getSuperType().getGeneric(0).resolve();
		}
		this.valueType = valueType;
		notNull(valueType, "No scan value java type is specified. Use constructs that can set value java type.");
		this.adapter = adapter;
		this.params = param != null ? param : NONE_PARAMS;
		this.nodePools = adapter.getClusterNodes().values().stream().map(n -> n).collect(toList());
		this.state = CursorState.READY;
		this.cursor = cursor;
		this.iter = new ScanIterable<>(cursor, emptyList());
		CursorWrapper.validate(cursor);
		notEmptyOf(nodePools, "Jedis nodes is empty.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.core.Cursor#getCursorId()
	 */
	public CursorWrapper getCursor() {
		Assert.state(nonNull(cursor), "Jedis scanner cursor is null.");
		return cursor;
	}

	/**
	 * Is ready
	 * 
	 * @return
	 */
	public final boolean isReady() {
		return state == CursorState.READY;
	}

	/**
	 * Is open
	 * 
	 * @return
	 */
	public final boolean isOpen() {
		return state == CursorState.OPEN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.core.Cursor#isClosed()
	 */
	public boolean isFinished() {
		return state == CursorState.CLOSED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	public synchronized final void finished() {
		if (!isFinished()) {
			try {
				doFinished();
			} finally {
				state = CursorState.CLOSED;
			}
		}
	}

	/**
	 * Customization hook for cleaning up resources on when calling
	 * {@link #finished()}.
	 */
	protected synchronized void doFinished() {
	}

	/**
	 * Initialize the {@link CursorWrapper} prior to usage.
	 */
	@SuppressWarnings("unchecked")
	public synchronized final <T extends ScanCursor<E>> T open() {
		if (isOpen()) {
			log.debug("Cursor already " + state + ", no need (re)open it.");
			return (T) this;
		}

		state = CursorState.OPEN;
		nextScan();
		return (T) this;
	}

	/**
	 * Scan keys.
	 * 
	 * @return
	 */
	public List<byte[]> keys() {
		return iter.getItems();
	}

	/**
	 * Scan keys as string.
	 * 
	 * @return
	 */
	public List<String> keysAsString() {
		return iter.getItems().stream().map(e -> new String(e)).collect(toList());
	}

	/**
	 * Mutual exclusion with the {@link ScanCursor#next()} method (only one can
	 * be used)
	 * 
	 * @throws IOException
	 * 
	 * @see ScanCursor#next()
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<E> readValues() throws IOException {
		try {
			// Not iterated yet?
			if (isEmpty(iter.getItems())) {
				List<E> list = new ArrayList<>(64);
				while (hasNext()) {
					list.add(next());
				}
				return list;
			}

			// Iterated yet?
			return (List<E>) iter.getItems().stream().map(key -> {
				return deserialize(adapter.get(key), valueType);
			}).collect(toList());
		} finally {
			iter.getItems().clear();
		}
	}

	/**
	 * Fetch the next value from the underlying {@link Iterable}. mutual
	 * exclusion with {@link ScanCursor#readValues()} method (only one can be
	 * used)
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public synchronized E next() {
		assertCursorIsOpen();

		if (!hasNext()) {
			throw new NoSuchElementException("No more elements available for cursor " + getCursor() + ".");
		}

		return (E) deserialize(adapter.get(iter.iterator().next()), valueType);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public synchronized boolean hasNext() {
		assertCursorIsOpen();

		// If the current 'iter' is fully traversed, you need to check whether
		// the next node has data.
		while (!iter.iterator().hasNext() && CursorState.FINISHED != state) {
			nextScan();
		}

		return (iter.iterator().hasNext() || !checkScanCompleted());
	}

	/**
	 * Execute scan by cursor index.
	 */
	protected synchronized void nextScan() {
		// Select a node
		try (Jedis jedis = nodePools.get(getCursor().getSelectionPos()).getResource()) {
			// Traverse only the primary node
			if (containsIgnoreCase(jedis.info(REPLICATION), ROLE_MASTER)) {
				processResult(doScanNode(jedis));
			} else {
				nextTo();
			}
		}
	}

	/**
	 * Performs the actual scan command using the native client implementation.
	 * The given {@literal options} are never {@code null}.
	 * 
	 * @param jedis
	 * @return
	 */
	protected synchronized ScanIterable<byte[]> doScanNode(Jedis jedis) {
		ScanResult<byte[]> res = jedis.scan(getCursor().getCursorByteArray(), params);

		List<byte[]> items = res.getResult();
		if (isEmpty(items)) {
			items = emptyList();
		}

		return new ScanIterable<byte[]>(cursor.setCursor(res.getStringCursor()), items);
	}

	/**
	 * After process result
	 * 
	 * @param res
	 */
	private synchronized void processResult(ScanIterable<byte[]> res) {
		this.iter = res;
		this.cursor = res.getCursor();

		// The current node has completed traversal
		if (checkScanCompleted()) { // End?
			nextTo(); // Select to next node.
		}
	}

	/**
	 * Selection next node
	 */
	private synchronized void nextTo() {
		cursor.nextSelectiveNode(); // Next new node.

		// Safe check fully scanned.
		if (checkScanNodesCompleted()) {
			log.debug(format("Scanned all jedis nodes. size: %s", nodePools.size()));
			state = CursorState.FINISHED;
			cursor.setSelectionPos(nodePools.size() - 1);
		}
	}

	/**
	 * Check that currently node finished.
	 * 
	 * @return
	 */
	private boolean checkScanCompleted() {
		return trimToEmpty(getCursor().getCursor()).equalsIgnoreCase("0");
	}

	/**
	 * Check that all nodes are currently fully scanned.
	 * 
	 * @return
	 */
	private boolean checkScanNodesCompleted() {
		return cursor.getSelectionPos() >= nodePools.size();
	}

	/**
	 * Assertion cursor is open
	 */
	private synchronized void assertCursorIsOpen() {
		if (isReady() || isFinished()) {
			throw new RuntimeException("Cannot access closed cursor. Did you forget to call open()?");
		}
	}

	/**
	 * Cursor state
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年4月1日
	 * @since
	 */
	private enum CursorState {
		READY, OPEN, FINISHED, CLOSED;
	}

	/**
	 * Scan cursor wrapper.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月4日
	 * @since
	 */
	final public static class CursorWrapper implements Serializable {
		private static final long serialVersionUID = 4547949424670284416L;

		/** Cursor end spec. */
		private transient static final String STARTEND = "0";

		/** Scan node position */
		private Integer selectionPos = 0;

		/** Scan node cursor value */
		private String cursor = STARTEND;

		public CursorWrapper() {
			super();
		}

		public CursorWrapper(Integer selectionPos, String cursor) {
			setSelectionPos(selectionPos);
			setCursor(cursor);
		}

		@JsonIgnore
		public Integer getSelectionPos() {
			return selectionPos;
		}

		public CursorWrapper setSelectionPos(Integer selectionPos) {
			notNull(selectionPos, "Jedis scan selectionPos must not be empty.");
			notNull(selectionPos >= 0, "Jedis scan selectionPos must >=0.");
			this.selectionPos = selectionPos;
			return this;
		}

		@JsonIgnore
		public String getCursor() {
			return cursor;
		}

		public CursorWrapper setCursor(String cursor) {
			hasText(cursor, "Jedis scan cursor must not be empty.");
			this.cursor = cursor;
			return this;
		}

		@Override
		public String toString() {
			return getCursorString();
		}

		@JsonIgnore
		public void nextSelectiveNode() {
			++this.selectionPos; // Reset cursor.
			setCursor("0");// Reset cursor.
		}

		@JsonIgnore
		public byte[] getCursorByteArray() {
			return cursor.getBytes(Charsets.UTF_8);
		}

		/**
		 * Check has hext records.
		 * 
		 * @return
		 */
		public boolean getHasNext() {
			return !endsWithIgnoreCase(getCursor(), STARTEND);
		}

		/**
		 * As cursor to string.
		 * 
		 * @return
		 */
		public String getCursorString() {
			return getCursor() + "@" + getSelectionPos();
		}

		/**
		 * Parse cursor string
		 * 
		 * @param cursorString
		 * @return
		 */
		public static CursorWrapper parse(String cursorString) {
			hasText(cursorString, "Jedis scan cursorString must not be empty.");
			String errmsg = String.format("Invalid cursorString with %s", cursorString);
			isTrue(cursorString.contains("@"), errmsg);
			String[] parts = split(trimToEmpty(cursorString), "@");
			isTrue(parts.length >= 2, errmsg);
			return new CursorWrapper(Integer.parseInt(parts[1]), parts[0]);
		}

		/**
		 * Validation for {@link CursorWrapper}
		 * 
		 * @param cursor
		 */
		public static void validate(CursorWrapper cursor) {
			notNull(cursor, "Jedis scan cursor must not be null.");
			hasText(cursor.getCursor(), "Jedis scan cursor value must not be empty.");
			notNull(cursor.getSelectionPos(), "Jedis scan selectionPos must not be empty.");
			notNull(cursor.getSelectionPos() >= 0, "Jedis scan selectionPos must >=0.");
		}

	}

	/**
	 * {@link ScanIterable} holds the values contained in Redis
	 * {@literal Multibulk reply} on exectuting {@literal SCAN} command.
	 * 
	 * @author Christoph Strobl
	 * @since 1.4
	 */
	final static class ScanIterable<K> implements Iterable<K> {

		final private CursorWrapper cursor;
		final private List<K> items;
		final private Iterator<K> iter;

		/**
		 * Scan iterable
		 */
		public ScanIterable() {
			this(new CursorWrapper());
		}

		/**
		 * Scan iterable
		 * 
		 * @param cursor
		 * @param items
		 */
		public ScanIterable(CursorWrapper cursor) {
			this(cursor, Collections.emptyList());
		}

		/**
		 * Scan iterable
		 * 
		 * @param cursor
		 * @param items
		 */
		public ScanIterable(CursorWrapper cursor, List<K> items) {
			this.cursor = cursor;
			this.items = (isEmpty(items) ? emptyList() : new ArrayList<K>(items));
			this.iter = this.items.iterator();
		}

		/**
		 * The cursor id to be used for subsequent requests.
		 * 
		 * @return
		 */
		public CursorWrapper getCursor() {
			return cursor;
		}

		/**
		 * Get the items returned.
		 * 
		 * @return
		 */
		public List<K> getItems() {
			return items;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<K> iterator() {
			return iter;
		}

	}

}