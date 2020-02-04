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
package com.wl4g.devops.shell.handler;

import com.wl4g.devops.shell.signal.ProgressSignal;

public class ProgressShellContext extends ShellContext {
	final public static int DEFAULT_WHOLE = 100;

	ProgressShellContext(ShellContext context) {
		super(context);
	}

	/**
	 * Print progress message to client console.
	 *
	 * @param title
	 * @param progressPercent
	 * @return
	 */
	public ProgressShellContext printf(String title, float progressPercent) {
		return (ProgressShellContext) printf0(new ProgressSignal(title, DEFAULT_WHOLE, (int) (DEFAULT_WHOLE * progressPercent)));
	}

	/**
	 * Print progress message to client console.
	 *
	 * @param title
	 * @param whole
	 * @param progress
	 * @return
	 */
	public ProgressShellContext printf(String title, int whole, int progress) {
		return (ProgressShellContext) printf0(new ProgressSignal(title, whole, progress));
	}

}
