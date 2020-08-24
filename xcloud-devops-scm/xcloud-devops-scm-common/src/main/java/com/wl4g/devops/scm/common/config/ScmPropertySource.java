package com.wl4g.devops.scm.common.config;

import java.io.Serializable;
import java.util.function.Function;

/**
 * SCM property source interface definition.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-15
 * @sine v1.0.0
 * @see
 */
public interface ScmPropertySource extends Serializable {

	/**
	 * Check whether the source type is configured and supported.
	 * 
	 * @param type
	 * @return
	 */
	default boolean support(String type) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Read and parse configuration property source to itself.
	 * 
	 * @param sourceContent
	 */
	default void read(String sourceContent) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Check if the encryption configuration source has been resolved.
	 * 
	 * @return If not resolved, false is returned
	 */
	default boolean isResolved() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Hierarchical resolve configuration property source cipher value, ignored
	 * if resolved.
	 * 
	 * @param cipherResolver
	 * @return Return decrypted property source
	 */
	default ScmPropertySource resolveCipher(Function<String, Object> cipherResolver) {
		throw new UnsupportedOperationException();
	}

}
