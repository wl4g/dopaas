//package cn.edu.njupt.controller;
//
//import cn.edu.njupt.bean.UploadImageInfo;
//import cn.edu.njupt.configure.SystemVariables;
//import cn.edu.njupt.dto.Result;
//import cn.edu.njupt.utils.uploadUtils.CheckImageUtils;
//import cn.edu.njupt.utils.uploadUtils.Snowflake;
//import cn.edu.njupt.utils.uploadUtils.UploadUtils;
//import org.apache.commons.io.FileUtils;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.multipart.MultipartFile;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.File;
//import java.io.IOException;
//
///**
// * 文件的上传
// *
// */
////@Controller
//public class UploadFIleController {
//    //日志
//    private static final Logger logger = (Logger) LoggerFactory.getLogger(UploadFIleController.class);
//
//    //唯一随机数
//    private static final Snowflake snowflake = new Snowflake(5,5);
//
//    //..................................仅测试opencv
////    @Autowired
////    private ImageHandleService imageHandleService;
//    //..................................
//
//    //单文件上传
////    @PostMapping(value = "/upload")
//    @ResponseBody
//    public Result<UploadImageInfo> queryFileData(MultipartFile file , HttpServletRequest request) {
//        Result<UploadImageInfo> result;
//        // MultipartFile是对当前上传的文件的封装，当要同时上传多个文件时，可以给定多个MultipartFile参数(数组)
//        if (file != null && !file.isEmpty()) {
//
//            //检测文件上传的类型
//            boolean b = CheckImageUtils.checkImage(file);
//
//            if(b == false){
//                logger.warn("文件类型上传错误");
//                result = new Result<>(false , null , "文件类型上传错误");
//            }else{
//                // 取文件格式后缀名
//                String type = file.getOriginalFilename().substring(
//                        file.getOriginalFilename().indexOf("."));
//
//                // 采用Snowflake生成图像名字
//                String filename = snowflake.nextId() + type;
//
//                logger.info("fileName: " + filename);
//
//                //先存在tomcat容器中
//                String relativePath = SystemVariables.saveImgRelavePATH();// 存放位置
//
//                // 存放到绝对路径中，需要更改的地方
//                String absolutePath = SystemVariables.saveImgAbsolutePath();
//
//
//                File relativeDestFile = UploadUtils.fileFactory(relativePath , filename);
//                File absoluteDestFile =  UploadUtils.fileFactory(absolutePath , filename);
//
//                try {
//                    // FileUtils.copyInputStreamToFile()这个方法里对IO进行了自动操作，不需要额外的再去关闭IO流
//                    // 复制临时文件到指定tomcat目录下
//                    FileUtils.copyInputStreamToFile(file.getInputStream(), relativeDestFile);
//                    // 复制临时文件到指定系统目录下
//                    FileUtils.copyInputStreamToFile(file.getInputStream(), absoluteDestFile);
//
//                    logger.info("relativeDestFile: " + relativeDestFile.toString());
//                    logger.info("absoluteDestFile: " + absoluteDestFile.toString());
//
//                    //..................................仅测试opencv
////                Mat src = imageHandleService.matFactory(absoluteDestFile.toString());
////
////                logger.info(src.toString());
////                logger.info("判空：" + src.empty());
////
////                logger.info("图像的通道是：" + imageHandleService.channels(src));
//                    //..................................
//
//
//                    //TODO,构造返回对象 , 需要更改的地方
//                    result = new Result<>(true ,new UploadImageInfo(filename , SystemVariables.getImagePrefixUrl() + filename , absoluteDestFile.toString().replaceAll("\\\\" , "/")) , null);
//                } catch (IOException e) {
//                    logger.error("文件写入失败: " + e.toString());
//                    //构造返回对象
//                    result = new Result<>(false , null , "文件写入失败，请重新上传");
//                }
//            }
//
//        } else {
//            logger.warn("没有选择文件或者文件为空");
//            result = new Result<>(false , null , "没有选择文件或者文件为空");
//        }
//        return result;
//    }
//}
