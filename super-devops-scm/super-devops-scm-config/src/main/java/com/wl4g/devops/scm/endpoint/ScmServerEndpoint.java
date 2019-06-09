/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.scm.endpoint;

import com.wl4g.devops.common.bean.scm.model.GetRelease;
import com.wl4g.devops.common.bean.scm.model.PreRelease;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage;
import com.wl4g.devops.common.bean.scm.model.ReportInfo;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.scm.annotation.ScmEndpoint;
import com.wl4g.devops.scm.context.ConfigContextHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.wl4g.devops.common.constants.SCMDevOpsConstants.*;

/**
 * SCM server endpoint api
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月27日
 * @since
 */
@ScmEndpoint
@ResponseBody
public class ScmServerEndpoint extends BaseController {

	final private ConfigContextHandler handler;

	public ScmServerEndpoint(ConfigContextHandler handler) {
		super();
		this.handler = handler;
	}

	@GetMapping(value = URI_S_SOURCE_GET)
	public RespBase<ReleaseMessage> getSource(@Validated GetRelease req) {
		if (log.isInfoEnabled()) {
			log.info("On config source ... {}", req);
		}

		RespBase<ReleaseMessage> resp = new RespBase<>();
		try {
			// Got config source
			resp.getData().put(KEY_RELEASE, handler.findSource(req));
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(ExceptionUtils.getRootCauseMessage(e));
			log.error("Get config-source failed.", e);
		}

		if (log.isInfoEnabled()) {
			log.info("Got config response: {}", resp);
		}
		return resp;
	}

	@PostMapping(value = URI_S_REPORT_POST)
	public RespBase<?> report(@Validated @RequestBody ReportInfo report) {
		if (log.isInfoEnabled()) {
			log.info("On report ... {}", report);
		}

		RespBase<?> resp = new RespBase<>();
		try {
			// Post to adminServer report-message.
			handler.report(report);
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(ExceptionUtils.getRootCauseMessage(e));
			log.error("Report persistence failed.", e);
		}

		if (log.isDebugEnabled()) {
			log.debug("Report response: {}", resp);
		}
		return resp;
	}

	/* for test */ // @PostMapping(value = URL_CONF_RELEASE)
	public RespBase<?> releaseTests(@Validated @RequestBody PreRelease pre) {
		if (log.isInfoEnabled()) {
			log.info("On releasing tests ... {}", pre);
		}

		RespBase<?> resp = new RespBase<>();
		// Invoke release.
		handler.release(pre);

		return resp;
	}

}