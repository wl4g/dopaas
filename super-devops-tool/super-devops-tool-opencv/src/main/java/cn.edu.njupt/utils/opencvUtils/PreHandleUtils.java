package cn.edu.njupt.utils.opencvUtils.PreHandleUtils;

import cn.edu.njupt.configure.SystemVariables;
import cn.edu.njupt.utils.opencvUtils.ContoursUtils.ContoursUtils;
import cn.edu.njupt.utils.opencvUtils.GeneralUtils.GeneralUtils;
import cn.edu.njupt.utils.opencvUtils.GrayUtils.GrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.*;


/**
 * 预处理类
 */
public class PreHandleUtils {


    /**
     * 把矫正后的图像切割出来
     *
     * @param src
     *            图像矫正后的Mat矩阵
     */
    public static Mat cutRect(Mat src , RotatedRect rect) {
        Rect r = rect.boundingRect();

        int extend = r.width > r.height ? r.height / 64 : r.width / 64;

        int x = r.x - extend < 0 ? 0 : r.x - extend;
        int y = r.y - extend < 0 ? 0 : r.y - extend;
        int width = x + r.width + 2 * extend >= src.width() ? src.width() - x : r.width + 2 * extend;
        int height = y + r.height + 2 * extend >= src.height() ? src.height() - y : r.height + 2 * extend;

//        System.out.println(x + width > src.width());
//        System.out.println(y + height > src.height());

        Mat temp = new Mat(src, new Rect(x , y , width , height));

//        System.out.println("............................");
        return temp;
    }

    /**
     * 截取出图像中的有效片段
     * @param src
     * @return
     */
    public static Mat cutFragment(Mat src){
        //灰度化
        Mat grayMat = GrayUtils.grayColByPartAdapThreshold(src);

        //获取最大矩形
        RotatedRect maxRotatedRect= ContoursUtils.findMaxRect(GeneralUtils.canny(grayMat));

        //裁剪有效区域
        Mat dst = cutRect(grayMat , maxRotatedRect);


        return dst;
    }

    /**
     * 对图像进行预处理
     * 旋转变换
     * @param src
     */
    public static Mat preHandleUtils(Mat src){
        return preHandleUtils(src , null);
    }

    /**
     * 对图像进行预处理
     * 旋转变换
     * @param src
     */
    public static Mat preHandleUtils(Mat src , String path){
        //截取出图像中的有效片段
        src = cutFragment(src);

        /**
         * 1、还未作透视变换
         */

        //保存图像
        if(path != null && !"".equals(path)){
            GeneralUtils.saveImg(src , path);
        }

        return src;
    }

    @Before
    public void init(){
        System.load(SystemVariables.loadOpencvSystemFile());
    }

    @Test
    public void testPre(){
        for(int i = 1 ; i <= 6 ; i++){
            String imgPath = "C:/Users/X240/Desktop/opencv/web/p"+i+".jpg";
            String destPath = "C:/Users/X240/Desktop/opencv/web/";

            Mat src = GeneralUtils.matFactory(imgPath);

            src = preHandleUtils(src);

            GeneralUtils.saveImg(src , destPath + "b-"+i+".jpg");
        }

    }
}
