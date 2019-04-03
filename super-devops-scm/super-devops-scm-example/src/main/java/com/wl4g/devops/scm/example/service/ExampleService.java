package com.wl4g.devops.scm.example.service;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.wl4g.devops.scm.client.configure.RefreshBean;

@Service
@RefreshBean
@ConfigurationProperties(prefix = "example")
public class ExampleService implements InitializingBean, DisposableBean {
	final protected static Logger log = LoggerFactory.getLogger(ExampleService.class);

	/**
	 * 每对#{}里可以写动态计算表达式，每对${}可以写应用占位符，可以嵌套或组合使用
	 */
	@Value("#{'${example.firstName:unname}'.toUpperCase()}-${random.int(1000)}")
	private String firstName; // 用于测试@Value注解
	private String lastName; // 用于测试@ConfigurationProperties注解

	private AtomicBoolean running = new AtomicBoolean(false);
	private Thread thread;

	@PostConstruct
	public void customInit() {
		// System.out.println("@PostConstruct..." + this);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("afterPropertiesSet..." + this);
	}

	@PreDestroy
	public void customDestroy() {
		// System.out.println("@PreDestroy..." + this);
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("Destory thread..." + this);
	}

	public void start() {
		System.out.println("Starting... firstName=" + firstName + ", lastName=" + lastName + " " + this);
		if (running.compareAndSet(false, true)) {
			this.createThread();
			this.thread.start();
		} else
			throw new IllegalStateException("Thread started..." + this);
	}

	public void stop() {
		System.out.println("Stoping... firstName=" + firstName + ", lastName=" + lastName + " " + this);
		if (running.compareAndSet(true, false)) {
			this.thread = null;
		} else
			throw new IllegalStateException("Thread already stoped.");
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	private synchronized void createThread() {
		if (this.thread != null) {
			System.out.println("Already thread " + thread);
		}
		this.thread = new Thread(() -> {
			while (running.get()) {
				System.out.println(thread.getName() + ", firstName=" + firstName + ", lastName=" + lastName + " " + this);
				try {
					Thread.sleep(2000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
