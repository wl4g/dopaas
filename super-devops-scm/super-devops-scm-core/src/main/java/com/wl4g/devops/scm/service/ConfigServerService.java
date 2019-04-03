package com.wl4g.devops.scm.service;

import com.wl4g.devops.common.bean.scm.model.*;

public interface ConfigServerService {

	/**
	 * Find configuration property-source.
	 * 
	 * @param getRelease
	 *            request client instance.
	 * @return
	 */
	public ReleaseModel findSource(GetReleaseModel getRelease);

	/**
	 * Access configuration client report configure result.
	 * 
	 * @param report
	 *            request parameter.
	 * @param resp
	 *            response parameter.
	 * @return
	 */
	public void report(ReportModel report);

	/**
	 * Release configuration property-sources.
	 * 
	 * @param preRelease
	 *            request parameter.
	 */
	public void release(PreReleaseModel preRelease);

}
