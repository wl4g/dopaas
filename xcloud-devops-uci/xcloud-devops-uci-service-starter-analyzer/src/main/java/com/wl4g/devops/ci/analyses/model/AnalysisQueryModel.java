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
package com.wl4g.devops.ci.analyses.model;

import java.io.Serializable;

import com.wl4g.devops.ci.analyses.coordinate.AnalysisCoordinator.AnalyzerKind;

/**
 * Analysis query result model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月19日
 * @since
 */
public class AnalysisQueryModel implements Serializable {
	private static final long serialVersionUID = -9071662524738147385L;

	/**
	 * Analyzer kind. {@link AnalyzerKind}
	 */
	private int kind;

	public int getKind() {
		return kind;
	}

	public void setKind(int analyzerKind) {
		this.kind = analyzerKind;
	}

}