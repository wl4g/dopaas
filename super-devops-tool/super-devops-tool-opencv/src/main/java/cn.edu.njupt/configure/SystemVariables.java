package cn.edu.njupt.configure;

import cn.edu.njupt.utils.uploadUtils.UploadUtils;

/**
 * 系统环境变量统一配置类
 */
public class SystemVariables {

    //系统统一变量
    private static final String SYS_VARIABLE = "WIN";

    //系统文件路径
    private static final String WIN_SYSTEM = "G:/opencv/opencv/build/java/x64/opencv_java341.dll";

    private static final String STITP_SYSTEM = "/home/stitp/apps/opencv/opencv-3.4.1/build/lib/libopencv_java341.so";

    private static final String YTO_SYSTEM = "/usr/local/opencv/opencv-3.4.1/build/lib/libopencv_java341.so";

    private static final String ALIYUN_SYSTEM = "/usr/local/src/opencv/opencv-3.4.1/build/lib/libopencv_java341.so";

    //切割后的图像保存路径
    private static final String WIN_PATH = "C:/Users/X240/Desktop/upload/cut/";

    private static final String STITP_PATH = "/home/stitp/apps/apache-tomcat-7.0.52/webapps/cut/";

    private static final String YTO_PATH = "/usr/local/tomcat8-2/apache-tomcat-8.5.34/webapps/cut/";

    private static final String ALIYUN_PATH = "/usr/local/src/tomcat/tomcat8/apache-tomcat-8.5.37/webapps/cut/";

    //图像上传后的保存路径
    //WIN
    private static final String ABSOLUTE_PATH = "C:/Users/X240/Desktop/upload/";//绝对路径
    private static final String RELATIVE_PATH = "G:/tomcat8/apache-tomcat-8.0.53/webapps/upload/";//相对路径
    private static final String IMAGE_PREFIX_URL = "http://127.0.0.1:8080/upload/";//图片url前缀

    //STITP
    private static final String STITP_ABSOLUTE_PATH = "/home/stitp/javaData/data/";
    //TODO
    private static final String STITP_RELATIVE_PATH = "/home/stitp/apps/apache-tomcat-7.0.52/webapps/upload/";//相对路径
    private static final String STITP_IMAGE_PREFIX_URL = "http://10.166.33.86:8080/upload/";

    //YTO
    private static final String YTO_ABSOLUTE_PATH = "/usr/local/data/";
    private static final String YTO_RELATIVE_PATH = "/usr/local/tomcat8-2/apache-tomcat-8.5.34/webapps/upload/";//相对路径
    private static final String YTO_IMAGE_PREFIX_URL = "http://39.108.188.185:8080/upload/";

    //ALIYUN
    private static final String ALIYUN_ABSOLUTE_PATH = "/usr/local/src/data/";
    private static final String ALIYUN_RELATIVE_PATH = "/usr/local/src/tomcat/tomcat8/apache-tomcat-8.5.37/webapps/upload/";
    private static final String ALIYUN_IMAGE_PREFIX_URL = "http://47.100.42.186:8080/upload/";

    //标志
    private static final String WIN = "WIN";

    private static final String STITP = "STITP";

    private static final String YTO = "YTO";

    private static final String ALIYUN = "ALIYUN";

    //python识别接口
    public static final String PYTHON_URL = "http://10.166.33.86:5000/todos";
    public static final String IDENTIFY_URL = "http://10.166.33.86:5555/todos";
//    public static final String PYTHON_URL = "http://192.168.43.237:5000/todos";

    /**
     * 加载opencv系统文件
     * @return
     */
    public static String loadOpencvSystemFile(){
        switch (SYS_VARIABLE){
            case WIN:
                return WIN_SYSTEM;
            case STITP:
                return STITP_SYSTEM;
            case YTO:
                return YTO_SYSTEM;
            case ALIYUN:
                return ALIYUN_SYSTEM;
            default:
                return WIN_SYSTEM;
        }
    }

    /**
     * 获取图像保存的绝对路径
     * @return
     */
    public static String saveImgAbsolutePath(){
        switch (SYS_VARIABLE){
            case WIN:
                return UploadUtils.dirFactory(ABSOLUTE_PATH).replaceAll("\\\\" , "/");
            case STITP:
                return UploadUtils.dirFactory(STITP_ABSOLUTE_PATH).replaceAll("\\\\" , "/");
            case YTO:
                return UploadUtils.dirFactory(YTO_ABSOLUTE_PATH).replaceAll("\\\\" , "/");
            case ALIYUN:
                return UploadUtils.dirFactory(ALIYUN_ABSOLUTE_PATH).replaceAll("\\\\" , "/");
            default:
                return UploadUtils.dirFactory(ABSOLUTE_PATH).replaceAll("\\\\" , "/");
        }
    }

    /**
     * 获取图像url的前缀
     * @return
     */
    public static String getImagePrefixUrl(){
        switch (SYS_VARIABLE){
            case WIN:
                return IMAGE_PREFIX_URL;
            case STITP:
                return STITP_IMAGE_PREFIX_URL;
            case YTO:
                return YTO_IMAGE_PREFIX_URL;
            case ALIYUN:
                return ALIYUN_IMAGE_PREFIX_URL;
            default:
                return IMAGE_PREFIX_URL;
        }
    }

    /**
     * 获取图像切割后保存的路径
     * @return
     */
    public static String saveImgCutPATH(){
        switch (SYS_VARIABLE){
            case WIN:
                return UploadUtils.dirFactory(WIN_PATH).replaceAll("\\\\" , "/");
            case STITP:
                return UploadUtils.dirFactory(STITP_PATH).replaceAll("\\\\" , "/");
            case YTO:
                return UploadUtils.dirFactory(YTO_PATH).replaceAll("\\\\" , "/");
            case ALIYUN:
                return UploadUtils.dirFactory(ALIYUN_PATH).replaceAll("\\\\" , "/");
            default:
                return UploadUtils.dirFactory(WIN_PATH).replaceAll("\\\\" , "/");
        }
    }

    /**
     * 返回图像保存的相对路径
     * @return
     */
    public static String saveImgRelavePATH(){
        switch (SYS_VARIABLE){
            case WIN:
                return UploadUtils.dirFactory(RELATIVE_PATH).replaceAll("\\\\" , "/");
            case STITP:
                return UploadUtils.dirFactory(STITP_RELATIVE_PATH).replaceAll("\\\\" , "/");
            case YTO:
                return UploadUtils.dirFactory(YTO_RELATIVE_PATH).replaceAll("\\\\" , "/");
            case ALIYUN:
                return UploadUtils.dirFactory(ALIYUN_RELATIVE_PATH).replaceAll("\\\\" , "/");
            default:
                return UploadUtils.dirFactory(RELATIVE_PATH).replaceAll("\\\\" , "/");
        }
    }

}
