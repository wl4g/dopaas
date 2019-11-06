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
package com.wl4g.devops.iam.controller;

import com.wl4g.devops.common.bean.share.EntryAddress;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.share.EntryAddressDao;
import com.wl4g.devops.iam.common.web.model.SessionDestroyModel;
import com.wl4g.devops.iam.common.web.model.SessionQueryModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_API_V1_BASE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_API_V1_SESSION;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

/**
 * IAM management API v1 controller.</br>
 * For example, get the API of Iam service of remote independent deployment.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月4日
 * @since
 */
@RestController
@RequestMapping("/mgr/v1")
public class IamManagerApiV1Controller extends BaseController {

	@Autowired
	protected RestTemplate restTemplate;

	@Autowired
	private EntryAddressDao entryAddressDao;

	@Value("${spring.profiles.active}")
	private String profile;

	/**
	 * Obtain remote IAM server sessions.
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "getSessions")
	public RespBase<?> getRemoteSessions(@Validated SessionQueryModel query, Integer id) throws Exception {
		notNull(id, "Please select a Iam server");
		if (log.isInfoEnabled()) {
			log.info("Get remote sessions for <= {} ...", query);
		}

		// Get remote IAM base URI.
		EntryAddress entryAddress = entryAddressDao.selectByPrimaryKey(id);
		String url = getRemoteApiV1SessionUri(entryAddress.getExtranetBaseUri());
		if (log.isInfoEnabled()) {
			log.info("Request get remote sessions for: {}", url);
		}

		// Do exchange.
		RespBase<Object> resp = restTemplate
				.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<RespBase<Object>>() {
				}).getBody();

		if (log.isInfoEnabled()) {
			log.info("Got remote sessions response for => {}", resp);
		}
		return resp;
	}

	@RequestMapping(path = "getIamServer")
	public RespBase<?> getIamServer() throws Exception {
		RespBase<Object> resp = RespBase.create();
		List<EntryAddress> iamServer = entryAddressDao.getIamServer();
		resp.getData().put("data", iamServer);
		return resp;
	}

	/**
	 * Destroy cleanup remote session.
	 * 
	 * @param destroy
	 * @return
	 * @throws Exception
	 */
	@PostMapping(path = "destroySessions")
	public RespBase<?> destroyRemoteSession(@Validated SessionDestroyModel destroy) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("Destroy remote sessions by <= {}", destroy);
		}

		// TODO --- get remote api baseUri from DB.

		String url = getRemoteApiV1SessionUri("");
		log.info("Request destroy remote sessions for: {}", url);
		// Do request.
		RespBase<String> resp = restTemplate
				.exchange(url, HttpMethod.DELETE, null, new ParameterizedTypeReference<RespBase<String>>() {
				}).getBody();

		log.info("Destroyed remote sessions response for => {}", resp);
		return null;
	}

	/**
	 * Get remote API v1 session URI.
	 * 
	 * @param remoteBaseUri
	 * @return
	 */
	private String getRemoteApiV1SessionUri(String remoteBaseUri) {
		hasText(remoteBaseUri, "Iam mangement for to remoteApiBase URI must not be empty");
		return remoteBaseUri + URI_S_API_V1_BASE + URI_S_API_V1_SESSION;
	}

}