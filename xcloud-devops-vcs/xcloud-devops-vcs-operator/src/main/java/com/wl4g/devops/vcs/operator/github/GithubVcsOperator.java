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
package com.wl4g.devops.vcs.operator.github;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.components.common.annotation.Reserved;
import com.wl4g.components.common.serialize.JacksonUtils;
import com.wl4g.components.core.bean.ci.Vcs;
import com.wl4g.components.core.bean.vcs.CompositeBasicVcsProjectModel;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.components.support.concurrent.locks.JedisLockManager;
import com.wl4g.components.support.redis.jedis.JedisService;
import com.wl4g.devops.vcs.operator.GenericBasedGitVcsOperator;
import com.wl4g.devops.vcs.operator.model.VcsBranchModel;
import com.wl4g.devops.vcs.operator.model.VcsTagModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.common.serialize.JacksonUtils.parseJSON;
import static java.lang.String.valueOf;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * VCS operator for GITHUB.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月5日
 * @since
 */
@Reserved
public class GithubVcsOperator extends GenericBasedGitVcsOperator {

	final static private String REDIS_KEY = "GITHUB_CACHE_";

	final static private long DEFAULT_EXPIRE_MS = 60 * 1000;

	@Autowired
	private JedisService jedisService;

	@Autowired
	private JedisLockManager jedisLockManager;

	@Override
	public VcsProviderKind kind() {
		return VcsProviderKind.GITHUB;
	}

	@Override
	protected HttpEntity<String> createVcsRequestHttpEntity(Vcs credentials) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/vnd.github.v3+json");
		headers.add("Authorization", "token " + credentials.getAccessToken());
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		return entity;
	}

	@Override
	public List<VcsBranchModel> getRemoteBranchs(Vcs credentials, CompositeBasicVcsProjectModel vcsProject) throws Exception {
		super.getRemoteBranchs(credentials, vcsProject);
		String url = String.format((credentials.getBaseUri() + "/repos/%s/branches"), vcsProject.getPathWithNamespace());
		HttpHeaders headers = new HttpHeaders();
		// Search projects.
		List<VcsBranchModel> branchs = doRemoteExchangeSSL(credentials, url, headers, new TypeReference<List<VcsBranchModel>>() {
		});
		return branchs;
	}

	@Override
	public List<VcsTagModel> getRemoteTags(Vcs credentials, CompositeBasicVcsProjectModel vcsProject) throws Exception {
		super.getRemoteTags(credentials, vcsProject);
		String url = String.format((credentials.getBaseUri() + "/repos/%s/tags"), vcsProject.getPathWithNamespace());
		HttpHeaders headers = new HttpHeaders();
		// Search projects.
		List<VcsTagModel> tags = doRemoteExchangeSSL(credentials, url, headers, new TypeReference<List<VcsTagModel>>() {
		});
		return tags;
	}

	@Override
	public Long getRemoteProjectId(Vcs credentials, String projectName) throws Exception {
		super.getRemoteProjectId(credentials, projectName);
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<GithubV3SimpleProjectModel> searchRemoteProjects(Vcs credentials, Long groupId, String projectName, long limit,
			PageModel pm) throws Exception {
		super.searchRemoteProjects(credentials, groupId, projectName, limit, pm);

		String redisKey = REDIS_KEY.concat(valueOf(credentials.getId()));
		List<GithubV3SimpleProjectModel> result = getFromRedis(credentials);
		if (!isEmpty(result)) {
			log.debug("Got from cache, size: {}, Repositories: {}", result.size(), result);
			return safeList(result).stream().filter(project -> nonNull(project) && contains(project.getName(), projectName))
					.collect(Collectors.toList());
		} else {
			Lock lock = jedisLockManager.getLock(redisKey);
			log.debug("Trying get from Github, key: {}", redisKey);
			try {
				if (lock.tryLock(10, SECONDS)) {
					result = getFromRedis(credentials);
					if (isEmpty(result)) {
						result = getFromGithub(credentials);
						if (!isEmpty(result)) {
							jedisService.set(redisKey, JacksonUtils.toJSONString(result), DEFAULT_EXPIRE_MS);
						}
					}
				}
			} finally {
				lock.unlock();
				log.debug("Unlock get from Github, key: {}", redisKey);
			}
		}
		return safeList(result).stream().filter(project -> nonNull(project) && contains(project.getName(), projectName))
				.collect(Collectors.toList());
	}

	private List<GithubV3SimpleProjectModel> getFromRedis(Vcs credentials) throws Exception {
		String redisKey = REDIS_KEY.concat(valueOf(credentials.getId()));
		String resultJson = jedisService.get(redisKey);
		if (isNotBlank(resultJson)) {
			List<GithubV3SimpleProjectModel> result = parseJSON(resultJson,
					new TypeReference<List<GithubV3SimpleProjectModel>>() {
					});
			jedisService.expire(redisKey, DEFAULT_EXPIRE_MS);
			return result;
		}
		return null;
	}

	private List<GithubV3SimpleProjectModel> getFromGithub(Vcs credentials) throws Exception {
		log.info("into getFromGithub");
		int limit = 100;
		int pageNum = 1;
		List<GithubV3SimpleProjectModel> result = new ArrayList<>();
		boolean cycle = true;
		while (cycle) {
			String url = String.format((credentials.getBaseUri() + "/user/repos?per_page=%s&page=%s"), limit, pageNum);
			HttpHeaders headers = new HttpHeaders();
			// Search projects.
			List<GithubV3SimpleProjectModel> projects = doRemoteExchangeSSL(credentials, url, headers,
					new TypeReference<List<GithubV3SimpleProjectModel>>() {
					});
			result.addAll(projects);
			if (projects.size() < limit) {
				cycle = false;
			} else {
				pageNum++;
			}
		}
		return result;
	}

}