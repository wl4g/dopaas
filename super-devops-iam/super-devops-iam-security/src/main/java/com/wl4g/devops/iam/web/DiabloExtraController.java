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
package com.wl4g.devops.iam.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.util.Assert;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wl4g.devops.common.exception.iam.AccessPermissionDeniedException;
import com.wl4g.devops.common.utils.Exceptions;
import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.annotation.ExtraController;
import com.wl4g.devops.iam.authc.credential.secure.IamCredentialsSecurer;
import com.wl4g.devops.iam.common.utils.SessionBindings;
import com.wl4g.devops.iam.handler.CaptchaHandler;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.getFailConditions;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_EXT_CHECK;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_EXT_CAPTCHA_APPLY;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_EXT_LOCALE_APPLY;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_EXT_ERRREAD;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_USE_LOCALE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_ERR_SESSION_SAVED;

import java.util.List;
import java.util.Locale;

/**
 * IAM diablo extra controller
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月22日
 * @since
 */
@ExtraController
public class DiabloExtraController extends AbstractAuthenticatorController {

	/**
	 * Captcha handler
	 */
	@Autowired
	protected CaptchaHandler captchaHandler;

	/**
	 * IAM credentials securer
	 */
	@Autowired
	protected IamCredentialsSecurer securer;

	/**
	 * Initialization before login checks whether authentication code is
	 * enabled, etc.
	 * 
	 * @param principal
	 * @param request
	 */
	@GetMapping(URI_S_EXT_CHECK)
	@ResponseBody
	public RespBase<String> check(@RequestParam String principal, HttpServletRequest request) {
		RespBase<String> resp = new RespBase<>();
		try {
			// Get failure locker conditions
			List<String> conditions = getFailConditions(WebUtils2.getHttpRemoteIpAddress(request), principal);
			// Get the validation code enabled status
			String enabled = captchaHandler.isEnabled(conditions) ? "yes" : "no";
			resp.getData().put(config.getParam().getCaptchaEnabled(), enabled);

			// Apply credentials encryption secret key
			String secret = this.securer.applySecretKey(principal);
			resp.getData().put(config.getParam().getSecret(), secret);

		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(Exceptions.getRootCauses(e).getMessage());
			log.error("Failure to initial check", e);
		}
		return resp;
	}

	/**
	 * Apply captcha image
	 * 
	 * @param response
	 */
	@GetMapping(URI_S_EXT_CAPTCHA_APPLY)
	public void applyCaptcha(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (!interceptor.preApplyCapcha(request, response)) {
				throw new AccessPermissionDeniedException(String.format("Access permission denied for remote IP:%s",
						WebUtils2.getHttpRemoteIpAddress(WebUtils.toHttp(request))));
			}

			this.captchaHandler.apply(response);
		} catch (Exception e) {
			log.error("Failure to apply for captcha", e);
		}
	}

	/**
	 * Apply locale.</br>
	 * See:{@link com.wl4g.devops.iam.common.i18n.DelegateBundleMessageSource}
	 * See:{@link org.springframework.context.support.MessageSourceAccessor}
	 * 
	 * @param response
	 */
	@GetMapping(URI_S_EXT_LOCALE_APPLY)
	public RespBase<Locale> applyLocale(HttpServletRequest request) {
		RespBase<Locale> resp = new RespBase<>();
		try {
			String lang = WebUtils.getCleanParam(request, config.getParam().getLanguage());
			Assert.hasText(lang, String.format("'%s' must not be empty", config.getParam().getLanguage()));

			Locale locale = new Locale(lang);
			SessionBindings.bind(KEY_USE_LOCALE, locale);

			resp.getData().put(KEY_USE_LOCALE, locale);
		} catch (Exception e) {
			log.error("Failure to apply for locale", e);
			resp.setCode(RetCode.PARAM_ERR);
			resp.setMessage(Exceptions.getRootCauseMessage(e));
		}
		return resp;
	}

	/**
	 * Read the error message stored in the current session.
	 * 
	 * @param request
	 * @return
	 */
	@GetMapping(URI_S_EXT_ERRREAD)
	@ResponseBody
	public RespBase<String> errReads(HttpServletRequest request, HttpServletResponse response) {
		RespBase<String> resp = new RespBase<>();
		try {
			// Get error message in session
			String errmsg = SessionBindings.getBindValue(KEY_ERR_SESSION_SAVED, true);
			errmsg = StringUtils.isEmpty(errmsg) ? "" : errmsg;
			resp.getData().put(KEY_ERR_SESSION_SAVED, errmsg);
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(Exceptions.getRootCauses(e).getMessage());
			log.error("Get error on session failed", e);
		}
		return resp;
	}

}