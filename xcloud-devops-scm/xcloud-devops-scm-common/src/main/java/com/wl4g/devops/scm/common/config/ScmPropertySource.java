package com.wl4g.devops.scm.common.config;

import java.io.Serializable;

import com.wl4g.devops.scm.common.model.AbstractConfigInfo.ConfigProfile;

/**
 * SCM property source interface definition.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
public interface ScmPropertySource extends Serializable {

	/**
	 * Gets {@link ConfigProfile}
	 */
	default void getConfigProfile() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Read and parse configuration property source to itself.
	 * 
	 * @param profile
	 * @param sourceContent
	 */
	default void read(ConfigProfile profile, String sourceContent) {
		throw new UnsupportedOperationException();
	}

}
