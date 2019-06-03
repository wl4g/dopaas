/*
 * Copyright 2015 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.wl4g.devops.common.constants.SCMDevOpsConstants.*;
import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseMeta;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage.ReleasePropertySource;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.scm.client.configure.refresh.ScmContextRefresher;

/**
 * https://blog.csdn.net/cml_blog/article/details/78411312
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月9日
 * @since
 */
@ResponseBody
@com.wl4g.devops.scm.annotation.ScmEndpoint
public class ScmClientEndpoint extends BaseController {

	private ScmContextRefresher refresher;

	public ScmClientEndpoint(ScmContextRefresher refresher) {
		this.refresher = refresher;
	}

	@PostMapping(value = URI_C_REFRESH)
	public RespBase<?> refresh(@RequestParam("releaseMeta") ReleaseMeta releaseMeta, BindingResult bind) {
		if (log.isInfoEnabled()) {
			log.info("Post configure refresh ...");
		}

		RespBase<?> resp = new RespBase<>();
		if (bind.hasErrors()) {
			resp.setCode(RetCode.PARAM_ERR);
			resp.setMessage(bind.toString());
		} else {
			try {
				// Do refresh.
				refresher.refresh();
			} catch (Exception e) {
				String errmsg = ExceptionUtils.getRootCauseMessage(e);
				resp.setCode(RetCode.SYS_ERR);
				resp.setMessage(errmsg);
				log.error("Invoke refresh failure. {}", errmsg);
			}
		}
		return resp;
	}

	@GetMapping(value = URI_C_LATEST)
	public RespBase<?> latest() {
		if (log.isInfoEnabled()) {
			log.info("Got config latest ...");
		}

		RespBase<List<ReleasePropertySource>> resp = new RespBase<>();
		try {
			// Got current environment scm property sources.
			resp.getData().put(KEY_ENV_SOURCES, getEnvironmentPropertySources());
		} catch (Exception e) {
			String errmsg = ExceptionUtils.getRootCauseMessage(e);
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(errmsg);
			log.error("Got latest config failure. {}", errmsg);
		}

		return resp;
	}

	private List<ReleasePropertySource> getEnvironmentPropertySources() {
		List<ReleasePropertySource> sources = new ArrayList<>();
		//
		// TODO
		//
		return sources;
	}

}