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
package com.wl4g.dopaas.ucm.endpoint;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.infra.core.web.BaseController;
import com.wl4g.dopaas.ucm.annotation.UcmEndpoint;
import com.wl4g.dopaas.ucm.common.model.FetchReleaseConfigRequest;
import com.wl4g.dopaas.ucm.common.model.ReleaseConfigInfo;
import com.wl4g.dopaas.ucm.common.model.ReportChangedRequest;
import com.wl4g.dopaas.ucm.handler.CentralConfigServerHandler;
//import com.wl4g.dopaas.ucm.session.HandshakeRequest;
//import com.wl4g.dopaas.ucm.session.ConfigServerSecurityManager;

import static com.wl4g.dopaas.ucm.common.UCMConstants.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * UCM server end-point API
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月27日
 * @since
 */
@UcmEndpoint
@ResponseBody
public class UcmServerEndpoint extends BaseController {

	/**
	 * Ucm config handler.
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
//		log.info("Ucm handshaking ... <= {}", reg);
//
//		RespBase<Object> resp = new RespBase<>();
//		resp.setData(securityManager.registerSession(reg.getClientSecretKey()));
//
//		log.info("Ucm handshake => {}", resp);
//		return resp;
//	}

	/**
	 * Watching configuration source. </br>
	 * <a href=
	 * "#">http://localhost:17030/ucm/ucm-server/watch?instance.host=localhost&instance.port=14044&group=ucm-example&namespace=application-test.yml&profile=test&meta.version=1&meta.releaseId=1</a>
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