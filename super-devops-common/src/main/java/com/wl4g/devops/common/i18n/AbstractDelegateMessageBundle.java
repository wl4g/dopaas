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
package com.wl4g.devops.common.i18n;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.Assert;

import java.util.Locale;

/**
 * Abstract resource boundle message source bundle.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年3月24日
 * @since
 */
public abstract class AbstractDelegateMessageBundle extends ResourceBundleMessageSource {

	/**
	 * Message source accessor delegate.
	 */
	private MessageSourceAccessor accessor;

	public AbstractDelegateMessageBundle() {
		this(AbstractDelegateMessageBundle.class);
	}

	public AbstractDelegateMessageBundle(Class<?> withClassPath) {
		this(getBasename(withClassPath));
	}

	public AbstractDelegateMessageBundle(String... basenames) {
		Assert.isTrue((basenames != null && basenames.length > 0), "'basenames' cannot not be empty");
		super.setBasenames(basenames);
	}

	/**
	 * Get locale message
	 * 
	 * @param code
	 * @return
	 */
	public String getMessage(String code, Object... args) {
		return getSource().getMessage(code, args, getSessionLocale());
	}

	/**
	 * Get actual message source
	 * 
	 * @return
	 */
	public MessageSourceAccessor getSource() {
		if (accessor != null) {
			return accessor;
		}
		return (accessor = new MessageSourceAccessor(this));
	}

	/**
	 * Default i18n message base class-path prefix.
	 * 
	 * @return
	 */
	protected static String getBasename(Class<?> clazz) {
		Assert.notNull(clazz, "Basename class cannot not be null");
		String path = clazz.getName();
		return path.substring(0, path.lastIndexOf(".")) + ".messages";
	}

	/**
	 * Get current session locale.</br>
	 * 
	 * @return
	 */
	protected abstract Locale getSessionLocale();

}