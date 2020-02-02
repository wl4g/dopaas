/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.shell.handler;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.zip.CRC32;
import java.util.Optional;

import static com.wl4g.devops.tool.common.reflect.ReflectionUtils2.doFullWithFields;
import static com.wl4g.devops.tool.common.reflect.ReflectionUtils2.isGenericModifier;
import static java.lang.System.*;

import static org.apache.commons.lang3.StringUtils.*;
import static com.wl4g.devops.tool.common.lang.Assert2.*;

import com.wl4g.devops.shell.annotation.ShellOption;
import com.wl4g.devops.shell.config.AbstractConfiguration;
import com.wl4g.devops.shell.registry.ShellHandlerRegistrar;
import com.wl4g.devops.shell.registry.TargetMethodWrapper;
import com.wl4g.devops.shell.registry.TargetMethodWrapper.TargetParameter;
import com.wl4g.devops.shell.utils.LineUtils;
import com.wl4g.devops.tool.common.reflect.TypeUtils2;
import static com.wl4g.devops.shell.utils.ShellUtils.*;

/**
 * Abstract shell component actuator handler.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public abstract class AbstractShellHandler implements ShellHandler {

	/**
	 * Enable shell console debug.
	 */
	final public static boolean DEBUG = getProperty("xdebug") != null;

	/**
	 * Shell handler bean registry
	 */
	final protected ShellHandlerRegistrar registrar;

	/**
	 * Shell configuration
	 */
	final protected AbstractConfiguration config;

	public AbstractShellHandler(AbstractConfiguration config, ShellHandlerRegistrar registry) {
		notNull(registry, "Registry must not be null");
		notNull(config, "Registry must not be null");
		this.registrar = registry;
		this.config = config;
	}

	@Override
	public Object process(String line) {
		if (isEmpty(line)) {
			return null;
		}

		// Invocation and result processing
		Object result = doProcess(resolveCommands(line));

		// Post processing result
		postHandleOutput(result);

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
		notNull(commands, "Console input commands must not be null");

		// Main argument option.(remove)
		String mainArg = commands.remove(0);

		// Target method wrap
		isTrue(registrar.contains(mainArg), String.format("'%s': command not found", mainArg));
		TargetMethodWrapper tm = registrar.getTargetMethods().get(mainArg);

		try {
			// Resolve method parameters
			List<Object> args = resolveArguments(commands, tm);

			// PreProcessing
			preHandle(tm, args);

			// Invocation
			return tm.getMethod().invoke(tm.getTarget(), args.toArray());
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
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws Exception
	 */
	protected List<Object> resolveArguments(List<String> commands, TargetMethodWrapper tm)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		notNull(tm, "Error, Should targetMethodWrapper not be null?");

		/*
		 * Commands to javaBean map and validate protected. </br>
		 * (javaBean.fieldName or params.index(native type))->value
		 */
		final Map<String, String> beanMap = new HashMap<>();
		if (commands != null && !commands.isEmpty()) {
			for (int i = 0; i < commands.size() - 1; i++) {
				if (i % 2 == 0) {
					// Input opt
					String argname = commands.get(i);
					hasText(argname, String.format("Unable to get parameter name, i:%s", i));
					// Value(May be empty) See:[MARK3]
					String value = commands.get(i + 1);

					// Convert and save
					beanMap.put(convertIfNecessary(argname, tm), value);
				}
			}
		}

		// Method arguments.
		final List<Object> args = new ArrayList<>();
		for (TargetParameter parameter : tm.getParameters()) {
			// [MARK1]: To native parameter, See:[TargetParameter.MARK7]
			if (parameter.simpleType()) {
				ShellOption shOpt = parameter.getShellOption();
				// Matching argument value
				Optional<Entry<String, String>> val = beanMap.entrySet().stream()
						.filter(arg -> equalsAny(arg.getKey(), shOpt.opt(), shOpt.lopt())).findFirst();

				// Default value
				String value = shOpt.defaultValue();
				if (val.isPresent()) {
					value = val.get().getValue();
				}

				// Validate argument(if required)
				if (shOpt.required() && !beanMap.containsKey(shOpt.opt()) && !beanMap.containsKey(shOpt.lopt())
						&& isBlank(shOpt.defaultValue())) {
					throw new IllegalArgumentException(
							String.format("option: '-%s', '--%s' is required", shOpt.opt(), shOpt.lopt()));
				}
				args.add(TypeUtils2.instantiate(value, parameter.getParamType()));
			}
			// Convert javaBean parameter.
			// See: TargetMethodWrapper#initialize
			else {
				Object paramBean = parameter.getParamType().newInstance();

				// Recursive full traversal De-serialization.
				doFullWithFields(paramBean, field -> {
					// [MARK4],See:[ShellUtils.MARK0][TargetParameter.MARK1]
					return isGenericModifier(field.getModifiers());
				}, (field, objOfField) -> {
					if (Objects.isNull(objOfField)) {
						objOfField = TypeUtils2.instantiate(null, field.getType());
					}

					ShellOption shOpt = field.getDeclaredAnnotation(ShellOption.class);
					notNull(shOpt, "Error, Should shellOption not be null?");
					Object value = beanMap.get(field.getName());
					if (Objects.isNull(value)) {
						value = shOpt.defaultValue();
					}
					// Validate argument(if required)
					if (shOpt.required() && !beanMap.containsKey(field.getName()) && isBlank(shOpt.defaultValue())) {
						throw new IllegalArgumentException(
								String.format("option: '-%s', '--%s' is required", shOpt.opt(), shOpt.lopt()));
					}

					value = instantiateWithInitOptionValue((String) value, field.getType());
					field.setAccessible(true);
					field.set(objOfField, value);
				});
				args.add(paramBean);
			}
		}

		return args;
	}

	/**
	 * It the resolving parameters before handle.
	 * 
	 * @param tm
	 * @param args
	 */
	protected void preHandle(TargetMethodWrapper tm, List<Object> args) {

	}

	/**
	 * Post invocation stdout result message.
	 * 
	 * @param stdout
	 */
	protected void postHandleOutput(Object stdout) {

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
		notEmpty(commands, "Commands must not be empty");
		return commands;
	}

	/**
	 * Ensure resolve server listen port.
	 * 
	 * @param appName
	 * @return
	 */
	protected int ensureDetermineServPort(String appName) {
		hasLength(appName, "appName must not be empty");
		String origin = trimToEmpty(appName).toUpperCase(Locale.ENGLISH);

		CRC32 crc32 = new CRC32();
		crc32.update(origin.getBytes(Charset.forName("UTF-8")));
		int mod = config.getEndPort() - config.getBeginPort();
		int servport = (int) (config.getBeginPort() + (crc32.getValue() % mod & (mod - 1)));

		if (DEBUG) {
			out.println(String.format("Shell servports (%s ~ %s), origin(%s), sign(%s), determine(%s)", config.getBeginPort(),
					config.getEndPort(), origin, crc32.getValue(), servport));
		}
		return servport;
	}

}