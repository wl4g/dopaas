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
package com.wl4g.devops.iam.captcha.verification;

import com.wl4g.devops.iam.captcha.config.CaptchaProperties;
import com.wl4g.devops.iam.captcha.jigsaw.JigsawImageManager;
import com.wl4g.devops.iam.captcha.jigsaw.model.ApplyJigsawImgModel;
import com.wl4g.devops.iam.captcha.jigsaw.model.JigsawImgCode;
import com.wl4g.devops.iam.captcha.jigsaw.model.VerifyJigsawImgModel;
import com.wl4g.devops.iam.crypto.keypair.RSACryptographicService;
import com.wl4g.devops.iam.crypto.keypair.RSAKeySpecWrapper;
import com.wl4g.devops.iam.verification.GraphBasedSecurityVerifier;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Objects;

import static com.wl4g.devops.iam.common.utils.SessionBindings.getBindValue;

/**
 * Jigsaw slider CAPTCHA verification handler.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月28日
 * @since
 */
public class JigsawSecurityVerifier extends GraphBasedSecurityVerifier {

	/**
	 * Jigsaw image manager.
	 */
	@Autowired
	protected JigsawImageManager jigsawManager;

	/**
	 * RSA cryptographic service.
	 */
	@Autowired
	protected RSACryptographicService rsaCryptoService;

	/**
	 * CAPTCHA config properties.
	 */
	protected CaptchaProperties captchaConfig;

	@Override
	public VerifyType verifyType() {
		return VerifyType.GRAPH_JIGSAW;
	}

	@Override
	protected Object postApplyGraphProperties(String graphToken, VerifyCodeWrapper codeWrap) {
		JigsawImgCode code = codeWrap.getCode();
		// Build model
		ApplyJigsawImgModel model = new ApplyJigsawImgModel(graphToken, verifyType().getType());
		model.setY(code.getY());
		model.setPrimaryImg(code.getPrimaryImg());
		model.setBlockImg(code.getBlockImg());
		return model;
	}

	@Override
	protected Object generateCode() {
		return jigsawManager.borrow();
	}

	@Override
	protected Object getSubmittedCode(@NotNull HttpServletRequest request) {
		// TODO
		return null;
	}

	@Override
	protected boolean doMatch(VerifyCodeWrapper storedCode, Object submitCode) {
		JigsawImgCode code = (JigsawImgCode) storedCode.getCode();
		VerifyJigsawImgModel model = (VerifyJigsawImgModel) submitCode;

		// Analyze & verification jigsaw image.
		boolean matched = doAnalyzingJigsawGraph(code, model);
		if (log.isInfoEnabled()) {
			log.info("Jigsaw match result: {}, storedCode: {}, submitCode: {}", matched, code.toString(), model.toString());
		}
		return matched;
	}

	/**
	 * Analyze and verification jigsaw graph.
	 * 
	 * @param code
	 * @param model
	 * @return
	 */
	private boolean doAnalyzingJigsawGraph(JigsawImgCode code, VerifyJigsawImgModel model) {
		if (Objects.isNull(model.getX())) {
			log.warn("VerifyJigsaw image x-postition is empty. - {}", model);
			return false;
		}
		// Decryption slider block x-position.
		RSAKeySpecWrapper keySpec = getBindValue(model.getApplyToken(), true);
		String plainX = rsaCryptoService.decryptWithHex(keySpec, model.getX());
		if (log.isDebugEnabled()) {
			log.debug("Jigsaw analyze decrypt plain-X: {}, cipher-X", plainX, model.getX());
		}

		// Do matching
		return Math.abs(Integer.parseInt(plainX) - code.getX()) <= captchaConfig.getJigsaw().getAllowOffsetX();
	}

}