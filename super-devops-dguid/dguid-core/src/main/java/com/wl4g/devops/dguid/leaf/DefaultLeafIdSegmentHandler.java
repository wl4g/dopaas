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
package com.wl4g.devops.dguid.leaf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.wl4g.devops.dguid.baidu.utils.NamingThreadFactory;

/**
 * Segment 策略id生成实现类
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
 */
public class DefaultLeafIdSegmentHandler implements LeafIdSegmentHandler {

	/**
	 * 线程名-心跳
	 */
	public static final String THREAD_BUFFER_NAME = "leaf_buffer_sw";

	final private static ReentrantLock lock = new ReentrantLock();

	private JdbcTemplate jdbcTemplate;

	/**
	 * 创建线程池
	 */
	private ExecutorService taskExecutor;

	/**
	 * 两段buffer
	 */
	private volatile LeafSegment[] segment = new LeafSegment[2];

	/**
	 * Buffer switch ID (true switch, false do not switch)
	 */
	private volatile boolean sw;

	/**
	 * Current ID
	 */
	private AtomicLong currentId;

	/**
	 * Business ID tag.
	 */
	private String bizTag;

	/**
	 * 异步标识(true-异步，false-同步)
	 */
	private boolean asynLoadingSegment = true;

	/**
	 * 异步线程任务
	 */
	private FutureTask<Boolean> asynLoadSegmentTask = null;

	public DefaultLeafIdSegmentHandler(JdbcTemplate jdbcTemplate, String bizTag) {
		this.jdbcTemplate = jdbcTemplate;
		if (taskExecutor == null) {
			taskExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
					new NamingThreadFactory(THREAD_BUFFER_NAME));
		}
		this.bizTag = bizTag;
		// 获取第一段buffer缓冲
		segment[0] = doUpdateNextSegment(bizTag);
		setSw(false);
		// 初始id
		currentId = new AtomicLong(segment[index()].getMinId());
	}

	@Override
	public Long getId() {
		// 更改阈值(middle与max)lock在高速碰撞时的可能多次执行下一个id
		Long nextId = null;
		if (segment[index()].getMiddleId().equals(currentId.longValue())
				|| segment[index()].getMaxId().equals(currentId.longValue())) {
			try {
				lock.lock();
				// 阈值50%时，加载下一个buffer
				if (segment[index()].getMiddleId().equals(currentId.longValue())) {
					thresholdHandler();
					nextId = currentId.incrementAndGet();
				}
				if (segment[index()].getMaxId().equals(currentId.longValue())) {
					fullHandler();
					nextId = currentId.incrementAndGet();
				}
			} finally {
				lock.unlock();
			}
		}
		nextId = null == nextId ? currentId.incrementAndGet() : nextId;
		// 突破并发数被step限制的bug
		return nextId <= segment[index()].getMaxId() ? nextId : getId();
	}

	/**
	 * 阈值处理，是否同/异步加载下一个buffer的值(即更新DB)
	 */
	private void thresholdHandler() {
		if (asynLoadingSegment) {
			// 异步处理-启动线程更新DB，由线程池执行
			asynLoadSegmentTask = new FutureTask<>(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					final int currentIndex = reIndex();
					segment[currentIndex] = doUpdateNextSegment(bizTag);
					return true;
				}
			});
			taskExecutor.submit(asynLoadSegmentTask);
		} else {
			// 同步处理，直接更新DB
			final int currentIndex = reIndex();
			segment[currentIndex] = doUpdateNextSegment(bizTag);
		}
	}

	/**
	 * buffer使用完时切换buffer。
	 */
	public void fullHandler() {
		if (asynLoadingSegment) {
			// 异步时，需判断 异步线程的状态(是否已经执行)
			try {
				asynLoadSegmentTask.get();
			} catch (Exception e) {
				e.printStackTrace();
				// 未执行，强制同步切换
				segment[reIndex()] = doUpdateNextSegment(bizTag);
			}
		}
		// 设置切换标识
		setSw(!isSw());
		// 进行切换
		currentId = new AtomicLong(segment[index()].getMinId());
	}

	/**
	 * Gets the index of the next buffer
	 */
	private int reIndex() {
		return isSw() ? 0 : 1;
	}

	/**
	 * Gets the index of the current buffer
	 */
	private int index() {
		return isSw() ? 1 : 0;
	}

	/**
	 * <pre>
	 * 更新下一个buffer
	 * </pre>
	 * 
	 * @param bizTag
	 *            业务标识
	 * @return 下一个buffer的分段id实体
	 */
	private LeafSegment doUpdateNextSegment(String bizTag) {
		try {
			return updateId(bizTag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private LeafSegment updateId(String bizTag) throws Exception {
		String querySql = "select step, max_id, last_update_time, current_update_time from id_segment where biz_tag=?";
		String updateSql = "update id_segment set max_id=?, last_update_time=?, current_update_time=now() where biz_tag=? and max_id=?";
		final LeafSegment currentSegment = new LeafSegment();
		this.jdbcTemplate.query(querySql, new String[] { bizTag }, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Long step = null;
				Long currentMaxId = null;
				step = rs.getLong("step");
				currentMaxId = rs.getLong("max_id");
				Date lastUpdateTime = new Date();
				if (rs.getTimestamp("last_update_time") != null) {
					lastUpdateTime = (java.util.Date) rs.getTimestamp("last_update_time");
				}
				Date currentUpdateTime = new Date();
				if (rs.getTimestamp("current_update_time") != null) {
					currentUpdateTime = (java.util.Date) rs.getTimestamp("current_update_time");
				}
				currentSegment.setStep(step);
				currentSegment.setMaxId(currentMaxId);
				currentSegment.setLastUpdateTime(lastUpdateTime);
				currentSegment.setCurrentUpdateTime(currentUpdateTime);
			}
		});
		Long newMaxId = currentSegment.getMaxId() + currentSegment.getStep();
		int row = this.jdbcTemplate.update(updateSql,
				new Object[] { newMaxId, currentSegment.getCurrentUpdateTime(), bizTag, currentSegment.getMaxId() });
		if (row == 1) {
			LeafSegment newSegment = new LeafSegment();
			newSegment.setStep(currentSegment.getStep());
			newSegment.setMaxId(newMaxId);
			return newSegment;
		} else {
			// 递归，直至更新成功
			return updateId(bizTag);
		}
	}

	public void setTaskExecutor(ExecutorService taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	private boolean isSw() {
		return sw;
	}

	private void setSw(boolean sw) {
		this.sw = sw;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void setBizTag(String bizTag) {
		this.bizTag = bizTag;
	}

	public void setAsynLoadingSegment(boolean asynLoadingSegment) {
		this.asynLoadingSegment = asynLoadingSegment;
	}

}