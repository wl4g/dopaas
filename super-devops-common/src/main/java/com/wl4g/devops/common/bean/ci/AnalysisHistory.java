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
package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;

public class AnalysisHistory extends BaseBean {
	private static final long serialVersionUID = -7378346361141717320L;

	private Integer projectId;

	private String analyzerKind;

	private String language;

	private String assetVersion;

	private Integer assetBytes;

	private Integer assetAnalysisSize;

	private Integer state;

	private String bugCollectionFile;

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getAnalyzerKind() {
		return analyzerKind;
	}

	public void setAnalyzerKind(String analyzerKind) {
		this.analyzerKind = analyzerKind == null ? null : analyzerKind.trim();
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language == null ? null : language.trim();
	}

	public String getAssetVersion() {
		return assetVersion;
	}

	public void setAssetVersion(String assetVersion) {
		this.assetVersion = assetVersion == null ? null : assetVersion.trim();
	}

	public Integer getAssetBytes() {
		return assetBytes;
	}

	public void setAssetBytes(Integer assetBytes) {
		this.assetBytes = assetBytes;
	}

	public Integer getAssetAnalysisSize() {
		return assetAnalysisSize;
	}

	public void setAssetAnalysisSize(Integer assetAnalysisSize) {
		this.assetAnalysisSize = assetAnalysisSize;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getBugCollectionFile() {
		return bugCollectionFile;
	}

	public void setBugCollectionFile(String bugCollectionFile) {
		this.bugCollectionFile = bugCollectionFile == null ? null : bugCollectionFile.trim();
	}

}