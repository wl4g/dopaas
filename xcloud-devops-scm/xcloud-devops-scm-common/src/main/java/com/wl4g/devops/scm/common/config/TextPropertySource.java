package com.wl4g.devops.scm.common.config;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.isInstanceOf;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;

import java.util.function.Function;

/**
 * Origin plaintext property source.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
public class TextPropertySource extends AbstractPropertySource {
	private static final long serialVersionUID = -5037062685017411482L;

	@SuppressWarnings("unchecked")
	@Override
	public ScmPropertySource resolveCipher(Function<String, Object> resolveFunction) {
		Object result = resolveFunction.apply(content);
		isInstanceOf(String.class, result, "Cannot accept resolved configuration value class type");
		this.resolvedSource = (String) result;
		return this;
	}

}
