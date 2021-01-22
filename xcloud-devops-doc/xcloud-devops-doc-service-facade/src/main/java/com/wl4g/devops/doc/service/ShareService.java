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

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.rpc.springboot.feign.annotation.SpringBootFeignClient;
import com.wl4g.devops.common.bean.doc.Share;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * {@link ShareService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version v1.0 2020-01-14
 * @sine v1.0
 * @see
 */
@SpringBootFeignClient(name = "${provider.serviceId.doc-facade:share-service}")
@RequestMapping("/ShareService")
public interface ShareService {

	@RequestMapping(value = "/list", method = POST)
	PageHolder<Share> list(@RequestBody PageHolder<Share> pm);

	@RequestMapping(value = "/cancelShare", method = POST)
	void cancelShare(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/rendering", method = POST)
	RespBase<?> rendering(@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "passwd", required = false) String passwd);

}