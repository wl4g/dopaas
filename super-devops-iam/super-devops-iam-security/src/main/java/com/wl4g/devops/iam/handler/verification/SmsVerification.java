/*
 * Copyright 2015 the original author or authors.
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

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_FAILFAST_SMS_COUNTER;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.wl4g.devops.common.exception.iam.AccessRejectedException;
import com.wl4g.devops.iam.handler.verification.Cumulators.Cumulator;

/**
 * SMS verification code handler
 * 
 * @author wangl.sir
 * @version v1.0 2019年4月16日
 * @since
 */
public class SmsVerification extends AbstractVerification implements InitializingBean {

	/**
	 * Key name used to store authentication code to session
	 */
	final protected static String KEY_VERIFYCODE_SESSION = SmsVerification.class.getSimpleName() + ".VERIFYCODE";

	@Autowired
	private SmsHandleSender sender;

	/**
	 * Attempts SMS accumulator
	 */
	private Cumulator applyCumulator;

	@Override
	public void apply(@NotNull List<String> factors, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response)
			throws IOException {

		// Check limit attempts
		checkApplyAttempts(request, response, factors);

		// Create verify-code.
		reset(true);

		// Ready send to SMS gateway.
		sender.doSend(getParameters(request, getVerifyCode(true).getText()));
	}

	@Override
	public boolean isEnabled(@NotNull List<String> factors) {
		Assert.isTrue(!CollectionUtils.isEmpty(factors), "factors must not be empty");
		return getVerifyCode(false) != null;
	}

	@Override
	public VerifyCode getVerifyCode(boolean assertion) {
		return super.getVerifyCode(assertion);
	}

	@Override
	public long getExpireMs() {
		return config.getMatcher().getSmsExpireMs();
	}

	/**
	 * Get SMS send parameters
	 * 
	 * @param request
	 * @param verifyCode
	 * @return
	 */
	protected Map<String, Object> getParameters(HttpServletRequest request, String verifyCode) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("code", verifyCode);
		return parameters;
	}

	@Override
	protected String storageSessionKey() {
		return KEY_VERIFYCODE_SESSION;
	}

	@Override
	protected void checkApplyAttempts(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull List<String> factors) {
		long failFastSmsMaxAttempts = config.getMatcher().getFailFastSmsMaxAttempts();

		// Accumulated number of apply
		Long applyCumulatedCount = applyCumulator.accumulate(factors, 1, config.getMatcher().getFailFastSmsDelay());
		if (applyCumulatedCount >= failFastSmsMaxAttempts) {
			log.warn("Apply for SMS verification code too often, actual: {}, maximum: {}, factors: {}", applyCumulatedCount,
					failFastSmsMaxAttempts, factors);
			throw new AccessRejectedException(bundle.getMessage("AbstractAttemptsMatcher.ipAccessReject"));
		}
	}

	/**
	 * Initializing
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.applyCumulator = Cumulators.newCumulator(cacheManager, CACHE_FAILFAST_SMS_COUNTER);
		Assert.notNull(applyCumulator, "applyCumulator is null, please check configure");
	}

	/**
	 * SMS verification template handle sender.
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年4月17日
	 * @since
	 */
	public static interface SmsHandleSender {

		/**
		 * Do send to SMS provider gateway
		 * 
		 * @param parameters
		 */
		void doSend(Map<String, Object> parameters);

	}

	/**
	 * Default print SMS verification template handle sender.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年4月20日
	 * @since
	 */
	public static class PrintSmsHandleSender implements SmsHandleSender {

		final protected Logger log = LoggerFactory.getLogger(getClass());

		@Override
		public void doSend(Map<String, Object> parameters) {
			log.info(">>>> Start Sent SMS verification >>>>", parameters);
			log.info("SMS verification for : {}", parameters);
			log.info("<<<< End Sent SMS verification <<<<");
		}

	}

}
