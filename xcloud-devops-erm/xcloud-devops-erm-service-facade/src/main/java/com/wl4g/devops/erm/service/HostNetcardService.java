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

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.common.bean.erm.HostNetcard;

import java.util.Map;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
public interface HostNetcardService {

	PageHolder<HostNetcard> page(PageHolder<HostNetcard> pm, Long hostId, String name);

	void save(HostNetcard hostNetcard);

	HostNetcard detail(Long id);

	void del(Long id);

	Map<String, Object> getHostTunnel();
}