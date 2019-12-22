package cn.edu.njupt.service;


import org.opencv.core.Mat;

import java.util.List;

/**
 * 图像处理的服务层
 */
public interface ImageHandleService {
    /**
     * Mat工厂
     * @return
     */
    Mat matFactory(String imgPath);

    int channels(Mat src);

    List<String> cut(String path);
}
