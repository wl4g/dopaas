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
package com.wl4g.dopaas.cmdb.service;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.wl4g.component.core.page.PageHolder;
import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.cmdb.Host;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@FeignConsumer(name = "${provider.serviceId.cmdb-facade:cmdb-facade}")
@RequestMapping("/host-service")
public interface HostService {

	@RequestMapping(value = "/list", method = POST)
	List<Host> list(@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "hostname", required = false) String hostname,
			@RequestParam(name = "idcId", required = false) Long idcId);

	@RequestMapping(value = "/page", method = POST)
	PageHolder<Host> page(@RequestBody PageHolder<Host> pm, @RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "hostname", required = false) String hostname,
			@RequestParam(name = "idcId", required = false) Long idcId);

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody Host host);

	@RequestMapping(value = "/detail", method = POST)
	Host detail(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/del", method = POST)
	void del(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/createAndDownloadTemplate", method = POST)
	ResponseEntity<FileSystemResource> createAndDownloadTemplate(@RequestParam(name = "idcId", required = false) Long idcId,
			@RequestParam(name = "organizationCode", required = false) String organizationCode) throws IOException;

	@RequestMapping(value = "/importHost", method = POST)
	Map<String, Object> importHost(MultipartFile file, @RequestParam(name = "force", required = false) Integer force,
			@RequestParam(name = "sshAutoCreate", required = false) Integer sshAutoCreate) throws IOException;
}