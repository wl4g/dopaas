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
package com.wl4g.devops.components.tools.common.lang;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;

public class AssertTests {

	public static void main(String[] args) {
		// isTrue(false, IllegalArgumentException.class, "Failed to for aa=%s",
		// "11");
		// notNull(null, IllegalArgumentException.class, "Must be not null");
		// hasText(null, IllegalArgumentException.class, "Must be not empty");
		// isInstanceOf(String.class, new Object(), "Must be not empty");
		isAssignable(int.class, String.class, "Incompatible types");

	}

}