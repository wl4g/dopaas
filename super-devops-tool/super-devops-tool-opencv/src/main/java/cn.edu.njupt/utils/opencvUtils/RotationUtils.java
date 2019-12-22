package cn.edu.njupt.utils.opencvUtils.RotationUtils;


import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

/**
 * 旋转矩形工具类
 */
public class RotationUtils {

    /**
     * 旋转矩形
     * 返回旋转后的Mat
     * @param mat
     *            mat矩阵
     * @param rect
     *            矩形
     * @return
     */
    public static Mat rotation(Mat mat, RotatedRect rect) {
        if(rect.angle == 0.0){
            return  mat;
        }

        // 获取矩形的四个顶点
        Point[] rectPoint = new Point[4];
        rect.points(rectPoint);

        int a = 0;

        if (mat.height() > mat.width()) {
            a = 90;
        }

        double angle = rect.angle + 90 + a;

        System.out.println("angle: " + angle);

        Point center = rect.center;

        Mat CorrectImg = new Mat(mat.size(), mat.type());

        mat.copyTo(CorrectImg);

        // 得到旋转矩阵算子
        Mat matrix = Imgproc.getRotationMatrix2D(center, angle, 0.8);


        Imgproc.warpAffine(CorrectImg, CorrectImg, matrix, CorrectImg.size(), Imgproc.INTER_LINEAR , 0 , new Scalar(0 , 0 , 0));

        return CorrectImg;
    }

    /**
     * 垂直旋转90度
     * @param mat
     * @return
     */
    public static Mat rotation(Mat mat) {
        int len , wid;
        if(mat.width() > mat.height()){
            len = mat.width();
            wid = mat.height();
        }else {
            len = mat.height();
            wid = mat.width();
        }

        // 得到旋转矩阵算子
        Mat matrix = Imgproc.getRotationMatrix2D(new Point(len / 2 , len / 2), 90, 1);

        Mat CorrectImg = new Mat(new Size(len, len), mat.type());

        Imgproc.warpAffine(mat, CorrectImg, matrix, CorrectImg.size(), Imgproc.INTER_LINEAR , 0 , new Scalar(0 , 0 , 0));

        int extend = 18;

        Mat temp = new Mat(CorrectImg, new Rect(0 , len - wid + extend , len , wid - 2 * extend));

        return temp;
    }

}
