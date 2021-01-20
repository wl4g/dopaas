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
package com.wl4g.devops.ci.web;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.devops.ci.config.CiProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

//TODO using by coss-sdk
/**
 * @author vjay
 * @date 2020-01-06 17:16:00
 */
@RestController
@RequestMapping("/fs")
public class TmpTestedReportFSController {

	@Autowired
	protected CiProperties config;

	@PostMapping(value = "/upload")
	public RespBase<?> upload(@RequestParam(value = "file") MultipartFile file) {
		RespBase<Object> resp = RespBase.create();
		String fileName = file.getOriginalFilename();// 文件名
		String suffixName = fileName.substring(fileName.lastIndexOf("."));// 后缀名
		fileName = UUID.randomUUID() + suffixName;// 新文件名
		String path = config.getTestedReport().getUploadPath() + "/" + fileName;
		saveFile(file, path);
		resp.setData(config.getTestedReport().getDownloadUrl() + "/" + fileName);
		return resp;
	}

	private void saveFile(MultipartFile file, String localPath) {
		Assert.notNull(file, "文件为空");
		File dest = new File(localPath);
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		try {
			file.transferTo(dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/download/{fileName:.+}")
	public ResponseEntity<FileSystemResource> exportXls(@PathVariable String fileName) {
		File file = new File(config.getTestedReport().getUploadPath() + "/" + fileName);
		return export(file);
	}

	public ResponseEntity<FileSystemResource> export(File file) {
		if (file == null) {
			return null;
		}
		if (!file.exists()) {
			return null;
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		String suffixName = file.getName().substring(file.getName().lastIndexOf("."));// 后缀名
		headers.add("Content-Disposition", "attachment; filename=" + System.currentTimeMillis() + suffixName);
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		headers.add("Last-Modified", new Date().toString());
		headers.add("ETag", String.valueOf(System.currentTimeMillis()));

		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(new FileSystemResource(file));
	}

}