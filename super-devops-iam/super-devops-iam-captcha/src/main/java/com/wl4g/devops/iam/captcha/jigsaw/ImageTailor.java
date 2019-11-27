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
		int blockX0 = current().nextInt((int) (lx * 0.3), lx); // *0.3防止x坐标太靠左
		int blockY0 = current().nextInt(0, ly);
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
		// 第一个圆心的位置
		int c1_x0 = current().nextInt(blockWidth - 2 * circleR) + (blockX0 + circleR); // x+c_r+10;//圆心x坐标必须在(x+r,x+with-r)范围内
		int c1_y0 = blockY0;
		// 第二个圆（排除圆内的点）
		int c2_x0 = blockX0;
		// y+circleR+50 圆心y坐标必须在(y+r,y+height-r)范围内
		int c2_y0 = current().nextInt(blockHeight - 2 * circleR) + (blockY0 + circleR);

		for (int x = 0; x < sourceImg.getWidth(); x++) {
			for (int y = 0; y < sourceImg.getHeight(); y++) {
				// (x-a)²+(y-b)²=r²中，有三个参数a、b、r，即圆心坐标为(a，b)，半径r。
				double rr1 = Math.pow((x - c1_x0), 2) + Math.pow((y - c1_y0), 2);
				double rr2 = Math.pow((x - c2_x0), 2) + Math.pow((y - c2_y0), 2);

				int rgb = sourceImg.getRGB(x, y);
				if (x >= blockX0 && x < (blockX0 + blockWidth) && y >= blockY0 && y < (blockY0 + blockHeight) && rr2 >= rr) { // 在矩形块内
					// 设置块范围内的RGB
					setInternal(blockImg, primaryImg, blockX0, blockY0, blockWidth, blockHeight, x, y, rgb);
				} else if (rr1 <= rr) { // 在圆内
					// 设置块范围内的RGB
					setInternal(blockImg, primaryImg, blockX0, blockY0, blockWidth, blockHeight, x, y, rgb);
				} else {
					// 剩余位置设置成透明
					setExternal(blockImg, primaryImg, blockX0, blockY0, blockWidth, blockHeight, x, y, rgb);
				}
			}
		}
	}

	/**
	 * Set circle internal image RGB.
	 * 
	 * @param blockImg
	 * @param primaryImg
	 * @param blockX0
	 * @param blockY0
	 * @param blockWidth
	 * @param blockHeight
	 * @param x
	 * @param y
	 * @param rgb
	 */
	private void setInternal(BufferedImage blockImg, BufferedImage primaryImg, int blockX0, int blockY0, int blockWidth,
			int blockHeight, int x, int y, int rgb) {
		blockImg.setRGB(x, y, rgb);

		// 原图设置变灰
		int r = (0xff & rgb);
		int g = (0xff & (rgb >> 8));
		int b = (0xff & (rgb >> 16));
		rgb = r + (g << 8) + (b << 16) + (100 << 24);
		// rgb = r + (g << 8) + (b << 16); // 亮一些
		if (!isFuzzyBorder(blockImg, primaryImg, blockX0, blockY0, blockWidth, blockHeight, x, y, rgb)) {
			primaryImg.setRGB(x, y, rgb);
		}
	}

	/**
	 * Set circle external image RGB.
	 * 
	 * @param blockImg
	 * @param primaryImg
	 * @param blockX0
	 * @param blockY0
	 * @param blockWidth
	 * @param blockHeight
	 * @param x
	 * @param y
	 * @param rgb
	 */
	private void setExternal(BufferedImage blockImg, BufferedImage primaryImg, int blockX0, int blockY0, int blockWidth,
			int blockHeight, int x, int y, int rgb) {
		blockImg.setRGB(x, y, 0x00ffffff);

		// 检查是否边界(需高斯模糊)
		if (isFuzzyBorder(blockImg, primaryImg, blockX0, blockY0, blockWidth, blockHeight, x, y, rgb)) {
			// 抠图区域高斯模糊
			// int[][] martrix = new int[3][3];
			// int[] pixels = new int[9];
			// readPixel(primaryImg, x, y, pixels);
			// fillMatrix(martrix, pixels);
			// int rgb0 = avgMatrix(martrix);
			// primaryImg.setRGB( x, y, rgb0);
//			primaryImg.setRGB(x, y, Color.white.getRGB());
		} else {
//			primaryImg.setRGB(x, y, rgb);
		}
		primaryImg.setRGB(x, y, rgb);

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
	 * 检查是否带像素和无像素的界点，判断该点是不是临界轮廓点, 若是则需设置该坐标像素是白色
	 * 
	 * @param blockImg
	 * @param primaryImg
	 * @param blockX0
	 * @param blockY0
	 * @param blockWidth
	 * @param blockHeight
	 * @param x
	 * @param y
	 * @param rgb
	 * @return
	 */
	private boolean isFuzzyBorder(BufferedImage blockImg, BufferedImage primaryImg, int blockX0, int blockY0, int blockWidth,
			int blockHeight, int x, int y, int rgb) {
		int offset = 3;
		// Right
		if (x >= (blockX0 + blockWidth - offset) && x <= (blockX0 + blockWidth) && y >= blockY0 && y <= (blockY0 + blockHeight)) {
			return true;
		}
		// Bottom
		if (y >= (blockY0 + blockHeight - offset) && y <= (blockY0 + blockHeight) && x >= blockX0
				&& x <= (blockX0 + blockWidth)) {
			return true;
		}
		// Top
		if (y <= (blockY0 + offset) && y >= blockY0 && x >= blockX0 && x <= (blockX0 + blockWidth)) {
			return true;
		}
		// Left
		if (x <= (blockX0 + offset) && x >= blockX0 && y >= blockY0 && y <= (blockY0 + blockHeight)) {
			return true;
		}
		return false;
	}

	/**
	 * @param primaryImg
	 *            原图
	 * @param soruceImg
	 *            模板图
	 * @param blockImg
	 *            新抠出的小图
	 * @param blockX0
	 *            随机扣取坐标X
	 * @param blockY0
	 *            随机扣取坐标y
	 * @throws Exception
	 */
	private void cutByTemplate(BufferedImage primaryImg, BufferedImage soruceImg, BufferedImage blockImg, int blockX0,
			int blockY0) {
		// 临时数组遍历用于高斯模糊存周边像素值
		int[][] martrix = new int[3][3];
		int[] pixels = new int[9];

		int width = soruceImg.getWidth();
		int height = soruceImg.getHeight();
		// 模板图像宽度
		for (int x = 0; x < width; x++) {
			// 模板图片高度
			for (int y = 0; y < height; y++) {
				// 结束边界
				if (x == (width - 1) || y == (height - 1)) {
					continue;
				}

				// 如果模板图像当前像素点不是透明色 copy源文件信息到目标图片中
				int rgb = soruceImg.getRGB(x, y);
				if (rgb < 0) {
					blockImg.setRGB(x, y, primaryImg.getRGB(blockX0 + x, blockY0 + y));
					// 抠图区域高斯模糊
					readPixel(primaryImg, blockX0 + x, blockY0 + y, pixels);
					fillMatrix(martrix, pixels);
					primaryImg.setRGB(blockX0 + x, blockY0 + y, avgMatrix(martrix));
				}

				int rightRgb = soruceImg.getRGB(x + 1, y);
				int downRgb = soruceImg.getRGB(x, y + 1);
				// 描边处理，,取带像素和无像素的界点，判断该点是不是临界轮廓点,如果是设置该坐标像素是白色
				if ((rgb >= 0 && rightRgb < 0) || (rgb < 0 && rightRgb >= 0) || (rgb >= 0 && downRgb < 0)
						|| (rgb < 0 && downRgb >= 0)) {
					blockImg.setRGB(x, y, Color.white.getRGB());
					primaryImg.setRGB(blockX0 + x, blockY0 + y, Color.white.getRGB());
				}
			}
		}
	}

	private void readPixel(BufferedImage img, int x, int y, int[] pixels) {
		int xStart = x - 1;
		int yStart = y - 1;
		int current = 0;
		for (int i = xStart; i < 3 + xStart; i++) {
			for (int j = yStart; j < 3 + yStart; j++) {
				int tx = i;
				if (tx < 0) {
					tx = -tx;

				} else if (tx >= img.getWidth()) {
					tx = x;
				}
				int ty = j;
				if (ty < 0) {
					ty = -ty;
				} else if (ty >= img.getHeight()) {
					ty = y;
				}
				pixels[current++] = img.getRGB(tx, ty);
			}
		}
	}

	private void fillMatrix(int[][] matrix, int[] values) {
		int filled = 0;
		for (int i = 0; i < matrix.length; i++) {
			int[] x = matrix[i];
			for (int j = 0; j < x.length; j++) {
				x[j] = values[filled++];
			}
		}
	}

	private int avgMatrix(int[][] matrix) {
		int r = 0;
		int g = 0;
		int b = 0;
		for (int i = 0; i < matrix.length; i++) {
			int[] x = matrix[i];
			for (int j = 0; j < x.length; j++) {
				if (j == 1) {
					continue;
				}
				Color c = new Color(x[j]);
				r += c.getRed();
				g += c.getGreen();
				b += c.getBlue();
			}
		}
		return new Color(r / 8, g / 8, b / 8).getRGB();
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