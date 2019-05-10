package com.wl4g.devops.shell;

import com.wl4g.devops.shell.runner.InteractiveRunner;
import com.wl4g.devops.shell.runner.RunnerBuilder;

/**
 * Shell bootstrap program
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月2日
 * @since
 */
public class ShellBootstrap {

	public static void main(String[] args) {
		RunnerBuilder.builder().provider(InteractiveRunner.class).prompt("> ").build().run(args);
	}

}
