package com.wl4g.devops.shell.runner;

import java.lang.reflect.Constructor;
import java.net.URL;
import org.jline.utils.AttributedString;

import org.apache.commons.lang3.StringUtils;

import com.wl4g.devops.shell.config.Configuration;
import com.wl4g.devops.shell.utils.Assert;

/**
 * Runner builder
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月2日
 * @since
 */
public abstract class RunnerBuilder {

	private String conf;

	private AttributedString attributed;

	private Class<? extends Runner> provider;

	private RunnerBuilder() {
	}

	public final static RunnerBuilder builder() {
		return new RunnerBuilder() {
		};
	}

	public RunnerBuilder config(String conf) {
		Assert.hasText(conf, "conf is empty, please check configure");
		this.conf = conf;
		return this;
	}

	public RunnerBuilder prompt(String prompt) {
		Assert.hasText(prompt, "prompt is empty, please check configure");
		this.attributed = new AttributedString(prompt);
		return this;
	}

	public RunnerBuilder attributed(AttributedString attributed) {
		Assert.notNull(attributed, "attributed is null, please check configure");
		this.attributed = attributed;
		return this;
	}

	public RunnerBuilder provider(Class<? extends Runner> provider) {
		Assert.notNull(provider, "provider is null, please check configure");
		this.provider = provider;
		return this;
	}

	public Runner build() {
		try {
			Configuration config = null;
			if (StringUtils.isNotBlank(conf)) {
				config = Configuration.create(new URL("file://" + conf));
			} else {
				config = Configuration.create();
			}
			Assert.notNull(provider, "provider is null, please check configure");
			Assert.notNull(config, "config is null, please check configure");

			Constructor<? extends Runner> constr = provider.getConstructor(Configuration.class, AttributedString.class);
			return constr.newInstance(new Object[] { config, attributed });
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
