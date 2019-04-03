package com.wl4g.devops.umc.client.indicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Compound health monitoring processor.<br/>
 * Note: if you change it into an internal class `@Component`, it doesn't seem
 * to work.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月7日
 * @since
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CompositeHealthTaskProcessor implements InitializingBean, DisposableBean {
	final private static Logger logger = LoggerFactory.getLogger(CompositeHealthTaskProcessor.class);

	final private AtomicBoolean running = new AtomicBoolean(false);
	private long acq = 4_000L;
	private List<Runnable> tasks = new ArrayList<>();
	private ExecutorService executor;

	public void submit(Runnable task) {
		if (!this.tasks.contains(task)) {
			this.tasks.add(task);
		}
	}

	private void doStart() {
		if (!this.running.compareAndSet(false, true)) {
			throw new IllegalStateException("Already started health indicator executor.");
		}
		if (logger.isInfoEnabled())
			logger.info("Starting health indicator executor...");

		this.executor.submit(() -> {
			while (true) {
				try {
					this.tasks.forEach((task) -> {
						try {
							task.run();
						} catch (Exception e) {
							logger.error("Execution error.", e);
						}
					});
					Thread.sleep(this.acq);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	@Override
	public void destroy() throws Exception {
		if (logger.isInfoEnabled())
			logger.info("Destroy health indicator executor...");

		if (this.running.compareAndSet(true, false))
			this.executor.shutdownNow();
		else
			logger.warn("Non startup health indicator executor.");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.executor = Executors.newFixedThreadPool(2, (r) -> {
			Thread t = new Thread(r, CompositeHealthTaskProcessor.class.getSimpleName() + "-" + System.currentTimeMillis());
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		});

		this.doStart();
	}

}
