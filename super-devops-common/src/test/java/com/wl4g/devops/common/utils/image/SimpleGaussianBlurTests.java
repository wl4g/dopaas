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
package com.wl4g.devops.common.utils.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 简单高斯模糊算法
 *
 * @param args
 * @throws IOException
 *             [参数说明]
 *
 * @return void [返回类型说明] @exception throws [违例类型] [违例说明] @see [类、类#方法、类#成员]
 */
public class SimpleGaussianBlurTests {
	public static void main(String[] args) throws IOException {
		BufferedImage img = ImageIO.read(new File("d:\\src.jpg")); // 将这个图片拷贝到你项目根目录下
		System.out.println(img);
		int height = img.getHeight();
		int width = img.getWidth();
		for (int x = 20; x < width - 50; x++)
			for (int y = 20; y < height - 50; y++) {
				img.setRGB(x, y, getGaussainBlurRGB(img, x, y));
			}
		ImageIO.write(img, "jpeg", new File("d:\\dst.jpg"));
	}

	private static int getGaussainBlurRGB(BufferedImage img, int x, int y) {
		int[][] matrix = new int[3][3];
		int[] pixels = new int[9];
		readPixel(img, x, y, pixels);
		fillMatrix(matrix, pixels);
		return avgMatrix(matrix);
	}

	private static void readPixel(BufferedImage img, int x, int y, int[] pixels) {
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

	private static void fillMatrix(int[][] matrix, int[] values) {
		int filled = 0;
		for (int i = 0; i < matrix.length; i++) {
			int[] x = matrix[i];
			for (int j = 0; j < x.length; j++) {
				x[j] = values[filled++];
			}
		}
	}

	private static int avgMatrix(int[][] matrix) {
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

}