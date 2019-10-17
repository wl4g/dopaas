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
package com.wl4g.devops.iam.client.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import static org.springframework.http.HttpMethod.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_BASE;
import com.wl4g.devops.common.bean.iam.model.BasedModel;
import com.wl4g.devops.common.utils.bean.BeanMapConvert;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.client.config.IamClientProperties;

/**
 * Abstract validator implementation for tickets that must be validated against
 * a server.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月19日
 * @since
 */
public abstract class AbstractBasedIamValidator<R extends BasedModel, A> implements IamValidator<R, A> {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * IAM client properties
	 */
	final protected IamClientProperties config;

	/**
	 * RestTemplate
	 */
	final protected RestTemplate restTemplate;

	/**
	 * Constructs a new TicketValidator with the casServerUrlPrefix.
	 *
	 * @param casServerUrlPrefix
	 *            the location of the CAS server.
	 */
	protected AbstractBasedIamValidator(IamClientProperties config, RestTemplate restTemplate) {
		Assert.notNull(config, "'iamClientProperties' cannot be null.");
		Assert.notNull(restTemplate, "'restTemplate' cannot be null.");
		this.config = config;
		this.restTemplate = restTemplate;
	}

	/**
	 * Contacts the CAS Server to retrieve the response for the ticket
	 * validation.
	 *
	 * @param endpoint
	 *            the validate API endpoint
	 * @param req
	 *            the ticket parameters.
	 * @return the response from the CAS server.
	 */
	protected RespBase<A> doGetRemoteValidate(String endpoint, R req) {
		Assert.hasText(endpoint, "Validate endpoint must not be empty.");
		Assert.notNull(req, "Validate parameters must not be null.");

		StringBuffer url = new StringBuffer(config.getServerUri());
		url.append(URI_S_BASE).append("/").append(endpoint).append("?");

		// To request query parameters
		Map<String, Object> queryParams = new LinkedHashMap<String, Object>() {
			private static final long serialVersionUID = -7635430767361691087L;
			{
				put(config.getParam().getApplication(), req.getApplication());
			}
		};

		// Process URL query parameters
		postQueryParameterSet(req, queryParams);

		// Append parameters to URL
		url.append(BeanMapConvert.toUriParmaters(queryParams));
		if (log.isInfoEnabled()) {
			log.info("Grant ticket validate url: {}", url);
		}

		// Add header
		HttpEntity<R> entity = new HttpEntity<>(req, new LinkedMultiValueMap<String, String>(1) {
			private static final long serialVersionUID = -630070874678386724L;
			{
				add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
			}
		});

		// Request execute
		RespBase<A> resp = null;
		try {
			resp = restTemplate.exchange(url.toString(), POST, entity, getTypeReference()).getBody();
			if (log.isInfoEnabled()) {
				log.info("Grant ticket validate retrieve: {}", resp);
			}
		} catch (Throwable ex) {
			throw new RestClientException(String.format("Failed to validating ticket via URL: %s", url), ex);
		}
		return resp;
	}

	/**
	 * URL and parameter subsequent processing setting. See: {@link Synchronize
	 * with xx.xx.session.mgt.IamSessionManager#getSessionId}
	 * 
	 * @param req
	 *            request model
	 * @param queryParams
	 *            Validation request parameters
	 */
	protected abstract void postQueryParameterSet(R req, Map<String, Object> queryParams);

	/**
	 * Get parameterizedTypeReference
	 * 
	 * @return
	 */
	protected abstract ParameterizedTypeReference<RespBase<A>> getTypeReference();

}