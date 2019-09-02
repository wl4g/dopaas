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
package com.wl4g.devops.iam.captcha.jigsaw;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Jigsaw image code.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月30日
 * @since
 */
public class JigsawImgCode implements Serializable {
	private static final long serialVersionUID = 4975604362812626949L;

	private int x;
	private int y;
	private BufferedImage primaryImg;
	private BufferedImage blockImg;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public BufferedImage getPrimaryImg() {
		return primaryImg;
	}

	public void setPrimaryImg(BufferedImage primaryImg) {
		this.primaryImg = primaryImg;
	}

	public BufferedImage getBlockImg() {
		return blockImg;
	}

	public void setBlockImg(BufferedImage blockImg) {
		this.blockImg = blockImg;
	}

}