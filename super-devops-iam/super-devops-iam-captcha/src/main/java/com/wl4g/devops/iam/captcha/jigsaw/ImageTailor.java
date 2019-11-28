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

import com.wl4g.devops.iam.captcha.jigsaw.model.JigsawImgCode;

import javax.imageio.ImageIO;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.wl4g.devops.common.utils.io.FileIOUtils.writeFile;
import static io.netty.util.internal.ThreadLocalRandom.current;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;

/**
 * Image tailor.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-30
 * @since
 */
public class ImageTailor {
	final public static int DEFAULT_BORDER_WEIGHT = 3;

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
	/** Watermark string drawn. */
	final protected String watermark;

	// --- Block borders position. ---
	private int borderTopXMin; // Top
	private int borderTopXMax;
	private int borderTopYMin;
	private int borderTopYMax;
	private int borderRightXMin; // Right
	private int borderRightXMax;
	private int borderRightYMin;
	private int borderRightYMax;
	private int borderBottomXMin; // Bottom
	private int borderBottomXMax;
	private int borderBottomYMin;
	private int borderBottomYMax;
	private int borderLeftXMin; // Left
	private int borderLeftXMax;
	private int borderLeftYMin;
	private int borderLeftYMax;

	public ImageTailor() {
		this(46, 46, 10, "wanglsir@gmail.com");
	}

	public ImageTailor(String watermark) {
		this(46, 46, 10, watermark);
	}

	public ImageTailor(int blockWidth, int blockHeight, int circleR, String watermark) {
		this(300, 200, 200, 100, blockWidth, blockHeight, circleR, watermark);
	}

	public ImageTailor(int sourceMaxWidth, int sourceMaxHeight, int sourceMinWidth, int sourceMinHeight, int blockWidth,
			int blockHeight, int circleR, String watermark) {
		isTrue(sourceMaxWidth != 0, "sourceMaxWith cannot be less than 0");
		isTrue(sourceMaxHeight != 0, "sourceMaxHeight cannot be less than 0");
		isTrue(sourceMinWidth != 0, "sourceMinWidth cannot be less than 0");
		isTrue(sourceMinHeight != 0, "sourceMinHeight cannot be less than 0");
		isTrue(blockWidth != 0, "blockWidth cannot be less than 0");
		isTrue(blockHeight != 0, "blockHeight cannot be less than 0");
		isTrue(circleR != 0, "circleR cannot be less than 0");
		// hasText(watermark, "watermark cannot be empty");
		this.sourceMaxWidth = sourceMaxWidth;
		this.sourceMaxHeight = sourceMaxHeight;
		this.sourceMinWidth = sourceMinWidth;
		this.sourceMinHeight = sourceMinHeight;
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		this.circleR = circleR;
		this.watermark = watermark;
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
		return doImageProcess(source);
	}

	/**
	 * Get jigsaw cut image from URL
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public JigsawImgCode getJigsawImageUrl(String url) throws IOException {
		return doImageProcess(ImageIO.read(new URL(url)));
	}

	/**
	 * Get jigsaw cut image from input stream
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public JigsawImgCode getJigsawImageInputStream(InputStream in) throws IOException {
		return doImageProcess(ImageIO.read(in));
	}

	/**
	 * Do processing cut jigsaw image.
	 * 
	 * @param sourceImg
	 * @return
	 * @throws IOException
	 */
	private JigsawImgCode doImageProcess(BufferedImage sourceImg) throws IOException {
		int width = sourceImg.getWidth();
		int height = sourceImg.getHeight();
		// Check maximum effective width height
		isTrue((width <= sourceMaxWidth && height <= sourceMaxHeight),
				String.format("Source image is too big, max limits: %d*%d", sourceMaxWidth, sourceMaxHeight));
		isTrue((width >= sourceMinWidth && height >= sourceMinHeight),
				String.format("Source image is too small, min limits: %d*%d", sourceMinWidth, sourceMinHeight));

		// 创建背景图，TYPE_4BYTE_ABGR表示具有8位RGBA颜色分量的图像(支持透明的BufferedImage)，正常取bufImg.getType()
		BufferedImage primaryImg = new BufferedImage(sourceImg.getWidth(), sourceImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		// 创建滑块图
		BufferedImage blockImg = new BufferedImage(sourceImg.getWidth(), sourceImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

		// 随机截取的坐标
		int lx = width - blockWidth;
		int ly = height - blockHeight;
		int blockX0 = current().nextInt((int) (lx * 0.25), lx); // *0.25防止x坐标太靠左
		int blockY0 = current().nextInt(circleR, ly); // 从circleR开始是为了防止上边的耳朵显示不全
		// Setup block borders position.
		setBorderPositions(blockX0, blockY0, blockWidth, blockHeight);

		// 绘制生成新图(图片大小是固定，位置是随机)
		drawing(sourceImg, blockImg, primaryImg, blockX0, blockY0, blockWidth, blockHeight);
		// 截取可用区
		blockImg = blockImg.getSubimage(blockX0, (blockY0 - circleR) >= 0 ? (blockY0 - circleR) : 0, blockWidth,
				blockHeight + circleR);

		// Add watermark string.
		addWatermarkIfNecessary(primaryImg);

		// 输出图像数据
		JigsawImgCode img = new JigsawImgCode();
		// Primary image.
		ByteArrayOutputStream primaryData = new ByteArrayOutputStream();
		ImageIO.write(primaryImg, "PNG", primaryData);
		img.setPrimaryImg(primaryData.toByteArray());

		// Block image.
		ByteArrayOutputStream blockData = new ByteArrayOutputStream();
		ImageIO.write(blockImg, "PNG", blockData);
		img.setBlockImg(blockData.toByteArray());

		// Position
		img.setX(blockX0);
		img.setY(blockY0 - circleR >= 0 ? blockY0 - circleR : 0);
		return img;
	}

	/**
	 * Drawing images.
	 * 
	 * @param sourceImg
	 * @param blockImg
	 * @param primaryImg
	 * @param blockX0
	 * @param blockY0
	 * @param blockWidth
	 * @param blockHeight
	 */
	private void drawing(BufferedImage sourceImg, BufferedImage blockImg, BufferedImage primaryImg, int blockX0, int blockY0,
			int blockWidth, int blockHeight) {
		double rr = Math.pow(circleR, 2);// r平方
		// R1圆心坐标（顶部的圆）
		int c1_x0 = current().nextInt(blockWidth - 2 * circleR) + (blockX0 + circleR); // x+c_r+10;//圆心x坐标必须在(x+r,x+with-r)范围内
		int c1_y0 = blockY0;
		// R2圆心坐标（左边的圆）
		int c2_x0 = blockX0;
		// y+circleR+50圆心y坐标必须在(y+r,y+height-r)范围内
		int c2_y0 = current().nextInt(blockHeight - 2 * circleR) + (blockY0 + circleR);

		for (int x = 0; x < sourceImg.getWidth(); x++) {
			for (int y = 0; y < sourceImg.getHeight(); y++) {
				int rgb = sourceImg.getRGB(x, y);
				// (x-a)²+(y-b)²=r²中，有三个参数a、b、r，即圆心坐标为(a，b)，半径r。
				double rr1 = Math.pow((x - c1_x0), 2) + Math.pow((y - c1_y0), 2);
				double rr2 = Math.pow((x - c2_x0), 2) + Math.pow((y - c2_y0), 2);

				// 在矩形块区域内?
				boolean withInBlock = x >= blockX0 && x < (blockX0 + blockWidth) && y >= blockY0 && y < (blockY0 + blockHeight);
				// Primary image
				if (rr >= rr1) { // 在R1区域内
					primaryImg.setRGB(x, y, getGrayTransparentRGB(rgb));
				} else {
					if (withInBlock) { // 在矩形块区域内
						if (rr >= rr2) { // 在R2区域内
							primaryImg.setRGB(x, y, rgb);
						} else {
							if (!gaussainGradientIfNecessary(primaryImg, blockX0, blockY0, blockWidth, blockHeight, x, y, rgb)) { // 已设置高斯模糊渐变
																																	// ?
								primaryImg.setRGB(x, y, getGrayTransparentRGB(rgb));
							}
						}
					} else {
						primaryImg.setRGB(x, y, rgb);
					}
				}

				// Block image
				if (withInBlock || rr >= rr1) { // 在区块或R1内
					if (rr <= rr2) { // 不在R2内
						blockImg.setRGB(x, y, rgb);
					}
				}

			}
		}
	}

	/**
	 * Watermark string drawn
	 * 
	 * @param img
	 * @return
	 * @throws IOException
	 */
	private BufferedImage addWatermarkIfNecessary(BufferedImage img) throws IOException {
		if (isBlank(watermark)) {
			return img;
		}
		Graphics2D graphics2D = img.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		// 设置水印文字颜色
		graphics2D.setColor(Color.WHITE);
		// 设置水印文字Font
		graphics2D.setFont(new Font("微软雅黑", Font.BOLD, 16));
		// 设置水印文字透明度
		graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
		// 第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)， 其中10为微调的便宜倍数，为了动态解决靠右下角
		int x = (int) (img.getWidth() - watermark.getBytes().length * 10), y = (int) (img.getHeight() * 0.9);
		graphics2D.drawString(watermark, x, y);
		graphics2D.dispose(); // 释放
		return img;
	}

	/**
	 * Setup border positions.
	 * 
	 * @param blockX0
	 * @param blockY0
	 * @param blockWidth
	 * @param blockHeight
	 */
	private void setBorderPositions(int blockX0, int blockY0, int blockWidth, int blockHeight) {
		// Top
		borderTopXMin = blockX0;
		borderTopXMax = blockX0 + blockWidth;
		borderTopYMin = blockY0;
		borderTopYMax = blockY0 + DEFAULT_BORDER_WEIGHT;
		// Right
		borderRightXMin = blockX0 + blockWidth - DEFAULT_BORDER_WEIGHT;
		borderRightXMax = blockX0 + blockWidth;
		borderRightYMin = blockY0;
		borderRightYMax = blockY0 + blockHeight;
		// Bottom
		borderBottomXMin = blockX0;
		borderBottomXMax = blockX0 + blockWidth;
		borderBottomYMin = blockY0 + blockHeight - DEFAULT_BORDER_WEIGHT;
		borderBottomYMax = blockY0 + blockHeight;
		// Left
		borderLeftXMin = blockX0;
		borderLeftXMax = blockX0 + DEFAULT_BORDER_WEIGHT;
		borderLeftYMin = blockY0;
		borderLeftYMax = blockY0 + blockHeight;
	}

	/**
	 * 获取灰色半透明RGB
	 * 
	 * @param rgb
	 * @return
	 */
	private int getGrayTransparentRGB(int rgb) {
		int r = (0xff & rgb);
		int g = (0xff & (rgb >> 8));
		int b = (0xff & (rgb >> 16));
		rgb = r + (g << 8) + (b << 16) + (100 << 24);
		// rgb = r + (g << 8) + (b << 16); // 亮一些
		return rgb;
	}

	/**
	 * 设置高斯模糊渐变RGB（仅当是边界区域时）
	 * 
	 * @param img
	 * @param blockX0
	 * @param blockY0
	 * @param blockWidth
	 * @param blockHeight
	 * @param x
	 * @param y
	 * @param rgb
	 * @return 当成功设置返回TRUE，否则返回FALSE
	 */
	private boolean gaussainGradientIfNecessary(BufferedImage img, int blockX0, int blockY0, int blockWidth, int blockHeight,
			int x, int y, int rgb) {
		// Top
		if (x >= borderTopXMin && x <= borderTopXMax && y >= borderTopYMin && y <= borderTopYMax) {
			return true;
		}
		// Right
		if (x >= borderRightXMin && x <= borderRightXMax && y >= borderRightYMin && y <= borderRightYMax) {
			setGaussainRGB(img, x, y, DEFAULT_BORDER_WEIGHT, blockHeight, rgb);
			return true;
		}
		// Bottom
		if (x >= borderBottomXMin && x <= borderBottomXMax && y >= borderBottomYMin && y <= borderBottomYMax) {
			return true;
		}
		// Left
		if (x >= borderLeftXMin && x <= borderLeftXMax && y >= borderLeftYMin && y <= borderLeftYMax) {
			return true;
		}
		return false;
	}

	/**
	 * 设置高斯模糊渐变RGB.
	 * 
	 * @param img
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param rgb
	 */
	private void setGaussainRGB(BufferedImage img, int x, int y, int width, int height, int rgb) {
		// int[] inPixels = new int[width * height];
		// int[] outPixels = new int[width * height];
		// Kernel kernel = GaussianFilter.makeKernel(0.667f);
		// GaussianFilter.convolveAndTranspose(kernel, inPixels, outPixels,
		// width, height, true, GaussianFilter.CLAMP_EDGES);
		// GaussianFilter.convolveAndTranspose(kernel, outPixels, inPixels,
		// height, width, true, GaussianFilter.CLAMP_EDGES);
		// img.setRGB(x, y, width, height, inPixels, 0, width);

		// NormalDistribution nd = new NormalDistribution(0, 256);
		// double rgb0 = nd.cumulativeProbability(rgb);
		// rgb = (int) rgb0;
		// img.setRGB(x, y, (int) rgb0);

		// int r = (0xff & rgb);
		// int g = (0xff & (rgb >> 8));
		// int b = (0xff & (rgb >> 16));
		// rgb = r + (g << 8) + (b << 4) + (100 << 24);
		img.setRGB(x, y, new Color(220, 220, 220, 0.8f).getRGB());
	}

	/**
	 * Output write {@link BufferedImage} to file.
	 * 
	 * @param img
	 * @param filepath
	 * @throws IOException
	 */
	public static void writeImage(BufferedImage img, String filepath) throws IOException {
		hasText(filepath, "File path can't empty");
		ImageIO.write(img, "PNG", new File(filepath));
	}

	public static void main(String[] args) throws Exception {
		ImageTailor tailor = new ImageTailor();
		JigsawImgCode img = tailor.getJigsawImageFile(USER_DIR + "/src/main/resources/static/jigsaw/jigsaw_default1.jpg");

		// JigsawImgCode img =
		// tailor.getJigsawImageUrl("http://vps.vjay.pw/1.jpg");

		System.out.println(img);
		writeFile(new File("f:\\a.png"), img.getPrimaryImg(), false);
		writeFile(new File("f:\\b.png"), img.getBlockImg(), false);
	}

}