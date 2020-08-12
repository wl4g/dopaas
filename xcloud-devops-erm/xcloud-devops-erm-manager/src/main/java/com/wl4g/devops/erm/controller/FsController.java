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
package com.wl4g.devops.erm.controller;

import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.devops.erm.service.FsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/fs")
public class FsController {

	@Autowired
	private FsService fsService;

	@PostMapping(value = "/uploadImg")
	public RespBase<?> uploadImg(@RequestParam(value = "img") MultipartFile img) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(fsService.uploadImg(img));
		return resp;
	}

	@RequestMapping(value = "/downloadImg/{date}/{fileName:.+}") // produces =
																	// MediaType.TEXT_HTML
	public byte[] downloadImg(@PathVariable String date, @PathVariable String fileName) throws IOException {
		return fsService.downloadImg("/" + date + "/" + fileName);
	}

	@PostMapping(value = "/uploadFile")
	public RespBase<?> uploadFile(@RequestParam(value = "file") MultipartFile file) {
		RespBase<Object> resp = RespBase.create();
		String fileUrl = fsService.uploadFile(file);
		resp.setData(fileUrl);
		return resp;
	}

	@RequestMapping(value = "/downloadFile/{date}/{fileName:.+}")
	public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String date, @PathVariable String fileName)
			throws IOException {
		return fsService.downloadFile("/" + date + "/" + fileName);
	}

}