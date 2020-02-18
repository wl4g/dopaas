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
package com.wl4g.devops.doc.controller;

import com.wl4g.devops.common.bean.doc.FileChanges;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.doc.service.FileService;
import com.wl4g.devops.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Dictionaries controller
 * 
 * @author vjay
 * @date 2019-06-24 14:23:00
 */
@RestController
@RequestMapping("/file")
public class FileController extends BaseController {
	
	@Autowired
	private FileService fileService;

	@RequestMapping(value = "/list")
	public RespBase<?> list(PageModel pm, String name, String lang,Integer labelId) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(fileService.list(pm, name, lang,labelId));
		return resp;
	}

	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody FileChanges file) {
		RespBase<Object> resp = RespBase.create();
		fileService.save(file);
		return resp;
	}

	@RequestMapping(value = "/saveUpload")
	public RespBase<?> saveUpload(@RequestBody FileChanges file) {
		RespBase<Object> resp = RespBase.create();
		fileService.saveUpload(file);
		return resp;
	}

	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		RespBase<Object> resp = RespBase.create();
		FileChanges detail = fileService.detail(id);
		resp.setData(detail);
		return resp;
	}

	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		RespBase<Object> resp = RespBase.create();
		fileService.del(id);
		return resp;
	}

	@RequestMapping(value = "/getHistoryByFileCode")
	public RespBase<?> getChangesByFileId(String fileCode) {
		RespBase<Object> resp = RespBase.create();
		List<FileChanges> changesByFileId = fileService.getHistoryByFileCode(fileCode);
		resp.setData(changesByFileId);
		return resp;
	}

	@RequestMapping(value = "/compareWith")
	public RespBase<?> compareWith(Integer oldChangesId,Integer newChangesId) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(fileService.compareWith(oldChangesId,newChangesId));
		return resp;
	}

	@PostMapping(value = "/upload")
	public RespBase<?> upload(@RequestParam(value = "file") MultipartFile file) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(fileService.upload(file));
		return resp;
	}


	@RequestMapping(value = "/shareFile")
	public RespBase<?> shareFile(Integer id, Boolean isEncrypt) {
		RespBase<Object> resp = RespBase.create();
		String passwd = fileService.shareFile(id, isEncrypt);
		resp.setData(passwd);
		return resp;
	}





}