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
package com.wl4g.devops.ci.analyses.spotbugs.engine;

import static org.springframework.util.Assert.notNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.devops.ci.analyses.model.StageProgressModel;

import edu.umd.cs.findbugs.FindBugsProgress;
import edu.umd.cs.findbugs.L10N;

/***
 * {@link PrintStream} analyzing progress.</br>
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月18日
 * @since
 */
public class PrintAnalyzingProgress implements FindBugsProgress {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	final protected PrintStream out;

	private int totalArchives = 0;
	private List<Integer> totalStageData = new ArrayList<>();
	private int passStage = 0;
	private int stageGoalAnalysised = 0;
	private int passStageAnalysised = 0;

	public PrintAnalyzingProgress(PrintStream out) {
		notNull(out, "null PrintStream");
		this.out = out;
	}

	@Override
	public void reportNumberOfArchives(int numArchives) {
		String stage = L10N.getLocalString("progress.scanning_archives", "Scanning archives...");
		updateStage(stage);
	}

	@Override
	public void startArchive(String name) {
		updateStage(name);
	}

	@Override
	public void finishArchive() {
		++this.totalArchives;
		updateStage("已检索:" + totalArchives);
	}

	@Override
	public void predictPassCount(int[] totalStages) {
		for (int stages : totalStages) {
			this.totalStageData.add(stages);
		}
	}

	@Override
	public void startAnalysis(int stageGoalAnalysised) {
		this.stageGoalAnalysised = stageGoalAnalysised;
		this.passStageAnalysised = 0; // Reset
		++this.passStage;

		String stage = L10N.getLocalString("progress.analyzing_classes", "Analyzing classes...");
		updateStage(stage);
	}

	@Override
	public void finishClass() {
		++this.passStageAnalysised;
		updateStage("已分析:" + passStageAnalysised + "/" + stageGoalAnalysised + "/" + passStage);
	}

	@Override
	public void finishPerClassAnalysis() {
		String stage = L10N.getLocalString("progress.finishing_analysis", "Finishing archives...");
		updateStage(stage);
	}

	private void updateStage(String stageLabel) {
		StageProgressModel model = new StageProgressModel(stageLabel, totalArchives, totalStageData.size(), passStage,
				passStageAnalysised, stageGoalAnalysised);
		this.out.println(model);
	}

}