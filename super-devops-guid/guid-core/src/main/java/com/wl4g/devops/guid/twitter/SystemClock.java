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
package com.wl4g.devops.guid.twitter;

import java.sql.Timestamp;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.wl4g.devops.guid.baidu.utils.NamingThreadFactory;

/**
 * 高并发场景下System.currentTimeMillis()的性能问题的优化
 * <p>
 * <p>
 * System.currentTimeMillis()的调用比new一个普通对象要耗时的多（参加https://www.jianshu.com/p/3fbe607600a5）
 * <p>
 * System.currentTimeMillis()之所以慢是因为去跟系统打了一次交道
 * <p>
 * 后台定时更新时钟，JVM退出时，线程自动回收
 * <p>
 * 10亿：43410,206,210.72815533980582%
 * <p>
 * 1亿：4699,29,162.0344827586207%
 * <p>
 * 1000万：480,12,40.0%
 * <p>
 * 100万：50,10,5.0%
 * <p>
 * 
 * @author lry
 */
public class SystemClock {

	/**
	 * 线程名--系统时钟
	 */
	public static final String THREAD_CLOCK_NAME = "System Clock";

	private final long period;

	private final AtomicLong now;

	private SystemClock(long period) {
		this.period = period;
		this.now = new AtomicLong(System.currentTimeMillis());
		scheduleClockUpdating();
	}

	private static class InstanceHolder {
		public static final SystemClock INSTANCE = new SystemClock(1);
	}

	private static SystemClock instance() {
		return InstanceHolder.INSTANCE;
	}

	private void scheduleClockUpdating() {
		ScheduledExecutorService scheduledpool = new ScheduledThreadPoolExecutor(1,
				new NamingThreadFactory(THREAD_CLOCK_NAME, true));
		scheduledpool.scheduleAtFixedRate(() -> {
			now.set(System.currentTimeMillis());
		}, period, period, TimeUnit.MILLISECONDS);
	}

	private long currentTimeMillis() {
		return now.get();
	}

	public static long now() {
		return instance().currentTimeMillis();
	}

	public static String nowDate() {
		return new Timestamp(instance().currentTimeMillis()).toString();
	}
}