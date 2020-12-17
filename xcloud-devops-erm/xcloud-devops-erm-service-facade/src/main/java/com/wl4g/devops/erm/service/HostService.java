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
package com.wl4g.devops.erm.service;

import com.wl4g.component.core.bean.model.PageModel;
import com.wl4g.devops.common.bean.erm.Host;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
public interface HostService {

	List<Host> list(String name, String hostname, Long idcId);

	PageModel<Host> page(PageModel<Host> pm, String name, String hostname, Long idcId);

	void save(Host host);

	Host detail(Long id);

	void del(Long id);

	ResponseEntity<FileSystemResource> createAndDownloadTemplate(Long idcId, String organizationCode) throws IOException;

	Map<String, Object> importHost(MultipartFile file, Integer force, Integer sshAutoCreate) throws IOException;
}