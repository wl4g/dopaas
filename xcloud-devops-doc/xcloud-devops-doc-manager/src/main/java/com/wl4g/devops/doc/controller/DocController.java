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

import com.wl4g.component.common.lang.DateUtils2;
import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.component.core.bean.model.PageModel;
import com.wl4g.devops.common.bean.doc.FileChanges;
import com.wl4g.devops.common.bean.doc.Share;
import com.wl4g.devops.doc.service.DocService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * Dictionaries controller
 * 
 * @author vjay
 * @date 2019-06-24 14:23:00
 */
@RestController
@RequestMapping("/doc")
public class DocController extends BaseController {

	@Autowired
	private DocService docService;

	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "doc" }, logical = AND)
	public RespBase<?> list(PageModel<FileChanges> pm, String name, String lang, Long labelId) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(docService.list(pm, name, lang, labelId));
		return resp;
	}

	@RequestMapping(value = "/save")
	@RequiresPermissions(value = { "doc" }, logical = AND)
	public RespBase<?> save(@RequestBody FileChanges file) {
		RespBase<Object> resp = RespBase.create();
		docService.save(file);
		return resp;
	}

	@RequestMapping(value = "/saveUpload")
	public RespBase<?> saveUpload(@RequestBody FileChanges file) {
		RespBase<Object> resp = RespBase.create();
		docService.saveUpload(file);
		return resp;
	}

	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "doc" }, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		FileChanges detail = docService.detail(id);
		resp.setData(detail);
		return resp;
	}

	@RequestMapping(value = "/del")
	@RequiresPermissions(value = { "doc" }, logical = AND)
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		docService.del(id);
		return resp;
	}

	@RequestMapping(value = "/getHistoryByDocCode")
	@RequiresPermissions(value = { "doc" }, logical = AND)
	public RespBase<?> getHistoryByDocCode(String docCode) {
		RespBase<Object> resp = RespBase.create();
		List<FileChanges> changesByFileId = docService.getHistoryByDocCode(docCode);
		resp.setData(changesByFileId);
		return resp;
	}

	@RequestMapping(value = "/compareWith")
	@RequiresPermissions(value = { "doc" }, logical = AND)
	public RespBase<?> compareWith(Long oldChangesId, Long newChangesId) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(docService.compareWith(oldChangesId, newChangesId));
		return resp;
	}

	@PostMapping(value = "/upload")
	@RequiresPermissions(value = { "doc" }, logical = AND)
	public RespBase<?> upload(@RequestParam(value = "file") MultipartFile file) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(docService.upload(file));
		return resp;
	}

	@RequestMapping(value = "/shareDoc")
	@RequiresPermissions(value = { "doc" }, logical = AND)
	public RespBase<?> shareDoc(Long id, Boolean isEncrypt, Boolean isForever, Integer day, String expireTime) {
		RespBase<Object> resp = RespBase.create();
		Share share = docService.shareDoc(id, isEncrypt, isForever, day, DateUtils2.parseDate(expireTime));
		resp.setData(share);
		return resp;
	}

}