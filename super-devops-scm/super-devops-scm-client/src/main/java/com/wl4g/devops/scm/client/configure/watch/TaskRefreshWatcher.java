package com.wl4g.devops.scm.client.configure.watch;

import java.io.IOException;

import org.springframework.scheduling.annotation.Scheduled;

public class TaskRefreshWatcher extends AbstractRefreshWatcher implements Runnable {

	@Override
	protected void doStart() {
		//
		// Ignore operation.
		//
	}

	@Override
	@Scheduled(initialDelayString = "${spring.cloud.devops.scm.client.watch.init-delay:120000}", fixedDelayString = "${devops.config.watch.delay:10000}")
	public void run() {
		if (log.isInfoEnabled()) {
			log.info("Synchronizing refresh from configuration center ...");
		}

		if (this.running.get()) {
			try {
				super.doExecute(this, null, "Task watch event.");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void close() throws IOException {
		//
		// Ignore operation.
		//
	}

}
