package com.wl4g.devops.tool.opencv;

import java.io.File;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import com.wl4g.devops.tool.common.resource.resolver.PathPatternResourceMatchingResolver;
import com.wl4g.devops.tool.opencv.library.OpenCvNativeLibraryLoader;

public class VideoFaceTests {

	static {
		OpenCvNativeLibraryLoader.loadLibrarys();
	}

	public static void main(String[] args) throws IOException {
		videoFace();
	}

	/**
	 * OpenCV-4.0.0 实时人脸识fF别
	 * 
	 * @return: void
	 * @throws IOException
	 * @date: 2019年5月7日12:16:55
	 */
	public static void videoFace() throws IOException {
		VideoCapture capture = new VideoCapture(0);
		Mat image = new Mat();
		int index = 0;
		if (capture.isOpened()) {
			while (true) {
				capture.read(image);
				HighGui.imshow("实时人脸识别", getFace(image));
				index = HighGui.waitKey(1);
				if (index == 27) {
					break;
				}
			}
		}
		return;
	}

	/**
	 * OpenCV-4.0.0 人脸识别
	 * 
	 * @date: 2019年5月7日12:16:55
	 * @param image
	 *            待处理Mat图片(视频中的某一帧)
	 * @return 处理后的图片
	 * @throws IOException
	 */
	public static Mat getFace(Mat image) throws IOException {
		// 1 读取OpenCV自带的人脸识别特征XML文件
		File faceFile = new PathPatternResourceMatchingResolver().getResource("opencv/data/haarcascade_frontalface_alt.xml")
				.getFile();
		CascadeClassifier facebook = new CascadeClassifier(faceFile.getAbsolutePath());
		// 2 特征匹配类
		MatOfRect face = new MatOfRect();
		// 3 特征匹配
		facebook.detectMultiScale(image, face);
		Rect[] rects = face.toArray();
		System.out.println("匹配到 " + rects.length + " 个人脸");
		// 4 为每张识别到的人脸画一个圈
		for (int i = 0; i < rects.length; i++) {
			Imgproc.rectangle(image, new Point(rects[i].x, rects[i].y),
					new Point(rects[i].x + rects[i].width, rects[i].y + rects[i].height), new Scalar(0, 255, 0));
			Imgproc.putText(image, "Human", new Point(rects[i].x, rects[i].y), Imgproc.FONT_HERSHEY_SCRIPT_SIMPLEX, 1.0,
					new Scalar(0, 255, 0), 1, Imgproc.LINE_AA, false);
			// Mat dst=image.clone();
			// Imgproc.resize(image, image, new Size(300,300));
		}
		return image;
	}

}