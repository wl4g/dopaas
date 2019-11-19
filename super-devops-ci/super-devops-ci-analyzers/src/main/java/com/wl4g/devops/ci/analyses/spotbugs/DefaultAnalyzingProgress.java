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
//
//import static org.springframework.util.Assert.notNull;
//
//import java.io.IOException;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import javax.annotation.Nonnull;
//
//import org.springframework.util.Assert;
//
//import com.wl4g.devops.common.task.GenericTaskRunner;
//import com.wl4g.devops.common.task.RunnerProperties;
//
//import edu.umd.cs.findbugs.BugCollection;
//import edu.umd.cs.findbugs.FindBugsProgress;
//import edu.umd.cs.findbugs.Project;
//import edu.umd.cs.findbugs.gui2.AnalysisCallback;
//import edu.umd.cs.findbugs.gui2.BugLoader;
//import edu.umd.cs.findbugs.gui2.Driver;
//
///**
// * Note: Don't remove the final, if anyone extends this class, bad things could
// * happen, since a thread is started in this class's constructor. Creating an
// * instance of this class runs a FindBugs analysis, and pops up a nice progress
// * window.
// * 
// * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
// * @version v1.0 2019年11月18日
// * @since
// */
//public final class DefaultAnalyzingProgress implements FindBugsProgress {
//
//	@Nonnull
//	private final Project project;
//	private final AnalysisCallback callback;
//	private final AnalysisTask analysisTask = new AnalysisTask();
//
//	private final AtomicBoolean analysisFinishedState = new AtomicBoolean(false);
//	private AtomicInteger count = new AtomicInteger(0);
//	private int goal;
//
//	private int pass = 0;
//	private int[] classesPerPass;
//
//	/**
//	 * {@link DefaultAnalyzingProgress}
//	 *
//	 * @param project
//	 *            The Project to analyze
//	 * @param callback
//	 *            contains what to do if the analysis is interrupted and what to
//	 *            do if it finishes normally
//	 * @param joinThread
//	 *            Whether or not this constructor should return before the
//	 *            analysis is complete. If true, the constructor does not return
//	 *            until the analysis is either finished or interrupted.
//	 */
//	private DefaultAnalyzingProgress(@Nonnull Project project, AnalysisCallback callback, boolean joinThread) {
//		notNull(project, "null project");
//		this.project = project;
//		this.callback = callback;
//	}
//
//	@Override
//	public void run() {
//		DefaultAnalyzingProgress progress = new DefaultAnalyzingProgress(project, callback, joinThread);
//		MainFrame.getInstance().acquireDisplayWait();
//		try {
//			progress.analysisTask.start();
//			if (joinThread) {
//				try {
//					progress.analysisTask.join();
//				} catch (InterruptedException e) {
//				}
//			}
//		} finally {
//			if (joinThread) {
//				MainFrame.getInstance().releaseDisplayWait();
//			}
//		}
//	}
//
//	private void cancel() {
//		if (!analysisFinishedState.get()) {
//			analysisTask.interrupt();
//			setVisible(false);
//			// TODO there should be a call to dispose() here, but it seems to
//			// cause repainting issues
//		}
//	}
//
//	private void updateStage(String stage) {
//		statusLabel.setText(stage);
//	}
//
//	private void incrementCount() {
//		count.incrementAndGet();
//		SwingUtilities.invokeLater(() -> {
//			progressBar.setString(count + "/" + goal);
//			progressBar.setValue(count);
//		});
//	}
//
//	private void updateCount(final int count, final int goal) {
//		this.count.set(count);
//		this.goal = goal;
//		SwingUtilities.invokeLater(() -> {
//			progressBar.setString(count + "/" + goal);
//			progressBar.setValue(count);
//			progressBar.setMaximum(goal);
//		});
//	}
//
//	@Override
//	public void finishArchive() {
//		incrementCount();
//	}
//
//	@Override
//	public void finishClass() {
//		incrementCount();
//	}
//
//	@Override
//	public void finishPerClassAnalysis() {
//		updateStage(edu.umd.cs.findbugs.L10N.getLocalString("progress.finishing_analysis", "Finishing analysis..."));
//	}
//
//	@Override
//	public void reportNumberOfArchives(int numArchives) {
//		updateStage(edu.umd.cs.findbugs.L10N.getLocalString("progress.scanning_archives", "Scanning archives..."));
//		updateCount(0, numArchives);
//	}
//
//	@Override
//	public void startAnalysis(int numClasses) {
//		pass++;
//		String localString = edu.umd.cs.findbugs.L10N.getLocalString("progress.analyzing_classes", "Analyzing classes...");
//		updateStage(localString + ", pass " + pass + "/" + classesPerPass.length);
//		updateCount(0, numClasses);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see edu.umd.cs.findbugs.FindBugsProgress#predictPassCount(int[])
//	 */
//	@Override
//	public void predictPassCount(int[] classesPerPass) {
//		this.classesPerPass = classesPerPass;
//	}
//
//	@Override
//	public void startArchive(String name) {
//		// Ignore
//	}
//
//	/**
//	 * Code analysis thread.
//	 *
//	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
//	 * @version v1.0 2019年11月18日
//	 * @since
//	 */
//	private class AnalysisTask extends Thread {
//		{
//			// Give the analysis thread its (possibly user-defined) priority.
//			// The default is a slightly lower priority than the UI.
//			setPriority(Driver.getPriority());
//			setName("SpotbugsAnalysisThread");
//		}
//
//		@Override
//		public void run() {
//			BugCollection data;
//			try {
//				data = BugLoader.doAnalysis(project, DefaultAnalyzingProgress.this);
//			} catch (InterruptedException e) {
//				callback.analysisInterrupted();
//				// We don't have to clean up the dialog because the
//				// cancel button handler does this already.
//				return;
//			} catch (IOException e) {
//				Logger.getLogger(DefaultAnalyzingProgress.class.getName()).log(Level.WARNING,
//						"IO Error while performing analysis", e);
//				callback.analysisInterrupted();
//				scheduleErrorDialog("Analysis failed", e.getClass().getSimpleName() + ": " + e.getMessage());
//				return;
//			} catch (Throwable e) {
//				callback.analysisInterrupted();
//				scheduleErrorDialog("Analysis failed", e.getClass().getSimpleName() + ": " + e.getMessage());
//				return;
//			}
//
//			// Analysis succeeded
//			analysisFinishedState.set(true);
//			callback.analysisFinished(data);
//		}
//
//	}
//
//}