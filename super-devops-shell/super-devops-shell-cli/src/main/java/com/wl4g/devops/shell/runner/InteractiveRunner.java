package com.wl4g.devops.shell.runner;

import static java.lang.System.err;

import static org.apache.commons.lang3.exception.ExceptionUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

import com.wl4g.devops.shell.config.Configuration;

import org.jline.reader.UserInterruptException;
import org.jline.utils.AttributedString;

/**
 * Interactive shell component runner
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public class InteractiveRunner extends AbstractRunner {

	public InteractiveRunner(Configuration config, AttributedString attributed) {
		super(config, attributed);
	}

	@Override
	public void run(String[] args) {
		while (true) {
			String line = null;
			try {
				// Read line
				line = lineReader.readLine(attributed.toAnsi(lineReader.getTerminal()));

				// Submission processing
				if (isNotBlank(line)) {
					submit(line);
				}
			} catch (UserInterruptException e) {
				shutdown(line);
			} catch (Throwable e) {
				err.println(getStackTrace(e));
			}
		}
	}

}
