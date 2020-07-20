/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.components.tools.common.lang.period;

import static java.util.Locale.US;
import static java.lang.ThreadLocal.withInitial;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * {@link PeriodFormatterHolder}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月16日
 * @since
 */
public abstract class PeriodFormatterHolder {

	/**
	 * Set current thread locale.
	 * 
	 * @param l
	 */
	public static void setCurrentLocale(Locale l) {
		currentLocale.set(l);
	}

	/**
	 * Gets instance by default impl class.
	 * 
	 * @return
	 */
	public static PeriodFormatterHolder getDefault() {
		return getInstance(SamplePeriodFormatter.class);
	}

	/**
	 * Gets instance by impl class.
	 * 
	 * @param implClass
	 * @return
	 */
	public static PeriodFormatterHolder getInstance(Class<? extends PeriodFormatterHolder> implClass) {
		return registers.get(implClass);
	}

	/**
	 * Formatting to human Date time formatted as human friendly
	 * 
	 * @param nowTime
	 *            current timestamp
	 * @param targetTime
	 *            Target timestamp to format
	 * @return
	 */
	public abstract String formatHumanDate(long nowTime, long targetTime);

	/**
	 * Formatting to human Date time formatted as human friendly
	 * 
	 * @param targetTime
	 *            Target timestamp to format
	 * @return
	 */
	public abstract String formatHumanDate(long targetTime);

	/**
	 * Cleanup date empty string.
	 * 
	 * @param dateString
	 * @return
	 */
	protected static String cleanupDateEmptyString(String dateString) {
		int safeThreshold = 10;
		String cleared = dateString.toString().trim(), lastCleared = "";
		for (int i = 0; i < safeThreshold && !lastCleared.equals(cleared); i++, lastCleared = cleared) {
			cleared = cleared.replace("  ", " ");
		}
		return cleared;
	}

	/**
	 * Gets localized message by key.
	 * 
	 * @param localizedKey
	 * @return
	 */
	protected static String getLocalizedMessage(String localizedKey) {
		return getLocalizedMessage(US, localizedKey);
	}

	/**
	 * Gets localized message by key.
	 * 
	 * @param l
	 * @param localizedKey
	 * @return
	 */
	protected static String getLocalizedMessage(Locale l, String localizedKey) {
		try {
			return getResourceBundle(l).getString(localizedKey);
		} catch (MissingResourceException e) {
			return localizedKey;
		}
	}

	/**
	 * Gets resources bundle.
	 * 
	 * @param l
	 * @return
	 */
	protected static ResourceBundle getResourceBundle(Locale l) {
		try {
			return ResourceBundle.getBundle(defaultI18nResourcesBaseName, l);
		} catch (MissingResourceException e) {
			return ResourceBundle.getBundle(defaultI18nResourcesBaseName, US);
		}
	}

	/**
	 * Gets default i18n resources base-name.
	 * 
	 * @return
	 */
	final private static String getDefaultI18nResourcesBaseName0() {
		String className = PeriodFormatterHolder.class.getName();
		return className.substring(0, className.lastIndexOf(".")).replace(".", "/").concat("/messages");
	}

	/**
	 * Current locale.
	 */
	final private static ThreadLocal<Locale> currentLocale = withInitial(() -> US);

	/**
	 * Default i18n resources base-name.
	 */
	final private static String defaultI18nResourcesBaseName = getDefaultI18nResourcesBaseName0();

	/**
	 * PeriodFormatter register instances
	 */
	final private static Map<Class<? extends PeriodFormatterHolder>, PeriodFormatterHolder> registers = new HashMap<Class<? extends PeriodFormatterHolder>, PeriodFormatterHolder>() {
		private static final long serialVersionUID = 6381326188492266214L;
		{
			put(JodaPeriodFormatter.class, new JodaPeriodFormatter());
			put(SamplePeriodFormatter.class, new SamplePeriodFormatter());
		}
	};

}