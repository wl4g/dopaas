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
package com.wl4g.devops.shell.registry;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.cli.Option;
import static org.apache.commons.lang3.StringUtils.*;

import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.shell.annotation.ShellOption;
import com.wl4g.devops.shell.cli.HelpOption;
import com.wl4g.devops.shell.cli.HelpOptions;
import com.wl4g.devops.shell.cli.InternalCommand;
import com.wl4g.devops.shell.utils.Assert;
import static com.wl4g.devops.shell.utils.Reflections.*;
import static com.wl4g.devops.shell.utils.Types.*;
import static com.wl4g.devops.shell.cli.InternalCommand.*;
import static com.wl4g.devops.shell.registry.TargetMethodWrapper.TargetParameter.*;

/**
 * Shell component target method wrapper
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public class TargetMethodWrapper implements Serializable {
	final private static long serialVersionUID = -8763772515086222131L;

	/**
	 * Shell method annotation
	 */
	final private ShellMethod shellMethod;

	/**
	 * Shell native method.
	 */
	final transient private Method method;

	/**
	 * Shell method native target object
	 */
	final transient private Object target;

	/**
	 * Method parameters information
	 */
	final private List<TargetParameter> parameters = new ArrayList<>(4);

	/**
	 * Constructor target method.</br>
	 * 
	 * See: <a href=
	 * "https://www.cnblogs.com/guangshan/p/4660564.html">https://www.cnblogs.com/guangshan/p/4660564.html</a>
	 * 
	 * @param sm
	 * @param method
	 * @param target
	 */
	public TargetMethodWrapper(ShellMethod sm, Method method, Object target) {
		Assert.notNull(sm, "Shell method must not be null");
		Assert.notNull(method, "Shell target method must not be null");
		Assert.notNull(sm.keys(), "Shell method keys must not be null");
		this.shellMethod = sm;
		this.method = method;
		this.target = target;

		// Check whether there is a keyword.(if not an internal command)
		if (!(target instanceof InternalCommand)) {
			Assert.isTrue(!contains(sm.keys()),
					String.format(
							"The shell method: '%s' definition exists in conflict with the keywords: '%s' and is recommended to be renamed.",
							method, asCmdsString()));
		}

		// Initialization
		initialize();
	}

	public ShellMethod getShellMethod() {
		return shellMethod;
	}

	public Object getTarget() {
		return target;
	}

	public Method getMethod() {
		return method;
	}

	public List<TargetParameter> getParameters() {
		return parameters;
	}

	public HelpOptions getOptions() {
		final HelpOptions options = new HelpOptions(getShellMethod());
		getParameters().forEach(parameter -> {
			parameter.getAttributes().keySet().forEach(option -> options.addOption(option));
		});
		return options;
	}

	/**
	 * Acutal method parameter name or index
	 * 
	 * @param argname
	 * @return
	 */
	public String getSureParamName(String argname) {
		for (TargetParameter parameter : getParameters()) {
			for (Entry<HelpOption, String> attr : parameter.getAttributes().entrySet()) {
				Option option = attr.getKey();
				if (equalsAny(argname, option.getOpt(), option.getLongOpt())) {
					// See:[MARK0][AbstractActuator.MARK3]
					if (isNotBlank(attr.getValue())) {
						return attr.getValue();
					}
				}
			}
		}

		return argname;
	}

	/**
	 * Initialization
	 * 
	 * @param method
	 */
	private void initialize() {
		// Parameter annotations
		Annotation[][] paramAnnos = getMethod().getParameterAnnotations();

		// Parameter types
		Class<?>[] paramTypes = getMethod().getParameterTypes();
		Assert.state(paramAnnos.length == paramTypes.length,
				String.format("Error, method:%s parameter types length:%s parameter annotations:%s", getMethod(),
						paramTypes.length, paramAnnos.length));

		for (int i = 0; i < paramTypes.length; i++) {
			Class<?> paramType = paramTypes[i];
			ShellOption shOpt = findShellOption(paramAnnos[i]);

			// Wrap target method parameter
			TargetParameter parameter = new TargetParameter(getMethod(), paramType, shOpt, i);

			// Base type parameter?
			// (String,long,double... or List,Set,Map,Properties...)
			if (simpleType(paramType)) { // MARK4
				validateShellOption(shOpt, getMethod(), i);

				// See:[com.wl4g.devops.shell.command.DefaultInternalCommand.MARK0]
				HelpOption option = new HelpOption(paramType, shOpt.opt(), shOpt.lopt(), shOpt.defaultValue(), shOpt.required(),
						shOpt.help());

				// [MARK0] Native type parameter field name is null
				// See:[AbstractActuator.MARK3]
				parameter.addAttribute(option, null);
			}
			// Java bean parameter?
			else {
				populateDeepFields(paramType, parameter);
			}

			// Check parameters(options) repeat register.
			for (TargetParameter p : parameters) {
				parameter.getAttributes().keySet().forEach(option -> p.validateOption(option));
			}

			parameters.add(parameter);
		}

	}

	/**
	 * Find shell option annotation configuration
	 * 
	 * @param paramAnnotations
	 * @return
	 */
	private ShellOption findShellOption(Annotation[] paramAnnotations) {
		for (Annotation an : paramAnnotations) {
			if (an.annotationType() == ShellOption.class) {
				return (ShellOption) an;
			}
		}
		return null;
	}

	/**
	 * Validate shell option.
	 * 
	 * @param opt
	 * @param m
	 * @param index
	 */
	private void validateShellOption(ShellOption opt, Method m, int index) {
		Assert.state(opt != null, String
				.format("Declared as a shell method: %s, the parameter index: %s must be annotated by @ShellOption", m, index));
		Assert.hasText(opt.opt(), String.format("Options of the shell method: '%s' cannot be empty", m));
		Assert.hasText(opt.lopt(), String.format("Options of the shell method: '%s' cannot be empty", m));
		Assert.isTrue(isAlpha(opt.opt().substring(0, 1)),
				String.format("Options: '%s' for shell methods: '%s', must start with a letter", opt.opt(), m));
		Assert.isTrue(isAlpha(opt.lopt().substring(0, 1)),
				String.format("Options: '%s' for shell methods: '%s', must start with a letter", opt.lopt(), m));
	}

	/**
	 * Target parameter definition
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月3日
	 * @since
	 */
	public static class TargetParameter implements Serializable {
		final private static long serialVersionUID = -8763372515086222131L;

		/**
		 * Native method
		 */
		final transient private Method method;

		/**
		 * Method parameter type
		 */
		final transient private Class<?> paramType;

		/**
		 * Method parameter index
		 */
		final transient private int index;

		/**
		 * Method parameter shell option annotation.</br>
		 * Annotation for basic type parameters.
		 */
		final private ShellOption shellOption;

		/**
		 * Method parameter attributes
		 */
		final private Map<HelpOption, String> attributes = new HashMap<>(4);

		public TargetParameter(Method method, Class<?> paramType, ShellOption shOpt, int index) {
			this(method, paramType, index, shOpt, null);
		}

		public TargetParameter(Method method, Class<?> paramType, int index, ShellOption shOpt,
				Map<HelpOption, String> attributes) {
			Assert.notNull(method, "Method type is null, please check configure");
			Assert.notNull(paramType, "Parameter type is null, please check configure");
			Assert.isTrue(index >= 0, "Parameter index greater or equal to 0, please check configure");
			this.method = method;
			this.paramType = paramType;
			this.index = index;

			// Assertion shell option.
			if (simpleType()) { // [MARK7]
				Assert.state(shOpt != null,
						String.format("Declared as a shell method: %s, the parameter index: %s must be annotated by @ShellOption",
								getMethod(), getIndex()));
			}
			this.shellOption = shOpt;

			if (attributes != null && !attributes.isEmpty()) {
				this.attributes.putAll(attributes);
			}
		}

		public Method getMethod() {
			return method;
		}

		public Class<?> getParamType() {
			return paramType;
		}

		public ShellOption getShellOption() {
			return shellOption;
		}

		public int getIndex() {
			return index;
		}

		public final Map<HelpOption, String> getAttributes() {
			return Collections.unmodifiableMap(attributes);
		}

		public final TargetParameter addAttribute(HelpOption option, String fieldName) {
			validateOption(option);

			Assert.state(attributes.putIfAbsent(option, fieldName) == null,
					String.format("Repeatedly defined shell parameter index: %s, paramType: %s, option: '%s', method: '%s'",
							getIndex(), getParamType(), option, getMethod()));
			return this;
		}

		private void validateOption(HelpOption option) {
			// Option(opt)
			List<String> opts = getAttributes().keySet().stream().map(op -> op.getOpt()).collect(Collectors.toList());
			Assert.state(!opts.contains(option.getOpt()),
					String.format("Repeatedly defined short option: '%s', parameter index: %s, paramType: %s, method: '%s'",
							option.getOpt(), getIndex(), getParamType(), getMethod()));

			// Option(longOpt)
			List<String> lOpts = getAttributes().keySet().stream().map(op -> op.getLongOpt()).collect(Collectors.toList());
			Assert.state(!lOpts.contains(option.getLongOpt()),
					String.format("Repeatedly defined long option: '%s', parameter index: %s, paramType: %s, method: '%s'",
							option.getLongOpt(), getIndex(), getParamType(), getMethod()));
		}

		public boolean simpleType() {
			return simpleType(getParamType());
		}

		public static boolean simpleType(Class<?> paramType) {
			return isBaseType(paramType) || isGeneralSetType(paramType);
		}

		/**
		 * Extract deep full propertys to targetParameter.
		 * 
		 * @param clazz
		 * @param attributes
		 */
		public static void populateDeepFields(Class<?> clazz, TargetParameter parameter) {
			Class<?> cls = clazz;
			do {
				extractHierarchyFields(cls, parameter);
			} while ((cls = cls.getSuperclass()) != null);
		}

		/**
		 * Extract hierarchy propertys to targetParameter
		 * 
		 * @param clazz
		 * @param attributes
		 */
		private static void extractHierarchyFields(Class<?> clazz, TargetParameter parameter) {
			Assert.notNull(clazz, "The paramClazz must be null");
			try {
				for (Field f : clazz.getDeclaredFields()) {
					Class<?> ftype = f.getType();
					String fname = f.getName();

					if (simpleType(ftype)) {
						ShellOption opt = f.getAnnotation(ShellOption.class);

						// Filter unsafe field.
						if (opt != null) {
							// [MARK1],See:[AbstractActuator.MARK4]
							if (isSafetyModifier(f.getModifiers())) {
								HelpOption option = new HelpOption(ftype, opt.opt(), opt.lopt(), opt.defaultValue(),
										opt.required(), opt.help());
								parameter.addAttribute(option, fname);
							} else {
								System.err.println(String.format(
										"WARNINGS: Although the @%s annotation option has been used, it has not been registered in the parameter list because field: '%s' has modifiers final/static/transient/volatile/native/synchronized, etc.",
										ShellOption.class.getSimpleName(), f));
							}
						}
					}
					// Eliminate built-in injection parameters to prevent dead
					// cycle.
					else if (InternalInjectable.class.isAssignableFrom(ftype)) {
						extractHierarchyFields(ftype, parameter);
					}
				}
			} catch (Throwable e) {
				throw new IllegalStateException(e);
			}
		}

	}

}
