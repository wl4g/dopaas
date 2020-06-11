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
package com.wl4g.devops.dguid.worker;

import java.io.File;
import java.net.ServerSocket;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.InitializingBean;

import com.wl4g.devops.dguid.baidu.utils.NamingThreadFactory;
import com.wl4g.devops.dguid.util.WorkerIdUtils;

/**
 * Based on internal timer workerId assigner
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
 */
public abstract class AbstractIntervalWorkerId implements WorkerIdAssigner, InitializingBean {

	/**
	 * 心跳原子标识
	 */
	protected AtomicBoolean active = new AtomicBoolean(false);

	/**
	 * 心跳间隔
	 */
	protected Long interval = 3000L;

	/**
	 * workerID 文件存储路径
	 */
	protected String pidHome = PID_ROOT;

	/**
	 * 因子ID
	 */
	protected Long workerId;

	/**
	 * 使用端口(同机多uid应用时区分端口)
	 */
	private Integer pidPort = -1;

	protected String pidName;

	protected ServerSocket socket;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			/**
			 * 1、检查workId文件是否存在。文件名为ip:port-redis顺序编号
			 */
			pidName = WorkerIdUtils.getPidName(pidPort, socket);
			workerId = WorkerIdUtils.getPid(pidHome, pidName);
			/**
			 * 3、获取本地时间，跟uid 机器节点心跳列表的时间平均值做比较(uid 机器节点心跳列表
			 * 用于存储活跃节点的上报时间，每隔一段时间上报一次临时节点时间)
			 */
			long diff = System.currentTimeMillis() - action();
			if (diff < 0) {
				// 当前时间小于活跃节点的平均心跳时间，证明出现时间回拨，进入等待。
				WorkerIdUtils.sleepMs(interval * 2, diff);
			}
			if (null != workerId) {
				startHeartBeatThread();
				// 赋值workerId
				WorkerIdUtils.writePidFile(pidHome + File.separatorChar + pidName + WorkerIdUtils.WORKER_SPLIT + workerId);
			}
		} catch (Exception e) {
			active.set(false);
			if (null != socket) {
				socket.close();
			}
			throw e;
		}
	}

	/**
	 * <pre>
	 * workId文件不存在时的操作
	 * </pre>
	 * 
	 * @return 机器节点列表的 活跃时间平均值
	 */
	public abstract long action();

	/**
	 * 心跳线程，用于每隔一段时间上报一次临时节点时间
	 */
	protected void startHeartBeatThread() {
		ScheduledExecutorService schedule = new ScheduledThreadPoolExecutor(1,
				new NamingThreadFactory(THREAD_HEARTBEAT_NAME, true));
		schedule.scheduleAtFixedRate(() -> {
			if (active.get() == false) {
				schedule.shutdownNow();
			} else if (where()) {
				report();
			}
		}, 0L, interval, TimeUnit.MILLISECONDS);
	}

	/**
	 * <pre>
	 * 心跳条件
	 * </pre>
	 * 
	 * @return true:执行心跳上报，false:空动作
	 */
	public abstract boolean where();

	/**
	 * 心跳上报
	 */
	public abstract void report();

	@Override
	public long assignWorkerId() {
		return workerId;
	}

	public Long getInterval() {
		return interval;
	}

	public void setInterval(Long interval) {
		this.interval = interval;
	}

	public String getPidHome() {
		return pidHome;
	}

	public void setPidHome(String pidHome) {
		this.pidHome = pidHome;
	}

	public Integer getPidPort() {
		return pidPort;
	}

	public void setPidPort(Integer pidPort) {
		this.pidPort = pidPort;
	}

	/**
	 * 本地workid文件跟目录
	 */
	public static final String PID_ROOT = "/data/pids/";

	/**
	 * 线程名-心跳
	 */
	public static final String THREAD_HEARTBEAT_NAME = "zk_heartbeat";

}