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

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;

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
	 * Stage step label.
	 */
	private String stageLabel;

	/**
	 * Total number of files to be analyzed
	 */
	private int totalArchives;

	/**
	 * Number of task phases expected to be performed
	 */
	private int totalStages;

	/**
	 * Number of expected task phases completed
	 */
	private int passStage = 0;

	/**
	 * Number of classes analyzed in the current stage
	 */
	private int stagePassAnalysised;

	/**
	 * Total number of categories to be analyzed at the current stage
	 */
	private int stageGoalAnalysised;

	public StageProgressModel() {
		super();
	}

	public StageProgressModel(String stageLabel, int totalArchives, int totalStages, int passStage, int stagePassAnalysised,
			int stageGoalAnalysised) {
		super();
		this.stageLabel = stageLabel;
		this.totalArchives = totalArchives;
		this.totalStages = totalStages;
		this.passStage = passStage;
		this.stagePassAnalysised = stagePassAnalysised;
		this.stageGoalAnalysised = stageGoalAnalysised;
	}

	public String getStageLabel() {
		return stageLabel;
	}

	public void setStageLabel(String stageLabel) {
		this.stageLabel = stageLabel;
	}

	public int getTotalArchives() {
		return totalArchives;
	}

	public void setTotalArchives(int totalArchives) {
		this.totalArchives = totalArchives;
	}

	public int getTotalStages() {
		return totalStages;
	}

	public void setTotalStages(int totalStages) {
		this.totalStages = totalStages;
	}

	public int getPassStage() {
		return passStage;
	}

	public void setPassStage(int passStage) {
		this.passStage = passStage;
	}

	public int getStagePassAnalysised() {
		return stagePassAnalysised;
	}

	public void setStagePassAnalysised(int stagePassAnalysised) {
		this.stagePassAnalysised = stagePassAnalysised;
	}

	public int getStageGoalAnalysised() {
		return stageGoalAnalysised;
	}

	public void setStageGoalAnalysised(int stageGoalAnalysised) {
		this.stageGoalAnalysised = stageGoalAnalysised;
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

}
