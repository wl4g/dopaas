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
package com.wl4g.paas.udm.fsview.controller;

import com.wl4g.component.common.lang.Assert2;
import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.paas.udm.fsview.service.FsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.wl4g.paas.udm.fsview.util.PathUtils.splicePath;

@RestController
@RequestMapping("/fs")
public class FsController {

	@Autowired
	private FsService fsService;

	@RequestMapping("getTreeFiles")
	RespBase<?> getTreeFiles(String subPath){
		RespBase<Object> resp = RespBase.create();
		resp.setData(fsService.getTreeFiles(subPath));
		return resp;
	}

//	@RequestMapping("getFilesByParent")
//	RespBase<?> getFilesByParent(String parentPath){
//		RespBase<Object> resp = RespBase.create();
//		resp.setData(fsService.getFilesByParent(parentPath));
//		return resp;
//	}

	@RequestMapping("getFileInfo")
	RespBase<?> getFileInfo(String subPath,String path) throws IOException{
		RespBase<Object> resp = RespBase.create();
		Assert2.hasTextOf(subPath,"subPath");
		resp.setData(fsService.getFileInfo(path,subPath));
		return resp;
	}

	@RequestMapping("delFile")
	RespBase<?> delFile(String subPath,String path) throws IOException{
		RespBase<Object> resp = RespBase.create();
		fsService.delFile(splicePath(subPath , path));
		return resp;
	}

	@RequestMapping("addDir")
	RespBase<?> addDir(String subPath, String path) throws IOException{
		RespBase<Object> resp = RespBase.create();
		fsService.addDir(splicePath(subPath , path));
		return resp;
	}

	@RequestMapping("addFile")
	RespBase<?> addFile(String subPath, String path) throws IOException{
		RespBase<Object> resp = RespBase.create();
		fsService.addFile(splicePath(subPath , path));
		return resp;
	}

	@RequestMapping("renameFile")
	RespBase<?> renameFile(String subPath,String path,String toPath) throws IOException{
		RespBase<Object> resp = RespBase.create();
		fsService.renameFile(splicePath(subPath , path), splicePath(subPath , toPath));
		return resp;
	}

	@RequestMapping("saveFile")
	RespBase<?> saveFile(String subPath, String path, String content){
		RespBase<Object> resp = RespBase.create();
		fsService.saveFile(splicePath(subPath , path), content);
		return resp;
	}

	@PostMapping(value = "/uploadFile")
	public RespBase<?> uploadFile(@RequestParam(value = "file") MultipartFile file,String path,String subPath) {
		RespBase<Object> resp = RespBase.create();
		String fileUrl = fsService.uploadFile(file, splicePath(subPath , path));
		resp.setData(fileUrl);
		return resp;
	}

	@RequestMapping(value = "/downloadFile")
	public ResponseEntity<FileSystemResource> downloadFile(String subPath,String path)
			throws IOException {
		return fsService.downloadFile(splicePath(subPath , path));
	}

}