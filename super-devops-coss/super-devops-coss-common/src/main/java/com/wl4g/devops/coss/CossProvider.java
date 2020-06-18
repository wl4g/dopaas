package com.wl4g.devops.coss;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNull;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * COSS provider type definitions.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月5日
 * @since
 * @throws CossException
 * @throws ServerCossException
 */
public enum CossProvider {

	/**
	 * COSS provider for aliyun oss.
	 */
	AliyunOss("aliyunoss"),

	/**
	 * COSS provider for aws s3.
	 */
	AwsS3("awss3"),

	/**
	 * COSS provider for hdfs.
	 */
	Hdfs("hdfs"),

	/**
	 * COSS provider for glusterfs.
	 */
	GlusterFs("glusterfs"),

	/**
	 * COSS provider for native fs.
	 */
	NativeFs("nativefs");

	final private String value;

	private CossProvider(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	/**
	 * Safe converter string to {@link CossProvider}
	 * 
	 * @param cossProvider
	 * @return
	 */
	final public static CossProvider safeOf(String cossProvider) {
		if (isBlank(cossProvider))
			return null;

		for (CossProvider t : values())
			if (t.getValue().equalsIgnoreCase(cossProvider) || t.name().equalsIgnoreCase(cossProvider))
				return t;

		return null;
	}

	/**
	 * Converter string to {@link CossProvider}
	 * 
	 * @param cossProvider
	 * @return
	 */
	final public static CossProvider of(String cossProvider) {
		CossProvider type = safeOf(cossProvider);
		notNull(type, format("Unsupported COSS provider for %s", cossProvider));
		return type;
	}

}
