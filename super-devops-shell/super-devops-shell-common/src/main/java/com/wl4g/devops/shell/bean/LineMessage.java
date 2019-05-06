package com.wl4g.devops.shell.bean;

import com.wl4g.devops.shell.utils.Assert;

/**
 * Line commands message
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public class LineMessage extends Message {
	private static final long serialVersionUID = -8574315246731909685L;

	final private String line;

	public LineMessage(String line) {
		Assert.hasText(line, "line must not be empty");
		this.line = line;
	}

	public String getLine() {
		return line;
	}

	@Override
	public String toString() {
		return "LineMessage [line=" + line + "]";
	}

}
