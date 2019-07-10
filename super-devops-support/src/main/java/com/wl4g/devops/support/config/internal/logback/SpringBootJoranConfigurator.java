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
package com.wl4g.devops.support.config.internal.logback;

import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.action.NOPAction;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;

import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.core.env.Environment;

import com.wl4g.devops.support.config.internal.logback.SpringProfileAction;
import com.wl4g.devops.support.config.internal.logback.SpringPropertyAction;

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