package com.wl4g.devops.shell.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wl4g.devops.shell.registry.TargetMethodWrapper;

/**
 * Commands message
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public class CommandMessage extends Message {
	private static final long serialVersionUID = -8574315246731909685L;

	/**
	 * Shell component target methods
	 */
	final private Map<String, TargetMethodWrapper> registed = new ConcurrentHashMap<>(16);

	public CommandMessage() {
	}

	public CommandMessage(Map<String, TargetMethodWrapper> wrapper) {
		if (wrapper != null) {
			this.registed.putAll(wrapper);
		}
	}

	public Map<String, TargetMethodWrapper> getRegisted() {
		return registed;
	}

	@Override
	public String toString() {
		return "CommandMessage [wrapper=" + registed + "]";
	}

}
