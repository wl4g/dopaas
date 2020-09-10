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
package com.wl4g.devops.ci.pcm;

import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.ci.config.CiProperties;

/**
 * Abstract PCM operator.
 * 
 * @param <K>
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月7日 v1.0.0
 * @see
 */
public abstract class AbstractPcmOperator implements PcmOperator, InitializingBean {
	final protected Logger log = getLogger(getClass());

	/**
	 * CICD PCM configuration.
	 */
	@Autowired
	protected CiProperties config;

	/**
	 * {@link RestTemplate} of PCM API provider.
	 */
	protected RestTemplate restTemplate;

	@Override
	public void afterPropertiesSet() throws Exception {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		factory.setConnectTimeout(config.getPcm().getConnectTimeout());
		factory.setReadTimeout(config.getPcm().getReadTimeout());
		factory.setMaxResponseSize(config.getPcm().getMaxResponseSize());
		this.restTemplate = new RestTemplate(factory);
	}

}