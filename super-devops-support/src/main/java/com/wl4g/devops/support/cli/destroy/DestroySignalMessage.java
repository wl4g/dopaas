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
package com.wl4g.devops.support.cli.destroy;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.Assert.notNull;

/**
 * Command-line process signal model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月21日
 * @since
 */
public class DestroySignalMessage extends DestroySignal {
	private static final long serialVersionUID = -7048011146751774527L;

	/** Destroy result state. */
	private DestroyState state;

	/** Destroy throws message. */
	private String errmsg = EMPTY;

	public DestroySignalMessage(){

	}

	public DestroySignalMessage(DestroySignal signal) {
		this(signal, DestroyState.DESTROYED, "destroyed successful!");
	}

	public DestroySignalMessage(DestroySignal signal, String errmsg) {
		this(signal, DestroyState.DESTROY_FAIL, errmsg);
	}

	public DestroySignalMessage(DestroySignal signal, DestroyState state, String errmsg) {
		notNull(signal, "Destroy signal can't null");
		// notNull(throwable, "Destroy throwable can't null");
		setProcessId(signal.getProcessId());
		setTimeoutMs(signal.getTimeoutMs());
		setState(state);
		this.errmsg = errmsg;
	}

	public DestroyState getState() {
		return state;
	}

	public DestroySignalMessage setState(DestroyState state) {
		notNull(state, "Destroy result state can't null");
		this.state = state;
		return this;
	}

	public String getMessage() {
		return errmsg;
	}

	public DestroySignalMessage setMessage(String errmsg) {
		notNull(errmsg, "Destroy errmsg can't null");
		this.errmsg = errmsg;
		return this;
	}

	/**
	 * {@link DestroyState}
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年12月25日
	 * @since
	 */
	public static enum DestroyState {

		DESTROYED,

		DESTROY_FAIL,

	}

}