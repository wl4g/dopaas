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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.annotation.Nonnull;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugCollectionBugReporter;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.DetectorFactoryCollection;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.FindBugsProgress;
import edu.umd.cs.findbugs.IFindBugsEngine;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.config.UserPreferences;
import edu.umd.cs.findbugs.gui2.Driver;

/**
 * {@link edu.umd.cs.findbugs.gui2.BugLoader#doAnalysis(Project, FindBugsProgress)}
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月20日
 * @since
 */
public class SpotbugsAnalyzerEngine {

	public static void main(String[] args) throws Exception {
		Project project = new Project();
		File targetDir = new File("C:\\Users\\Administrator\\Desktop\\iam-server-master-bin\\libs");
		for (File assetFile : targetDir.listFiles()) {
			project.addFile(assetFile.getAbsolutePath());
		}

		// Create engine.
		StringWriter warnWriter = new StringWriter();
		// Do analyses.
		BugCollection bugs = doAnalysis(project, warnWriter, new PrintAnalyzingProgress(System.out));

		// Has warnings?
		String warnings = warnWriter.toString();
		if (!warnings.isEmpty()) {
			System.out.print(warnings);
		}

		bugs.writeXML("C:\\Users\\Administrator\\Desktop\\test-bugs.xml");
	}

	/**
	 * Create the IFindBugsEngine that will be used to analyze the application.
	 * 
	 * @param project
	 * @param warnWriter
	 * @param progress
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private final static BugCollection doAnalysis(@Nonnull Project project, @Nonnull StringWriter warnWriter,
			@Nonnull FindBugsProgress progress) throws IOException, InterruptedException {
		BugCollectionBugReporter reporter = new BugCollectionBugReporter(project, new PrintWriter(warnWriter, true));
		reporter.setPriorityThreshold(Priorities.LOW_PRIORITY);
		IFindBugsEngine engine = createEngine0(project, reporter);
		engine.setUserPreferences(UserPreferences.createDefaultUserPreferences());
		engine.setProgressCallback(progress);
		engine.setProjectName(project.getProjectName());
		engine.execute(); // Execution.
		return reporter.getBugCollection();
	}

	/**
	 * Create the IFindBugsEngine that will be used to analyze the application.
	 *
	 * @param p
	 *            the Project
	 * @param reporter
	 *            the PrintCallBack
	 * @return the IFindBugsEngine
	 */
	private final static IFindBugsEngine createEngine0(@Nonnull Project p, BugReporter reporter) {
		FindBugs2 engine = new FindBugs2();
		engine.setBugReporter(reporter);
		engine.setProject(p);
		engine.setDetectorFactoryCollection(DetectorFactoryCollection.instance());
		//
		// Honor -effort option if one was given on the command line.
		//
		engine.setAnalysisFeatureSettings(Driver.getAnalysisSettingList());
		return engine;
	}

}
