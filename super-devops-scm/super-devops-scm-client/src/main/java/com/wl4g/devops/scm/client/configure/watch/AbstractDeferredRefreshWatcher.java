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
package com.wl4g.devops.scm.client.configure.watch;

import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.configure.refresh.ScmContextRefresher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract refresh watcher.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年10月20日
 * @since
 * @see {@link org.springframework.cloud.zookeeper.config.ConfigWatcher
 *      ConfigWatcher}
 */
public abstract class AbstractDeferredRefreshWatcher implements InitializingBean, DisposableBean, Closeable {
	final protected Logger log = LoggerFactory.getLogger(getClass());
	final protected AtomicBoolean running = new AtomicBoolean(false);
	final protected ExecutorService worker;
	final protected ScmContextRefresher refresher;

	final protected RestTemplate restTemplate;

	final protected ScmClientProperties scmClientProperties;

	@Value("${spring.cloud.devops.scm.client.base-uri:http://localhost:6400/devops}")
	String baseUri;

	public AbstractDeferredRefreshWatcher(ScmContextRefresher refresher, ScmClientProperties scmClientProperties) {
		Assert.notNull(refresher, "Refresher must not be null");
		this.refresher = refresher;

		this.restTemplate = createSecureRestTemplate();
		this.scmClientProperties = scmClientProperties;

		// Initialize executor
		final AtomicInteger counter = new AtomicInteger(0);
		this.worker = new ThreadPoolExecutor(1, 2, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(16), r -> {
			String name = "scmRefreshWatch-" + counter.incrementAndGet();
			Thread t = new Thread(r, name);
			t.setDaemon(true);
			return t;
		});
	}

	protected RestTemplate createSecureRestTemplate() {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		factory.setReadTimeout(60000);
		RestTemplate template = new RestTemplate(factory);
		return template;
	}

	public boolean watch(String namespace){
		String url = baseUri+"/watch?namespace="+namespace;

		/*ResponseEntity<RespBase<ReleaseMessage>> responseEntity = this.restTemplate
				.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<RespBase<ReleaseMessage>>() {
				});*/

		ResponseEntity responseEntity  = restTemplate.getForEntity(url,String.class);

		HttpStatus httpStatus = responseEntity.getStatusCode();
		if(HttpStatus.OK.equals(httpStatus)){
			return true;
		}else{
			return false;
		}

		//RespBase<ReleaseMessage> resp = (RespBase<ReleaseMessage>) responseEntity.getBody();


	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (running.compareAndSet(false, true)) {
			// Do actual
			doStart();
		} else {
			throw new IllegalStateException("Already started watcher.");
		}
	}

	protected abstract void doStart();

	@Override
	public void destroy() throws Exception {
		if (running.compareAndSet(true, false)) {
			close();
		}
	}

	protected void doExecute() {
		worker.execute(() -> {
			try {
				refresher.refresh();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		});
	}

	private boolean isPayload(byte[] value) {
		return value != null && value.length > 0;
	}

}