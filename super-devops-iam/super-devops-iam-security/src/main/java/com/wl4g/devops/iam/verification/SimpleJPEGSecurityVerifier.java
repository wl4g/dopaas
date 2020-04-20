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

import com.wl4g.devops.iam.crypto.SecureCryptService.SecureAlgKind;
import com.wl4g.devops.iam.verification.model.GenericApplyResult;
import com.wl4g.devops.iam.verification.model.GenericVerifyResult;
import com.wl4g.devops.tool.common.crypto.asymmetric.spec.KeyPairSpec;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.wl4g.devops.tool.common.serialize.JacksonUtils.parseJSON;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Default JDK CAPTCHA handler.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月29日
 * @since
 */
public class SimpleJPEGSecurityVerifier extends GraphBasedSecurityVerifier {

	final private static Random RANDOM = new Random();
	final private static int DEFAULT_WIDTH = 60;
	final private static int DEFAULT_HEIGHT = 28;
	final private static Font DEFAULT_FONT = defaultFont();

	@Override
	public VerifyKind kind() {
		return VerifyKind.GRAPH_SIMPLE;
	}

	@Override
	protected Object postApplyGraphProperties(@NotNull SecureAlgKind kind, String applyToken, VerifyCodeWrapper codeWrap,
			KeyPairSpec keySpec) throws IOException {
		// Generate image & to base64 string.
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(createImage(codeWrap.getCode()), "JPEG", out);

		// Build model
		GenericApplyResult model = new GenericApplyResult(applyToken, kind().getAlias());
		model.setPrimaryImg(convertToBase64(out.toByteArray()));
		return model;
	}

	@Override
	protected Object getRequestVerifyCode(@NotBlank String params, @NotNull HttpServletRequest request) {
		GenericVerifyResult model = parseJSON(params, GenericVerifyResult.class);
		validator.validate(model);
		return model;
	}

	/**
	 * Create captcha image buffer.
	 *
	 * @param verifyCode
	 * @return
	 */
	private BufferedImage createImage(String verifyCode) {
		// BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
		BufferedImage image = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_BGR);
		Graphics g = image.getGraphics();// 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
		g.fillRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));
		g.setColor(getRandColor(110, 133));
		// Drawing interference lines
		for (int i = 0; i <= 10; i++) {
			drowLine(g);
		}
		// Drawing random characters
		char[] chs = verifyCode.toCharArray();
		for (int i = 0; i < chs.length; i++) {
			drowText(g, String.valueOf(chs[i]), (i + 1));
		}
		g.dispose();
		return image;
	}

	/**
	 * Drawing texts
	 *
	 * @param g
	 * @param str
	 * @param i
	 * @return
	 */
	private void drowText(Graphics g, String str, int i) {
		g.setFont(DEFAULT_FONT);
		g.setColor(new Color(RANDOM.nextInt(101), RANDOM.nextInt(111), RANDOM.nextInt(121)));
		g.translate(RANDOM.nextInt(3), RANDOM.nextInt(3));
		g.drawString(str, 9 * i, 16);
	}

	/**
	 * Drawing interference lines
	 *
	 * @param g
	 */
	private void drowLine(Graphics g) {
		int x = RANDOM.nextInt(DEFAULT_WIDTH);
		int y = RANDOM.nextInt(DEFAULT_HEIGHT);
		int xl = RANDOM.nextInt(13);
		int yl = RANDOM.nextInt(15);
		g.drawLine(x, y, x + xl, y + yl);
	}

	/**
	 * Get color
	 *
	 * @param fc
	 * @param bc
	 * @return
	 */
	private Color getRandColor(int fc, int bc) {
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + RANDOM.nextInt(bc - fc - 16);
		int g = fc + RANDOM.nextInt(bc - fc - 14);
		int b = fc + RANDOM.nextInt(bc - fc - 18);
		return new Color(r, g, b);
	}

	/**
	 * Get default font
	 *
	 * @return
	 */
	private static Font defaultFont() {
		return new Font("Fixedsys", Font.CENTER_BASELINE, 18);
	}

}