package com.wl4g.devops.shell.registry;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.Option;
import static org.apache.commons.lang3.StringUtils.*;

import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.shell.annotation.ShellOption;
import com.wl4g.devops.shell.cli.HelpOptions;
import com.wl4g.devops.shell.cli.InternalCommand;
import com.wl4g.devops.shell.utils.Assert;
import static com.wl4g.devops.shell.utils.Reflections.*;
import static com.wl4g.devops.shell.utils.Types.*;
import static com.wl4g.devops.shell.cli.InternalCommand.*;

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
			for (Entry<Option, String> attr : parameter.getAttributes().entrySet()) {
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
		Assert.isTrue(paramAnnos.length == paramTypes.length,
				String.format("Error, method:%s parameter types length:%s parameter annotations:%s", getMethod(),
						paramTypes.length, paramAnnos.length));

		for (int i = 0; i < paramTypes.length; i++) {
			Class<?> paramType = paramTypes[i];
			ShellOption opt = findShellOption(paramAnnos[i]);

			// Native type parameter must be annotated with @ShellOption?
			if (nativeType(paramType)) {
				Assert.state(opt != null, String.format(
						"Declared as a shell method: %s, the parameter index: %s must be annotated by @ShellOption", method, i));
			}

			// Wrap target method parameter
			TargetParameter parameter = new TargetParameter(getMethod(), paramType, opt, i);

			// Native field?
			if (nativeType(paramType)) { // String,long,double...?
				Assert.hasText(opt.opt(), String.format("Options of the shell method: '%s' cannot be empty", getMethod()));
				Assert.hasText(opt.lopt(), String.format("Options of the shell method: '%s' cannot be empty", getMethod()));
				Assert.isTrue(isAlpha(opt.opt().substring(0, 1)),
						String.format("Options: '%s' for shell methods: '%s', must start with a letter", opt.opt(), getMethod()));
				Assert.isTrue(isAlpha(opt.lopt().substring(0, 1)), String
						.format("Options: '%s' for shell methods: '%s', must start with a letter", opt.lopt(), getMethod()));

				boolean required = isBlank(opt.defaultValue());
				// See:[com.wl4g.devops.shell.command.DefaultInternalCommand.MARK0]
				Option option = new Option(opt.opt(), opt.lopt(), required, opt.help());
				// [MARK0] Native type parameter field name is null
				// See:[AbstractActuator.MARK3]
				parameter.getAttributes().put(option, null);
			}
			// Java bean?
			else {
				extFullParams(paramType, parameter.getAttributes());
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
		 * Method parameter shell option annotation
		 */
		final private ShellOption shellOption;

		/**
		 * Method parameter attributes
		 */
		final private Map<Option, String> attributes = new HashMap<>(4);

		public TargetParameter(Method method, Class<?> paramType, ShellOption shellOption, int index) {
			this(method, paramType, index, shellOption, null);
		}

		public TargetParameter(Method method, Class<?> paramType, int index, ShellOption shOpt, Map<Option, String> attributes) {
			Assert.notNull(method, "Method type is null, please check configure");
			Assert.notNull(paramType, "Parameter type is null, please check configure");
			Assert.isTrue(index >= 0, "Parameter index greater or equal to 0, please check configure");
			this.method = method;
			this.paramType = paramType;
			this.index = index;

			// Assertion shell option.
			if (isNativeType()) {
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

		public Map<Option, String> getAttributes() {
			return attributes;
		}

		public boolean isNativeType() {
			return nativeType(getParamType());
		}

	}

}
