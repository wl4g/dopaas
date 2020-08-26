package com.wl4g.devops.scm.common.config;

import java.util.function.Function;

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
public class YamlMapPropertySource extends GenericPropertySource {
	private static final long serialVersionUID = -4793468560178245882L;

	@Override
	protected ScmPropertySource doResolved(Function<String, Object> cipherResolver) {
		// TODO Auto-generated method stub

		return super.doResolved(cipherResolver);
	}

}
