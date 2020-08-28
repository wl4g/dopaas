package com.wl4g.devops.scm.common.config;

import com.wl4g.devops.scm.common.model.AbstractConfigInfo.ConfigProfile;

import lombok.Getter;

/**
 * {@link HoconPropertySource}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
public class HoconPropertySource extends GenericPropertySource {
	private static final long serialVersionUID = -2725870342625827000L;

	@Override
	public void doRead(ConfigProfile profile, String sourceContent) {
		throw new UnsupportedOperationException();
	}

}
