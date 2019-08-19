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
package com.wl4g.devops.ci.git;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.devops.ci.config.CiCdProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.parseJSON;

/**
 * GIT API template.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月2日
 * @since
 */
public abstract class AbstractGitTemplate implements GitTemplate, InitializingBean {

	protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected CiCdProperties config;

	/**
	 * Rest template.
	 */
	protected RestTemplate restTemplate;

	/**
	 * Do GITLAB exchange.
	 * 
	 * @param url
	 * @param typeRef
	 * @return
	 */
	protected <T> T doGitExchange(String url, TypeReference<T> typeRef) {
		// PRE call.
		HttpEntity<String> entity = preGitExchangeSet();

		// Do request.
		ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		if (null == resp || HttpStatus.OK != resp.getStatusCode()) {
			throw new IllegalStateException(String.format("Failed to request gitlab remote, status: %s, body: %s",
					resp.getStatusCodeValue(), resp.getBody()));
		}
		if (log.isInfoEnabled()) {
			log.info("Gitlab remote response: {}", resp.getBody());
		}
		return parseJSON(resp.getBody(), typeRef);
	}

	/**
	 * Pre request GIT exchange set.
	 */
	protected HttpEntity<String> preGitExchangeSet() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("PRIVATE-TOKEN", config.getGitToken());
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		return  entity;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		this.restTemplate = new RestTemplate(factory);
	}

}
