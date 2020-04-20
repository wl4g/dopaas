package com.wl4g.devops.rcm;

import static com.wl4g.devops.tool.common.lang.Assert2.notNull;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * RCM provider type definitions.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月5日
 * @since
 * @throws RcmException
 * @throws ServerRcmException
 */
public enum RcmProvider {

	/**
	 * RCM provider for aliyun saf.
	 */
	AliyunSafEngine("aliyunSafEngine"),

	/**
	 * RCM provider for native tensorflow+groovy engine.
	 */
	NativeEngine("nativeEngine");

	final private String value;

	private RcmProvider(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	/**
	 * Safe converter string to {@link RcmProvider}
	 * 
	 * @param cossProvider
	 * @return
	 */
	final public static RcmProvider safeOf(String cossProvider) {
		if (isBlank(cossProvider))
			return null;

		for (RcmProvider t : values())
			if (t.getValue().equalsIgnoreCase(cossProvider) || t.name().equalsIgnoreCase(cossProvider))
				return t;

		return null;
	}

	/**
	 * Converter string to {@link RcmProvider}
	 * 
	 * @param cossProvider
	 * @return
	 */
	final public static RcmProvider of(String cossProvider) {
		RcmProvider type = safeOf(cossProvider);
		notNull(type, format("Unsupported RCM provider for %s", cossProvider));
		return type;
	}

}
