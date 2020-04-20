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
package com.wl4g.devops.rcm.aliyun;

import com.wl4g.devops.rcm.RcmProvider;
import com.wl4g.devops.rcm.RiskAnalysisEngine;

/**
 * 
 * {@link SafRiskAnalysisEngine}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月21日 v1.0.0
 * @see https://help.aliyun.com/document_detail/90966.html?spm=a2c4g.11186623.6.564.4b0a3620ghFtdv
 * @see https://github.com/aliyun/aliyun-openapi-java-sdk/blob/master/aliyun-java-sdk-saf/src/test/java/SafPopTest.java
 */
public class SafRiskAnalysisEngine implements RiskAnalysisEngine {

	@Override
	public RcmProvider kind() {
		return RcmProvider.AliyunSafEngine;
	}

}
