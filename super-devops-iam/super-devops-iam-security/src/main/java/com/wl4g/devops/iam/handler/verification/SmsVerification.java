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
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.util.WebUtils;
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
	final public static String KEY_VERIFYCODE_SESSION = SmsVerification.class.getSimpleName() + ".VERIFYCODE";

	/**
	 * SMS verification code parameter name,
	 */
	final public static String PARAM_VERIFYCODE = "code";

	/**
	 * Target phone number parameter name sent by SMS verification code.
	 */
	final public static String PARAM_MOBILENUM = "mobileNum";

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
		sender.doSend(determineParameters(request, getVerifyCode(true).getText()));
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

	@Override
	protected String generateCode() {
		return RandomStringUtils.randomNumeric(6);
	}

	/**
	 * Determine SMS send parameters
	 * 
	 * @param request
	 * @param verifyCode
	 * @return
	 */
	protected Map<String, Object> determineParameters(HttpServletRequest request, String verifyCode) {
		return new HashMap<String, Object>() {
			private static final long serialVersionUID = 8964694616018054906L;
			{
				String mobileNum = WebUtils.getCleanParam(request, config.getParam().getPrincipalName());
				put(PARAM_VERIFYCODE, verifyCode);
				put(PARAM_MOBILENUM, MobileNumber.parse(mobileNum));
			}
		};
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
		Long applyCumulatedCount = applyCumulator.accumulate(factors, 1, config.getMatcher().getFailFastSmsMaxDelay());
		if (applyCumulatedCount >= failFastSmsMaxAttempts) {
			log.warn("Apply for SMS verification code too often, actual: {}, maximum: {}, factors: {}", applyCumulatedCount,
					failFastSmsMaxAttempts, factors);
			throw new AccessRejectedException(bundle.getMessage("AbstractAttemptsMatcher.ipAccessReject"));
		}
	}

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

	/**
	 * Mobile number parser.</br>
	 * See:<a href=
	 * "https://www.51-n.com/t-4274-1-1.html">https://www.51-n.com/t-4274-1-1.html</a>
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年4月22日
	 * @since
	 */
	public static class MobileNumber implements Serializable {

		private static final long serialVersionUID = 6285416806548742944L;

		final private int countryCode;

		final private long number;

		private MobileNumber(int countryCode, long number) {
			super();
			this.countryCode = countryCode;
			this.number = number;
		}

		public int getCountryCode() {
			return countryCode;
		}

		public long getNumber() {
			return number;
		}

		/**
		 * Check and parse mobile number.</br>
		 * 
		 * <pre>
		 * parse(null)   = false
		 * parse("null")   = false
		 * parse("")     = false
		 * parse(" ")   = false
		 * parse("123")  = false
		 * parse("+08618112349876")  = true
		 * parse("+8618112349876") = false
		 * parse("+086181123498 6") = false
		 * parse("08618112349876") = false
		 * parse("+018112349876") = false
		 * </pre>
		 * 
		 * @param number
		 */
		public static MobileNumber parse(String mobileNumString) {
			Assert.isTrue((StringUtils.isNotBlank(mobileNumString) && mobileNumString.length() >= 15),
					"Mobile number must be 15 digits long");
			Assert.isTrue(StringUtils.startsWith(mobileNumString, "+"),
					String.format("Mobile number '%s' must start with '+', e.g. +08618112349876", mobileNumString));
			Assert.isTrue(StringUtils.isNumeric(mobileNumString.substring(1)),
					String.format("Mobile number '%s' suffix exist non-numeric characters", mobileNumString));

			return new MobileNumber(Integer.parseInt(mobileNumString.substring(1, 4)),
					Long.parseLong(mobileNumString.substring(4)));
		}

		@Override
		public String toString() {
			return "MobileNumber [countryCode=" + countryCode + ", number=" + number + "]";
		}

	}

}
