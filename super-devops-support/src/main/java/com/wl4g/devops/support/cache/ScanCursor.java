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
package com.wl4g.devops.support.cache;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.core.ResolvableType;

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
public abstract class ScanCursor<E> implements Iterator<E>, Closeable {
	final public static String REPLICATION = "Replication";
	final public static String ROLE_MASTER = "role:master";
	final public static ScanParams NONE_PARAMS = new ScanParams();

	final private Logger log = LoggerFactory.getLogger(getClass());
	final private ScanParams params;
	final private Class<?> valueType;
	final private JedisCluster jdsCluster;
	final private List<JedisPool> jedisNodes;

	final private AtomicInteger selectionPos;
	private CursorState state;
	private String cursorId;
	private ScanIterable<byte[]> iter; // Values

	/**
	 * Crates new {@link ScanCursor} with {@code id=0} and
	 * {@link ScanParams#NONE}
	 */
	public ScanCursor(JedisCluster cluster, Class<?> valueType) {
		this(cluster, valueType, NONE_PARAMS);
	}

	/**
	 * Crates new {@link ScanCursor} with {@code id=0}.
	 * 
	 * @param params
	 */
	public ScanCursor(JedisCluster cluster, ScanParams params) {
		this(cluster, "0", null, params);
	}

	/**
	 * Crates new {@link ScanCursor} with {@code id=0}.
	 * 
	 * @param params
	 */
	public ScanCursor(JedisCluster cluster, Class<?> valueType, ScanParams params) {
		this(cluster, "0", valueType, params);
	}

	/**
	 * Crates new {@link ScanCursor} with {@link ScanParams#NONE}
	 * 
	 * @param cursorId
	 */
	public ScanCursor(JedisCluster cluster, String cursorId, Class<?> valueType) {
		this(cluster, cursorId, valueType, NONE_PARAMS);
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
	public ScanCursor(JedisCluster jdCluster, String cursorId, Class<?> valueType, ScanParams param) {
		notNull(jdCluster, "jedisCluster must not be null");
		hasText(cursorId, "cursorId must not be empty");
		if (isNull(valueType)) {
			valueType = ResolvableType.forClass(getClass()).getSuperType().getGeneric(0).resolve();
		}
		this.valueType = valueType;
		notNull(valueType, "No scan value java type is specified. Use constructs that can set value java type.");
		this.jdsCluster = jdCluster;
		this.params = param != null ? param : NONE_PARAMS;
		this.jedisNodes = jdCluster.getClusterNodes().values().stream().collect(toCollection(ArrayList::new));
		this.selectionPos = new AtomicInteger(0);
		this.state = CursorState.READY;
		this.cursorId = cursorId;
		this.iter = new ScanIterable<>(cursorId, emptyList());
		notEmpty(jedisNodes, "Jedis nodes is empty.");
		hasText(cursorId, "CursorId is empty.");
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
		return selectionPos.get();
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
	@Override
	public synchronized final void close() throws IOException {
		if (!isClosed()) {
			try {
				doClose();
			} finally {
				state = CursorState.CLOSED;
			}
		}
	}

	/**
	 * Customization hook for cleaning up resources on when calling
	 * {@link #close()}.
	 */
	protected synchronized void doClose() {
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
	 * Initialize the {@link Cursor} prior to usage.
	 */
	public synchronized final ScanCursor<E> open() {
		if (!isReady()) {
			throw new IllegalStateException("Cursor already " + state + ". Cannot (re)open it.");
		}

		state = CursorState.OPEN;
		scan(getCursorId());

		return this;
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
		try (ScanCursor<E> that = this) {
			// Not iterated yet?
			if (isEmpty(that.iter.getItems())) {
				List<E> list = new ArrayList<>(64);
				while (hasNext()) {
					list.add(next());
				}
				return list;
			}

			// Iterated yet?
			return (List<E>) that.iter.getItems().stream().map(key -> {
				return deserialize(jdsCluster.get(key), valueType);
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
			throw new NoSuchElementException("No more elements available for cursor " + getCursorId() + ".");
		}

		return (E) deserialize(jdsCluster.get(iter.iterator().next()), valueType);
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
			scan(getCursorId());
		}

		return (iter.iterator().hasNext() || !checkScanFinished());
	}

	/**
	 * Execute scan by cursor id
	 * 
	 * @param cursorId
	 */
	protected synchronized void scan(String cursorId) {
		// Select a node
		try (Jedis jedis = jedisNodes.get(getSelectionPos()).getResource()) {
			// Traverse only the primary node
			if (containsIgnoreCase(jedis.info(REPLICATION), ROLE_MASTER)) {
				processResult(doScan(jedis, cursorId, params));
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
	 * @param cursorId
	 * @param params
	 * @return
	 */
	protected synchronized ScanIterable<byte[]> doScan(Jedis jedis, String cursorId, ScanParams params) {
		ScanResult<byte[]> res = jedis.scan(cursorId.getBytes(Charsets.UTF_8), params);

		List<byte[]> items = res.getResult();
		if (isEmpty(items)) {
			items = emptyList();
		}

		return new ScanIterable<byte[]>(res.getStringCursor(), items);
	}

	/**
	 * After process result
	 * 
	 * @param res
	 */
	private synchronized void processResult(ScanIterable<byte[]> res) {
		this.iter = res;
		this.cursorId = res.getCursorId();

		// The current node has completed traversal
		if (checkScanFinished()) { // End?
			nextTo(); // Select to next node.
		}
	}

	/**
	 * Selection next node
	 */
	private synchronized void nextTo() {
		cursorId = "0"; // Reset cursor.
		selectionPos.incrementAndGet(); // Next node.

		// Safe check fully scanned.
		if (checkScanNodesCompleted()) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("Scanned all jedis nodes. size: %s", jedisNodes.size()));
			}
			state = CursorState.FINISHED;
			selectionPos.set(jedisNodes.size() - 1);
		}
	}

	/**
	 * Check that currently node finished.
	 * 
	 * @return
	 */
	private boolean checkScanFinished() {
		return trimToEmpty(getCursorId()).equalsIgnoreCase("0");
	}

	/**
	 * Check that all nodes are currently fully scanned.
	 * 
	 * @return
	 */
	private boolean checkScanNodesCompleted() {
		return selectionPos.get() >= jedisNodes.size();
	}

	/**
	 * Assertion cursor is open
	 */
	private synchronized void assertCursorIsOpen() {
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
	final static class ScanIterable<K> implements Iterable<K> {

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
			this.items = (isEmpty(items) ? emptyList() : new ArrayList<K>(items));
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