package com.wl4g.devops.ci.analyses.spotbugs;

import static org.springframework.util.Assert.notNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.FindBugsProgress;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.gui2.BugLoader;

/**
 * Code analysis thread.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月18日
 * @since
 */
public class AnalyzingTask implements Runnable {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	final private Project project;
	final private FindBugsProgress progress;

	public AnalyzingTask(Project project, FindBugsProgress progress) {
		notNull(project, "null analysisProject");
		notNull(progress, "null findBugsProgress");
		this.project = project;
		this.progress = progress;
	}

	@Override
	public void run() {
		try {
			BugCollection bugs = BugLoader.doAnalysis(project, progress);
		} catch (Throwable e) {
			log.error("Analysis failed", e.getClass().getSimpleName() + ": " + e.getMessage());
		}

	}

}