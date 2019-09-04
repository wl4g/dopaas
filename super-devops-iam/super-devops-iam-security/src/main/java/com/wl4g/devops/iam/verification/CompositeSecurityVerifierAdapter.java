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

import static java.util.stream.Collectors.toMap;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
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
	 * Verification registry.
	 */
	final protected Map<VerifyType, SecurityVerifier> registry = new OnceModifiableMap<>(new HashMap<>());

	/**
	 * Real delegate securityVerifier.
	 */
	final private ThreadLocal<SecurityVerifier> delegate = new InheritableThreadLocal<>();

	public CompositeSecurityVerifierAdapter(List<SecurityVerifier> verifiers) {
		Assert.state(!isEmpty(verifiers), "Verifications has at least one.");
		this.registry.putAll(verifiers.stream().collect(toMap(SecurityVerifier::verifyType, verifier -> verifier)));
	}

	@Override
	public VerifyType verifyType() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Making the adaptation actually execute securityVerifier.
	 * 
	 * @param type
	 * @return
	 */
	public SecurityVerifier forAdapt(@NotNull VerifyType type) {
		SecurityVerifier verifier = registry.get(type);
		Assert.notNull(verifier, String.format("Unsupport securityVerifier for '%s'", type));
		delegate.set(verifier);
		return verifier;
	}

	/**
	 * Making the adaptation actually execute securityVerifier.
	 * 
	 * @param request
	 * @return
	 */
	public SecurityVerifier forAdapt(@NotNull HttpServletRequest request) {
		return forAdapt(VerifyType.of(request));
	}

	@Override
	public Object apply(String owner, @NotNull List<String> factors, @NotNull HttpServletRequest request) throws IOException {
		return getAdapted().apply(owner, factors, request);
	}

	@Override
	public VerifyCodeWrapper getVerifyCode(boolean assertion) {
		return getAdapted().getVerifyCode(assertion);
	}

	@Override
	public boolean isEnabled(@NotNull List<String> factors) {
		return getAdapted().isEnabled(factors);
	}

	@Override
	public String verify(@NotBlank String params, @NotNull HttpServletRequest request, @NotNull List<String> factors)
			throws VerificationException {
		return getAdapted().verify(params, request, factors);
	}

	@Override
	public void validate(@NotNull List<String> factors, @NotNull String verifiedToken, boolean required)
			throws VerificationException {
		getAdapted().validate(factors, verifiedToken, required);
	}

	/**
	 * Get adapted securityVerifier.
	 * 
	 * @param type
	 * @return
	 */
	private SecurityVerifier getAdapted() {
		SecurityVerifier verifier = delegate.get();
		Assert.state(verifier != null,
				"Not adapted to specify actual securityVerifier, You must use adapted() to adapt before you can.");
		return verifier;
	}

}
