package com.wl4g.devops.scm.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExampleService2 implements InitializingBean, DisposableBean, Closeable {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Value("#{'${example.firstName:unname}'.toUpperCase()}-${random.int(1000)}")
	private String firstName;

	private String lastName;

	private AtomicBoolean running = new AtomicBoolean(false);
	private Thread thread;

	@PostConstruct
	public void init() {
		System.out.println("ExampleService2 init()..." + this);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("ExampleService2 afterPropertiesSet()..." + this);
	}

	@PreDestroy
	public void destroy1() {
		System.out.println("ExampleService2 @PreDestroy..." + this);
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("ExampleService2 destroy()..." + this);
	}

	@Override
	public void close() throws IOException {
		System.out.println("ExampleService2 close()..." + this);
	}

	public void start() {
		System.out.println("ExampleService2 Starting... firstName=" + firstName + ", lastName=" + lastName + " " + this);
		if (running.compareAndSet(false, true)) {
			this.createThread();
			this.thread.start();
		} else
			throw new IllegalStateException("Thread started..." + this);
	}

	public void stop() {
		System.out.println("ExampleService2 Stoping... firstName=" + firstName + ", lastName=" + lastName + " " + this);
		if (running.compareAndSet(true, false)) {
			this.thread = null;
		} else
			throw new IllegalStateException("ExampleService2 Thread already stoped.");
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	private synchronized void createThread() {
		if (this.thread != null) {
			System.out.println("ExampleService2 Already thread " + thread);
		}
		this.thread = new Thread(() -> {
			while (running.get()) {
				System.out.println("ExampleService2  " + thread.getName() + ", firstName=" + firstName + ", lastName=" + lastName
						+ " " + this);
				try {
					Thread.sleep(2000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
