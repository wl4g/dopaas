package com.wl4g.devops.tool.common.crypto.cipher;

import static com.wl4g.devops.tool.common.lang.Assert2.state;
import static java.lang.String.format;
import static java.util.Objects.isNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Astmmetric algorithm interface SPI.
 * 
 * @author wanglsir@gmail.com, 983708408@qq.com
 * @version 2019年12月28日 v1.0.0
 * @see
 */
public final class AsymeetricRegistrarFactory {

	/**
	 * List of instances for caching all encryption algorithms.<br/>
	 */
	final private static Map<String, AsymmetricEncryptor<?>> INSTANCES = new ConcurrentHashMap<>();

	/**
	 * Register cryptic instance
	 *
	 * @param encryptor
	 */
	final public static void register(AsymmetricEncryptor<?> encryptor) {
		state(isNull(INSTANCES.putIfAbsent(encryptor.getAlgorithmPrimary(), encryptor)),
				format("Already registed algorithm [%s]", encryptor.getAlgorithmPrimary()));
	}

	/**
	 * Get asymmetric cryptic encryptor instance.
	 * 
	 * @param <T>
	 * @param algorithm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final public static <T extends AsymmetricEncryptor<?>> T getEncryptor(String algorithm) {
		state(INSTANCES.containsKey(algorithm), format("No such cryptic algorithm: %s", algorithm));
		return (T) INSTANCES.get(algorithm);
	}

}
