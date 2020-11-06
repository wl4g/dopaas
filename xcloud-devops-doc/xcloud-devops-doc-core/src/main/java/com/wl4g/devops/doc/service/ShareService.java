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

import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.bean.doc.Share;
import com.wl4g.components.data.page.PageModel;

/**
 * @author vjay
 * @date 2020-02-19 16:22:00
 */
public interface ShareService {

	PageModel<Share> list(PageModel<Share> pm);

	void cancelShare(Long id);

	RespBase<?> rendering(String code, String passwd);

}