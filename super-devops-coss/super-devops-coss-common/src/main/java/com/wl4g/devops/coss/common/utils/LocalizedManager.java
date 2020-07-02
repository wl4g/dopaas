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
public class LocalizedManager {

	private ResourceBundle bundle;

	LocalizedManager(String baseName, Locale locale) {
		this.bundle = ResourceBundle.getBundle(baseName, locale);
	}

	public static LocalizedManager getInstance(String baseName) {
		return new LocalizedManager(baseName, Locale.getDefault());
	}

	public static LocalizedManager getInstance(String baseName, Locale locale) {
		return new LocalizedManager(baseName, locale);
	}

	public String getString(String key) {
		return bundle.getString(key);
	}

	public String getFormattedString(String key, Object... args) {
		return MessageFormat.format(getString(key), args);
	}

}
