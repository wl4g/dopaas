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
package com.wl4g.devops.ci;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * @author vjay
 * @date 2020-05-28 17:38:00
 */
@SuppressWarnings("deprecation")
public class UploadUtilTests {

	// final static private String uploadUrl =
	// "http://localhost:14051/erm-manager/fs/uploadFile";
	final static private String uploadUrl = "http://localhost:14062/coss-manager/webservice/putObject";

	@Test
	public void uploadTest() throws GitAPIException {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		// factory.setConnectTimeout(10_000);
		// factory.setReadTimeout(60_000);
		// factory.setMaxResponseSize(1024 * 1024 * 10);
		RestTemplate restTemplate = new RestTemplate(factory);

		FileSystemResource resource = new FileSystemResource(new File("/Users/vjay/Downloads/logo.png"));
		MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
		param.add("deviceId", "123424");
		param.add("file", resource);
		String recv = restTemplate.postForObject(uploadUrl, param, String.class);
		System.out.println(recv);

		/*
		 * MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		 * map.add("id",id); HttpHeaders header = new HttpHeaders(); //
		 * 需求需要传参为form-data格式
		 * header.setContentType(MediaType.MULTIPART_FORM_DATA);
		 * HttpEntity<MultiValueMap<String, String>> httpEntity = new
		 * HttpEntity<>(map, header);
		 */

	}

}