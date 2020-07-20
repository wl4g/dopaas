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
package com.wl4g.devops.components.tools.common.id;

import static java.lang.String.format;

/**
 * Snowflake algorithms Id generator.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018年6月10日
 * @since
 * @see <a href=
 *      "https://github.com/twitter/snowflake">https://github.com/twitter/snowflake</a>
 */
public class SnowflakeIdGenerator {

	// Worker node ID.
	private long workerId;
	// Data center ID.
	private long datacenterId;
	// Sequence ID.
	private long sequence = 0L;
	// Thu, 04 Nov 2010 01:42:54 GMT
	private long twepoch = 1288834974657L;
	// Node ID length
	private long workerIdBits = 5L;
	// Data center ID length.
	private long datacenterIdBits = 5L;
	// The maximum number of machine nodes supported is 0-31, a total of 32
	private long maxWorkerId = -1L ^ (-1L << workerIdBits);
	// The maximum number of data center nodes supported is 0-31, a total of 32
	private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
	// Serial number 12 digits.
	private long sequenceBits = 12L;
	// Machine node left shift 12 bits.
	private long workerIdShift = sequenceBits;
	// Data center node moves 17 bits left.
	private long datacenterIdShift = sequenceBits + workerIdBits;
	// Time milliseconds shift left 22 bits.
	private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
	// Sequence mask (4095 bits).
	private long sequenceMask = -1L ^ (-1L << sequenceBits);
	// Last concurrent timestamp.
	private long lastTimestamp = -1L;

	public SnowflakeIdGenerator() {
		this(0L, 0L);
	}

	public SnowflakeIdGenerator(long workerId, long datacenterId) {
		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(format("worker Id can't be greater than %d or less than 0", maxWorkerId));
		}
		if (datacenterId > maxDatacenterId || datacenterId < 0) {
			throw new IllegalArgumentException(format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
		}
		this.workerId = workerId;
		this.datacenterId = datacenterId;
	}

	/**
	 * Gets next ID.
	 * 
	 * @return
	 */
	public synchronized long nextId() {
		long now = timeGen();
		// 如果服务器时间有问题(时钟后退) 报错。
		if (now < lastTimestamp) {
			throw new IllegalStateException(
					format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - now));
		}
		// 如果上次生成时间和当前时间相同,在同一毫秒内
		if (lastTimestamp == now) {
			// sequence自增，因为sequence只有12bit，所以和sequenceMask相与一下，去掉高位
			sequence = (sequence + 1) & sequenceMask;
			// 判断是否溢出,也就是每毫秒内超过4095，当为4096时，与sequenceMask相与，sequence就等于0
			if (sequence == 0) {
				// 自旋等待到下一毫秒
				now = tilNextMillis(lastTimestamp);
			}
		} else {
			// 如果和上次生成时间不同,重置sequence，就是下一毫秒开始，sequence计数重新从0开始累加
			sequence = 0L;
		}
		lastTimestamp = now;
		// 最后按照规则拼出ID。
		// 000000000000000000000000000000000000000000 00000 00000 000000000000
		// time datacenterId workerId sequence
		return ((now - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift)
				| sequence;
	}

	/**
	 * Spin wait until next MS
	 * 
	 * @param lastTimestamp
	 * @return
	 */
	protected long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	/**
	 * Gets now timestamp.
	 * 
	 * @return
	 */
	protected long timeGen() {
		return System.currentTimeMillis();
	}

	/**
	 * Gets {@link SnowflakeIdGenerator} default instance.
	 * 
	 * @return
	 */
	public final static SnowflakeIdGenerator getDefault() {
		return SingletionHolder.instance;
	}

	/**
	 * {@link SingletionHolder} singletion holder
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年6月10日
	 * @since
	 */
	private final static class SingletionHolder {
		private static final SnowflakeIdGenerator instance = new SnowflakeIdGenerator();
	}

}