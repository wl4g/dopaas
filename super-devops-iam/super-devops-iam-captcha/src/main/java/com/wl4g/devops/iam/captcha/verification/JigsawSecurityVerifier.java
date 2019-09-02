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

import static org.apache.shiro.web.util.WebUtils.getCleanParam;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.iam.captcha.jigsaw.JigsawImageManager;
import com.wl4g.devops.iam.captcha.jigsaw.JigsawImgCode;
import com.wl4g.devops.iam.crypto.keypair.RSACryptographicService;
import com.wl4g.devops.iam.verification.GraphBasedSecurityVerifier;

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

	@Autowired
	protected JigsawImageManager jigsawManager;

	@Autowired
	protected RSACryptographicService rsaCryptoService;

	@Override
	public VerifyType verifyType() {
		return VerifyType.GRAPH_JIGSAW;
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
			ImageIO.write(code.getPrimaryImg(), "JPEG", out);
			break;
		case IMGTYPE_BLOCK:
			ImageIO.write(code.getBlockImg(), "JPEG", out);
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
		JigsawImgCode code = (JigsawImgCode) submitCode;
		if (log.isInfoEnabled()) {
			log.info(code.toString());
		}
		// TODO
		return false;
	}

}