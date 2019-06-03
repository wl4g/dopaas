package com.wl4g.devops.scm.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.devops.common.bean.scm.model.GetRelease;
import com.wl4g.devops.common.bean.scm.model.PreRelease;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage;
import com.wl4g.devops.common.bean.scm.model.ReportInfo;

public class NothingConfigSourceHandler implements ConfigContextHandler {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public ReleaseMessage findSource(GetRelease get) {
		log.info("Find config source: {}", get);
		return null;
	}

	@Override
	public void report(ReportInfo report) {
		log.info("Config release report: {}", report);
	}

	@Override
	public void release(PreRelease pre) {
		log.info("Config source release: {}", pre);
	}

}
