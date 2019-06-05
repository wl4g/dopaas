/*
 * Copyright 2017 ~ 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.common.internal.logback;

import java.lang.reflect.Method;
import java.nio.charset.Charset;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;

import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.boot.logging.logback.ColorConverter;
import org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter;
import org.springframework.boot.logging.logback.LevelRemappingAppender;
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.util.ReflectionUtils;

/**
 * Custom logback configuration used by Spring Boot. Uses
 * {@link LogbackConfigurator} to improve startup time. See also the
 * {@code defaults.xml}, {@code console-appender.xml} and
 * {@code file-appender.xml} files provided for classic {@code logback.xml} use.
 * </br>
 * </br>
 * 
 * <b>Set on the boot class:</b>
 * 
 * <pre>
 * public static MyBootstrap {
 * 	static {
 * 		System.setProperty(LoggingSystem.SYSTEM_PROPERTY, LogbackLoggingSystem.class.getName());
 * 	}
 * 	
 * 	public static void main(String[] args) {
 * 		SpringApplication(MyBootstrap.class, args);
 * 	}
 * }
 * </pre>
 * 
 * <b>application.yml e.g:</b>
 * 
 * <pre>
 * logging:
 *   file: /var/log/${spring.application.name}/${spring.application.name}.log
 *   root: INFO
 *   <b>policy:</b>
 *     <b>maxFileSize: 1GB</b>
 *     <b>minIndex: 1</b>
 *     <b>maxIndex: 30</b>
 *   level:
 *     org:
 *       springframework: INFO
 *       apache: INFO
 * </pre>
 * 
 * @author Wangl.sir
 * @author Phillip Webb
 * @since 1.1.2
 */
public class CustomLogbackConfiguration {

	private static final String CONSOLE_LOG_PATTERN = "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} "
			+ "%clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} "
			+ "%clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} "
			+ "%clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}";

	private static final String FILE_LOG_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} "
			+ "${LOG_LEVEL_PATTERN:-%5p} ${PID:- } --- [%t] %-40.40logger{39} : %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}";

	private static final Charset UTF8 = Charset.forName("UTF-8");

	private final PropertyResolver logging; // For customized

	private final LogFile logFile;

	CustomLogbackConfiguration(LoggingInitializationContext initializationContext, LogFile logFile) {
		this.logging = getPatternsResolver(initializationContext.getEnvironment());
		this.logFile = logFile;
	}

	private PropertyResolver getPatternsResolver(Environment environment) {
		if (environment == null) {
			return new PropertySourcesPropertyResolver(null);
		}
		return RelaxedPropertyResolver.ignoringUnresolvableNestedPlaceholders(environment, "logging.");
	}

	public void apply(LogbackConfigurator config) {
		synchronized (config.getConfigurationLock()) {
			base(config);
			Appender<ILoggingEvent> consoleAppender = consoleAppender(config);
			if (this.logFile != null) {
				Appender<ILoggingEvent> fileAppender = fileAppender(config, this.logFile.toString());
				config.root(Level.INFO, consoleAppender, fileAppender);
			} else {
				config.root(Level.INFO, consoleAppender);
			}
		}
	}

	private void base(LogbackConfigurator config) {
		config.conversionRule("clr", ColorConverter.class);
		config.conversionRule("wex", WhitespaceThrowableProxyConverter.class);
		config.conversionRule("wEx", ExtendedWhitespaceThrowableProxyConverter.class);
		LevelRemappingAppender debugRemapAppender = new LevelRemappingAppender("org.springframework.boot");
		config.start(debugRemapAppender);
		config.appender("DEBUG_LEVEL_REMAPPER", debugRemapAppender);
		config.logger("org.apache.catalina.startup.DigesterFactory", Level.ERROR);
		config.logger("org.apache.catalina.util.LifecycleBase", Level.ERROR);
		config.logger("org.apache.coyote.http11.Http11NioProtocol", Level.WARN);
		config.logger("org.apache.sshd.common.util.SecurityUtils", Level.WARN);
		config.logger("org.apache.tomcat.util.net.NioSelectorPool", Level.WARN);
		config.logger("org.crsh.plugin", Level.WARN);
		config.logger("org.crsh.ssh", Level.WARN);
		config.logger("org.eclipse.jetty.util.component.AbstractLifeCycle", Level.ERROR);
		config.logger("org.hibernate.validator.internal.util.Version", Level.WARN);
		config.logger("org.springframework.boot.actuate.autoconfigure." + "CrshAutoConfiguration", Level.WARN);
		config.logger("org.springframework.boot.actuate.endpoint.jmx", null, false, debugRemapAppender);
		config.logger("org.thymeleaf", null, false, debugRemapAppender);
	}

	private Appender<ILoggingEvent> consoleAppender(LogbackConfigurator config) {
		ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		String logPattern = this.logging.getProperty("pattern.console", CONSOLE_LOG_PATTERN);
		encoder.setPattern(OptionHelper.substVars(logPattern, config.getContext()));
		encoder.setCharset(UTF8);
		config.start(encoder);
		appender.setEncoder(encoder);
		config.appender("CONSOLE", appender);
		return appender;
	}

	private Appender<ILoggingEvent> fileAppender(LogbackConfigurator config, String logFile) {
		RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<ILoggingEvent>();
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		String logPattern = this.logging.getProperty("pattern.file", FILE_LOG_PATTERN);
		encoder.setPattern(OptionHelper.substVars(logPattern, config.getContext()));
		appender.setEncoder(encoder);
		config.start(encoder);
		appender.setFile(logFile);
		setRollingPolicy(appender, config, logFile);
		setMaxFileSize(appender, config);
		config.appender("FILE", appender);
		return appender;
	}

	private void setRollingPolicy(RollingFileAppender<ILoggingEvent> appender, LogbackConfigurator config, String logFile) {
		FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
		//
		// Start - for customized
		//
		int minIndex = Integer.parseInt(logging.getProperty("policy.minIndex", "1"));
		int maxIndex = Integer.parseInt(logging.getProperty("policy.maxIndex", "7"));
		rollingPolicy.setMinIndex(minIndex);
		rollingPolicy.setMaxIndex(maxIndex);
		// End - for customized

		rollingPolicy.setFileNamePattern(logFile + ".%i");
		appender.setRollingPolicy(rollingPolicy);
		rollingPolicy.setParent(appender);
		config.start(rollingPolicy);
	}

	private void setMaxFileSize(RollingFileAppender<ILoggingEvent> appender, LogbackConfigurator config) {
		SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<ILoggingEvent>();
		try {
			//
			// Start - for customized
			//
			String maxFileSize = this.logging.getProperty("policy.maxFileSize", "10MB");
			triggeringPolicy.setMaxFileSize(FileSize.valueOf(maxFileSize));
			// End - for customized

		} catch (NoSuchMethodError ex) {
			// Logback < 1.1.8 used String configuration
			Method method = ReflectionUtils.findMethod(SizeBasedTriggeringPolicy.class, "setMaxFileSize", String.class);
			ReflectionUtils.invokeMethod(method, triggeringPolicy, "10MB");
		}
		appender.setTriggeringPolicy(triggeringPolicy);
		config.start(triggeringPolicy);
	}

}