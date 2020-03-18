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
package com.wl4g.devops.iam.common.utils.cumulate;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Cumulative counter for security restrictions.
 *
 * @author wangl.sir
 * @version v1.0 2019年4月19日
 * @since
 */
public interface Cumulator {

	/**
	 * Number of cumulative processing (e.g. to limit the number of login
	 * failures or the number of short messages sent by same source IP requests)
	 *
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 * @param incrBy
	 *            Step increment value
	 * @return returns the maximum number of times under all constraint
	 *         factors.(e.g: max attempts failed count)
	 */
	default long accumulate(@NotNull List<String> factors, long incrBy) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the cumulative number of failures for the specified condition
	 *
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 * @return returns the maximum number of times under all constraint
	 *         factors.(e.g: max attempts failed count)
	 */
	default long getCumulative(@NotBlank String factors) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the cumulative number of failures for the specified condition
	 *
	 * @param factors
	 *            Cumulative scene factors or objects. (e.g. When used for
	 *            safety authenticating, factors can represent: remote user
	 *            IP/login account name)
	 * @return returns the maximum number of times under all constraint
	 *         factors.(e.g: max attempts failed count)
	 */
	default long getCumulatives(@NotNull List<String> factors) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Cancel verification code
	 *
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 */
	default void destroy(@NotNull List<String> factors) {
		throw new UnsupportedOperationException();
	}

}