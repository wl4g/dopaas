package com.wl4g.devops.common.internal.logback;

import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.action.NOPAction;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;

import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.core.env.Environment;

/**
 * Extended version of the Logback {@link JoranConfigurator} that adds
 * additional Spring Boot rules.
 *
 * @author Phillip Webb
 */
public class SpringBootJoranConfigurator extends JoranConfigurator {

	private LoggingInitializationContext initializationContext;

	SpringBootJoranConfigurator(LoggingInitializationContext initializationContext) {
		this.initializationContext = initializationContext;
	}

	@Override
	public void addInstanceRules(RuleStore rs) {
		super.addInstanceRules(rs);
		Environment environment = this.initializationContext.getEnvironment();
		rs.addRule(new ElementSelector("configuration/springProperty"), new SpringPropertyAction(environment));
		rs.addRule(new ElementSelector("*/springProfile"), new SpringProfileAction(this.initializationContext.getEnvironment()));
		rs.addRule(new ElementSelector("*/springProfile/*"), new NOPAction());
	}

}
