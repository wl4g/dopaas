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
package com.wl4g.devops.erm.service;

import com.wl4g.devops.common.bean.erm.DockerRepository;
import com.wl4g.devops.common.bean.erm.model.RepositoryProject;
import com.wl4g.devops.page.PageModel;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author vjay
 */
public interface DockerRepositoryService {

	PageModel page(PageModel pm, String name);

	List<DockerRepository> getForSelect();

	void save(DockerRepository dockerRepository);

	DockerRepository detail(Integer id);

	void del(Integer id);

	List<RepositoryProject> getRepositoryProjects(Integer id, String address,String name) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException;
}