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
package com.wl4g.devops.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("deprecation")
public class RestTemplateUploadTests {

	public static void main(String[] args) throws IOException {
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("myfile1", new FileSystemResource(createSampleFile("upload-file1-")));
		bodyMap.add("myfile2", new FileSystemResource(createSampleFile("upload-file2-")));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

		RestTemplate restTemplate = new RestTemplate(new Netty4ClientHttpRequestFactory());
		ResponseEntity<String> response = restTemplate.exchange("http://localhost:60000/upload", HttpMethod.POST, requestEntity,
				String.class);
		System.out.println("response status: " + response.getStatusCode());
		System.out.println("response body: " + response.getBody());
	}

	/**
	 * Create sample temporary file
	 * 
	 * @return
	 */
	private static File createSampleFile(String prefix) {
		try {
			File file = File.createTempFile(prefix, ".txt");
			file.deleteOnExit();

			Writer writer = new OutputStreamWriter(new FileOutputStream(file));
			writer.write("abcdefghijklmnopqrstuvwxyz\n");
			writer.write("0123456789011234567890\n");
			writer.close();

			return file;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
