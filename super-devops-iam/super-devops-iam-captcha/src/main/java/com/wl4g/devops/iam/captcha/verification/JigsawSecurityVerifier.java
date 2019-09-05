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

import com.wl4g.devops.common.utils.codec.CheckSums;
import com.wl4g.devops.iam.captcha.config.CaptchaProperties;
import com.wl4g.devops.iam.captcha.jigsaw.JigsawImageManager;
import com.wl4g.devops.iam.captcha.jigsaw.model.JigsawApplyImgModel;
import com.wl4g.devops.iam.captcha.jigsaw.model.JigsawImgCode;
import com.wl4g.devops.iam.captcha.jigsaw.model.JigsawVerifyImgModel;
import com.wl4g.devops.iam.crypto.keypair.RSACryptographicService;
import com.wl4g.devops.iam.crypto.keypair.RSAKeySpecWrapper;
import com.wl4g.devops.iam.verification.GraphBasedSecurityVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

import static com.wl4g.devops.common.utils.codec.Encodes.encodeBase64;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.iam.common.utils.SessionBindings.getBindValue;

/**
 * JIGSAW slider CAPTCHA verification handler.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月28日
 * @since
 */
public class JigsawSecurityVerifier extends GraphBasedSecurityVerifier {

	/**
	 * JIGSAW image manager.
	 */
	@Autowired
	protected JigsawImageManager jigsawManager;

	/**
	 * RSA cryptographic service.
	 */
	@Autowired
	protected RSACryptographicService rsaCryptoService;

	/**
	 * CAPTCHA configuration.
	 */
	@Autowired
	protected CaptchaProperties capConfig;

	@Override
	public VerifyType verifyType() {
		return VerifyType.GRAPH_JIGSAW;
	}

	@Override
	protected Object postApplyGraphProperties(String graphToken, VerifyCodeWrapper codeWrap, RSAKeySpecWrapper keySpec) {
		JigsawImgCode code = codeWrap.getCode();
		// Build model
		JigsawApplyImgModel model = new JigsawApplyImgModel(graphToken, verifyType().getType());
		model.setY(code.getY());
		model.setPrimaryImg(encodeBase64(code.getPrimaryImg()));
		model.setBlockImg(encodeBase64(code.getBlockImg()));
		model.setSecret(keySpec.getPubHexString());
		return model;
	}

	@Override
	protected Object generateCode() {
		return jigsawManager.borrow();
	}

	@Override
	protected Object getRequestVerifyCode(@NotBlank String params, @NotNull HttpServletRequest request) {
		JigsawVerifyImgModel model = parseJSON(params, JigsawVerifyImgModel.class);
		validator.validate(model);
		return model;
	}

	@Override
	final protected boolean doMatch(VerifyCodeWrapper storedCode, Object submitCode) {
		JigsawImgCode code = (JigsawImgCode) storedCode.getCode();
		JigsawVerifyImgModel model = (JigsawVerifyImgModel) submitCode;

		// Analyze & verification jigsaw image.
		boolean matched = doAnalyzingJigsawGraph(code, model);
		if (log.isInfoEnabled()) {
			log.info("Jigsaw match result: {}, storedCode: {}, submitCode: {}", matched, code.toString(), model.toString());
		}
		return matched;
	}

	/**
	 * Analyzing & verification JIGSAW graph.
	 * 
	 * @param code
	 * @param model
	 * @return
	 */
	final private boolean doAnalyzingJigsawGraph(JigsawImgCode code, JigsawVerifyImgModel model) {
		if (Objects.isNull(model.getX())) {
			log.warn("VerifyJigsaw image x-postition is empty. - {}", model);
			return false;
		}

		// DECRYPT slider block x-position.
		RSAKeySpecWrapper keySpec = getBindValue(model.getApplyToken(), true);
		String plainX = rsaCryptoService.decryptWithHex(keySpec, model.getX());
		Assert.hasText(plainX, "Invalid x-position, unable to resolve.");
		if (log.isDebugEnabled()) {
			log.debug("Jigsaw analyze decrypt plain x-position: {}, cipher x-position: {}", plainX, model.getX());
		}
		// Parsing additional algorithmic salt.
		Assert.isTrue(plainX.length() > 66,
				String.format("Failed to analyze jigsaw, illegal additional ciphertext. '%s'", plainX));
		// Reduction analysis.
		int prototypeX = parseAdditionalWithAlgorithmicSalt(plainX, model);
		// Do match
		return Math.abs(prototypeX - code.getX()) <= capConfig.getJigsaw().getAllowOffsetX();
	}

	/**
	 * Parse additionalWith algorithmic salt.
	 * 
	 * @param plainX
	 * @param model
	 * @return
	 */
	final private int parseAdditionalWithAlgorithmicSalt(String plainX, JigsawVerifyImgModel model) {
		try {
			final int tmp0 = Integer.parseInt(plainX.substring(67));
			final long tmp1 = CheckSums.crc16String(model.getApplyToken());
			final long tmp2 = tmp0 / tmp1;
			return (int) Math.sqrt(tmp2);
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't parse additional alg salt.");
		}
	}

}