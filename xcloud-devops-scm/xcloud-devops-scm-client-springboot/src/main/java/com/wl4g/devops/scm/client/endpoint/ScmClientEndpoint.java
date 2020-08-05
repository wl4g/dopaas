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
package com.wl4g.devops.scm.client.endpoint;

import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestParam;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import com.wl4g.components.core.bean.scm.model.GenericInfo.ReleaseMeta;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.core.web.RespBase;
import com.wl4g.components.core.web.RespBase.RetCode;
import com.wl4g.devops.scm.annotation.ScmEndpoint;
import com.wl4g.devops.scm.client.handler.refresh.ScmContextRefresher;

/**
 * See:<a href=
 * "https://blog.csdn.net/cml_blog/article/details/78411312">https://blog.csdn.net/cml_blog/article/details/78411312</a>
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月9日
 * @since
 */
@ScmEndpoint
public class ScmClientEndpoint extends BaseController {

	final Environment environment;

	final private ScmContextRefresher refresher;

	public ScmClientEndpoint(Environment environment, ScmContextRefresher refresher) {
		Assert.notNull(environment, "Environment must not be null");
		Assert.notNull(refresher, "ContextRefresher must not be null");
		this.environment = environment;
		this.refresher = refresher;
	}

	//
	// Used for tests
	//

	// @PostMapping(value = URI_C_REFRESH)
	// @ResponseBody
	public RespBase<?> refresh(@RequestParam("releaseMeta") ReleaseMeta meta) {
		log.info("Refresh client config meta for ... {}", meta);

		RespBase<?> resp = new RespBase<>();
		try {
			refresher.refresh();
		} catch (Exception e) {
			String errmsg = getRootCauseMessage(e);
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(errmsg);
			log.error("Refresh failed! {}", errmsg);
		}
		return resp;
	}

}