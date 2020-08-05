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
package com.wl4g.devops.scm.publish;

import java.io.Serializable;

import org.springframework.web.context.request.async.DeferredResult;

import com.wl4g.components.core.bean.scm.model.GetRelease;

/**
 * SCM configuration soruce server deferred result watch.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月27日
 * @since
 */
public class WatchDeferredResult<T> extends DeferredResult<T> implements Serializable {
	private static final long serialVersionUID = -4499490832010100671L;

	private GetRelease watch;

	public WatchDeferredResult() {
	}

	/**
	 * Create a DeferredResult with a timeout value.
	 * <p>
	 * By default not set in which case the default configured in the MVC Java
	 * Config or the MVC namespace is used, or if that's not set, then the
	 * timeout depends on the default of the underlying server.
	 * 
	 * @param timeout
	 *            timeout value in milliseconds
	 */
	public WatchDeferredResult(Long timeout) {
		super(timeout);
	}

	/**
	 * Create a DeferredResult with a timeout value and a default result to use
	 * in case of timeout.
	 * 
	 * @param timeout
	 *            timeout value in milliseconds (ignored if {@code null})
	 * @param timeoutResult
	 *            the result to use
	 */
	public WatchDeferredResult(Long timeout, Object timeoutResult) {
		super(timeout, timeoutResult);
	}

	public WatchDeferredResult(Long timeout, GetRelease watch) {
		super(timeout);
		this.watch = watch;
	}

	public GetRelease getWatch() {
		return watch;
	}

	public WatchDeferredResult<T> setWatch(GetRelease get) {
		this.watch = get;
		return this;
	}

	@Override
	public String toString() {
		return "WatchDeferredResult [watch=" + watch + "]";
	}

}