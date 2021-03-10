/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.umc.client.store;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.devops.umc.client.indicator.AbstractAdvancedHealthIndicator.Partition;

/**
 * Memory event store
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月7日
 * @since
 */
public class DefaultMemoryEventStore implements EventStore<Partition> {
	final private static Logger logger = LoggerFactory.getLogger(DefaultMemoryEventStore.class);

	/**
	 * Default number of retained samples.
	 */
	final public static int DEFAULT_CAPACITY = 32;
	final public static int MAX_CAPACITY = DEFAULT_CAPACITY * 20;
	final public static long DEFAULT_RETAIN = 5 * 60 * 1000;
	final public static long MAX_RETAIN = DEFAULT_RETAIN * 5;

	final private LinkedList<Partition> records = new LinkedList<>();
	private int capacity = DEFAULT_CAPACITY;
	private long retainTime = DEFAULT_RETAIN;

	public DefaultMemoryEventStore(int capacity, long retainTime) {
		if (capacity <= 0 || retainTime < 0 || capacity > MAX_CAPACITY || retainTime > MAX_RETAIN)
			throw new IllegalArgumentException(
					"`capacity/retainTime` effective scope is capacity>0 and retainTime>0 and capacity<=" + MAX_CAPACITY
							+ " and retainTime<=" + MAX_RETAIN);

		this.capacity = capacity;
		this.retainTime = retainTime;
	}

	@Override
	public synchronized void save(Partition part) {
		if (logger.isDebugEnabled()) {
			logger.debug("Save partition:{}", part);
		}
		// Timeout clean.
		this.checkExpiredSampleClean();

		// Overflow clean.
		if (this.records.size() > (this.capacity - 1)) {
			// LinkedList<Partition> records0 = this.getASCRecords();
			// Partition oldLeast = records0.peekFirst();
			// Partition oldLargest = records0.peekLast();
			// // Bigger than big.
			// if (part.compareTo(oldLargest) > 0)
			// // Remove old largest.
			// this.records.remove(oldLargest);
			// // Smaller than small.
			// if (part.compareTo(oldLeast) < 0)
			// // Remove old least.
			// this.records.remove(oldLeast);
			// else
			// Default remove first(oldest).
			this.records.pollFirst();
		}

		// enqueue.
		this.records.offer(part);
	}

	@Override
	public synchronized Partition largest() {
		if (!this.records.isEmpty()) {
			// Expired clean.
			this.checkExpiredSampleClean();

			LinkedList<Partition> records0 = this.getASCRecords();
			Partition part = records0.peekLast();
			if (part != null) {
				part.setSamples(records0.size());
				return part;
			}
		}
		return null;
	}

	@Override
	public synchronized Partition least() {
		if (!this.records.isEmpty()) {
			// Expired clean.
			this.checkExpiredSampleClean();

			LinkedList<Partition> records0 = this.getASCRecords();
			Partition part = records0.peekFirst();
			if (part != null) {
				part.setSamples(records0.size());
				return part;
			}
		}
		return null;
	}

	@Override
	public synchronized Partition latest() {
		if (!this.records.isEmpty()) {
			// Expired clean.
			this.checkExpiredSampleClean();

			Partition part = this.records.peekLast();
			if (part != null) {
				part.setSamples(this.records.size());
				return part;
			}
		}
		return null;
	}

	@Override
	public long average() {
		long sum = 0L;
		Iterator<Partition> it = this.records.iterator();
		while (it.hasNext()) {
			sum += it.next().getValue();
		}
		return (sum / this.records.size());
	}

	/**
	 * Check and clean expired sample.
	 */
	private void checkExpiredSampleClean() {
		long now = System.currentTimeMillis();
		Iterator<Partition> it = this.records.iterator();
		while (it.hasNext()) {
			Partition tmpPart = it.next();
			if (Math.abs(now - tmpPart.getTimestamp()) >= this.retainTime)
				it.remove();
		}
	}

	/**
	 * From small to large
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private LinkedList<Partition> getASCRecords() {
		LinkedList<Partition> cloneRecords = (LinkedList<Partition>) this.records.clone();
		Collections.sort(cloneRecords); // ASC(value)
		return cloneRecords;
	}

}