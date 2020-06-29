package com.wl4g.devops.coss.common.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Manager class to get localized resources.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年6月29日 v1.0.0
 * @see
 */
public class LocalizedHelper {

	private ResourceBundle bundle;

	LocalizedHelper(String baseName, Locale locale) {
		this.bundle = ResourceBundle.getBundle(baseName, locale);
	}

	public static LocalizedHelper getInstance(String baseName) {
		return new LocalizedHelper(baseName, Locale.getDefault());
	}

	public static LocalizedHelper getInstance(String baseName, Locale locale) {
		return new LocalizedHelper(baseName, locale);
	}

	public String getString(String key) {
		return bundle.getString(key);
	}

	public String getFormattedString(String key, Object... args) {
		return MessageFormat.format(getString(key), args);
	}
}
