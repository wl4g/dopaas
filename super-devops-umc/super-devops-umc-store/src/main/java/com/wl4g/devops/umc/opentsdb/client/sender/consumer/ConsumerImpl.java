package com.wl4g.devops.umc.opentsdb.client.sender.consumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.devops.umc.opentsdb.client.OpenTSDBConfig;
import com.wl4g.devops.umc.opentsdb.client.bean.request.Point;
import com.wl4g.devops.umc.opentsdb.client.http.HttpClient;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/23 下午4:36
 * @Version: 1.0
 */

public class ConsumerImpl implements Consumer {

	final private Logger log = LoggerFactory.getLogger(getClass());

	private final BlockingQueue<Point> queue;

	private final HttpClient httpClient;

	private final ExecutorService threadPool;

	private final int threadCount;

	private final OpenTSDBConfig config;

	private final CountDownLatch countDownLatch;

	public ConsumerImpl(BlockingQueue<Point> queue, HttpClient httpClient, OpenTSDBConfig config) {
		this.queue = queue;
		this.httpClient = httpClient;
		this.config = config;
		this.threadCount = config.getPutConsumerThreadCount();
		final int[] i = new int[1];
		this.threadPool = Executors.newFixedThreadPool(threadCount,
				(runnable) -> new Thread(runnable, "batch-put-thread-" + ++i[0]));
		this.countDownLatch = new CountDownLatch(threadCount);

		log.debug("the consumer has started");
	}

	@Override
	public void start() {
		for (int i = 0; i < threadCount; i++) {
			threadPool.execute(new ConsumerRunnable(queue, httpClient, config, countDownLatch));
		}
	}

	@Override
	public void gracefulStop() {
		this.stop(false);
	}

	@Override
	public void forceStop() {
		this.stop(true);
	}

	/***
	 * 关闭线程池
	 * 
	 * @param force
	 *            是否强制关闭
	 */
	private void stop(boolean force) {
		if (threadPool != null) {
			if (force) {
				// 强制退出不等待，截断消费者线程。
				threadPool.shutdownNow();
			} else {
				// 截断消费者线程。
				while (!threadPool.isShutdown() || !threadPool.isTerminated()) {
					threadPool.shutdownNow();
				}

				// 等待所有消费者线程结束。
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					log.error("An error occurred waiting for the consumer thread to close", e);
				}
			}
		}
	}

}
