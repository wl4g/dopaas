package cn.edu.njupt.utils.uploadUtils;

import java.io.File;

/**
 * 上传工具类
 */
public class UploadUtils {
    /**
     * 文件工厂
     * @param filePath
     * @param fileName
     * @return
     */
    public static File fileFactory(String filePath , String fileName){
        File path = new File(filePath);
        if(!path.exists()){
           path.mkdirs();
        }
        return new File(path + "/" + fileName);
    }

    public static String dirFactory(String filePath){
        File path = new File(filePath);
        if(!path.exists()){
            path.mkdirs();
        }
        return path.toString() + "/";
    }

}
