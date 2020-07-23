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
package com.wl4g.devops.components.shell.signal;

import static com.wl4g.devops.components.tools.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.components.tools.common.lang.Assert2.isTrue;
import static java.lang.String.format;

/**
 * Progress bar message
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2020年1月4日
 * @since
 */
public class ProgressSignal extends Signal {
	private static final long serialVersionUID = -8574315246731906685L;

	/**
	 * Current progress title.
	 */
	final private String title;

	/**
	 * Total number.
	 */
	final private int whole;

	/**
	 * Current progress number.
	 */
	final private int progress;

	public ProgressSignal(String title, int whole, int progress) {
		hasTextOf(title, "title");
		isTrue(progress >= 0 && whole >= 0, format("Illegal arguments, progress: %s, whole: %s", progress, whole));
		isTrue(progress <= whole, format("Progress number out of bounds, current progress: %s, whole: %s", progress, whole));
		this.title = title;
		this.whole = whole;
		this.progress = progress;
	}

	public String getTitle() {
		return title;
	}

	public int getWhole() {
		return whole;
	}

	public int getProgress() {
		return progress;
	}

	@Override
	public String toString() {
		return super.toString().concat("[" + progress + "/" + whole + "]");
	}

}