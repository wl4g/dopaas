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
package com.wl4g.devops.scm.endpoint;

import com.wl4g.devops.common.bean.scm.model.GetRelease;
import com.wl4g.devops.common.bean.scm.model.PreRelease;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage;
import com.wl4g.devops.common.bean.scm.model.ReportInfo;
import com.wl4g.devops.common.constants.SCMDevOpsConstants;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.scm.context.ConfigContextHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.wl4g.devops.common.constants.SCMDevOpsConstants.*;

@RestController
@RequestMapping(SCMDevOpsConstants.URI_S_BASE)
public class ScmServerEndpoint extends BaseController {

	@Autowired
	private ConfigContextHandler handler;

	@GetMapping(value = URI_S_SOURCE_GET)
	public RespBase<ReleaseMessage> getSourceConfig(@Validated GetRelease req, BindingResult bind) {
		if (log.isInfoEnabled()) {
			log.info("Get config-source request: {}, bind: {}", req, bind);
		}

		RespBase<ReleaseMessage> resp = new RespBase<>();
		try {
			if (bind.hasErrors()) {
				resp.setCode(RetCode.PARAM_ERR);
				resp.setMessage(bind.toString());
			} else {
				// Get adminServer source configuration.
				ReleaseMessage release = handler.findSource(req);
				resp.getData().put(KEY_RELEASE, release);
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(ExceptionUtils.getRootCauseMessage(e));
			log.error("Get config-source failed.", e);
		}

		if (log.isInfoEnabled()) {
			log.info("Get config-source response: {}", resp);
		}
		return resp;
	}

	@PostMapping(value = URI_S_REPORT_POST)
	public RespBase<?> report(@Validated @RequestBody ReportInfo req, BindingResult bind) {
		if (log.isInfoEnabled()) {
			log.info("Report request: {}, bind: {}", req, bind);
		}

		RespBase<?> resp = new RespBase<>();
		try {
			if (bind.hasErrors()) {
				resp.setCode(RetCode.PARAM_ERR);
				resp.setMessage(bind.toString());
			} else {
				// Post to adminServer report-message.
				handler.report(req);
			}
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

	/* for test */ // @PostMapping(value = DevOpsConstants.URL_CONF_RELEASE)
	public RespBase<?> release(@Validated @RequestBody PreRelease req, BindingResult bind) {
		if (log.isInfoEnabled()) {
			log.info("Releasing... {}, bind: {}", req, bind);
		}

		RespBase<?> resp = new RespBase<>();
		if (bind.hasErrors()) {
			resp.setCode(RetCode.PARAM_ERR);
			resp.setMessage(bind.toString());
		} else {
			// Invoke release.
			handler.release(req);
		}

		return resp;
	}

}