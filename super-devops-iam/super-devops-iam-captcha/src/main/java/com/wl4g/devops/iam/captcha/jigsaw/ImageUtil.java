/*
 * 根据源图片，剪切出两张图
 * */
package com.wl4g.devops.iam.captcha.jigsaw;

import com.wl4g.devops.iam.captcha.jigsaw.Image.ImageInfo;
import org.apache.commons.lang3.RandomUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Random;

public class ImageUtil {

	//原图最大宽度
	private int maxWidth = 300;
	//原图最大高度
	private int maxHeight = 200;
	//原图最小宽度
	private int minWidth = 200;
	//原图最小高度
	private int minHeight = 100;

	//滑块宽度
	private int blockWidth = 46;
	//滑块高度
	private int blockHeight = 46;
	// 固定圆半径为5
	private int c_r = 10;

	//拼图在原图中的位置范围
	private int  marginLeft = 10;
	private int  marginRight = 10;
	private int  marginTop = 10;
	private int  marginBottom = 10;


	public ImageUtil() {
		//System.out.println("into");
	}

	public ImageUtil(int maxWidth, int maxHeight, int minWidth, int minHeight, int blockWidth, int blockHeight, int c_r) {
		if (maxWidth != 0) this.maxWidth = maxWidth;
		if (maxHeight != 0) this.maxHeight = maxHeight;
		if (minWidth != 0) this.minWidth = minWidth;
		if (minWidth != 0) this.minWidth = minHeight;
		if (blockWidth != 0) this.blockWidth = blockWidth;
		if (blockHeight != 0) this.blockHeight = blockHeight;
		if (blockHeight != 0) this.c_r = c_r;
	}

	//DEMO
	public static void main(String[] args) throws Exception {

		//使用默认值
		//ImageUtil imageUtil = new ImageUtil();
		//自行定义
		ImageUtil imageUtil = new ImageUtil(300,200,200,100,46,46,10);

		//从本地文件获取图片
		//ImageInfo image = imageUtil.cutImageFile("/Users/vjay/Downloads/0.jpg");
		//从网络获取图片
		Image.ImageInfo image = imageUtil.cutImageHttp("http://vps.vjay.pw/1.jpg");

		//写入 , just for test
		ImageIO.write(image.getBackImage(), "png", new File("/Users/vjay/Downloads/1.png"));
		ImageIO.write(image.getMoveImage(), "png", new File("/Users/vjay/Downloads/2.png"));
	}


	public ImageInfo cutImageFile(String imgPath) throws Exception{
		BufferedImage originalImg = ImageIO.read(new File(imgPath));
		return cut(originalImg);
	}

	public ImageInfo cutImageHttp(String url) throws Exception{
		URL url1 = new URL(url);
		BufferedImage originalImg = ImageIO.read(url1);
		return cut(originalImg);
	}

	public ImageInfo cut(BufferedImage originalImg) throws Exception{
		//原图宽度
		int width = originalImg.getWidth();
		//原图高度
		int height = originalImg.getHeight();
		//校验原图
		if(width>maxWidth||height>maxHeight){
			throw new Exception("image is too big");
		}
		if(width<minWidth||height<minHeight){
			throw new Exception("image is too small");
		}
		// 移动图
		BufferedImage newSrc = new BufferedImage(originalImg.getWidth(), originalImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);// 新建一个类型支持透明的BufferedImage
		// 对比图
		BufferedImage newSrc2 = new BufferedImage(originalImg.getWidth(), originalImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);// 新建一个类型支持透明的BufferedImage
		//截取坐标
		int x = RandomUtils.nextInt(marginLeft, width - blockWidth-marginRight);
		int y = RandomUtils.nextInt(marginTop, height - blockHeight-marginBottom);
		//生成
		cutByTemplate(originalImg, newSrc, newSrc2, x, y, blockWidth, blockHeight);// 图片大小是固定，位置是随机
		//截取
		newSrc = newSrc.getSubimage(x,y-c_r>=0?y-c_r:0,blockWidth,blockHeight+c_r);
		//封装
		ImageInfo image = new ImageInfo();
		image.setBackImage(newSrc2);
		image.setMoveImage(newSrc);
		image.setX(x);
		image.setY(y-c_r>=0?y-c_r:0);
		//System.out.println("get image x="+x+" y="+y);
		return image;
	}




	private void cutByTemplate(BufferedImage oriImage, BufferedImage newSrc, BufferedImage newSrc2, int x, int y, int width,
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

	private void in(BufferedImage newSrc, BufferedImage newSrc2, int i, int j, int rgb) {
		newSrc.setRGB(i, j, rgb);
		// 原图设置变灰
		int r = (0xff & rgb);
		int g = (0xff & (rgb >> 8));
		int b = (0xff & (rgb >> 16));
		rgb = r + (g << 8) + (b << 16) + (100 << 24);
		// rgb = r + (g << 8) + (b << 16);
		newSrc2.setRGB(i, j, rgb);
	}

	private void out(BufferedImage newSrc, BufferedImage newSrc2, int i, int j, int rgb) {
		newSrc.setRGB(i, j, 0x00ffffff);
		newSrc2.setRGB(i, j, rgb);
	}


}