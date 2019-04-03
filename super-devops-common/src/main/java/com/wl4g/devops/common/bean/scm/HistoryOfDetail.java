package com.wl4g.devops.common.bean.scm;

import java.util.List;

public class HistoryOfDetail extends ReleaseHistory {

	private List<ReleaseDetail> releaseDetails;

	public List<ReleaseDetail> getReleaseDetails() {
		return releaseDetails;
	}

	public void setReleaseDetails(List<ReleaseDetail> releaseDetails) {
		this.releaseDetails = releaseDetails;
	}
}
