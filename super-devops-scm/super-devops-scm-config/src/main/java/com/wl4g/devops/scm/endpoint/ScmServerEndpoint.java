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
import com.wl4g.devops.scm.annotation.ScmEndpoint;
import com.wl4g.devops.scm.context.ConfigContextHandler;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import static com.wl4g.devops.common.constants.SCMDevOpsConstants.*;

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

	final private ConfigContextHandler contextHandler;

	public ScmServerEndpoint(ConfigContextHandler handler) {
		super();
		this.contextHandler = handler;
	}

	@GetMapping(value = URI_S_SOURCE_GET)
	public RespBase<ReleaseMessage> getSource(@Validated GetRelease get) {
		if (log.isInfoEnabled()) {
			log.info("Get config source - {}", get);
		}

		RespBase<ReleaseMessage> resp = new RespBase<>();
		// Got configuration source
		resp.getData().put(KEY_RELEASE, contextHandler.getSource(get));

		if (log.isInfoEnabled()) {
			log.info("Response config source - {}", resp);
		}
		return resp;
	}

	@PostMapping(value = URI_S_REPORT_POST)
	public RespBase<?> report(@Validated @RequestBody ReportInfo report) {
		if (log.isInfoEnabled()) {
			log.info("Report for - {}", report);
		}

		RespBase<?> resp = new RespBase<>();
		contextHandler.report(report);

		if (log.isInfoEnabled()) {
			log.info("Refresh report response for - {}", resp);
		}
		return resp;
	}

	/**
	 * Watching configuration source. </br>
	 * <a href=
	 * "#">http://localhost:14043/scm/scm-server/watch?instance.host=localhost&instance.port=1&group=scm-example&namespace=application-test.yml&profile=test&meta.version=1&meta.releaseId=1</a>
	 * 
	 * @param watch
	 * @return
	 */
	@RequestMapping(value = URI_S_WATCH_GET, method = GET)
	public DeferredResult<?> watch(@Validated GetRelease watch) {
		if (log.isInfoEnabled()) {
			log.info("Watching for - {}", watch);
		}

		return contextHandler.watch(watch);
	}

	/**
	 * <h6>For releaseTests</h6></br>
	 * <b>Header:</b></br>
	 * <a href="#">http://localhost:14043/scm/scm-server/releaseTests</a></br>
	 * <b>Body:</b>
	 * 
	 * <pre>
	 *	{
	 *		"profile": "test",
	 *		"group": "scm-example",
	 *		"instances": [{
	 *			"host": "localhost",
	 *			"port": 8848
	 *		}],
	 *		"meta": {
	 *			"releaseId": "1",
	 *			"version": "1.0.1"
	 *		}
	 *	}
	 * </pre>
	 * 
	 * @param pre
	 * @return
	 */
	// @PostMapping("releaseTests")
	public RespBase<?> releaseTests(@Validated @RequestBody PreRelease pre) {
		if (log.isInfoEnabled()) {
			log.info("PreRelease tests - {}", pre);
		}

		RespBase<?> resp = new RespBase<>();
		contextHandler.release(pre);
		return resp;
	}

}