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
package com.wl4g.devops.shell.util;

import static java.util.Objects.isNull;

/**
 * {@link ShellContextUtils}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年2月2日 v1.0.0
 * @see
 */
public abstract class ShellContextUtils {

	/** Shell context cache. */
	final private static ThreadLocal<Object> contextCache = new InheritableThreadLocal<>();

	/**
	 * Bind shell context.
	 *
	 * @param context
	 * @return
	 */
	public final static <T> T bind(T context) {
		if (context != null) {
			contextCache.set(context);
		}
		return context;
	}

	/**
	 * Got current bind {@link ShellContext}. </br>
	 * 
	 * @see {@link EmbeddedServerShellHandler#run()#MARK1}
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final static <T> T getContext() {
		Object context = contextCache.get();
		if (isNull(context)) {
			throw new IllegalStateException("The context object was not retrieved. first use bind()");
		}
		return (T) context;
	}

}
