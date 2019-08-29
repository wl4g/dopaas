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
package com.wl4g.devops.iam.handler.verification;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import com.wl4g.devops.common.exception.iam.VerificationException;
import com.wl4g.devops.common.utils.lang.OnceModifiableMap;

/**
 * Composite verification adapter.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月29日
 * @since
 */
public class CompositeSecurityVerifierAdapter implements SecurityVerifier {

	/**
	 * Verification definitions.
	 */
	final protected Map<VerifyType, SecurityVerifier> verifications = new OnceModifiableMap<>(new HashMap<>());

	public CompositeSecurityVerifierAdapter(Map<VerifyType, SecurityVerifier> verifications) {
		Assert.state(!isEmpty(verifications), "Verifications has at least one.");
		this.verifications.putAll(verifications);
	}

	@Override
	public VerifyType verifyType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void validate(@NotNull List<String> factors, String verifyCodeReq, boolean required) throws VerificationException {

	}

	@Override
	public void apply(String owner, @NotNull List<String> factors, @NotNull HttpServletRequest request,
			@NotNull HttpServletResponse response) throws IOException {

	}

	@Override
	public boolean isEnabled(@NotNull List<String> factors) {
		return SecurityVerifier.super.isEnabled(factors);
	}

}
