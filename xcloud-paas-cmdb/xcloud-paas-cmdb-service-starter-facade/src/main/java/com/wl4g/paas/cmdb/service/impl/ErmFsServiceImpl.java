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
package com.wl4g.paas.cmdb.service.impl;

import com.wl4g.component.common.lang.Assert2;
import com.wl4g.component.common.lang.DateUtils2;
import com.wl4g.paas.cmdb.config.FsProperties;
import com.wl4g.paas.cmdb.service.FsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
public class ErmFsServiceImpl implements FsService {

	@Autowired
	private FsProperties fsProperties;

	@Override
	public String uploadFile(MultipartFile file) {
		Date now = new Date();
		String fileName = file.getOriginalFilename();// 文件名
		String suffixName = fileName.substring(fileName.lastIndexOf("."));// 后缀名
		fileName = UUID.randomUUID() + suffixName;// 新文件名
		fileName = "/" + DateUtils2.formatDate(now, "yyyyMMddHHmmss") + "/" + fileName;// 加一级日期目录
		String path = fsProperties.getBaseFilePath() + fileName;
		saveFile(file, path);
		return fsProperties.getBaseFileUrl() + fileName;
	}

	@Override
	public ResponseEntity<FileSystemResource> downloadFile(String path) throws IOException {
		File file = new File(fsProperties.getBaseFilePath() + "/" + path);
		return downloadFile(file);
	}

	@Override
	public String uploadImg(MultipartFile img) {
		Date now = new Date();
		String fileName = img.getOriginalFilename();// 文件名
		String suffixName = fileName.substring(fileName.lastIndexOf("."));// 后缀名

		String fileCode = UUID.randomUUID().toString().replaceAll("-", "");
		fileName = "/" + DateUtils2.formatDate(now, "yyyyMMddHHmmss") + "/" + fileCode + suffixName;

		saveFile(img, fsProperties.getBaseImgPath() + fileName);
		return fsProperties.getBaseImgUrl() + fileName;
	}

	@Override
	public byte[] downloadImg(String path) throws IOException {
		Assert2.hasTextOf(path, "path");
		File file = new File(fsProperties.getBaseImgPath() + path);
		if (!file.exists() || !file.isFile()) {
			return null;
		}
		try (FileInputStream inputStream = new FileInputStream(file);) {
			byte[] bytes = new byte[inputStream.available()];
			inputStream.read(bytes, 0, inputStream.available());
			return bytes;
		}
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

	private ResponseEntity<FileSystemResource> downloadFile(File file) {
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