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
package com.wl4g.devops.scm.endpoint;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.devops.scm.annotation.ScmEndpoint;
import com.wl4g.devops.scm.common.model.FetchReleaseConfigRequest;
import com.wl4g.devops.scm.common.model.ReleaseConfigInfo;
import com.wl4g.devops.scm.common.model.ReportChangedRequest;
import com.wl4g.devops.scm.handler.CentralConfigServerHandler;
//import com.wl4g.devops.scm.session.HandshakeRequest;
//import com.wl4g.devops.scm.session.ConfigServerSecurityManager;

import static com.wl4g.devops.scm.common.SCMConstants.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

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
	protected CentralConfigServerHandler handler;

//	@Autowired
//	protected ConfigServerSecurityManager securityManager;
//
//	/**
//	 * Init handshaking and register connection.
//	 * 
//	 * @param reg
//	 * @return
//	 */
//	@RequestMapping(value = URI_S_HANDSHAKE, method = POST)
//	public RespBase<?> handshake(@Validated HandshakeRequest reg) {
//		log.info("Scm handshaking ... <= {}", reg);
//
//		RespBase<Object> resp = new RespBase<>();
//		resp.setData(securityManager.registerSession(reg.getClientSecretKey()));
//
//		log.info("Scm handshake => {}", resp);
//		return resp;
//	}

	/**
	 * Watching configuration source. </br>
	 * <a href=
	 * "#">http://localhost:18053/scm/scm-server/watch?instance.host=localhost&instance.port=14044&group=scm-example&namespace=application-test.yml&profile=test&meta.version=1&meta.releaseId=1</a>
	 * 
	 * @param watch
	 * @return
	 */
	@PostMapping(value = URI_S_SOURCE_WATCH)
	public DeferredResult<?> watch(@Validated @RequestBody FetchReleaseConfigRequest watch) {
		log.info("Long polling watching... <= {}", watch);
		return handler.watch(watch);
	}

//	@PostMapping(value = URI_S_SOURCE_FETCH)
//	public RespBase<WatchCommandResult> fetchSource(@Validated @RequestBody WatchCommand fetch) {
//		log.info("Fetching config... <= {}", fetch);
//
//		RespBase<WatchCommandResult> resp = new RespBase<>();
//		// Fetching configuration source
//		resp.setData(handler.getSource(fetch));
//
//		log.info("Fetch config => {}", resp);
//		return resp;
//	}

	@PostMapping(value = URI_S_REFRESHED_REPORT)
	public RespBase<?> report(@Validated @RequestBody ReportChangedRequest report) {
		log.info("Reporting... <= {}", report);
		RespBase<?> resp = new RespBase<>();
		handler.report(report);
		log.info("Reported => {}", resp);
		return resp;
	}

}