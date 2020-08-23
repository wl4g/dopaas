package com.wl4g.devops.scm.common.config;

import java.util.Map;

import lombok.Getter;

/**
 * {@link IniPropertySource}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
@Getter
public class IniPropertySource extends TextPropertySource<IniPropertySource> {
	private static final long serialVersionUID = 4885899687723244374L;

	/** Configuration source typeof map */
	private Map<String, Object> source;

	public IniPropertySource() {
		super();
	}

	public IniPropertySource(String profile, String content) {
		super(profile, content);
		this.source = null; // TODO
	}

}
