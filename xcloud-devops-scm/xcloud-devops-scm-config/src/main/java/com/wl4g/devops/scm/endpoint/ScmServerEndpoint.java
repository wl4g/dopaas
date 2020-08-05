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
package com.wl4g.devops.scm.endpoint;

import com.wl4g.components.core.bean.scm.model.GetRelease;
import com.wl4g.components.core.bean.scm.model.ReleaseMessage;
import com.wl4g.components.core.bean.scm.model.ReportInfo;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.core.web.RespBase;
import com.wl4g.devops.scm.annotation.ScmEndpoint;
import com.wl4g.devops.scm.handler.CentralConfigureHandler;
import com.wl4g.devops.scm.session.HandshakeRequest;
import com.wl4g.devops.scm.session.ScmServerConfigSecurityManager;

import static com.wl4g.components.core.constants.SCMDevOpsConstants.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * SCM server end-point API
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月27日
 * @since
 */
@ScmEndpoint
@ResponseBody
public class ScmServerEndpoint extends BaseController {

	/**
	 * Scm config handler.
	 */
	@Autowired
	protected CentralConfigureHandler contextHandler;

	@Autowired
	protected ScmServerConfigSecurityManager securityManager;

	/**
	 * Init handshaking and register connection.
	 * 
	 * @param reg
	 * @return
	 */
	@RequestMapping(value = URI_S_HANDSHAKE, method = POST)
	public RespBase<?> handshake(@Validated HandshakeRequest reg) {
		log.info("Scm handshaking ... <= {}", reg);

		RespBase<Object> resp = new RespBase<>();
		resp.setData(securityManager.registerSession(reg.getClientSecretKey()));

		log.info("Scm handshake => {}", resp);
		return resp;
	}

	/**
	 * Watching configuration source. </br>
	 * <a href=
	 * "#">http://localhost:14043/scm/scm-server/watch?instance.host=localhost&instance.port=14044&group=scm-example&namespace=application-test.yml&profile=test&meta.version=1&meta.releaseId=1</a>
	 * 
	 * @param watch
	 * @return
	 */
	@RequestMapping(value = URI_S_WATCH_GET, method = GET)
	public DeferredResult<?> watch(@Validated GetRelease watch) {
		log.info("Long polling watching... <= {}", watch);
		return contextHandler.watch(watch);
	}

	@GetMapping(value = URI_S_SOURCE_GET)
	public RespBase<ReleaseMessage> fetchSource(@Validated GetRelease get) {
		log.info("Fetching config source... <= {}", get);

		RespBase<ReleaseMessage> resp = new RespBase<>();
		// Fetching configuration source
		resp.setData(contextHandler.getSource(get));

		log.info("Fetched config source => {}", resp);
		return resp;
	}

	@PostMapping(value = URI_S_REPORT_POST)
	public RespBase<?> report(@Validated @RequestBody ReportInfo report) {
		log.info("Reporting... <= {}", report);
		RespBase<?> resp = new RespBase<>();
		contextHandler.report(report);
		log.info("Reported => {}", resp);
		return resp;
	}

}