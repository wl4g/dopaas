/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.support.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.core.ResolvableType;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Charsets;

import static com.wl4g.devops.common.utils.serialize.ProtostuffUtils.*;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

/**
 * Redis client agnostic {@link Cursor} implementation continuously loading
 * additional results from Redis server until reaching its starting point
 * {@code zero}. <br />
 * <strong>Note:</strong> Please note that the {@link ScanCursor} has to be
 * initialized ({@link #open()} prior to usage.
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

	final private ScanParams params;
	final private Class<?> ofType;
	final private JedisCluster jdsCluster;
	final private List<JedisPool> jdsPools;

	private int selectionPos;
	private CursorState state;
	private String cursorId;
	private ScanIterable<byte[]> iter;

	/**
	 * Crates new {@link ScanCursor} with {@code id=0} and
	 * {@link ScanParams#NONE}
	 */
	public ScanCursor(JedisCluster cluster) {
		this(cluster, NONE_PARAMS);
	}

	/**
	 * Crates new {@link ScanCursor} with {@code id=0}.
	 * 
	 * @param params
	 */
	public ScanCursor(JedisCluster cluster, ScanParams params) {
		this(cluster, "0", params);
	}

	/**
	 * Crates new {@link ScanCursor} with {@link ScanParams#NONE}
	 * 
	 * @param cursorId
	 */
	public ScanCursor(JedisCluster cluster, String cursorId) {
		this(cluster, cursorId, NONE_PARAMS);
	}

	/**
	 * Crates new {@link ScanCursor}
	 * 
	 * @param jdCluster
	 *            JedisCluster
	 * @param cursorId
	 * @param param
	 *            Defaulted to {@link ScanParams#NONE} if nulled.
	 */
	public ScanCursor(JedisCluster jdCluster, String cursorId, ScanParams param) {
		this.params = param != null ? param : NONE_PARAMS;
		this.ofType = ResolvableType.forClass(getClass()).getSuperType().getGeneric(0).resolve();
		this.jdsCluster = jdCluster;
		this.jdsPools = jdCluster.getClusterNodes().values().stream().collect(Collectors.toCollection(ArrayList::new));
		this.selectionPos = 0;
		this.state = CursorState.READY;
		this.cursorId = cursorId;
		this.iter = new ScanIterable<>(cursorId, Collections.emptyList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.core.Cursor#getCursorId()
	 */
	public String getCursorId() {
		return cursorId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.core.Cursor#getPosition()
	 */
	public int getSelectionPos() {
		return selectionPos;
	}

	/**
	 * Mutual exclusion with the {@link ScanCursor#next()} method (only one can
	 * be used)
	 * 
	 * @see ScanCursor#next()
	 */
	@SuppressWarnings("unchecked")
	public List<E> readItem() {
		try {
			return (List<E>) iter.getItems().stream().map(key -> deserialize(jdsCluster.get(key), ofType))
					.collect(Collectors.toList());
		} finally {
			iter.getItems().clear();
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public synchronized boolean hasNext() {
		assertCursorIsOpen();

		while (!iter.iterator().hasNext() && CursorState.FINISHED != state) {
			scan(getCursorId());
		}

		return (iter.iterator().hasNext() || Long.valueOf(getCursorId()) > 0);
	}

	/**
	 * Fetch the next item from the underlying {@link Iterable}. mutual
	 * exclusion with {@link ScanCursor#readItem()} method (only one can be
	 * used)
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public synchronized E next() {
		assertCursorIsOpen();

		if (!hasNext()) {
			throw new NoSuchElementException("No more elements available for cursor " + getCursorId() + ".");
		}

		return (E) deserialize(jdsCluster.get(iter.iterator().next()), ofType);
	}

	/**
	 * Initialize the {@link Cursor} prior to usage.
	 */
	public synchronized final ScanCursor<E> open() {
		if (!isReady()) {
			throw new RuntimeException("Cursor already " + state + ". Cannot (re)open it.");
		}

		state = CursorState.OPEN;
		scan(getCursorId());

		return this;
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
	public boolean isClosed() {
		return state == CursorState.CLOSED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Closeable#close()
	 */
	public synchronized final void close() throws IOException {
		try {
			doClose();
		} finally {
			state = CursorState.CLOSED;
		}
	}

	/**
	 * Customization hook for cleaning up resources on when calling
	 * {@link #close()}.
	 */
	protected void doClose() {
	}

	/**
	 * Performs the actual scan command using the native client implementation.
	 * The given {@literal options} are never {@code null}.
	 * 
	 * @param jedis
	 * @param cursorId
	 * @param params
	 * @return
	 */
	protected ScanIterable<byte[]> doScan(Jedis jedis, String cursorId, ScanParams params) {
		ScanResult<byte[]> res = jedis.scan(cursorId.getBytes(Charsets.UTF_8), params);

		List<byte[]> items = res.getResult();
		if (CollectionUtils.isEmpty(items)) {
			items = Collections.emptyList();
		}

		return new ScanIterable<byte[]>(res.getStringCursor(), items);
	}

	/**
	 * Execute scan by cursor id
	 * 
	 * @param cursorId
	 */
	private synchronized void scan(String cursorId) {
		Jedis jedis = null;
		try {
			// Select a node
			jedis = jdsPools.get(getSelectionPos()).getResource();

			// Traverse only the primary node
			if (jedis.info(REPLICATION).contains(ROLE_MASTER)) {
				processResult(doScan(jedis, cursorId, params));
			} else {
				nextTo();
			}
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * After process result
	 * 
	 * @param res
	 */
	private void processResult(ScanIterable<byte[]> res) {
		iter = res;

		// The current node has completed traversal
		if ((cursorId = res.getCursorId()).equals("0")) { // Scan end

			// All nodes are traversed
			if (selectionPos >= (jdsPools.size() - 1)) {
				state = CursorState.FINISHED;
			}

			nextTo(); // Traversed, go to the next node
		}

	}

	/**
	 * Selection next node
	 */
	private void nextTo() {
		cursorId = "0"; // Reset cursor
		selectionPos++; // Next node
	}

	/**
	 * Assertion cursor is open
	 */
	private void assertCursorIsOpen() {
		if (isReady() || isClosed()) {
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
	enum CursorState {
		READY, OPEN, FINISHED, CLOSED;
	}

	/**
	 * {@link ScanIterable} holds the values contained in Redis
	 * {@literal Multibulk reply} on exectuting {@literal SCAN} command.
	 * 
	 * @author Christoph Strobl
	 * @since 1.4
	 */
	public static class ScanIterable<K> implements Iterable<K> {

		final private String cursorId;
		final private List<K> items;
		final private Iterator<K> iter;

		/**
		 * Scan iterable
		 */
		public ScanIterable() {
			this("0");
		}

		/**
		 * Scan iterable
		 * 
		 * @param cursorId
		 * @param items
		 */
		public ScanIterable(String cursorId) {
			this(cursorId, Collections.emptyList());
		}

		/**
		 * Scan iterable
		 * 
		 * @param cursorId
		 * @param items
		 */
		public ScanIterable(String cursorId, List<K> items) {
			this.cursorId = cursorId;
			this.items = (!CollectionUtils.isEmpty(items) ? new ArrayList<K>(items) : Collections.<K> emptyList());
			this.iter = this.items.iterator();
		}

		/**
		 * The cursor id to be used for subsequent requests.
		 * 
		 * @return
		 */
		public String getCursorId() {
			return cursorId;
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