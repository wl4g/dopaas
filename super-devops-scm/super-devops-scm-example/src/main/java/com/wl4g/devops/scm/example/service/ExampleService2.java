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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExampleService2 implements InitializingBean, DisposableBean, Closeable {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	//@Value("#{'${example.firstName:unname}'.toUpperCase()}-${random.int(1000)}")
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
		running.compareAndSet(true,false);
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
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