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
package com.wl4g.devops.iam.captcha.gif;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import com.wl4g.devops.iam.captcha.gif.Streams;
import static com.wl4g.devops.iam.captcha.gif.Randoms.num;

/**
 * <p>
 * Gif验证码类
 * </p>
 * 
 * @author wangl.sir
 * @version v1.0 2019年3月11日
 * @since
 */
public class GifCaptcha extends Captcha {

	public GifCaptcha(String capText) {
		super(capText);
	}

	public GifCaptcha(int width, int height, String capText) {
		super(width, height, capText);
	}

	public GifCaptcha(Font font, int width, int height, String capText) {
		super(font, width, height, capText);
	}

	@Override
	public void out(OutputStream os) throws IOException {
		try {
			GifEncoder gifEncoder = new GifEncoder(); // gif编码类，这个利用了洋人写的编码类，所有类都在附件中
			// 生成字符
			gifEncoder.start(os);
			gifEncoder.setQuality(180);
			gifEncoder.setDelay(100);
			gifEncoder.setRepeat(0);
			char[] rands = super.getCapText().toCharArray();
			Color fontcolor[] = new Color[getLen()];
			for (int i = 0; i < super.getLen(); i++) {
				fontcolor[i] = new Color(20 + num(110), 20 + num(110), 20 + num(110));
			}
			BufferedImage frame;
			for (int i = 0; i < getLen(); i++) {
				frame = graphicsImage(fontcolor, rands, i);
				gifEncoder.addFrame(frame);
				frame.flush();
			}
			gifEncoder.finish();
		} finally {
			Streams.close(os);
		}
	}

	/**
	 * 画随机码图
	 * 
	 * @param fontcolor
	 *            随机字体颜色
	 * @param strs
	 *            字符数组
	 * @param flag
	 *            透明度使用
	 * @return BufferedImage
	 */
	private BufferedImage graphicsImage(Color[] fontcolor, char[] strs, int flag) {
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		// 或得图形上下文
		// Graphics2D g2d=image.createGraphics();
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		// 利用指定颜色填充背景
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		AlphaComposite ac3;
		int h = getHeight() - ((getHeight() - getFont().getSize()) >> 1);
		int w = getWidth() / getLen();
		g2d.setFont(getFont());
		for (int i = 0; i < getLen(); i++) {
			ac3 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha(flag, i));
			g2d.setComposite(ac3);
			g2d.setColor(fontcolor[i]);
			g2d.drawOval(num(getWidth()), num(getHeight()), 5 + num(10), 5 + num(10));
			g2d.drawString(strs[i] + "", (getWidth() - (getLen() - i) * w) + (w - getFont().getSize()) + 1, h - 4);
		}
		g2d.dispose();
		return image;
	}

	/**
	 * 获取透明度,从0到1,自动计算步长
	 * 
	 * @return float 透明度
	 */
	private float getAlpha(int i, int j) {
		int num = i + j;
		float r = (float) 1 / getLen(), s = (getLen() + 1) * r;
		return num > getLen() ? (num * r - s) : num * r;
	}

}