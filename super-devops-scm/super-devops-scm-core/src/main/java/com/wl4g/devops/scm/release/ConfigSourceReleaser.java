package com.wl4g.devops.scm.release;

import com.wl4g.devops.common.bean.scm.model.PreReleaseModel;

public interface ConfigSourceReleaser {

	public void release(PreReleaseModel preRelease);

}
