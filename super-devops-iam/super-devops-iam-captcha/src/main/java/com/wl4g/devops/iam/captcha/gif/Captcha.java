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
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.util.Assert;

import static com.wl4g.devops.iam.captcha.gif.Randoms.num;

/**
 * <p>
 * 验证码抽象类,暂时不支持中文
 * </p>
 * 
 * @author wangl.sir
 * @version v1.0 2019年3月11日
 * @since
 */
public abstract class Captcha {
	private Font font = new Font("Verdana", Font.ITALIC | Font.BOLD, 28); // 字体
	private int width = 150; // 验证码显示跨度
	private int height = 40; // 验证码显示高度
	private String capText; // 随机字符串
	private int len; // 随机字符串的长度

	public Captcha(String capText) {
		this(0, 0, capText);
	}

	public Captcha(int width, int height, String capText) {
		this(null, width, height, capText);
	}

	public Captcha(Font font, int width, int height, String capText) {
		super();
		this.font = (font == null) ? this.font : font;
		this.width = (width == 0) ? this.width : width;
		this.height = (height == 0) ? this.height : height;
		Assert.hasText(capText, "'capText' must not be empty");
		this.capText = capText;
		this.len = this.capText.length();
	}

	public String getCapText() {
		return capText;
	}

	public Font getFont() {
		return font;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getLen() {
		return len;
	}

	/**
	 * 给定范围获得随机颜色
	 * 
	 * @return Color 随机颜色
	 */
	protected Color color(int fc, int bc) {
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + num(bc - fc);
		int g = fc + num(bc - fc);
		int b = fc + num(bc - fc);
		return new Color(r, g, b);
	}

	/**
	 * 验证码输出,抽象方法，由子类实现
	 * 
	 * @param os
	 *            输出流
	 * @throws IOException
	 */
	public abstract void out(OutputStream os) throws IOException;

}