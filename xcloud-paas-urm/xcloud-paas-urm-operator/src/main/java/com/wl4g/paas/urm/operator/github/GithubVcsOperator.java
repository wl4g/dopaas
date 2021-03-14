/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.paas.urm.operator.github;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.component.support.redis.jedis.JedisService;
import com.wl4g.component.support.redis.locks.JedisLockManager;
import com.wl4g.paas.common.bean.uci.Vcs;
import com.wl4g.paas.common.bean.urm.CompositeBasicVcsProjectModel;
import com.wl4g.paas.urm.operator.GenericBasedGitVcsOperator;
import com.wl4g.paas.urm.operator.model.VcsBranchModel;
import com.wl4g.paas.urm.operator.model.VcsProjectModel;
import com.wl4g.paas.urm.operator.model.VcsTagModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static com.wl4g.component.common.collection.CollectionUtils2.safeList;
import static com.wl4g.component.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.component.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * VCS operator for GITHUB.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月5日
 * @since
 */
public class GithubVcsOperator extends GenericBasedGitVcsOperator {

	@Autowired
	private JedisService jedisService;

	@Autowired
	private JedisLockManager jedisLockManager;

	@Override
	public VcsProviderKind kind() {
		return VcsProviderKind.GITHUB;
	}

	@Override
	public List<VcsBranchModel> getRemoteBranchs(Vcs credentials, CompositeBasicVcsProjectModel vcsProject) throws Exception {
		super.getRemoteBranchs(credentials, vcsProject);

		// Search projects.
		String url = format((credentials.getBaseUri() + "/repos/%s/branches"), vcsProject.getPathWithNamespace());
		return doRemoteRequest(GET, credentials, url, null, new ParameterizedTypeReference<List<VcsBranchModel>>() {
		}).getBody();
	}

	@Override
	public List<VcsTagModel> getRemoteTags(Vcs credentials, CompositeBasicVcsProjectModel vcsProject) throws Exception {
		super.getRemoteTags(credentials, vcsProject);

		// Search projects.
		String url = format((credentials.getBaseUri() + "/repos/%s/tags"), vcsProject.getPathWithNamespace());
		return doRemoteRequest(GET, credentials, url, null, new ParameterizedTypeReference<List<VcsTagModel>>() {
		}).getBody();
	}

	@Override
	public Long getRemoteProjectId(Vcs credentials, String projectName) throws Exception {
		super.getRemoteProjectId(credentials, projectName);
		throw new UnsupportedOperationException();
	}

	@Override
	public List<VcsProjectModel> searchRemoteProjects(Vcs credentials, Long groupId, String projectName, SearchMeta meta)
			throws Exception {
		super.searchRemoteProjects(credentials, groupId, projectName, meta);

		String redisKey = DEFAULT_CACHE_KEY.concat(valueOf(credentials.getId()));

		List<GithubV3SimpleProjectModel> projects = getCachedProjects(credentials);
		if (!isEmpty(projects)) {
			log.debug("Got from cache, size: {}, Repositories: {}", projects.size(), projects);
			return safeList(projects).stream().filter(project -> nonNull(project) && contains(project.getName(), projectName))
					.collect(toList());
		} else {
			// Due to the slow response of requests, it is necessary to increase
			// the request lock in order to solve the problem that the cache
			// cannot be hit during concurrent requests.
			Lock lock = jedisLockManager.getLock(redisKey);
			log.debug("Trying get from Github, key: {}", redisKey);
			try {
				if (lock.tryLock(10, SECONDS)) {
					projects = getCachedProjects(credentials);
					if (isEmpty(projects)) {
						projects = doQueryRemoteProjects(credentials);
						if (!isEmpty(projects)) {
							jedisService.set(redisKey, toJSONString(projects), DEFAULT_CACHE_EXPIRE_MS);
						}
					}
				}
			} finally {
				lock.unlock();
				log.debug("Unlock get from Github, key: {}", redisKey);
			}
		}

		return safeList(projects).stream().filter(project -> nonNull(project) && contains(project.getName(), projectName))
				.collect(Collectors.toList());
	}

	@Override
	protected HttpEntity<String> createRequestEntity(Vcs credentials) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/vnd.github.v3+json");
		headers.add("Authorization", "token " + credentials.getAccessToken());
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		return entity;
	}

	/**
	 * Gets GITHUB v3 projects from cached.
	 * 
	 * @param credentials
	 * @return
	 * @throws Exception
	 */
	private List<GithubV3SimpleProjectModel> getCachedProjects(Vcs credentials) throws Exception {
		String cacheKey = DEFAULT_CACHE_KEY.concat(valueOf(credentials.getId()));
		String projects = jedisService.get(cacheKey);
		if (isNotBlank(projects)) {
			List<GithubV3SimpleProjectModel> result = parseJSON(projects, new TypeReference<List<GithubV3SimpleProjectModel>>() {
			});
			jedisService.expire(cacheKey, DEFAULT_CACHE_EXPIRE_MS);
			return result;
		}
		return null;
	}

	/**
	 * DO request query remote GITHUB servers projects.
	 * 
	 * @param credentials
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private List<GithubV3SimpleProjectModel> doQueryRemoteProjects(Vcs credentials) throws Exception {
		int limit = 100;
		int pageNum = 1;
		List<GithubV3SimpleProjectModel> result = new ArrayList<>();
		boolean next = true;
		while (next) {
			// @see:https://docs.github.com/en/free-pro-team@latest/rest/reference/repos#create-a-repository-for-the-authenticated-user--code-samples
			String url = format((credentials.getBaseUri() + "/user/repos?per_page=%s&page=%s"), limit, pageNum);
			// Search projects.
			List<GithubV3SimpleProjectModel> projects = doRemoteRequest(GET, credentials, url, null,
					new ParameterizedTypeReference<List<GithubV3SimpleProjectModel>>() {
					}).getBody();
			log.debug("Receiving search GITHUB projects: {}", () -> toJSONString(projects));

			result.addAll(projects);
			if (projects.size() < limit) {
				next = false;
			} else {
				pageNum++;
			}
		}
		return result;
	}

	private static final String DEFAULT_CACHE_KEY = "GITHUB_CACHE_";
	private static final long DEFAULT_CACHE_EXPIRE_MS = 60 * 1000;

}