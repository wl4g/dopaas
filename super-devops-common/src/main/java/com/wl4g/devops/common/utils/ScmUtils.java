package com.wl4g.devops.common.utils;

import com.wl4g.devops.common.bean.scm.model.BaseModel;
import com.wl4g.devops.common.bean.scm.model.BaseModel.ReleaseInstance;
import com.wl4g.devops.common.constants.SCMDevOpsConstants;

public final class ScmUtils {

	/**
	 * Generate Zookeeper configuration path.
	 * 
	 * @param base
	 *            Basic release information.
	 * @param hostPortString
	 *            Host and port(host:port) information.
	 * @return
	 */
	public final static String genZkConfigPath(BaseModel base, ReleaseInstance instance) {
		checkInvalid(base, instance);
		StringBuffer path = new StringBuffer(SCMDevOpsConstants.CONF_DISCOVERY_ROOT);
		path.append("/");
		path.append(base.getApplication());
		path.append("/");
		path.append(base.getProfile());
		path.append("/");
		path.append(instance.toString());
		return path.toString();
	}

	private final static void checkInvalid(BaseModel base, ReleaseInstance instance) {
		base.validation(false, false);
		instance.validation();
	}

}
