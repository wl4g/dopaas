package com.wl4g.devops.iam.sns.handler;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.util.StringUtils;

import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.config.IamProperties;

/**
 * IAM Social delegate handler factory
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月4日
 * @since
 */
public class DelegateSnsHandler implements SnsHandler {

	/**
	 * SNS handler repository
	 */
	final private Map<Which, SnsHandler> repository = new ConcurrentHashMap<>();

	/**
	 * IAM server properties configuration
	 */
	final protected IamProperties config;

	public DelegateSnsHandler(IamProperties config, List<SnsHandler> handlers) {
		Assert.notNull(config, "'config' must not be null");
		Assert.notEmpty(handlers, "'handlers' must not be empty");
		this.config = config;
		for (SnsHandler handler : handlers) {
			if (this.repository.putIfAbsent(handler.whichType(), handler) != null) {
				throw new IllegalStateException(String.format("Already sns handler register", handler.whichType()));
			}
		}
	}

	@Override
	public String connect(Which which, String provider, String state, Map<String, String> connectParams) {
		state = StringUtils.isEmpty(state) ? UUID.randomUUID().toString().replaceAll("-", "") : state;
		return this.getTarget(which).connect(which, provider, state, connectParams);
	}

	@Override
	public String callback(Which which, String provider, String state, String code, HttpServletRequest request) {
		return this.getTarget(which).callback(which, provider, state, code, request);
	}

	@Override
	public Which whichType() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get target SNS handler
	 * 
	 * @param which
	 * @return
	 */
	private SnsHandler getTarget(Which which) {
		Assert.notNull(which, String.format("Illegal parameter %s[%s]", config.getParam().getWhich(), which));
		if (!this.repository.containsKey(which)) {
			throw new NoSuchBeanDefinitionException(String.format("No such sns handler of which[%s]", which));
		}
		return this.repository.get(which);
	}

}
