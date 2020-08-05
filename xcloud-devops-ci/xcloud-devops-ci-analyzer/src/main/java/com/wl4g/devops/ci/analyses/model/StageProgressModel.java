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
package com.wl4g.devops.ci.analyses.model;

import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;

import java.io.Serializable;

/**
 * Analyzing stage progress model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月20日
 * @since
 */
public class StageProgressModel implements Serializable {
	private static final long serialVersionUID = 8386866351185047399L;

	/**
	 * (stage label) Stage step label.
	 */
	private AnalysisStage stage;

	/**
	 * (totalArchives) Total number of files to be analyzed
	 */
	private int archives;

	/**
	 * (totalStages) Number of task phases expected to be performed
	 */
	private int stages;

	/**
	 * (passStage) Number of expected task phases completed
	 */
	private int passStage = 0;

	/**
	 * (stagePassAnalysised) Number of classes analyzed in the current stage
	 */
	private int passAnalysis;

	/**
	 * (stageGoalAnalysised) Total number of categories to be analyzed at the
	 * current stage
	 */
	private int goalAnalysis;

	public StageProgressModel() {
		super();
	}

	public StageProgressModel(AnalysisStage stage, int archives, int stages, int passStage, int passAnalysis, int goalAnalysis) {
		super();
		this.stage = stage;
		this.archives = archives;
		this.stages = stages;
		this.passStage = passStage;
		this.passAnalysis = passAnalysis;
		this.goalAnalysis = goalAnalysis;
	}

	public AnalysisStage getStage() {
		return stage;
	}

	public void setStageLabel(AnalysisStage stage) {
		this.stage = stage;
	}

	public int getArchives() {
		return archives;
	}

	public void setArchives(int totalArchives) {
		this.archives = totalArchives;
	}

	public int getStages() {
		return stages;
	}

	public void setStages(int totalStages) {
		this.stages = totalStages;
	}

	public int getPassStage() {
		return passStage;
	}

	public void setPassStage(int passStage) {
		this.passStage = passStage;
	}

	public int getPassAnalysis() {
		return passAnalysis;
	}

	public void setPassAnalysis(int stagePassAnalysised) {
		this.passAnalysis = stagePassAnalysised;
	}

	public int getGoalAnalysis() {
		return goalAnalysis;
	}

	public void setGoalAnalysis(int stageGoalAnalysised) {
		this.goalAnalysis = stageGoalAnalysised;
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

	/**
	 * Analysis stage definitions.
	 * 
	 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0.0 2019-11-21
	 * @since
	 */
	public static enum AnalysisStage {

		READY,

		SCAN_START, SCANING, SCAN_END,

		ANALYSIS_START, ANALYSING, ANALYSIS_END,

		DONE;

	}

}