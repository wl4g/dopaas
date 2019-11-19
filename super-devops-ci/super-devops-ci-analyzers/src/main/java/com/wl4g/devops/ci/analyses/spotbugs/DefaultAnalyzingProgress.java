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
package com.wl4g.devops.ci.analyses.spotbugs;

import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import org.springframework.util.Assert;

import com.wl4g.devops.common.task.GenericTaskRunner;
import com.wl4g.devops.common.task.RunnerProperties;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.FindBugsProgress;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.gui2.AnalysisCallback;
import edu.umd.cs.findbugs.gui2.BugLoader;
import edu.umd.cs.findbugs.gui2.Driver;

/***
 * Default analyzing progress.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月18日
 * @since
 */
public class DefaultAnalyzingProgress implements FindBugsProgress {

	private AtomicInteger count = new AtomicInteger(0);

	private int pass = 0;
	private int[] classesPerPass;

	private void updateStage(String stage) {
		// statusLabel.setText(stage);
	}

	private void incrementCount() {
		count.incrementAndGet();
		// SwingUtilities.invokeLater(() -> {
		// progressBar.setString(count + "/" + goal);
		// progressBar.setValue(count);
		// });
	}

	private void updateCount(final int count, final int goal) {
		this.count.set(count);
		// SwingUtilities.invokeLater(() -> {
		// progressBar.setString(count + "/" + goal);
		// progressBar.setValue(count);
		// progressBar.setMaximum(goal);
		// });
	}

	@Override
	public void finishArchive() {
		incrementCount();
	}

	@Override
	public void finishClass() {
		incrementCount();
	}

	@Override
	public void finishPerClassAnalysis() {
		updateStage(edu.umd.cs.findbugs.L10N.getLocalString("progress.finishing_analysis", "Finishing analysis..."));
	}

	@Override
	public void reportNumberOfArchives(int numArchives) {
		updateStage(edu.umd.cs.findbugs.L10N.getLocalString("progress.scanning_archives", "Scanning archives..."));
		updateCount(0, numArchives);
	}

	@Override
	public void startAnalysis(int numClasses) {
		pass++;
		String localString = edu.umd.cs.findbugs.L10N.getLocalString("progress.analyzing_classes", "Analyzing classes...");
		updateStage(localString + ", pass " + pass + "/" + classesPerPass.length);
		updateCount(0, numClasses);
	}

	@Override
	public void predictPassCount(int[] classesPerPass) {
		this.classesPerPass = classesPerPass;
	}

	@Override
	public void startArchive(String name) {
		// Ignore
	}

}