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
package com.wl4g.paas.udm.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.paas.common.bean.udm.Label;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * {@link LabelService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version v1.0 2020-01-14
 * @sine v1.0
 * @see
 */
@FeignConsumer(name = "${provider.serviceId.udm-facade:udm-facade}")
@RequestMapping("/labelService-service")
public interface LabelService {

	@RequestMapping(value = "/list", method = POST)
	PageHolder<Label> list(@RequestBody PageHolder<Label> pm, @RequestParam(name = "name", required = false) String name);

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody Label label);

	@RequestMapping(value = "/detail", method = GET)
	Label detail(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/del", method = POST)
	void del(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/allLabel", method = GET)
	List<Label> allLabel();

}