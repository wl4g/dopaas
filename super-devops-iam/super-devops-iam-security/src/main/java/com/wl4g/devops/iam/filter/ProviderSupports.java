package com.wl4g.devops.iam.filter;

import java.util.HashSet;
import java.util.Set;

import com.wl4g.devops.common.exception.iam.NoSuchSocialProviderException;

/**
 * Save Supported Social Network Service Providers
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月7日
 * @since
 */
public enum ProviderSupports {

	/**
	 * WeChat public platform definitions.
	 */
	WECHATMP("wechatmp", "WeChatMp", "微信"),

	/**
	 * WeChat definitions.
	 */
	WECHAT("wechat", "WeChat", "微信"),

	/**
	 * QQ definitions.
	 */
	QQ("qq", "QQ", "企鹅"),

	/**
	 * Sina definitions.
	 */
	SINA("sina", "Sina", "新浪"),

	/**
	 * Google definitions.
	 */
	GOOGLE("google", "Google", "谷歌"),

	/**
	 * WeChat definitions.
	 */
	GITHUB("github", "Github", "Github"),

	/**
	 * Facebook definitions.
	 */
	FACEBOOK("facebook", "Facebook", "脸书网"),

	/**
	 * Dingtalk definitions.
	 */
	DINGTALK("dingtalk", "Dingtalk", "钉钉"),

	/**
	 * Twitter definitions.
	 */
	TWITTER("twitter", "Twitter", "Twitter");

	/**
	 * Supported SNS providers.
	 */
	final private static Set<String> supported = new HashSet<>();

	final private String name;
	final private String displayNameEn;
	final private String displayNameZh;

	private ProviderSupports(String name, String displayNameEn, String displayNameZh) {
		this.name = name;
		this.displayNameEn = displayNameEn;
		this.displayNameZh = displayNameZh;
	}

	final public String getName() {
		return name;
	}

	final public String getDisplayNameEn() {
		return displayNameEn;
	}

	final public String getDisplayNameZh() {
		return displayNameZh;
	}

	final public boolean isEqual(String provider) {
		return name().equalsIgnoreCase(String.valueOf(provider));
	}

	/**
	 * Check if you are a supported social network provider
	 * 
	 * @param providerId
	 * @throws NoSuchSocialProviderException
	 */
	final public static void checkSupport(String providerId) throws NoSuchSocialProviderException {
		if (!isSupport(providerId)) {
			throw new NoSuchSocialProviderException(String.format("Unsupported SNS service providers:[%s]", providerId));
		}
	}

	/**
	 * Check if you are a supported social network provider
	 * 
	 * @param provider
	 * @return
	 */
	final public static boolean isSupport(String provider) {
		return supported.contains(provider);
	}

	/**
	 * Add supported social networking service provider
	 * 
	 * @param providerId
	 */
	final static void addSupport(String providerId) {
		supported.add(providerId);
	}

}