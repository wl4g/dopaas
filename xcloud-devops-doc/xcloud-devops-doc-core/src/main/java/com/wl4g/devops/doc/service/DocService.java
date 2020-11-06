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
package com.wl4g.devops.doc.service;

import com.wl4g.components.core.bean.doc.FileChanges;
import com.wl4g.components.core.bean.doc.Share;
import com.wl4g.components.data.page.PageModel;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2020-01-14 11:48:00
 */
public interface DocService {

	PageModel<FileChanges> list(PageModel<FileChanges> pm, String name, String lang, Long labelId);

	void save(FileChanges fileChanges);

	void saveUpload(FileChanges fileChanges);

	FileChanges detail(Long id);

	void del(Long id);

	List<FileChanges> getHistoryByDocCode(String docCode);

	Map<String, FileChanges> compareWith(Long oldChangesId, Long newChangesId);

	Map<String, Object> upload(MultipartFile file);

	Share shareDoc(Long id, boolean isEncrypt, boolean isForever, Integer day, Date expireTime);

	FileChanges getLastByDocCode(String docCode);

}