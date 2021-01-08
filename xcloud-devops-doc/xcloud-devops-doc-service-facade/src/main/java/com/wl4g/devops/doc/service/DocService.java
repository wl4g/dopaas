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
package com.wl4g.devops.doc.service;

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.rpc.springboot.feign.annotation.SpringBootFeignClient;
import com.wl4g.devops.common.bean.doc.FileChanges;
import com.wl4g.devops.common.bean.doc.Share;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author vjay
 * @date 2020-01-14 11:48:00
 */
@SpringBootFeignClient("docService")
@RequestMapping("/doc")
public interface DocService {

	@RequestMapping(value = "/list", method = POST)
	PageHolder<FileChanges> list(@RequestBody PageHolder<FileChanges> pm,
								 @RequestParam(name="name",required=false) String name,
								 @RequestParam(name="lang",required=false) String lang,
								 @RequestParam(name="labelId",required=false) Long labelId);

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody FileChanges fileChanges);

	@RequestMapping(value = "/saveUpload", method = POST)
	void saveUpload(@RequestBody FileChanges fileChanges);

	@RequestMapping(value = "/detail", method = POST)
	FileChanges detail(@RequestParam(name="id",required=false) Long id);

	@RequestMapping(value = "/del", method = POST)
	void del(@RequestParam(name="id",required=false) Long id);

	@RequestMapping(value = "/getHistoryByDocCode", method = POST)
	List<FileChanges> getHistoryByDocCode(@RequestParam(name="docCode",required=false) String docCode);

	@RequestMapping(value = "/compareWith", method = POST)
	Map<String, FileChanges> compareWith(@RequestParam(name="oldChangesId",required=false) Long oldChangesId,
										 @RequestParam(name="newChangesId",required=false) Long newChangesId);

	@RequestMapping(value = "/upload", method = POST)
	Map<String, Object> upload(@RequestBody MultipartFile file);

	@RequestMapping(value = "/shareDoc", method = POST)
	Share shareDoc(@RequestParam(name="id",required=false) Long id,
				   @RequestParam(name="isEncrypt",required=false) boolean isEncrypt,
				   @RequestParam(name="isForever",required=false) boolean isForever,
				   @RequestParam(name="day",required=false) Integer day,
				   @RequestParam(name="expireTime",required=false) Date expireTime);

	@RequestMapping(value = "/getLastByDocCode", method = POST)
	FileChanges getLastByDocCode(@RequestParam(name="docCode",required=false) String docCode);

}