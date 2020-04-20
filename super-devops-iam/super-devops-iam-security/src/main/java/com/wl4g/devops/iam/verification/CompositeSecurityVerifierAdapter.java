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
package com.wl4g.devops.iam.verification;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.iam.verification.SecurityVerifier.VerifyKind;

/**
 * Composite verification adapter.
 *
 * @author Wangl.sir
 * @version v1.0 2019年8月29日
 * @since
 */
public class CompositeSecurityVerifierAdapter extends GenericOperatorAdapter<VerifyKind, SecurityVerifier> {

	public CompositeSecurityVerifierAdapter(List<SecurityVerifier> verifiers) {
		super(verifiers);
	}

	/**
	 * Making the adaptation actually execute securityVerifier.
	 *
	 * @param request
	 * @return
	 */
	public SecurityVerifier forOperator(@NotNull HttpServletRequest request) {
		return forOperator(VerifyKind.of(request));
	}

}
