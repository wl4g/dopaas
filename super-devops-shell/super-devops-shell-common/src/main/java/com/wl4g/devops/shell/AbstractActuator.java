/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.shell;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.*;

import static com.wl4g.devops.shell.utils.Types.*;
import static com.wl4g.devops.shell.utils.ReflectionUtils2.*;
import com.wl4g.devops.shell.utils.ReflectionUtils2.FieldCallback2;
import com.wl4g.devops.shell.utils.ReflectionUtils2.FieldFilter2;
import com.wl4g.devops.shell.annotation.ShellOption;
import com.wl4g.devops.shell.registry.ShellBeanRegistry;
import com.wl4g.devops.shell.registry.TargetMethodWrapper;
import com.wl4g.devops.shell.utils.Assert;
import com.wl4g.devops.shell.utils.LineUtils;
import com.wl4g.devops.shell.utils.ResultFormatter;

/**
 * Abstract shell component actuator
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public abstract class AbstractActuator implements Actuator {

	/**
	 * Shell handler bean registry
	 */
	final protected ShellBeanRegistry registry;

	public AbstractActuator(ShellBeanRegistry registry) {
		Assert.notNull(registry, "Registry must not be null");
		this.registry = registry;
	}

	@Override
	public Object process(String line) {
		if (isEmpty(line)) {
			return null;
		}

		// Invocation and result processing
		Object result = doProcess(resolveCommands(line));

		// Post processing result
		postProcessResult(result);

		return result;
	}

	/**
	 * Commands processing
	 * 
	 * @param commands
	 * @return
	 * @throws Exception
	 */
	protected Object doProcess(List<String> commands) {
		Assert.notNull(commands, "Console input commands must not be null");

		// Main argument option.(remove)
		String mainArg = commands.remove(0);

		// Target method wrap
		Assert.isTrue(registry.contains(mainArg), String.format("'%s': command not found", mainArg));
		TargetMethodWrapper tm = registry.getTargetMethods().get(mainArg);

		// When the shell method parameter list is not empty, the command
		// line argument is required.
		if (!tm.getParameters().isEmpty()) {
			if (commands == null || commands.isEmpty()) {
				return ResultFormatter.getUsageFormat(mainArg, tm.getOptions());
			}
		}

		// Resolve method parameters
		Object[] args = resolveArguments(commands, tm);

		// Invocation
		try {
			return tm.getMethod().invoke(tm.getTarget(), args);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Resolve arguments to method parameters
	 * 
	 * @param commands
	 * @param tm
	 * @return
	 * @throws Exception
	 */
	protected Object[] resolveArguments(List<String> commands, TargetMethodWrapper tm) {
		/*
		 * Commands to javaBean map and validate protected. </br>
		 * (javaBean.fieldName or params.index(native type))->value
		 */
		final Map<String, String> beanMap = new HashMap<>();
		for (int i = 0; i < commands.size() - 1; i++) {
			if (i % 2 == 0) {
				// Input opt
				String argname = commands.get(i);
				Assert.hasText(argname, String.format("Unable to get parameter name, i:%s", i));
				// Value(May be empty) See:[MARK3]
				String value = commands.get(i + 1);

				// Convert and save
				beanMap.put(convertIfNecessary(argname, tm), value);
			}
		}

		// Validate arguments(if required)
		validateArguments(tm, beanMap);

		// Method arguments
		List<Object> args = new ArrayList<>();

		tm.getParameters().forEach(parameter -> {
			// See: TargetMethodWrapper#initialize
			try {
				// To javaBean parameter
				if (!parameter.baseType()) {
					Object paramBean = parameter.getParamType().newInstance();

					// Recursive full traversal deserialization.
					doWithFullFields(paramBean, new FieldFilter2() {
						@Override
						public boolean match(Object attach, Field f, Object property) {
							return beanMap.containsKey(f.getName());
						}
					}, new FieldCallback2() {
						@Override
						public void doWith(Object attach, Field f, Object property)
								throws IllegalArgumentException, IllegalAccessException {
							f.setAccessible(true);
							f.set(attach, baseConvert(beanMap.get(f.getName()), f.getType()));
						}
					});
					args.add(paramBean);
				}
				// [MARK1]: To native parameter
				else {
					ShellOption opt = parameter.getShellOption();

					// Mathing argument value
					Optional<Entry<String, String>> val = beanMap.entrySet().stream().filter(arg -> {
						return equalsAny(arg.getKey(), opt.opt(), opt.lopt());
					}).findFirst();

					// Default value
					String value = opt.defaultValue();
					if (val.isPresent()) {
						value = val.get().getValue();
					}
					Assert.hasText(value,
							String.format("Argument option: '-%s' or long option: '--%s' is required", opt.opt(), opt.lopt()));
					args.add(baseConvert(value, parameter.getParamType()));
				}

			} catch (Throwable e) {
				throw new IllegalStateException(e);
			}
		});

		return args.toArray();
	}

	/**
	 * Validate arguments(if required)
	 * 
	 * @param tm
	 * @param beanMap
	 */
	protected void validateArguments(TargetMethodWrapper tm, Map<String, String> beanMap) {
		tm.getParameters().forEach(parameter -> {
			if (parameter.baseType()) {
				return; // See:[MARK1][TargetMethodWrapper.MARK0]
			}

			// [MARK3]: Just verify the bean type parameter
			parameter.getAttributes().forEach((option, fname) -> {
				if (option.hasArg()) { // required?
					// javaBean.fieldName
					Assert.hasText(beanMap.get(fname),
							String.format("option: -%s, --%s cannot be empty", option.getOpt(), option.getLongOpt()));
				}
			});
		});
	}

	/**
	 * Post invocation result prcessing
	 * 
	 * @param result
	 */
	protected void postProcessResult(Object result) {

	}

	/**
	 * Convert argument to java bean actual param field name
	 * 
	 * @param argname
	 * @param tm
	 * @return java bean actual param field name or index(if native type)
	 */
	protected String convertIfNecessary(String argname, TargetMethodWrapper tm) {
		return tm.getSureParamName(LineUtils.clean(argname));
	}

	/**
	 * Resolve source commands
	 * 
	 * @param args
	 * @return
	 */
	protected List<String> resolveCommands(String line) {
		List<String> commands = LineUtils.parse(line);
		Assert.notEmpty(commands, "Commands must not be empty");
		return commands;
	}

}
