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
package com.wl4g.devops.common.utils.reflect;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isNative;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isSynchronized;
import static java.lang.reflect.Modifier.isTransient;
import static java.lang.reflect.Modifier.isVolatile;

import org.springframework.util.ReflectionUtils;

/**
 * Enhanced utility class for working with the reflection API and handling
 * reflection exceptions.
 *
 * <p>
 * Only intended for internal use.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Rod Johnson
 * @author Costin Leau
 * @author Sam Brannen
 * @author Chris Beams
 * @since 1.2.2 {@link org.springframework.util.ReflectionUtils}
 */
public abstract class ReflectionUtils2 extends ReflectionUtils {

	// --- Extended reflection's. ---

	/**
	 * Assert whether the two types are compatible
	 * 
	 * @return
	 */
	public static boolean isCompatibleType(Class<?> clazz1, Class<?> clazz2) {
		assert (clazz1 != null && clazz2 != null);
		return clazz1.isAssignableFrom(clazz2) || clazz2.isAssignableFrom(clazz1);
	}

	/**
	 * Check for accessible general security modifiers.
	 * 
	 * @param modifer
	 * @return
	 */
	public static boolean isGenericAccessibleModifier(int modifer) {
		return !(isFinal(modifer) || isStatic(modifer) || isTransient(modifer) || isNative(modifer) || isVolatile(modifer)
				|| isSynchronized(modifer));
	}

}