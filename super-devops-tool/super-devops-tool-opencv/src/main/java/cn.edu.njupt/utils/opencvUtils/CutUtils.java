package cn.edu.njupt.utils.opencvUtils.CutUtils;

import cn.edu.njupt.configure.SystemVariables;
import cn.edu.njupt.utils.opencvUtils.BinaryUtils.BinaryUtils;
import cn.edu.njupt.utils.opencvUtils.ContoursUtils.ContoursUtils;
import cn.edu.njupt.utils.opencvUtils.GeneralUtils.GeneralUtils;
import cn.edu.njupt.utils.opencvUtils.GrayUtils.GrayUtils;
import cn.edu.njupt.utils.opencvUtils.PaintUtils.PaintUtils;
import cn.edu.njupt.utils.opencvUtils.RemoveNoiseUtils.RemoveNoiseUtils;
import cn.edu.njupt.utils.opencvUtils.RotationUtils.RotationUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;


/**
 * 切割工具类
 */
public class CutUtils {

    /**
     * 切割
     * @param src
     * @return
     */
    public static List<Mat> cutUtils(Mat src){
        if(src.channels() != 1){
            src = GrayUtils.grayColByPartAdapThreshold(src);
            src = BinaryUtils.binaryzation(src);
        }

        if(src.height() > src.width()){
            src = RotationUtils.rotation(src);
        }

        //求最大轮廓的点集
        Point[] points = ContoursUtils.useApproxPolyDPFindPoints(GeneralUtils.canny(src));

        src = PaintUtils.paintCircle(src , points , 10 , new Scalar(255 , 255 , 255));

        //清除最大的连通域
        src = RemoveNoiseUtils.findMaxConnected(src , 255);

        //降噪
        src = RemoveNoiseUtils.connectedRemoveNoise(src , 100);

        //水平切割
        List<Mat> xList = cutUtilsX(src);

        //垂直切割
        List<Integer> yPoint = cutUtilsY(xList.get(0));

        //最终的结果集
        List<Mat> result = new ArrayList<>();

        int xlen = xList.size();
        for(int i = 0 ; i < xlen ; i++){
            if(xlen > 1 && i == 0){
                continue;
            }
            for(int j = 1 ; j < yPoint.size() ; j++){
                Mat temp = new Mat(xList.get(i), new Rect(yPoint.get(j - 1) , 0 , yPoint.get(j) - yPoint.get(j - 1) , xList.get(i).height()));
                result.add(temp);
            }

        }

        return result;

    }

    /**
     * 水平切割
     * @param src
     * @return
     */
    private static List<Mat> cutUtilsX(Mat src){
        List<Mat> result = new ArrayList<>();

        int width = GeneralUtils.getImgWidth(src), height = GeneralUtils.getImgHeight(src);
        int[] xNum = new int[height];
        int i , j , value;
        // 统计每行每列的黑色像素值
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                value = GeneralUtils.getPixel(src, j, i);
                if (value == GeneralUtils.getBLACK()) {
                    xNum[j]++;
                }
            }
        }

        int xThreshold = getThreshold(xNum);

        xNum = updateArray(xNum , xThreshold);

        int threshold = 10;
        List<Integer> cutX = countCutPoint(xNum , threshold);

        //确定水平切割点
        int cutXpoint = -1;
        if(cutX.size() > 1){
            cutXpoint = cutX.get(1);
        }else if(cutX.size() > 0){
            cutXpoint = cutX.get(0);
        }

        if(cutXpoint != -1 && cutXpoint != 0){
            result.add(new Mat(src, new Rect(0 , 0  , width , cutXpoint)));
            result.add(new Mat(src , new Rect(0 , cutXpoint , width , height - cutXpoint)));
        }else{
            result.add(src);
        }

        return result;
    }

    /**
     * 垂直切割 返回切割点
     * @param src
     * @return
     */
    private static List<Integer> cutUtilsY(Mat src){

        int width = GeneralUtils.getImgWidth(src), height = GeneralUtils.getImgHeight(src);
        int[] yNum = new int[width];
        int i , j , value;
        // 统计每行每列的黑色像素值
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                value = GeneralUtils.getPixel(src, j, i);
                if (value == GeneralUtils.getBLACK()) {
                    yNum[i]++;
                }
            }
        }

        int yThreshold = getThreshold(yNum);

        yNum = updateArray(yNum , yThreshold);

        int threshold = 20;
        List<Integer> cutY = countCutPoint(yNum , threshold);
        List<Integer> cutBack = new ArrayList<>();

        int extend = - 1;
        //优化cutY
        for(i = 1 ; i < cutY.size() ; i++){
            if(i == 1){
                extend = cutY.get(i) - cutY.get(0);
                cutBack.add(cutY.get(0));
                cutBack.add(cutY.get(1));
            }else{
                int distance = cutY.get(i) - cutBack.get(cutBack.size() - 1);
//                System.out.println("extend * 0.65: " + extend * 0.65);
//                System.out.println("distance: " + distance);
                if(distance > extend * 0.65){
                    cutBack.add(cutY.get(i));
                }
            }
        }

        cutBack.add(width);
        return cutBack;
    }

    /**
     * 统计切割点
     * @param arr
     * @param threshold
     * @return
     */
    private static List<Integer> countCutPoint(int[] arr, int threshold) {
        List<Integer> cut = new ArrayList<>();
        int pre = -1 , end = -1;
        int count = 0;
        for(int k = 0 ; k < arr.length ; k++){
            if(arr[k] == 0){
                if(pre != -1){
                    end = k;
                }else{
                    pre = k;
                }
                count++;
            }else {
                if(count >= threshold){
                    cut.add((end + pre) / 2);
                }
                pre = end = -1;
                count = 0;
            }
        }

        return cut;
    }

    /**
     * 根据threshold更新数组
     * @param arr
     * @param threshold
     * @return
     */
    private static int[] updateArray(int[] arr , int threshold){
        for(int i = 0 ; i < arr.length ; i++){
            if(arr[i] < threshold){
                arr[i] = 0;
            }
        }
        return arr;
    }

    /**
     * 获取阀值
     * @param arr
     * @return
     */
    public static int getThreshold(int[] arr){
        int threshold = 0 , new_threshold = arr.length / 2;
        int sum_invalid , sum_valid , count_invalid , count_valid;
        int i , value;

        while (threshold != new_threshold){
            sum_invalid = sum_valid = count_invalid = count_valid = 0;
            for(i = 0 ; i < arr.length ; i++){
                value = arr[i];
                if(value >= new_threshold){
                    sum_valid += value;
                    count_valid++;
                }else{
                    sum_invalid += value;
                    count_invalid++;
                }
            }
            threshold = new_threshold;
            if(count_valid == 0 || count_invalid == 0){
                new_threshold = (sum_valid + sum_invalid) / (count_valid + count_invalid);
            }else{
                new_threshold = (sum_invalid / count_invalid + sum_valid / count_valid) / 2;
            }

        }

        return (int)(new_threshold * 0.25);
    }

    @Before
    public void init(){
        System.load(SystemVariables.loadOpencvSystemFile());
    }

    @Test
    public void testPre(){
        for(int i = 1 ; i <= 6 ; i++){
            String imgPath = "C:/Users/X240/Desktop/opencv/web/cut/b-"+i+".jpg";
            String destPath = "C:/Users/X240/Desktop/opencv/web/cut/cut/";

            Mat src = GeneralUtils.matFactory(imgPath);

            //灰度化
            src = GrayUtils.grayColByAdapThreshold(src);

            //二值化
            src = BinaryUtils.binaryzation(src);

            List<Mat> list = cutUtils(src);

            for(int j = 0 ; j < list.size() ; j++){
                GeneralUtils.saveImg(list.get(j) , destPath + "cut-" + i +"-"+ j +".jpg");
            }



//            CutUtils.cutUtils(src);


        }

    }


    @Test
    /**
     * 测试垂直切割
     */
    public void testCutY(){
        for(int i = 1 ; i <= 6 ; i++){
            String imgPath = "C:/Users/X240/Desktop/opencv/web/cut/cut-"+i+"-0.jpg";
            String destPath = "C:/Users/X240/Desktop/opencv/web/cut/";

            Mat src = GeneralUtils.matFactory(imgPath);

            //灰度化
            src = GrayUtils.grayColByAdapThreshold(src);

            //二值化
            src = BinaryUtils.binaryzation(src);

//            Mat list = cutUtilsY(src);

//            GeneralUtils.saveImg(list , destPath + "y" + i + ".jpg");
        }
    }
}
