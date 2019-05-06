package com.wl4g.devops.shell.bean;

import com.wl4g.devops.shell.utils.Assert;

/**
 * Result transform message
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public class ResultMessage extends Message {
	private static final long serialVersionUID = -8574315246731909685L;

	final private String content;

	public ResultMessage(String content) {
		Assert.hasText(content, "Content must not be empty");
		this.content = content;
	}

	public String getContent() {
		return content;
	}

}
