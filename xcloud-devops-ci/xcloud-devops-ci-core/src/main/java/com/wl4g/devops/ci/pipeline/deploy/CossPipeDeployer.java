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
package com.wl4g.devops.ci.pipeline.deploy;

import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.common.serialize.JacksonUtils;
import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.bean.ci.PipelineHistoryInstance;
import com.wl4g.components.core.bean.erm.AppInstance;
import com.wl4g.components.core.bean.iam.ClusterConfig;
import com.wl4g.devops.ci.pipeline.ViewNativePipelineProvider;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * NPM view deployments pipeline handler tasks.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
@SuppressWarnings("deprecation")
public class CossPipeDeployer extends GenericHostPipeDeployer<ViewNativePipelineProvider> {

	public CossPipeDeployer(ViewNativePipelineProvider provider, AppInstance instance,
			List<PipelineHistoryInstance> pipelineHistoryInstances) {
		super(provider, instance, pipelineHistoryInstances);
	}

	@Override
	protected void doRemoteDeploying(String remoteHost, String user, String sshkey) throws Exception {
		// super.doRemoteDeploying(remoteHost, user, sshkey);
		// TODO
		// String url = "http://localhost:8080/Lock/nbdc/remoteUpdate";
		// String filePath = "/Users/vjay/Downloads/logo.png";
		String localFile = config.getJobBackupDir(getContext().getPipelineHistory().getId()) + "/" + getPrgramInstallFileName()
				+ "." + DEFAULT_FILE_SUFFIX;
		// CossCluster cossCluster =
		// cossClusterDao.selectByPrimaryKey(instance.getCossId());

		ClusterConfig clusterConfig = clusterConfigDao.getByAppName("coss-manager", profile, null);
		String uploadServerUrl = clusterConfig.getExtranetBaseUri() + "/webservice/putObject";

		String cossRefBucket = instance.getCossRefBucket();
		Assert2.hasTextOf(cossRefBucket, "cossRefBucket");
		String[] split = cossRefBucket.split(":");
		Assert2.notEmptyOf(split, "cossRefBucket");
		Assert2.isTrue(split.length == 2, "cossRefBucket unmatch format, cossRefBucket=%s", cossRefBucket);
		String cossProvider = split[0];
		String bucketName = split[1];
		transFile(uploadServerUrl, new File(localFile), cossProvider, bucketName);
	}

	public static void transFile(String uploadUrl, File file, String cossProvider, String bucketName) {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		// factory.setConnectTimeout(10_000);
		// factory.setReadTimeout(60_000);
		// factory.setMaxResponseSize(1024 * 1024 * 10);
		RestTemplate restTemplate = new RestTemplate(factory);
		FileSystemResource resource = new FileSystemResource(file);
		MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
		param.add("file", resource);
		// need add few coss params
		param.add("cossProvider", cossProvider);
		param.add("bucketName", bucketName);
		param.add("acl", "default");
		RespBase respBase = restTemplate.postForObject(uploadUrl, param, RespBase.class);
		Assert2.isTrue(Objects.nonNull(respBase) && respBase.getCode() == 200, "TransFile Fail, cause: %s",
				JacksonUtils.toJSONString(respBase));
	}

}