package com.wl4g.devops.scm.common.config;

import com.wl4g.devops.scm.common.model.AbstractConfigInfo.ConfigProfile;

import lombok.Getter;

/**
 * {@link YamlMapPropertySource}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
public class YamlMapPropertySource extends AbstractConfigSource {
	private static final long serialVersionUID = -4793468560178245882L;

	@Override
	public void doRead(ConfigProfile profile, String sourceContent) {
		log.debug("Resolving cipher configuration propertySource ...");

		// TODO

	}

}
