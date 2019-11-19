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
package com.wl4g.devops.ci.analyses.config;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.URL_ANALYZER_BASE_PATH;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.ci.analyses.CodesAnalyzer;
import com.wl4g.devops.ci.analyses.model.SpotbugsAnalysingModel;
import com.wl4g.devops.ci.analyses.spotbugs.SpotbugsCodesAnalyzer;
import com.wl4g.devops.ci.analyses.web.CodesAnalyzerController;
import com.wl4g.devops.common.config.OptionalPrefixControllerAutoConfiguration;

/**
 * CI analyzers auto configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月19日
 * @since
 */
@Configuration
public class CiAnalyzerAutoConfiguration extends OptionalPrefixControllerAutoConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "spring.cloud.devops.ci.analyses")
	public CiAnalyzerProperties ciAnalysesProperties() {
		return new CiAnalyzerProperties();
	}

	// --- Codes analyzer. ---

	@Bean
	public CodesAnalyzer<SpotbugsAnalysingModel> spotbugsCodesAnalyzer() {
		return new SpotbugsCodesAnalyzer();
	}

	// --- Analyzer controller. ---

	@Bean
	public CodesAnalyzerController codesAnalyzerController() {
		return new CodesAnalyzerController();
	}

	@Bean
	public PrefixHandlerMapping codesAnalyzerControllerPrefixHandlerMapping() {
		return super.newPrefixHandlerMapping(URL_ANALYZER_BASE_PATH,
				com.wl4g.devops.ci.analyses.annotation.CodesAnalyzerController.class);
	}

}
