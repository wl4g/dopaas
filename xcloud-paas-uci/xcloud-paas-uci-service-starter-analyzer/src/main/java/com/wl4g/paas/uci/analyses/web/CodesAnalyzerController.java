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
package com.wl4g.paas.uci.analyses.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.paas.uci.analyses.coordinate.CompositeAnalysisCoordinatorAdapter;
import com.wl4g.paas.uci.analyses.coordinate.AnalysisCoordinator.AnalyzerKind;
import com.wl4g.paas.uci.analyses.model.AnalysisQueryModel;
import com.wl4g.paas.uci.analyses.model.SpotbugsAnalysingModel;
import com.wl4g.paas.uci.analyses.model.SpotbugsAnalysisResultModel;

import static com.wl4g.paas.uci.analyses.coordinate.AnalysisCoordinator.AnalyzerKind.*;

import java.io.File;

/**
 * Codes analysis controller.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月18日
 * @since
 */
@RequestMapping("/analyzer/")
@ResponseBody
public class CodesAnalyzerController extends BaseController {

	@Autowired
	protected CompositeAnalysisCoordinatorAdapter adapter;

	/**
	 * Submitted analyzing assets file for java.
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@PostMapping("java")
	public RespBase<?> submitJavaAnalyzer(@RequestParam("assetFile") CommonsMultipartFile file,
			@RequestBody SpotbugsAnalysingModel model) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("Analyzing assetFile for spotbugs-java: {}", model);
		}
		RespBase<Object> resp = RespBase.create();

		file.transferTo(new File("/mnt/disk1/ci-analyzer/data/assetFiles/" + model.getProjectName()));

		adapter.forAdapt(AnalyzerKind.SPOTBUGS).analyze(model);
		return resp;
	}

	/**
	 * Get analysis bugs collection result.
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@GetMapping("getBugCollection")
	public RespBase<?> getAnalysisBugCollection(@RequestBody AnalysisQueryModel model) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("Get analysis result bugCollection for: {}", model);
		}
		RespBase<SpotbugsAnalysisResultModel> resp = RespBase.create();
		adapter.forAdapt(of(model.getKind())).getBugCollection(model);
		return resp;
	}

}