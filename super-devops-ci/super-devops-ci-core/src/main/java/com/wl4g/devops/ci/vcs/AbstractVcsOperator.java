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
package com.wl4g.devops.ci.vcs;

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
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.util.List;

/**
 * Abstract VCS API operator.
 *
 * @author Wangl.sir
 * @version v1.0 2019年8月2日
 * @since
 */
public abstract class AbstractVcsOperator implements VcsOperator, InitializingBean {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected CiCdProperties config;

	/**
	 * Rest template.
	 */
	protected RestTemplate restTemplate;

	/**
	 * Do VCS apiServer exchange.
	 *
	 * @param url
	 * @param typeRef
	 * @return
	 */
	protected <T> T doGitExchange(String url, TypeReference<T> typeRef) {
		// Create httpEntity.
		HttpEntity<String> entity = createVcsRequestHttpEntity();

		// Do request.
		ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		if (null == resp || HttpStatus.OK != resp.getStatusCode()) {
			throw new IllegalStateException(String.format("Failed to request vcs remote, status: %s, body: %s",
					resp.getStatusCodeValue(), resp.getBody()));
		}
		if (log.isInfoEnabled()) {
			log.info("Vcs remote response <= {}", resp.getBody());
		}
		return parseJSON(resp.getBody(), typeRef);
	}

	/**
	 * Create vcs API http request entity.
	 * 
	 * @return
	 */
	protected abstract HttpEntity<String> createVcsRequestHttpEntity();

	@Override
	public void afterPropertiesSet() throws Exception {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		this.restTemplate = new RestTemplate(factory);
	}

	@Override
	public List<String> getRemoteBranchNames(int projectId) {
		isTrue(projectId > 0, "Get remote branchs must projectId >= 0");
		if (log.isInfoEnabled()) {
			log.info("Get remote branchs by projectId: {}", projectId);
		}
		return null;
	}

	@Override
	public List<String> getRemoteTags(int projectId) {
		isTrue(projectId > 0, "Get remote tags must projectId >= 0");
		if (log.isInfoEnabled()) {
			log.info("Get remote tags by projectId: {}", projectId);
		}
		return null;
	}

	@Override
	public Integer findRemoteProjectId(String projectName) {
		hasText(projectName, "Project name can't is empty");
		if (log.isInfoEnabled()) {
			log.info("Search remote projectIds by projectName: {}", projectName);
		}
		return null;
	}

	@Override
	public <T> T clone(Object credentials, String remoteUrl, String projecDir, String branchName) throws IOException {
		notNull(credentials, "Clone credentials is requires.");
		hasText(remoteUrl, "Clone remoteUrl can't is empty");
		hasText(projecDir, "Clone projecDir can't is empty");
		hasText(branchName, "Clone branchName can't is empty");
		if (log.isInfoEnabled()) {
			log.info("Cloning VCS repository for remoteUrl: {}, projecDir: {}, branchName:{}", remoteUrl, projecDir, branchName);
		}
		return null;
	}

	@Override
	public void checkoutAndPull(Object credentials, String projecDir, String branchName) {
		notNull(credentials, "Checkout & pull credentials is requires.");
		hasText(projecDir, "Checkout & pull projecDir can't is empty");
		hasText(branchName, "Checkout & pull branchName can't is empty");
		if (log.isInfoEnabled()) {
			log.info("Checkout & pull for projecDir: {}, branchName: {}", projecDir, branchName);
		}
	}

	@Override
	public List<String> delLocalBranch(String projecDir, String branchName, boolean force) {
		hasText(projecDir, "Deletion local branch projecDir can't is empty");
		hasText(branchName, "Deletion local branch  branchName can't is empty");
		if (log.isInfoEnabled()) {
			log.info("Deletion local branch for projecDir: {}, branchName: {}, force: {}", projecDir, branchName, force);
		}
		return null;
	}

	@Override
	public boolean ensureRepo(String projecDir) {
		hasText(projecDir, "Check VCS repository projecDir can't is empty");
		if (log.isInfoEnabled()) {
			log.info("Check VCS repository for projecDir: {}", projecDir);
		}
		return false;
	}

	@Override
	public String getLatestCommitted(String projecDir) throws Exception {
		hasText(projecDir, "Get committed projecDir can't is empty");
		if (log.isInfoEnabled()) {
			log.info("Get latest committed for projecDir: {}", projecDir);
		}
		return null;
	}

	@Override
	public <T> T rollback(Object credentials, String projecDir, String sign) {
		notNull(credentials, "Rollback credentials is requires.");
		hasText(projecDir, "Rollback projecDir can't is empty");
		hasText(sign, "Rollback sign can't is empty");
		if (log.isInfoEnabled()) {
			log.info("Rollback for projecDir: {}, sign: {}", projecDir, sign);
		}
		return null;
	}

}