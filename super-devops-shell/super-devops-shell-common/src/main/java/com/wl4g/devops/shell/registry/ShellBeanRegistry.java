package com.wl4g.devops.shell.registry;

import java.io.Serializable;
import java.lang.reflect.Method;
import static java.lang.reflect.Modifier.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.shell.utils.Assert;

/**
 * Shell bean registry
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public abstract class ShellBeanRegistry implements Serializable {
	final private static long serialVersionUID = -8763772555086222131L;

	/**
	 * Local registed shell targetMethodWrappers
	 */
	final private Map<String, TargetMethodWrapper> registry = new ConcurrentHashMap<>(16);

	public Map<String, TargetMethodWrapper> getTargetMethods() {
		return registry;
	}

	public TargetMethodWrapper getTargetMethod(String argname) {
		return registry.get(argname);
	}

	public boolean contains(String argname) {
		return getTargetMethods().containsKey(argname);
	}

	/**
	 * Registion shell component of bean
	 * 
	 * @param bean
	 */
	public ShellBeanRegistry register(Object bean) {
		Assert.notNull(bean, "bean is null, please check configure");

		for (Method m : bean.getClass().getDeclaredMethods()) {
			int mod = m.getModifiers();
			if (isStatic(mod) || isAbstract(mod) || isTransient(mod) || isAbstract(mod) || isNative(mod) || isInterface(mod)) {
				continue;
			}

			// Shell method?
			ShellMethod sm = m.getAnnotation(ShellMethod.class);
			if (sm != null) {
				Assert.notNull(sm.keys(), "Shell method key must not be null");
				for (String k : sm.keys()) {
					register0(k, new TargetMethodWrapper(sm, m, bean));
				}
			}
		}

		return this;
	}

	/**
	 * Internal registion shell bean stored
	 * 
	 * @param mainOpt
	 * @param tm
	 */
	private void register0(String mainOpt, TargetMethodWrapper tm) {
		Assert.state(registry.putIfAbsent(mainOpt, tm) == null, String.format("Repeatedly defined shell methods: %s", mainOpt));
	}

}
