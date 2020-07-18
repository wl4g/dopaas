package com.wl4g.devops.erm.dns.stardand;

/**
 * DNS zone resolving stardand definitions.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @date 2020-07-02 15:19:00
 * @see
 */
public enum ResolvingType {

	A, AAAA, CNAME, TXT, NS, MX, SRV, SOA;

	/**
	 * Converter string to {@link ResolvingType}
	 *
	 * @param resolveType
	 * @return
	 */
	public static ResolvingType of(String resolveType) {
		ResolvingType wh = safeOf(resolveType);
		if (wh == null) {
			throw new IllegalArgumentException(String.format("Illegal resolveType '%s'", resolveType));
		}
		return wh;
	}

	/**
	 * Safe converter string to {@link Action}
	 *
	 * @param resolveType
	 * @return
	 */
	public static ResolvingType safeOf(String resolveType) {
		for (ResolvingType t : values()) {
			if (String.valueOf(resolveType).equalsIgnoreCase(t.name())) {
				return t;
			}
		}
		return null;
	}

}
