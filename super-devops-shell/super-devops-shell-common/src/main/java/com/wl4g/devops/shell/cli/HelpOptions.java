package com.wl4g.devops.shell.cli;

import org.apache.commons.cli.Options;

import com.wl4g.devops.shell.annotation.ShellMethod;

/**
 * Help option.</br>
 * See:{@link com.wl4g.devops.shell.command.DefaultInternalCommand#help()}[MARK0]
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public class HelpOptions extends Options {
	private static final long serialVersionUID = 2206030510132539771L;

	final private ShellMethod shellMethod;

	public HelpOptions(ShellMethod shellMethod) throws IllegalArgumentException {
		this.shellMethod = shellMethod;
	}

	public ShellMethod getShellMethod() {
		return shellMethod;
	}

}
