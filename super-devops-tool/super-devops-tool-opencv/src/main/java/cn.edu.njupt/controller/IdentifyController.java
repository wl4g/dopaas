package cn.edu.njupt.controller;

import cn.edu.njupt.bean.UploadImageInfo;
import cn.edu.njupt.configure.SystemVariables;
import cn.edu.njupt.dto.IdentifyRequestHead;
import cn.edu.njupt.dto.Result;
import cn.edu.njupt.service.ImageHandleService;
import cn.edu.njupt.utils.httpUtils.PostPythonHttpUtils;
import cn.edu.njupt.utils.redisUtils.RedisDaoUtils;
import cn.edu.njupt.utils.uploadUtils.CheckImageUtils;
import cn.edu.njupt.utils.uploadUtils.Snowflake;
import cn.edu.njupt.utils.uploadUtils.UploadUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

//新的识别接口
@Controller
public class IdentifyController {
    //日志
    private static final Logger logger = (Logger) LoggerFactory.getLogger(IdentifyController.class);

    //唯一随机数
    private static final Snowflake snowflake = new Snowflake(5,5);

    @Autowired
    private ImageHandleService imageHandleService;

    //图像上传
    @PostMapping(value = "/upload")
    @ResponseBody
    public Result<UploadImageInfo> uploadAndCut(MultipartFile file , HttpServletRequest request) {
        Result<UploadImageInfo> result;
        // MultipartFile是对当前上传的文件的封装，当要同时上传多个文件时，可以给定多个MultipartFile参数(数组)
        if (file != null && !file.isEmpty()) {

            //检测文件上传的类型
            boolean b = CheckImageUtils.checkImage(file);

            if(b == false){
                logger.warn("文件类型上传错误");
                result = new Result<>(false , null , "文件类型上传错误");
            }else{
                logger.info("文件开始写入到本地");
                result = saveImg(file);

                if(result.isStatus()){
                    //如果写入成功，则切割
                    cutAndIdentify(result.getData().getAbsolutePath() , imageHandleService);
                }
            }
        } else {
            logger.warn("没有选择文件或者文件为空");
            result = new Result<>(false , null , "没有选择文件或者文件为空");
        }
        return result;
    }


    //识别接口
    @PostMapping(value = "/identify")
    @ResponseBody
    public Result<String> identify(@RequestParam("imgName") String imgName , @RequestParam("scores") String scores) {
        List<String> fileList = packImgFile(imgName);
        List<Integer> scoreList = packScores(scores);

        if(fileList == null || scoreList == null){
            return null;
        }

        if(fileList.size() > scoreList.size()){
            fileList.remove(0);
        }

        String data = identifyMaster(fileList , scoreList);

        /**
         * {…}
         * ​
         * "{'isSucess': True,
         * 'count': 5,
         * 'key': '290678887313723392',
         * 'serial': '290678887313723392',
         * 'time': '2019-03-14 10:54:26',
         * 'information': None,
         * 'result1': 3, 'proportion1': 0.35354719,
         * 'result2': 18, 'proportion2': 0.43521941,
         * 'result3': 4, 'proportion3': 0.61217213,
         * 'result4': 4, 'proportion4': 0.46072996,
         * 'result5': 5, 'proportion5': 0.38109913,
         * 'accuracy': 0}"
         */
        data = data.replaceAll("\\\\" , "");
        return new Result<>(true , parseData(data) , null);
//        return new Result<>(true , parseData(data) , null);
    }

    //解析识别后的数据
    private static String parseData(String data){
        try{
            JsonObject json = new JsonParser().parse(data).getAsJsonObject();
            return json.toString();
        }catch (Exception e){
            return "{\"isSucess\": 0 , \"information\": \""+data+"\"}";
        }
    }

    //识别的主控函数
    private static String identifyMaster(List<String> fileList , List<Integer> scoreList){
        //1、向python服务请求识别
        String response = postIdentify(fileList , scoreList);

        JsonObject json = new JsonParser().parse(response).getAsJsonObject();

        String status = "";
        String data = "";

        try{
            status = json.get("status").toString().replaceAll("\"" , "").trim();
        }catch (Exception e){
            //
            data = json.get("information").toString();
        }

        if(status.equals("succeed")){
            //可以请求识别数据
            data = queryRedisData(Integer.parseInt(json.get("time").toString().replaceAll("\"" , "").trim()) , json.get("serial").toString().replaceAll("\"" , "").trim());
        }

        return data;
    }

    //查询识别的结果
    private static String queryRedisData(int time , String key){
        String ip = "10.166.33.86";
        int port = 6379;
        String PREX = "RegResult:";

        key = PREX + key;

        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RedisDaoUtils utils = new RedisDaoUtils(ip , port);
        String data = utils.getOCRData(key);
        int count = 0;

        while (null == data || "".equals(data)){
            count++;
            data = utils.getOCRData(key);
            if(count > 10){
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    //python识别服务
    private static String postIdentify (List<String> fileList , List<Integer> scoreList){
        //1、构造请求体
        String json = constructRequestHead(fileList , scoreList);

        //2、向识别服务发送请求
        int count = 0;
        String responsedata = "";
        do{
            count++;
            //获取到的返回数据
            responsedata = PostPythonHttpUtils.postPython(json);
            if(count > 3){
                break;
            }
        }while ("".equals(responsedata) || null == responsedata);

        return responsedata;
    }

    //构造请求体
    private static String constructRequestHead(List<String> fileList , List<Integer> scoreList){
        IdentifyRequestHead head = new IdentifyRequestHead();
        head.setCategory("1");
        head.setImg(fileList);
        head.setScore(scoreList);
        head.setCount(fileList.size());
        String[] values = fileList.get(0).replaceAll("\\\\" , "/").split("/");
        String value = values[values.length - 2];
        head.setKey(value);
        head.setSerial(value);
        head.setProportion(3);
        return new Gson().toJson(head, IdentifyRequestHead.class);
    }



    //封装成绩
    private static List<Integer> packScores(String scores){
        String[] scoreList = scores.split(",");
        if(scoreList.length != 0){
            List<Integer> res = new ArrayList<>();
            for(String s : scoreList){
                try{
                    int temp = Integer.parseInt(s);
                    res.add(temp);
                }catch (Exception e){
                    res.add(null);
                }
            }
            return res;
        }
        return null;
    }

    //封装请求的图片数据
    private static List<String> packImgFile(String imgName){
        //1、获得图像切割后存储的绝对目录
        String cutDir = SystemVariables.saveImgCutPATH() + imgName;

        File cutFile = new File(cutDir);

        if(cutFile.isDirectory()){
            //是目录，获取目录中的所有文件
            File[] files = cutFile.listFiles();
            int count = 0;
            while(files.length == 0){
                count++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(count > 3){
                    break;
                }
            }
            if(files.length != 0){
                List<String> res = new ArrayList<>();
                for(File f : files){
                    res.add(f.getAbsolutePath().replaceAll("\\\\" , "/"));
                }
                return res;
            }
        }
        return null;
    }



    /**
     * 切割
     * @param absoluteName
     */
    private static void cutAndIdentify(final String absoluteName , final ImageHandleService imageHandleService){
        new Thread(new Runnable() {
            @Override
            public void run() {
                imageHandleService.cut(absoluteName);
            }
        }).start();
    }

    /**
     * 保存图像
     */
    private static Result<UploadImageInfo> saveImg(MultipartFile file){
        Result<UploadImageInfo> result;
        // 取文件格式后缀名
        String type = file.getOriginalFilename().substring(
                file.getOriginalFilename().indexOf("."));

        // 采用Snowflake生成图像名字
        String filename = snowflake.nextId() + type;

        logger.info("fileName: " + filename);

        //先存在tomcat容器中
        String relativePath = SystemVariables.saveImgRelavePATH();// 存放位置

        // 存放到绝对路径中
        String absolutePath = SystemVariables.saveImgAbsolutePath();

        File relativeDestFile = UploadUtils.fileFactory(relativePath , filename);
        File absoluteDestFile =  UploadUtils.fileFactory(absolutePath , filename);

        try {
            // FileUtils.copyInputStreamToFile()这个方法里对IO进行了自动操作，不需要额外的再去关闭IO流
            // 复制临时文件到指定tomcat目录下
            FileUtils.copyInputStreamToFile(file.getInputStream(), relativeDestFile);
            // 复制临时文件到指定系统目录下
            FileUtils.copyInputStreamToFile(file.getInputStream(), absoluteDestFile);

            logger.info("relativeDestFile: " + relativeDestFile.toString());
            logger.info("absoluteDestFile: " + absoluteDestFile.toString());

            //TODO,构造返回对象 , 需要更改的地方
            result = new Result<>(true ,new UploadImageInfo(filename , SystemVariables.getImagePrefixUrl() + filename , absoluteDestFile.toString().replaceAll("\\\\" , "/")) , null);
        } catch (IOException e) {
            logger.error("文件写入失败: " + e.toString());
            //构造返回对象
            result = new Result<>(false , null , "文件写入失败，请重新上传");
        }

        return result;
    }
}
