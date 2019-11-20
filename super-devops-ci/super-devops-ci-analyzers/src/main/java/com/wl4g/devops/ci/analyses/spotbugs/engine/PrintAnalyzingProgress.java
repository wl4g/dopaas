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
import java.util.concurrent.atomic.AtomicInteger;

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

	private AtomicInteger count = new AtomicInteger(0);
	private AtomicInteger scanCount = new AtomicInteger(0);
	private int[] classesPerPass;

	public PrintAnalyzingProgress(PrintStream out) {
		notNull(out, "null PrintStream");
		this.out = out;
	}

	@Override
	public void predictPassCount(int[] classesPerPass) {
		this.classesPerPass = classesPerPass;
	}

	@Override
	public void startAnalysis(int scanClassesCount) {
		String msg = L10N.getLocalString("progress.analyzing_classes", "Analyzing classes...");
		String stage = msg + ", scan " + scanCount.incrementAndGet() + "/" + classesPerPass.length;
		updateStage(stage, 0, scanClassesCount);
	}

	@Override
	public void startArchive(String name) {
		// Ignore
	}

	@Override
	public void finishArchive() {
		count.incrementAndGet();
	}

	@Override
	public void finishClass() {
		count.incrementAndGet();
	}

	@Override
	public void finishPerClassAnalysis() {
		String stage = L10N.getLocalString("progress.finishing_analysis", "Finishing archives...");
		updateStage(stage, 0, 0);
	}

	@Override
	public void reportNumberOfArchives(int numArchives) {
		String stage = L10N.getLocalString("progress.scanning_archives", "Scanning archives...");
		updateStage(stage, 0, numArchives);
	}

	private void updateStage(String stage, final int count, final int goal) {
		this.count.set(count);
		out.print(new StageProgressModel(stage, count, goal));
	}

}