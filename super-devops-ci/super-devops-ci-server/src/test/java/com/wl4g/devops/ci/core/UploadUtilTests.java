package com.wl4g.devops.ci.core;

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
