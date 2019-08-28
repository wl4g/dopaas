/* 
 * 根据源图片，剪切出两张图 
 * */
package com.wl4g.devops.iam.captcha.slider;

import org.apache.commons.lang3.RandomUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

public class ImageUtil {

	//原图最大长度
	private static int maxWidth = 300;
	//原图最大宽度
	private static int maxHeight = 200;

	//滑块宽度
	private static int blockWidth = 46;
	//滑块高度
	private static int blockHeight = 46;
	// 固定圆半径为5
	private static int c_r = 20;




	public static void cut(int x, int y, int width, int height, String srcpath, String subpath) throws IOException {// 裁剪方法
		FileInputStream is = null;
		ImageInputStream iis = null;
		try {
			is = new FileInputStream(srcpath); // 读取原始图片
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg"); // ImageReader声称能够解码指定格式
			ImageReader reader = it.next();
			iis = ImageIO.createImageInputStream(is); // 获取图片流
			reader.setInput(iis, true); // 将iis标记为true（只向前搜索）意味着包含在输入源中的图像将只按顺序读取
			ImageReadParam param = reader.getDefaultReadParam(); // 指定如何在输入时从
																	// Java
																	// Image
																	// I/O框架的上下文中的流转换一幅图像或一组图像
			Rectangle rect = new Rectangle(x, y, width, height); // 定义空间中的一个区域
			param.setSourceRegion(rect); // 提供一个 BufferedImage，将其用作解码像素数据的目标。
			BufferedImage bi = reader.read(0, param); // 读取索引imageIndex指定的对象
			ImageIO.write(bi, "jpg", new File(subpath)); // 保存新图片
		} finally {
			if (is != null)
				is.close();
			if (iis != null)
				iis.close();
		}
	}

	private static void cutByTemplate2(BufferedImage oriImage, BufferedImage newSrc, BufferedImage newSrc2, int x, int y, int width,
			int height) {
		double rr = Math.pow(c_r, 2);// r平方
		// 圆心的位置
		Random rand = new Random();
		int c_a = rand.nextInt(width - 2 * c_r) + (x + c_r);// x+c_r+10;//圆心x坐标必须在(x+r,x+with-r)范围内
		// System.out.println(c_a);
		int c_b = y;

		// 第二个圆（排除圆内的点）
		Random rand2 = new Random();
		int c2_a = x;
		int c2_b = rand2.nextInt(height - 2 * c_r) + (y + c_r);// y+c_r+50;//圆心y坐标必须在(y+r,y+height-r)范围内

		// System.out.println(oriImage.getWidth()+" "+oriImage.getHeight());
		for (int i = 0; i < oriImage.getWidth(); i++) {
			for (int j = 0; j < oriImage.getHeight(); j++) {
				// (x-a)²+(y-b)²=r²中，有三个参数a、b、r，即圆心坐标为(a，b)，半径r。
				double f = Math.pow((i - c_a), 2) + Math.pow((j - c_b), 2);
				double f2 = Math.pow((i - c2_a), 2) + Math.pow((j - c2_b), 2);

				int rgb = oriImage.getRGB(i, j);
				if (i >= x && i < (x + width) && j >= y && j < (y + height) && f2 >= rr) {// 在矩形内
					// 块范围内的值
					in(newSrc, newSrc2, i, j, rgb);
				} else if (f <= rr) {
					// 在圆内
					in(newSrc, newSrc2, i, j, rgb);
				} else {
					// 剩余位置设置成透明
					out(newSrc, newSrc2, i, j, rgb);
				}
			}
		}
	}

	private static void in(BufferedImage newSrc, BufferedImage newSrc2, int i, int j, int rgb) {
		newSrc.setRGB(i, j, rgb);
		// 原图设置变灰
		int r = (0xff & rgb);
		int g = (0xff & (rgb >> 8));
		int b = (0xff & (rgb >> 16));
		rgb = r + (g << 8) + (b << 16) + (100 << 24);
		// rgb = r + (g << 8) + (b << 16);
		newSrc2.setRGB(i, j, rgb);
	}

	private static void out(BufferedImage newSrc, BufferedImage newSrc2, int i, int j, int rgb) {
		newSrc.setRGB(i, j, 0x00ffffff);
		newSrc2.setRGB(i, j, rgb);
	}


	public static void cutImage(String imgPath) throws Exception{
		BufferedImage originalImg = ImageIO.read(new File(imgPath));
		//原图宽度
		int width = originalImg.getWidth();
		//原图高度
		int height = originalImg.getHeight();
		//TODO 校验原图
		if(width>maxWidth||height>maxHeight){
			throw new Exception("image is too big");
		}

		// 移动图
		BufferedImage newSrc = new BufferedImage(originalImg.getWidth(), originalImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);// 新建一个类型支持透明的BufferedImage
		// 对比图
		BufferedImage newSrc2 = new BufferedImage(originalImg.getWidth(), originalImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);// 新建一个类型支持透明的BufferedImage

		int x = RandomUtils.nextInt(0, width - blockWidth);
		int y = RandomUtils.nextInt(0, height - blockHeight);
		cutByTemplate2(originalImg, newSrc, newSrc2, x, y, blockWidth, blockHeight);// 图片大小是固定，位置是随机

		newSrc = newSrc.getSubimage(x,y-20,blockWidth,blockHeight+20);

		// 生成移动图
		ImageIO.write(newSrc, "png", new File("/Users/vjay/Downloads/1.png"));
		// 生成对比图
		ImageIO.write(newSrc2, "png", new File("/Users/vjay/Downloads/2.png"));

	}

	public static void main(String[] args) throws Exception {

		// 图片必须是png格式，因为需要做透明背景
		// 原图
		BufferedImage src = ImageIO.read(new File("/Users/vjay/Downloads/4.png"));
		// 移动图
		BufferedImage newSrc = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);// 新建一个类型支持透明的BufferedImage
		// 对比图
		BufferedImage newSrc2 = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);// 新建一个类型支持透明的BufferedImage

		// 抠块的大小
		int blockWidth = 150;
		int blockHeight = 150;

		int x = RandomUtils.nextInt(0, src.getWidth() - blockWidth);
		int y = RandomUtils.nextInt(0, src.getHeight() - blockHeight);
		cutByTemplate2(src, newSrc, newSrc2, x, y, blockWidth, blockHeight);// 图片大小是固定，位置是随机

		newSrc = newSrc.getSubimage(x,y-20,blockWidth,blockHeight+20);

		// 生成移动图
		ImageIO.write(newSrc, "png", new File("/Users/vjay/Downloads/1.png"));
		// 生成对比图
		ImageIO.write(newSrc2, "png", new File("/Users/vjay/Downloads/2.png"));
	}

}