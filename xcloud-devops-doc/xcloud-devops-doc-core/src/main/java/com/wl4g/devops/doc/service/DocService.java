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

import com.wl4g.devops.common.bean.doc.FileChanges;
import com.wl4g.devops.common.bean.doc.Share;
import com.wl4g.devops.page.PageModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2020-01-14 11:48:00
 */
public interface DocService {

	PageModel list(PageModel pm, String name, String lang, Integer labelId);

	void save(FileChanges fileChanges);

	void saveUpload(FileChanges fileChanges);

	FileChanges detail(Integer id);

	void del(Integer id);

	List<FileChanges> getHistoryByDocCode(String docCode);

	Map<String, FileChanges> compareWith(Integer oldChangesId, Integer newChangesId);

	Map<String, Object> upload(MultipartFile file);

	Share shareDoc(Integer id, boolean isEncrypt, boolean isForever, Integer day, Date expireTime);

	FileChanges getLastByDocCode(String docCode);

}