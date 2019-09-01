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
import java.io.Serializable;
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
	public SecurityVerifier forAdapts(@NotNull VerifyType type) {
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
	public SecurityVerifier forAdapts(@NotNull HttpServletRequest request) {
		return forAdapts(VerifyType.of(request));
	}

	@Override
	public void apply(String owner, @NotNull List<String> factors, @NotNull HttpServletRequest request) {
		getAdaptedVerifier().apply(owner, factors, request);
	}

	@Override
	public void render(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
		getAdaptedVerifier().render(request, response);
	}

	@Override
	public VerifyCodeWrapper getVerifyCode(boolean assertion) {
		return getAdaptedVerifier().getVerifyCode(assertion);
	}

	@Override
	public boolean isEnabled(@NotNull List<String> factors) {
		return getAdaptedVerifier().isEnabled(factors);
	}

	@Override
	public String analyze(@NotNull HttpServletRequest request, @NotNull List<String> factors, @NotNull Object reqCode)
			throws VerificationException {
		return getAdaptedVerifier().analyze(request, factors, (Serializable) reqCode);
	}

	@Override
	public void validate(@NotNull List<String> factors, @NotNull String verifiedToken, boolean required)
			throws VerificationException {
		getAdaptedVerifier().validate(factors, verifiedToken, required);
	}

	/**
	 * Get adapted securityVerifier.
	 * 
	 * @param type
	 * @return
	 */
	private SecurityVerifier getAdaptedVerifier() {
		SecurityVerifier verifier = delegate.get();
		Assert.state(verifier != null,
				"Not adapted to specify actual securityVerifier, You must use forAdapts() to adapt before you can.");
		return verifier;
	}

}
