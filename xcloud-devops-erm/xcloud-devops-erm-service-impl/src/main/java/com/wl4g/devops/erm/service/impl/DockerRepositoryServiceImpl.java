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
package com.wl4g.devops.erm.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.common.serialize.JacksonUtils;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.common.bean.erm.DockerRepository;
import com.wl4g.devops.common.bean.erm.model.RepositoryProject;
import com.wl4g.devops.erm.dao.DockerRepositoryDao;
import com.wl4g.devops.erm.service.DockerRepositoryService;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

import static com.wl4g.iam.core.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.iam.core.utils.IamOrganizationHolder.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class DockerRepositoryServiceImpl implements DockerRepositoryService {

	final private static String URL_FOR_PROJECT = "/api/v2.0/projects";

	@Autowired
	private DockerRepositoryDao dockerRepositoryDao;

	@Override
	public PageModel<DockerRepository> page(PageModel<DockerRepository> pm, String name) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(dockerRepositoryDao.list(getRequestOrganizationCodes(), name));
		return pm;
	}

	@Override
	public List<DockerRepository> getForSelect() {
		return dockerRepositoryDao.list(getRequestOrganizationCodes(), null);
	}

	public void save(DockerRepository dockerRepository) {
		Assert2.notNullOf(dockerRepository, "dockerRepository");
		if (Objects.nonNull(dockerRepository.getAuthConfigModel())) {
			dockerRepository.setAuthConfig(JacksonUtils.toJSONString(dockerRepository.getAuthConfigModel()));
		}
		if (isNull(dockerRepository.getId())) {
			dockerRepository.preInsert(getRequestOrganizationCode());
			insert(dockerRepository);
		} else {
			dockerRepository.preUpdate();
			update(dockerRepository);
		}
	}

	private void insert(DockerRepository dockerRepository) {
		dockerRepositoryDao.insertSelective(dockerRepository);
	}

	private void update(DockerRepository dockerRepository) {
		dockerRepositoryDao.updateByPrimaryKeySelective(dockerRepository);
	}

	public DockerRepository detail(Long id) {
		Assert.notNull(id, "id is null");
		DockerRepository dockerRepository = dockerRepositoryDao.selectByPrimaryKey(id);
		if (StringUtils.isNotBlank(dockerRepository.getAuthConfig())) {
			dockerRepository.setAuthConfigModel(
					JacksonUtils.parseJSON(dockerRepository.getAuthConfig(), DockerRepository.AuthConfigModel.class));
		}
		return dockerRepository;
	}

	public void del(Long id) {
		Assert.notNull(id, "id is null");
		DockerRepository dockerRepository = new DockerRepository();
		dockerRepository.setId(id);
		dockerRepository.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		dockerRepositoryDao.updateByPrimaryKeySelective(dockerRepository);
	}

	@Override
	public List<RepositoryProject> getRepositoryProjects(Long id, String address, String name)
			throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		if (Objects.nonNull(id) && id != -1) {
			DockerRepository dockerRepository = dockerRepositoryDao.selectByPrimaryKey(id);
			Assert2.notNullOf(dockerRepository, "dockerRepository");
			address = dockerRepository.getRegistryAddress();
		}
		if (StringUtils.isBlank(address)) {
			return null;
		}

		String url = "https://" + address + URL_FOR_PROJECT;
		if (StringUtils.isNotBlank(name)) {
			url = url + "?name=" + name;
		}
		// Netty4ClientHttpRequestFactory factory = new
		// Netty4ClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(generateHttpRequestFactory());
		ParameterizedTypeReference<List<RepositoryProject>> responseType = new ParameterizedTypeReference<List<RepositoryProject>>() {
		};
		ResponseEntity<List<RepositoryProject>> result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
		return result.getBody();
	}

	private HttpComponentsClientHttpRequestFactory generateHttpRequestFactory()
			throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
		TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
				new NoopHostnameVerifier());

		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setSSLSocketFactory(connectionSocketFactory);
		CloseableHttpClient httpClient = httpClientBuilder.build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(httpClient);
		return factory;
	}

}