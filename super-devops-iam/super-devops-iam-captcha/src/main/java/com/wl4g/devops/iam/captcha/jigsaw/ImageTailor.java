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

import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.wl4g.devops.common.utils.codec.Encodes.encodeBase64;
import static io.netty.util.internal.ThreadLocalRandom.current;

/**
 * Image tailor.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-30
 * @since
 */
public class ImageTailor {

	/** Material source drawing requires maximum width(:PX) */
	final protected int sourceMaxWidth;
	/** Material source drawing requires maximum height(:PX) */
	final protected int sourceMaxHeight;
	/** Material source drawing requires minimum width(:PX) */
	final protected int sourceMinWidth;
	/** Material source drawing requires minimum height(:PX) */
	final protected int sourceMinHeight;
	/** Block width(:PX) */
	final protected int blockWidth;
	/** Block height(:PX) */
	final protected int blockHeight;
	/** The radius of the circle of the tailored ear. */
	final protected int circleR;

	public ImageTailor() {
		this.sourceMaxWidth = 300;
		this.sourceMaxHeight = 200;
		this.sourceMinWidth = 200;
		this.sourceMinHeight = 100;
		this.blockWidth = 46;
		this.blockHeight = 46;
		this.circleR = 10;
	}

	public ImageTailor(int blockWidth, int blockHeight, int circleR) {
		this(300, 200, 200, 100, blockWidth, blockHeight, circleR);
	}

	public ImageTailor(int sourceMaxWidth, int sourceMaxHeight, int sourceMinWidth, int sourceMinHeight, int blockWidth,
			int blockHeight, int circleR) {
		Assert.isTrue(sourceMaxWidth != 0, "sourceMaxWith cannot be less than 0");
		Assert.isTrue(sourceMaxHeight != 0, "sourceMaxHeight cannot be less than 0");
		Assert.isTrue(sourceMinWidth != 0, "sourceMinWidth cannot be less than 0");
		Assert.isTrue(sourceMinHeight != 0, "sourceMinHeight cannot be less than 0");
		Assert.isTrue(blockWidth != 0, "blockWidth cannot be less than 0");
		Assert.isTrue(blockHeight != 0, "blockHeight cannot be less than 0");
		Assert.isTrue(circleR != 0, "circleR cannot be less than 0");
		this.sourceMaxWidth = sourceMaxWidth;
		this.sourceMaxHeight = sourceMaxHeight;
		this.sourceMinWidth = sourceMinWidth;
		this.sourceMinHeight = sourceMinHeight;
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		this.circleR = circleR;
	}

	/**
	 * Get jigsaw cut image from local file
	 * 
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public JigsawImgCode getJigsawImageFile(String filepath) throws IOException {
		BufferedImage source = ImageIO.read(new File(filepath));
		return doProcess(source);
	}

	/**
	 * Get jigsaw cut image from URL
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public JigsawImgCode getJigsawImageUrl(String url) throws IOException {
		BufferedImage source = ImageIO.read(new URL(url));
		return doProcess(source);
	}

	/**
	 * Get jigsaw cut image from input stream
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public JigsawImgCode getJigsawImageInputStream(InputStream in) throws IOException {
		BufferedImage source = ImageIO.read(in);
		return doProcess(source);
	}

	/**
	 * Do processing cut jigsaw image.
	 * 
	 * @param source
	 * @return
	 * @throws IOException
	 */
	private JigsawImgCode doProcess(BufferedImage source) throws IOException {
		int width = source.getWidth();
		int height = source.getHeight();
		// Check maximum effective width height
		Assert.isTrue((width <= sourceMaxWidth && height <= sourceMaxHeight),
				String.format("Source image is too big, max limits: %d*%d", sourceMaxWidth, sourceMaxHeight));
		Assert.isTrue((width >= sourceMinWidth && height >= sourceMinHeight),
				String.format("Source image is too small, min limits: %d*%d", sourceMinWidth, sourceMinHeight));

		// 移动图
		BufferedImage blockImg = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);// 新建一个类型支持透明的BufferedImage
		// 对比图
		BufferedImage primaryImg = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);// 新建一个类型支持透明的BufferedImage
		// 截取坐标
		int x = current().nextInt(0, width - blockWidth);
		int y = current().nextInt(0, height - blockHeight);
		// 生成
		drawing(source, blockImg, primaryImg, x, y, blockWidth, blockHeight);// 图片大小是固定，位置是随机
		// 截取
		blockImg = blockImg.getSubimage(x, y - circleR >= 0 ? y - circleR : 0, blockWidth, blockHeight + circleR);

		JigsawImgCode img = new JigsawImgCode();
		// Primary image.
		ByteArrayOutputStream primaryData = new ByteArrayOutputStream();
		ImageIO.write(primaryImg, "PNG", primaryData);
		img.setPrimaryImg(encodeBase64(primaryData.toByteArray()));
		// Block image.
		ByteArrayOutputStream blockData = new ByteArrayOutputStream();
		ImageIO.write(blockImg, "PNG", blockData);
		img.setBlockImg(encodeBase64(blockData.toByteArray()));
		// Position
		img.setX(x);
		img.setY(y - circleR >= 0 ? y - circleR : 0);
		return img;
	}

	/**
	 * Drawing images.
	 * 
	 * @param source
	 * @param blockImg
	 * @param primaryImg
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	private void drawing(BufferedImage source, BufferedImage blockImg, BufferedImage primaryImg, int x, int y, int width,
			int height) {
		double rr = Math.pow(circleR, 2);// r平方
		// 圆心的位置
		int c_a = current().nextInt(width - 2 * circleR) + (x + circleR); // x+c_r+10;//圆心x坐标必须在(x+r,x+with-r)范围内
		int c_b = y;

		// 第二个圆（排除圆内的点）
		int c2_a = x;
		// y+circleR+50 圆心y坐标必须在(y+r,y+height-r)范围内
		int c2_b = current().nextInt(height - 2 * circleR) + (y + circleR);

		for (int i = 0; i < source.getWidth(); i++) {
			for (int j = 0; j < source.getHeight(); j++) {
				// (x-a)²+(y-b)²=r²中，有三个参数a、b、r，即圆心坐标为(a，b)，半径r。
				double f = Math.pow((i - c_a), 2) + Math.pow((j - c_b), 2);
				double f2 = Math.pow((i - c2_a), 2) + Math.pow((j - c2_b), 2);

				int rgb = source.getRGB(i, j);
				if (i >= x && i < (x + width) && j >= y && j < (y + height) && f2 >= rr) {// 在矩形内
					// 块范围内的值
					setInternal(blockImg, primaryImg, i, j, rgb);
				} else if (f <= rr) {
					// 在圆内
					setInternal(blockImg, primaryImg, i, j, rgb);
				} else {
					// 剩余位置设置成透明
					setExternal(blockImg, primaryImg, i, j, rgb);
				}
			}
		}
	}

	/**
	 * Set internal image RGB.
	 * 
	 * @param blockImg
	 * @param primaryImg
	 * @param i
	 * @param j
	 * @param rgb
	 */
	private void setInternal(BufferedImage blockImg, BufferedImage primaryImg, int i, int j, int rgb) {
		blockImg.setRGB(i, j, rgb);
		// 原图设置变灰
		int r = (0xff & rgb);
		int g = (0xff & (rgb >> 8));
		int b = (0xff & (rgb >> 16));
		rgb = r + (g << 8) + (b << 16) + (100 << 24);
		// rgb = r + (g << 8) + (b << 16);
		primaryImg.setRGB(i, j, rgb);
	}

	/**
	 * Set external image RGB.
	 * 
	 * @param blockImg
	 * @param primaryImg
	 * @param i
	 * @param j
	 * @param rgb
	 */
	private void setExternal(BufferedImage blockImg, BufferedImage primaryImg, int i, int j, int rgb) {
		blockImg.setRGB(i, j, 0x00ffffff);
		primaryImg.setRGB(i, j, rgb);
	}

	public static void writeImage(BufferedImage img, String filepath) throws IOException {
		ImageIO.write(img, "png", new File(filepath));
	}

	public static void main(String[] args) throws Exception {
		ImageTailor tailor = new ImageTailor(300, 200, 200, 100, 46, 46, 10);
		// 从本地文件获取图片
		// JigsawImgCode img =
		// imageUtil.getJigsawImageFile("/Users/vjay/Downloads/0.jpg");
		JigsawImgCode img = tailor.getJigsawImageUrl("http://vps.vjay.pw/1.jpg");
		System.out.println(img);

		// writeImage(img.getPrimaryImg(), "f:\\a.png");
		// writeImage(img.getBlockImg(), "f:\\b.png");
	}

}