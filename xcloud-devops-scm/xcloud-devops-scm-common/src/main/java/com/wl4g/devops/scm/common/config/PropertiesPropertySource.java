package com.wl4g.devops.scm.common.config;

import com.wl4g.devops.scm.common.model.AbstractConfigInfo.ConfigProfile;

import lombok.Getter;

/**
 * {@link PropertiesPropertySource}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
public class PropertiesPropertySource extends GenericPropertySource {
	private static final long serialVersionUID = 1755382479743018762L;

	@Override
	public void doRead(ConfigProfile profile, String sourceContent) {
		throw new UnsupportedOperationException();
	}

}
