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
import com.wl4g.devops.common.bean.erm.IdcBean;

import java.util.List;

/**
 * @author vjay
 */
public interface IdcService {

	PageHolder<IdcBean> page(PageHolder<IdcBean> pm, String name);

	List<IdcBean> getForSelect();

	void save(IdcBean idc);

	IdcBean detail(Long id);

	void del(Long id);
}