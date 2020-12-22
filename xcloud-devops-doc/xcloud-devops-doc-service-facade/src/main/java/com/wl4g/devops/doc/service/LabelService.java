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
import com.wl4g.devops.common.bean.doc.Label;

import java.util.List;

/**
 * @author vjay
 * @date 2020-01-14 11:48:00
 */
public interface LabelService {

	PageHolder<Label> list(PageHolder<Label> pm, String name);

	void save(Label label);

	Label detail(Long id);

	void del(Long id);

	List<Label> allLabel();

}