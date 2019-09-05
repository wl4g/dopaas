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
package com.wl4g.devops.iam.captcha.jigsaw.model;

import static com.wl4g.devops.common.utils.codec.Compresss.snappyCompress;
import static com.wl4g.devops.common.utils.codec.Compresss.snappyUnCompress;

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
	private byte[] primaryImg; // Base64
	private byte[] blockImg;

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

	public byte[] getPrimaryImg() {
		return primaryImg;
	}

	public void setPrimaryImg(byte[] primaryImg) {
		this.primaryImg = primaryImg;
	}

	public byte[] getBlockImg() {
		return blockImg;
	}

	public void setBlockImg(byte[] blockImg) {
		this.blockImg = blockImg;
	}

	@Override
	public String toString() {
		return "JigsawImgCode [x=" + x + ", y=" + y + ", primaryImg=" + primaryImg + ", blockImg=" + blockImg + "]";
	}

	/**
	 * Compression primary and block image.
	 * 
	 * @return
	 */
	public JigsawImgCode compress() {
		setPrimaryImg(snappyCompress(getPrimaryImg()));
		setBlockImg(snappyCompress(getBlockImg()));
		return this;
	}

	/**
	 * Compression primary and block image.
	 * 
	 * @return
	 */
	public JigsawImgCode uncompress() {
		setPrimaryImg(snappyUnCompress(getPrimaryImg()));
		setBlockImg(snappyUnCompress(getBlockImg()));
		return this;
	}

}