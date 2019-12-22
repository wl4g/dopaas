package cn.edu.njupt.utils.opencvUtils.RemoveNoiseUtils;

import cn.edu.njupt.configure.SystemVariables;
import cn.edu.njupt.utils.opencvUtils.BinaryUtils.BinaryUtils;
import cn.edu.njupt.utils.opencvUtils.GeneralUtils.GeneralUtils;
import cn.edu.njupt.utils.opencvUtils.GrayUtils.GrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * 降噪工具类
 */
public class RemoveNoiseUtils {

    /**
     * 8邻域降噪，又有点像9宫格降噪;即如果9宫格中心被异色包围，则同化 作用：降噪(默认白底黑字)
     *
     * @param src
     *            Mat矩阵对象
     * @param pNum
     *            阀值 默认取1即可
     * @return
     */
    public static Mat eghitRemoveNoise(Mat src, int pNum) {
        int i, j, m, n, nValue, nCount;
        int width = GeneralUtils.getImgWidth(src), height = GeneralUtils.getImgHeight(src);

        // 如果一个点的周围都是白色的，自己确实黑色的，同化
        for (j = 1; j < height - 1; j++) {
            for (i = 1; i < width - 1; i++) {
                nValue = GeneralUtils.getPixel(src, j, i);
                if (nValue == 0) {
                    nCount = 0;
                    // 比较(j , i)周围的9宫格，如果周围都是白色，同化
                    for (m = j - 1; m <= j + 1; m++) {
                        for (n = i - 1; n <= i + 1; n++) {
                            if (GeneralUtils.getPixel(src, m, n) == 0) {
                                nCount++;
                            }
                        }
                    }
                    if (nCount <= pNum) {
                        // 周围黑色点的个数小于阀值pNum,把自己设置成白色
                        GeneralUtils.setPixel(src, j, i, GeneralUtils.getWHITE());
                    }
                } else {
                    nCount = 0;
                    // 比较(j , i)周围的9宫格，如果周围都是黑色，同化
                    for (m = j - 1; m <= j + 1; m++) {
                        for (n = i - 1; n <= i + 1; n++) {
                            if (GeneralUtils.getPixel(src, m, n) == 0) {
                                nCount++;
                            }
                        }
                    }
                    if (nCount >= 8 - pNum) {
                        // 周围黑色点的个数大于等于(8 - pNum),把自己设置成黑色
                        GeneralUtils.setPixel(src, j, i, GeneralUtils.getBLACK());
                    }
                }
            }
        }
        return src;
    }

    /**
     * 连通域降噪 作用：降噪(默认白底黑字)
     *
     * @param src
     *            Mat矩阵对象
     * @param pArea
     *            阀值 默认取1即可
     * @return
     */
    public static Mat connectedRemoveNoise(Mat src, double pArea) {
        int i, j, color = 1;
        int width = GeneralUtils.getImgWidth(src), height = GeneralUtils.getImgHeight(src);

        Result result = floodFill(new Result(src) , pArea);
        src = result.mat;

        // 二值化
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                if (GeneralUtils.getPixel(src, j, i) < GeneralUtils.getWHITE()) {
                    GeneralUtils.setPixel(src, j, i, GeneralUtils.getBLACK());
                }
            }
        }

        if(result.status == false && result.count <= 100){
            connectedRemoveNoise(src , pArea);
        }

        return src;
    }


    /**
     * 连通域填充颜色
     * @param result
     * @return
     */
    public static Result floodFill(Result result , double pArea){
        Mat src = result.mat;
        if(src == null){
            return null;
        }
        int i, j, color = 1;
        int width = GeneralUtils.getImgWidth(src), height = GeneralUtils.getImgHeight(src);

        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                if (GeneralUtils.getPixel(src, j, i) == GeneralUtils.getBLACK()) {
                    // 用不同的颜色填充连接区域中的每个黑色点
                    // floodFill就是把与点(i , j)的所有相连通的区域都涂上color颜色
                    int area = Imgproc.floodFill(src, new Mat(), new Point(i, j), new Scalar(color));
                    if(area <= pArea) {
                        Imgproc.floodFill(src, new Mat(), new Point(i, j), new Scalar(255));
                    }else{
                        color++;
                    }
                    if(color == 255){
                        result.status = false;//连通域还没填充完
                        result.mat = src;
                        result.count = result.count + 1;
                        return result;
                    }
                }
            }
        }
        result.mat = src;
        result.status = true;//表示所有的连通域都已填充完毕
        return result;
    }

    /**
     * 连通域填充颜色
     * @param src
     * @param type [true,false] true表示把小于阀值的填充为白色，false相反
     * @return
     */
    public static Mat floodFill(Mat src ,double pArea , boolean type){
        if(src == null){
            return null;
        }
        int i, j, color = 1;
        int width = GeneralUtils.getImgWidth(src), height = GeneralUtils.getImgHeight(src);

        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                if (GeneralUtils.getPixel(src, j, i) == GeneralUtils.getBLACK()) {
                    // 用不同的颜色填充连接区域中的每个黑色点
                    // floodFill就是把与点(i , j)的所有相连通的区域都涂上color颜色
                    int area = Imgproc.floodFill(src, new Mat(), new Point(i, j), new Scalar(color));
                    if(type){
                        if(area <= pArea) {
                            Imgproc.floodFill(src, new Mat(), new Point(i, j), new Scalar(255));
                        }
                    }else{
                        if(area > pArea){
                            Imgproc.floodFill(src, new Mat(), new Point(i, j), new Scalar(255));
                        }
                    }
                    color++;
                }
            }
        }
        return src;
    }

    //只填充最大的连通域
    public static Mat findMaxConnected(Mat src ){
        return findMaxConnected(src , 127);
    }

    //只填充最大的连通域
    public static Mat findMaxConnected(Mat src , int color){
        int i, j;
        int width = GeneralUtils.getImgWidth(src), height = GeneralUtils.getImgHeight(src);
        int maxArea = Integer.MAX_VALUE;
        int maxI = -1 , maxJ = -1;
        int gColor = 200 != color ? 200 : 127;
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                if (GeneralUtils.getPixel(src, j, i) == GeneralUtils.getBLACK()) {
                    // 用不同的颜色填充连接区域中的每个黑色点
                    // floodFill就是把与点(i , j)的所有相连通的区域都涂上color颜色
                    int area = Imgproc.floodFill(src, new Mat(), new Point(i, j), new Scalar(gColor));
                    if(maxI != -1 && maxJ != -1){
                        if(area > maxArea){
                            maxArea = area;
//                            Imgproc.floodFill(src, new Mat(), new Point(maxI, maxJ), new Scalar(255));
                            maxI = i;
                            maxJ = j;
                        }else{
//                            Imgproc.floodFill(src, new Mat(), new Point(i, j), new Scalar(255));
                        }
                    }else{
                        maxI = i;
                        maxJ = j;
                        maxArea = area;
                    }
                }
            }
        }

        Imgproc.floodFill(src, new Mat(), new Point(maxI, maxJ), new Scalar(color));
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                if (GeneralUtils.getPixel(src, j, i) == gColor) {
                    GeneralUtils.setPixel(src , j , i , GeneralUtils.getBLACK());
                }
            }
        }
        return src;
    }

    private static class Result{
        Mat mat;//Mat对象
        boolean status;//是否填充完毕
        int count;//记录填充的次数
        int height;//记录上一次填充的height位置
        int width;//记录上一次填充的width位置

        public Result(){}

        public Result(Mat src){
            this.mat = src;
        }
    }

    @Before
    public void init(){
        System.load(SystemVariables.loadOpencvSystemFile());
    }

    @Test
    public void testPre(){
        for(int i = 1 ; i <= 6 ; i++){
            String imgPath = "C:/Users/X240/Desktop/opencv/web/binary/b-"+i+".jpg";
            String destPath = "C:/Users/X240/Desktop/opencv/web/binary/";

            Mat src = GeneralUtils.matFactory(imgPath);

            src = GrayUtils.grayColByAdapThreshold(src);

            src = BinaryUtils.binaryzation(src);

            src = RemoveNoiseUtils.connectedRemoveNoise(src , 50);

            GeneralUtils.saveImg(src , destPath + "noise-"+i+".jpg");
        }

    }
}
