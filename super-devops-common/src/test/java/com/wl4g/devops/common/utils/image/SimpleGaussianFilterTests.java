package com.wl4g.devops.common.utils.image;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * 高斯模糊Tests
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月28日
 * @since
 */
public class SimpleGaussianFilterTests {

	public static void main(String[] args) throws Exception {
		test1(args);
	}

	public static void test1(String[] args) throws Exception {
		File file = new File("f:\\src.jpg");
		BufferedImage b1 = ImageIO.read(file);
		// 模糊
		SimpleGaussianFilter filter = new SimpleGaussianFilter(10);
		BufferedImage blurredImage = filter.filter(b1,
				new BufferedImage(b1.getWidth(), b1.getHeight(), BufferedImage.TYPE_INT_ARGB));
		// 保存处理后的图
		ImageIO.write(blurredImage, "png", file);

		// 打开文件
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
			Desktop.getDesktop().open(file);
	}

}
