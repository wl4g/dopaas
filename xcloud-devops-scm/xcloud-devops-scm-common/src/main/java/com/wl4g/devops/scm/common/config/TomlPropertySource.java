package com.wl4g.devops.scm.common.config;

import java.util.Map;

import com.wl4g.devops.scm.common.model.AbstractConfigInfo.ConfigProfile;

import lombok.Getter;

/**
 * {@link TomlPropertySource}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
public class TomlPropertySource extends GenericPropertySource {
	private static final long serialVersionUID = 4885899687723244374L;

	/** Configuration source typeof map */
	private Map<String, Object> source;

	@Override
	public void doRead(ConfigProfile profile, String sourceContent) {
		throw new UnsupportedOperationException();
	}

}
