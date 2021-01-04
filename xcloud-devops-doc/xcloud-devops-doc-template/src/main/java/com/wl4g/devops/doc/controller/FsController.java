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
package com.wl4g.devops.doc.controller;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.devops.doc.service.FsService;
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

	@RequestMapping("getTreeFiles")
	RespBase<?> getTreeFiles(String parentPath){
		RespBase<Object> resp = RespBase.create();
		resp.setData(fsService.getTreeFiles());
		return resp;
	}

//	@RequestMapping("getFilesByParent")
//	RespBase<?> getFilesByParent(String parentPath){
//		RespBase<Object> resp = RespBase.create();
//		resp.setData(fsService.getFilesByParent(parentPath));
//		return resp;
//	}

	@RequestMapping("getFileInfo")
	RespBase<?> getFileInfo(String path) throws IOException{
		RespBase<Object> resp = RespBase.create();
		resp.setData(fsService.getFileInfo(path));
		return resp;
	}

	@RequestMapping("delFile")
	RespBase<?> delFile(String path) throws IOException{
		RespBase<Object> resp = RespBase.create();
		fsService.delFile(path);
		return resp;
	}

	@RequestMapping("addDir")
	RespBase<?> addDir(String path) throws IOException{
		RespBase<Object> resp = RespBase.create();
		fsService.addDir(path);
		return resp;
	}

	@RequestMapping("addFile")
	RespBase<?> addFile(String path) throws IOException{
		RespBase<Object> resp = RespBase.create();
		fsService.addFile(path);
		return resp;
	}

	@RequestMapping("renameFile")
	RespBase<?> renameFile(String path,String toPath) throws IOException{
		RespBase<Object> resp = RespBase.create();
		fsService.renameFile(path, toPath);
		return resp;
	}

	@RequestMapping("saveFile")
	RespBase<?> saveFile(String path, String content){
		RespBase<Object> resp = RespBase.create();
		fsService.saveFile(path, content);
		return resp;
	}

	@PostMapping(value = "/uploadFile")
	public RespBase<?> uploadFile(@RequestParam(value = "file") MultipartFile file,String path) {
		RespBase<Object> resp = RespBase.create();
		String fileUrl = fsService.uploadFile(file, path);
		resp.setData(fileUrl);
		return resp;
	}

	@RequestMapping(value = "/downloadFile")
	public ResponseEntity<FileSystemResource> downloadFile(String path)
			throws IOException {
		return fsService.downloadFile(path);
	}

}