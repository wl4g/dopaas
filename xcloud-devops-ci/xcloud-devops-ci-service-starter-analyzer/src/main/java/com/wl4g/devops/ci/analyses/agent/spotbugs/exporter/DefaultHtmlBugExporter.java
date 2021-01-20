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
package com.wl4g.devops.ci.analyses.agent.spotbugs.exporter;

import java.io.File;
import java.io.FileOutputStream;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.HTMLBugReporter;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.charsets.UTF8;

/**
 * Default HTML bug exporter
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月21日
 * @since
 */
public class DefaultHtmlBugExporter implements BugExporter {

	@Override
	public void doExport(Project project, BugCollection bugs, File f) throws Exception {
		HTMLBugReporter reporter = new HTMLBugReporter(project, "default.xsl");
		reporter.setIsRelaxed(true);
		reporter.setOutputStream(UTF8.printStream(new FileOutputStream(f)));
		for (BugInstance bug : bugs.getCollection()) {
			try {
				reporter.reportBug(bug);
			} catch (Exception e) {
				e.printStackTrace(); // Print to parent process
			}
		}
		reporter.finish();
	}

}