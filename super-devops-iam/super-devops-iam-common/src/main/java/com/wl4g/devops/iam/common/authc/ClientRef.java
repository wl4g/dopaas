package com.wl4g.devops.iam.common.authc;

/**
 * IAM authentication client reference type definition.
 * 
 * @author wangl.sir
 * @version v1.0 2019年3月8日
 * @since
 */
public enum ClientRef {

	/**
	 * Client android type.
	 */
	Android("android"),

	/**
	 * Client iOS type.
	 */
	iOS("ios"),

	/**
	 * Client Mac type.
	 */
	Mac("mac"),

	/**
	 * Client WeChat official platform type.
	 */
	WeChatMp("wechatmp"),

	/**
	 * Client windows type.
	 */
	WINDOWS("windows");

	final private String value;

	private ClientRef(String value) {
		this.value = value;
	}

	final public String getValue() {
		return value;
	}

	final public static ClientRef of(String clientRef) {
		ClientRef ref = safeOf(clientRef);
		if (ref == null) {
			throw new IllegalArgumentException(String.format("Illegal clientRef '%s'", clientRef));
		}
		return ref;
	}

	final public static ClientRef safeOf(String clientRef) {
		for (ClientRef ref : values()) {
			if (ref.getValue().equalsIgnoreCase(clientRef) || ref.name().equalsIgnoreCase(clientRef)) {
				return ref;
			}
		}
		return null;
	}

}
