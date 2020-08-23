package com.wl4g.devops.scm.common.config;

import java.io.Serializable;
import java.util.function.Function;

import com.wl4g.devops.scm.common.exception.UnresolvedPropertySourceException;

/**
 * SCM property source interface definition.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
public interface ScmPropertySource<T extends ScmPropertySource<?>> extends Serializable {

	/**
	 * Hierarchical recursion resolving cipher configuration property source
	 * value, Ignored if resolved.
	 * 
	 * @param resolveFunction
	 * @return
	 */
	default T resolveCipher(Function<String, Object> resolveFunction) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets resolved cipher property source, If there is no resolved, throw
	 * {@link UnresolvedPropertySourceException}
	 * 
	 * @return
	 */
	default T getResolvedSource() throws UnresolvedPropertySourceException {
		throw new UnsupportedOperationException();
	}

}
