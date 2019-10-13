package com.wl4g.devops.ci.core;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.GenericTaskRunner.RunProperties;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Pipeline job executor runner.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class PipelineJobExecutor extends GenericTaskRunner<RunProperties> {

	final protected CiCdProperties config;

	public PipelineJobExecutor(CiCdProperties config) {
		super(config.getExecutor());
		this.config = config;
	}

	@Override
	public void run() {
		// Ignore.
	}

	public ThreadPoolExecutor getWorker() {
		return super.getWorker();
	}

}
