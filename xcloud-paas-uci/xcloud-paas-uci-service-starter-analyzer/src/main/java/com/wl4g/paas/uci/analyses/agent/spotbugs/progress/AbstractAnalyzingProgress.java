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
package com.wl4g.paas.uci.analyses.agent.spotbugs.progress;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.paas.uci.analyses.model.StageProgressModel;
import com.wl4g.paas.uci.analyses.model.StageProgressModel.AnalysisStage;

import edu.umd.cs.findbugs.FindBugsProgress;

/***
 * Abstract generic analyzing progress.</br>
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月18日
 * @since
 */
public abstract class AbstractAnalyzingProgress implements FindBugsProgress {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	private int totalArchives = 0;
	private List<Integer> totalStageData = new ArrayList<>();
	private int passStage = 0;
	private int stageGoalAnalysised = 0;
	private int passStageAnalysised = 0;

	@Override
	public void reportNumberOfArchives(int numArchives) {
		// String stage = L10N.getLocalString("progress.scanning_archives",
		// "Scanning archives...");
		updateStage(AnalysisStage.SCAN_START);
	}

	@Override
	public void startArchive(String name) {
		++totalArchives;
		updateStage(AnalysisStage.SCANING);
	}

	@Override
	public void finishArchive() {
		// updateStage(AnalysisStage.SCANING);
	}

	@Override
	public void predictPassCount(int[] classesPerPass) {
		for (int stages : classesPerPass) {
			totalStageData.add(stages);
		}
		updateStage(AnalysisStage.SCAN_END);
	}

	@Override
	public void startAnalysis(int numClasses) {
		stageGoalAnalysised = numClasses;
		passStageAnalysised = 0; // Reset
		++passStage;

		// String stage = L10N.getLocalString("progress.analyzing_classes",
		// "Analyzing classes...");
		updateStage(AnalysisStage.ANALYSIS_START);
	}

	@Override
	public void finishClass() {
		++passStageAnalysised;
		updateStage(AnalysisStage.ANALYSING);
	}

	@Override
	public void finishPerClassAnalysis() {
		// String stage = L10N.getLocalString("progress.finishing_analysis",
		// "Finishing archives...");
		updateStage(AnalysisStage.ANALYSIS_END);
	}

	/**
	 * Updating stage.
	 * 
	 * @param stage
	 */
	private void updateStage(AnalysisStage stage) {
		StageProgressModel model = new StageProgressModel(stage, totalArchives, totalStageData.size(), passStage,
				passStageAnalysised, stageGoalAnalysised);
		doStage(stage, model);
	}

	/**
	 * Do stage processing.
	 * 
	 * @param stage
	 * @param model
	 */
	protected abstract void doStage(AnalysisStage stage, StageProgressModel model);

}