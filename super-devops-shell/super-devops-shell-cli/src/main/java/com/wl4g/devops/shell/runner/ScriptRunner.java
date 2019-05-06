package com.wl4g.devops.shell.runner;

import org.jline.utils.AttributedString;

import static org.apache.commons.lang3.exception.ExceptionUtils.*;

import org.apache.commons.lang3.StringUtils;

import com.wl4g.devops.shell.config.Configuration;

import static java.lang.System.err;

/**
 * Script shell component runner
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public class ScriptRunner extends AbstractRunner {

	public ScriptRunner(Configuration config, AttributedString attributed) {
		super(config, attributed);
	}

	@Override
	public void run(String[] args) {
		String line = StringUtils.join(args, " ");
		try {
			submit(line);
		} catch (Throwable e) {
			err.println(getStackTrace(e));
			shutdown(line);
		}
	}

}
