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
package com.wl4g.devops.vcs.operator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.vcs.operator.model.VcsBranchModel;
import com.wl4g.devops.vcs.operator.model.VcsGroupModel;
import com.wl4g.devops.vcs.operator.model.VcsProjectModel;
import com.wl4g.devops.vcs.operator.model.VcsTagModel;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.*;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.parseJSON;
import static org.springframework.util.Assert.*;

/**
 * Abstract VCS API operator.
 *
 * @author Wangl.sir
 * @version v1.0 2019年8月2日
 * @since
 */
public abstract class AbstractVcsOperator implements VcsOperator, InitializingBean {
	final protected Logger log = getLogger(getClass());

	/**
	 * Rest template.
	 */
	protected RestTemplate restTemplate;

	/**
	 * Do VCS apiServer exchange.
	 *
	 * @param credentials
	 * @param url
	 * @param typeRef
	 * @return
	 */
	protected <T> T doRemoteExchange(Vcs credentials, String url, HttpHeaders headers, TypeReference<T> typeRef) {
		// Create httpEntity.
		HttpEntity<String> entity = createVcsRequestHttpEntity(credentials);

		// Do request.
		ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		if (null == resp || HttpStatus.OK != resp.getStatusCode()) {
			throw new IllegalStateException(String.format("Failed to request vcs remote, status: %s, body: %s",
					resp.getStatusCodeValue(), resp.getBody()));
		}
		if (log.isInfoEnabled()) {
			log.info("Vcs remote response <= {}", resp.getBody());
		}
		if (Objects.nonNull(headers)) {
			headers.putAll(resp.getHeaders());
		}
		return parseJSON(resp.getBody(), typeRef);
	}

	/**
	 * Do VCS apiServer post.
	 *
	 * @param credentials
	 * @param url
	 * @param typeRef
	 * @return
	 */
	protected <T> T doRemotePost(Vcs credentials, String url, HttpHeaders headers, TypeReference<T> typeRef) {
		// Create httpEntity.
		HttpEntity<String> entity = createVcsRequestHttpEntity(credentials);
		// Do request.
		ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		if (null == resp || (HttpStatus.OK != resp.getStatusCode() && HttpStatus.CREATED != resp.getStatusCode())) {
			throw new IllegalStateException(String.format("Failed to request vcs remote, status: %s, body: %s",
					resp.getStatusCodeValue(), resp.getBody()));
		}
		if (log.isInfoEnabled()) {
			log.info("Vcs remote response <= {}", resp.getBody());
		}
		if (Objects.nonNull(headers)) {
			headers.putAll(resp.getHeaders());
		}
		return parseJSON(resp.getBody(), typeRef);
	}

	/**
	 * Create vcs API http request entity.
	 * 
	 * @param credentials
	 * @return
	 */
	protected abstract HttpEntity<String> createVcsRequestHttpEntity(Vcs credentials);

	@Override
	public <T extends VcsBranchModel> List<T> getRemoteBranchs(Vcs credentials, int projectId) {
		notNull(credentials, "Get remote branchs credentials can't is null.");
		isTrue(projectId > 0, "Get remote branchs must projectId >= 0");
		if (log.isInfoEnabled()) {
			log.info("Get remote branchs by projectId: {}", projectId);
		}
		return null;
	}

	@Override
	public <T extends VcsTagModel> List<T> getRemoteTags(Vcs credentials, int projectId) {
		notNull(credentials, "Get remote tags credentials can't is null.");
		isTrue(projectId >= 0, "Get remote tags must projectId >= 0");
		if (log.isInfoEnabled()) {
			log.info("Get remote tags by projectId: {}", projectId);
		}
		return null;
	}

	@Override
	public Integer getRemoteProjectId(Vcs credentials, String projectName) {
		notNull(credentials, "Get remote projectId credentials can't is null.");
		hasText(projectName, "Get remote projectId can't is empty");
		if (log.isInfoEnabled()) {
			log.info("Search remote projectIds by projectName: {}", projectName);
		}
		return null;
	}

	@Override
	public <T extends VcsProjectModel> List<T> searchRemoteProjects(Vcs credentials, Integer groupId, String projectName,
			int limit, PageModel pm) {
		notNull(credentials, "Search remote projects credentials can't is null.");
		/*
		 * The item name to be searched can be empty. If it is empty, it means
		 * unconditional.
		 */
		// hasText(projectName, "Search remote projects name can't is empty");
		isTrue(limit > 0, "Search remote projects must limit > 0");
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		factory.setConnectTimeout(10_000);
		factory.setReadTimeout(60_000);
		factory.setMaxResponseSize(1024 * 1024 * 10);
		this.restTemplate = new RestTemplate(factory);
		this.restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
	}

	@Override
	public <T extends VcsGroupModel> List<T> searchRemoteGroups(Vcs credentials, String groupName, int limit) {
		return null;
	}

	@Override
	public <T extends VcsProjectModel> T searchRemoteProjectsById(Vcs credentials, Integer projectId) {
		return null;
	}

	@Override
	public <T extends VcsBranchModel> T createRemoteBranch(Vcs credentials, int projectId, String branch, String ref) {
		return null;
	}

	@Override
	public <T extends VcsTagModel> T createRemoteTag(Vcs credentials, int projectId, String tag, String ref, String message,
			String releaseDescription) {
		return null;
	}

	@Override
	public <T> T clone(Vcs credentials, String remoteUrl, String projecDir, String branchName) throws IOException {
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
	public <T> T checkoutAndPull(Vcs credentials, String projecDir, String branchName, VcsAction action) {
		notNull(credentials, "Checkout & pull credentials is requires.");
		hasText(projecDir, "Checkout & pull projecDir can't is empty");
		hasText(branchName, "Checkout & pull branchName can't is empty");
		if (log.isInfoEnabled()) {
			log.info("Checkout & pull for projecDir: {}, branchName: {}", projecDir, branchName);
		}
		return null;
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
	public boolean hasLocalRepository(String projecDir) {
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
	public <T> T rollback(Vcs credentials, String projecDir, String sign) {
		notNull(credentials, "Rollback credentials is requires.");
		hasText(projecDir, "Rollback projecDir can't is empty");
		hasText(sign, "Rollback sign can't is empty");
		if (log.isInfoEnabled()) {
			log.info("Rollback for projecDir: {}, sign: {}", projecDir, sign);
		}
		return null;
	}
}