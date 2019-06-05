/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.scm.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RefreshScope
@ConfigurationProperties(prefix = "example")
public class ExampleService implements InitializingBean, DisposableBean, Closeable {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * 每对#{}里可以写动态计算表达式，每对${}可以写应用占位符，可以嵌套或组合使用
	 */
	@Value("#{'${example.firstName:unname}'.toUpperCase()}-${random.int(1000)}")
	private String firstName; // 用于测试@Value注解

	private String lastName; // 用于测试@ConfigurationProperties注解

	private AtomicBoolean running = new AtomicBoolean(false);
	private Thread thread;

	@PostConstruct
	public void init() {
		System.out.println("ExampleService init()..." + this);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("ExampleService afterPropertiesSet()..." + this);
	}

	@PreDestroy
	public void destroy1() {
		System.out.println("ExampleService destroy1()..." + this);
		running.compareAndSet(true,false);
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("ExampleService destroy()..." + this);
	}

	@Override
	public void close() throws IOException {
		System.out.println("ExampleService close()..." + this);
	}

	public void start() {
		System.out.println("ExampleService Starting... firstName=" + firstName + ", lastName=" + lastName + " " + this);
		if (running.compareAndSet(false, true)) {
			this.createThread();
			this.thread.start();
		} else
			throw new IllegalStateException("ExampleService Thread started..." + this);
	}

	public void stop() {
		System.out.println("ExampleService Stoping... firstName=" + firstName + ", lastName=" + lastName + " " + this);
		if (running.compareAndSet(true, false)) {
			this.thread = null;
		} else
			throw new IllegalStateException("ExampleService Thread already stoped.");
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	private synchronized void createThread() {
		if (this.thread != null) {
			System.out.println("ExampleService Already thread " + thread);
		}
		this.thread = new Thread(() -> {
			while (running.get()) {
				System.out.println("ExampleService  " + thread.getName() + ", firstName=" + firstName + ", lastName=" + lastName
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