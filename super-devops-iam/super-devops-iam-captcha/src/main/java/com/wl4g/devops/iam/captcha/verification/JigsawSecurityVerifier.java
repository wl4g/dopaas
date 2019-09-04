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
import com.wl4g.devops.iam.captcha.jigsaw.ApplyJigsawImgModel;
import com.wl4g.devops.iam.captcha.jigsaw.JigsawImageManager;
import com.wl4g.devops.iam.captcha.jigsaw.JigsawImgCode;
import com.wl4g.devops.iam.captcha.jigsaw.VerifyJigsawImgModel;
import com.wl4g.devops.iam.crypto.keypair.RSACryptographicService;
import com.wl4g.devops.iam.crypto.keypair.RSAKeySpecWrapper;
import com.wl4g.devops.iam.verification.GraphBasedSecurityVerifier;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.wl4g.devops.iam.common.utils.SessionBindings.getBindValue;
import static com.wl4g.devops.iam.verification.SecurityVerifier.VerifyType.PARAM_VERIFYTYPE;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;

/**
 * Jigsaw slider CAPTCHA verification handler.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月28日
 * @since
 */
public class JigsawSecurityVerifier extends GraphBasedSecurityVerifier {

	final public static String PARAM_IMGTYPE_NAME = "imgType";
	final public static String IMGTYPE_PRIMARY = "primary";
	final public static String IMGTYPE_BLOCK = "block";

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
	public Map<String, Object> apply(String owner, @NotNull List<String> factors, @NotNull HttpServletRequest request) {
		Map<String, Object> applyResp = super.apply(owner, factors, request);
		// Get generated verifyCode.
		VerifyCodeWrapper wrap = getVerifyCode(true);
		JigsawImgCode code = (JigsawImgCode) wrap.getCode();

		Map<String, Object> primaryParams = new HashMap<>();
		primaryParams.put(PARAM_IMGTYPE_NAME,IMGTYPE_PRIMARY);
		primaryParams.put(PARAM_VERIFYTYPE,verifyType().getType());
		primaryParams.put(DEFAULT_PARAM_APPLY_UUID,applyResp.get(DEFAULT_PARAM_APPLY_UUID));

		Map<String, Object> blockParams = new HashMap<>();
		blockParams.put(PARAM_IMGTYPE_NAME,IMGTYPE_BLOCK);
		blockParams.put(PARAM_VERIFYTYPE,verifyType().getType());
		blockParams.put(DEFAULT_PARAM_APPLY_UUID,applyResp.get(DEFAULT_PARAM_APPLY_UUID));

		//String primaryImgUrl = getRFCBaseURI(request, true) + URI_S_VERIFY_BASE +"/"+URI_S_VERIFY_RENDER_CAPTCHA+ "?" + BeanMapConvert.toUriParmaters(primaryParams);
		//String blockImgUrl = getRFCBaseURI(request, true) + URI_S_VERIFY_BASE + "/"+URI_S_VERIFY_RENDER_CAPTCHA+"?"+BeanMapConvert.toUriParmaters(blockParams);

		// Build apply model.
		ApplyJigsawImgModel model = new ApplyJigsawImgModel();
		model.setY(code.getY());
		model.setPrimaryImg(code.getPrimaryImg());
		model.setBlockImg(code.getBlockImg());

		applyResp.put("jigsaw", model);
		return applyResp;
	}

	@Override
	protected void imageWrite(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, Object storedCode)
			throws IOException {
		JigsawImgCode code = (JigsawImgCode) storedCode;
		if (log.isDebugEnabled()) {
			log.debug("Write captcha image for: {}", code.toString());
		}
		ServletOutputStream out = response.getOutputStream();

		// Corresponding write image CAPTCHA.
		String imgType = getCleanParam(request, PARAM_IMGTYPE_NAME);
		switch (String.valueOf(imgType)) {
		case IMGTYPE_PRIMARY:
			//TODO
			//ImageIO.write(code.getPrimaryImg(), "JPEG", out);
			break;
		case IMGTYPE_BLOCK:
			//TODO
			//ImageIO.write(code.getBlockImg(), "JPEG", out);
			break;
		default:
			throw new IllegalArgumentException(String.format("Unknown jigsaw image type '%s'", imgType));
		}

	}

	@Override
	protected Object generateCode() {
		return jigsawManager.borrow();
	}

	@Override
	protected boolean doMatch(VerifyCodeWrapper storedCode, Object submitCode) {
		JigsawImgCode code = (JigsawImgCode) storedCode.getCode();
		VerifyJigsawImgModel model = (VerifyJigsawImgModel) submitCode;

		// Analyze & verification jigsaw image.
		boolean matched = doAnalyzeJigsaw(code, model);
		if (log.isInfoEnabled()) {
			log.info("Jigsaw match result: {}, storedCode: {}, submitCode: {}", matched, code.toString(), model.toString());
		}
		return matched;
	}

	/**
	 * Analyze and verification jigsaw image.
	 * 
	 * @param code
	 * @param model
	 * @return
	 */
	private boolean doAnalyzeJigsaw(JigsawImgCode code, VerifyJigsawImgModel model) {
		if (Objects.isNull(model.getX())) {
			return false;
		}

		// Decryption slider block X-position.
		RSAKeySpecWrapper keySpec = getBindValue(DEFAULT_PARAM_APPLY_UUID, true);
		String plainX = rsaCryptoService.decryptWithHex(keySpec, model.getX());
		if (log.isDebugEnabled()) {
			log.debug("Jigsaw analyze decrypt plain-X: {}, cipher-X", plainX, model.getX());
		}

		// Matching
		return Math.abs(Integer.parseInt(plainX) - code.getX()) <= captchaConfig.getJigsaw().getAllowOffsetX();
	}

}