package cn.edu.njupt.utils.opencvUtils;

import cn.edu.njupt.configure.SystemVariables;
import cn.edu.njupt.utils.opencvUtils.BinaryUtils.BinaryUtils;
import cn.edu.njupt.utils.opencvUtils.CutUtils.CutUtils;
import cn.edu.njupt.utils.opencvUtils.GeneralUtils.GeneralUtils;
import cn.edu.njupt.utils.opencvUtils.PreHandleUtils.PreHandleUtils;
import cn.edu.njupt.utils.opencvUtils.RemoveNoiseUtils.RemoveNoiseUtils;
import cn.edu.njupt.utils.opencvUtils.ResizeUtils.ResizeUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Mat;

import java.util.List;

public class Main {

    @Before
    public void init(){
        System.load(SystemVariables.loadOpencvSystemFile());
    }

    @Test
    public void testPre(){
        for(int i = 1 ; i <= 6 ; i++){
            String imgPath = "C:/Users/X240/Desktop/opencv/web/p"+i+".jpg";
            String destPath = "C:/Users/X240/Desktop/opencv/web/web/";

            Mat src = GeneralUtils.matFactory(imgPath);

            src = PreHandleUtils.preHandleUtils(src);

            src = BinaryUtils.binaryzation(src);

            List<Mat> result = CutUtils.cutUtils(src);

            for(int j = 0 ; j < result.size() ; j++){
                Mat dst = ResizeUtils.resize(result.get(j) , GeneralUtils.getDsize());
                GeneralUtils.saveImg(dst , destPath + "cut-" + i +"-"+ j +".jpg");
            }
        }

    }
    
    @Test
    public void test1(){
        String imgPath = "C:/Users/X240/Desktop/resize/1.jpg";
        String destPath = "C:/Users/X240/Desktop/resize/cut/";

        Mat src = GeneralUtils.matFactory(imgPath);

        src = PreHandleUtils.preHandleUtils(src);

        src = BinaryUtils.binaryzation(src);

        List<Mat> result = CutUtils.cutUtils(src);

        for(int j = 0 ; j < result.size() ; j++){
            Mat dst = result.get(j);
            dst = ResizeUtils.resize(dst , GeneralUtils.getDsize());

            //dst = GeneralUtils.erode(dst);

            GeneralUtils.saveImg(dst , destPath + "cut-" + j +".jpg");
        }


    }


}
