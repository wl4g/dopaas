package com.wl4g.devops.shell.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wl4g.devops.shell.cli.HelpOptions;
import com.wl4g.devops.shell.registry.ShellBeanRegistry;
import com.wl4g.devops.shell.registry.TargetMethodWrapper;
import com.wl4g.devops.shell.utils.Assert;

/**
 * Default bean registry
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月3日
 * @since
 */
public class DefaultBeanRegistry extends ShellBeanRegistry {
	final private static long serialVersionUID = -6852880158146389409L;

	private static class Holder {
		final private static DefaultBeanRegistry INSTANCE = new DefaultBeanRegistry();
	}

	/**
	 * Local and remote registed shell target methods for help.
	 */
	final private Map<String, HelpOptions> helpOptions = new ConcurrentHashMap<>(16);

	public final static DefaultBeanRegistry getSingle() {
		return Holder.INSTANCE;
	}

	/**
	 * Merge remote and local targetMethodWrapper
	 * 
	 * @param registed
	 * @return
	 */
	public DefaultBeanRegistry merge(Map<String, TargetMethodWrapper> registed) {
		Assert.state(helpOptions.isEmpty(), "Remote server registed target methods is null");

		// Registion from local.
		getTargetMethods().forEach((argname, tm) -> {
			Assert.state(helpOptions.putIfAbsent(argname, tm.getOptions()) == null,
					String.format("Already local registed commands: '%s'", argname));
		});

		// Registion from remote registed.
		registed.forEach((argname, tm) -> {
			Assert.state(helpOptions.putIfAbsent(argname, tm.getOptions()) == null,
					String.format(
							"Already remote registed commands: '%s', It is recommended to replace the shell definition @ShellMethod(name=xx)",
							argname));
		});

		return this;
	}

	public Map<String, HelpOptions> getHelpOptions() {
		return helpOptions;
	}

}
