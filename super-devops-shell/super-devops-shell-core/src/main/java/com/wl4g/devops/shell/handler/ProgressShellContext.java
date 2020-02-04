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

import static com.wl4g.devops.tool.common.lang.Assert2.isTrue;
import static java.util.Objects.isNull;

import com.wl4g.devops.shell.signal.ProgressSignal;
import com.wl4g.devops.tool.common.math.Maths;

/**
 * {@link ProgressShellContext}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年2月3日 v1.0.0
 * @see
 */
public class ProgressShellContext extends ShellContext {
	final public static int DEFAULT_WHOLE = 100;

	/**
	 * Last progress signal.
	 */
	protected ProgressSignal lastProgress;

	ProgressShellContext(ShellContext context) {
		super(context);
	}

	/**
	 * Output current progress percentage to client console.
	 *
	 * @param title
	 * @param progressPercent
	 * @return
	 */
	public ProgressShellContext printf(String title, float progressPercent) {
		isTrue(progressPercent >= 0 && progressPercent <= 1, "Progress percentage must be between 0 and 1");
		int progressWithDefault = Maths.multiply(DEFAULT_WHOLE, progressPercent).intValue();
		return (ProgressShellContext) printf0(lastProgress = new ProgressSignal(title, DEFAULT_WHOLE, progressWithDefault));
	}

	/**
	 * Output the number of current progress to the client console.
	 *
	 * @param title
	 * @param whole
	 * @param progress
	 * @return
	 */
	public ProgressShellContext printf(String title, int whole, int progress) {
		return (ProgressShellContext) printf0(lastProgress = new ProgressSignal(title, whole, progress));
	}

	/**
	 * Complete command execution manually, for example, when receiving an
	 * interrupt event, call it to output the message for the last time. </br>
	 * </br>
	 * <b><font color=red>Note: Don't forget to execute it, or the client
	 * console will pause until it timeout.</font><b>
	 * 
	 * @param message
	 * @see {@link #completed()}
	 */
	public void completed(String message) {
		printf(message, getProgressed());
		super.completed();
	}

	/**
	 * Get current progressed percentage.
	 * 
	 * @return
	 */
	public float getProgressed() {
		if (isNull(lastProgress)) {
			return 0f;
		}
		return Maths.divide(lastProgress.getProgress(), lastProgress.getWhole()).floatValue();
	}

}
