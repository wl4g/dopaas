package cn.edu.njupt.controller;

import cn.edu.njupt.dto.PostObj;
import cn.edu.njupt.dto.Result;
import cn.edu.njupt.service.ImageHandleService;
import cn.edu.njupt.utils.httpUtils.PostPythonHttpUtils;
import cn.edu.njupt.utils.redisUtils.RedisDaoUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CutImageController {

    private static final String IP = "10.166.33.86";
    private static final int PORT = 6379;

    private static final String PREX = "RegResult:";

    //日志
    private static final Logger logger = (Logger) LoggerFactory.getLogger(CutImageController.class);

    @Autowired
    private ImageHandleService imageHandleService;


    @RequestMapping(value = "/cut")
    @ResponseBody
    public Result<List<Integer>> cut(HttpServletRequest request , @RequestParam String imgPath){
        Result<List<Integer>> result;

        List<String> data = imageHandleService.cut(imgPath);

        if(data == null){
            result = new Result<>(false , null , "不是一个有效的图像");
        }else {
            //向python服务提交识别数据
            List<Integer> score = new ArrayList<>();
            String scores = request.getParameter("score");

            if(score != null && !"".equals(scores)){
                String[] strs = scores.replaceAll("\\D" , " ").trim().split(" ");
                for(String s : strs){
                    try{
                        score.add(Integer.parseInt(s));
                    }catch (Exception e){
                        score.add(null);
                    }

                }
            }
            //主控函数
            result = cutMaster(data , score);
        }
        return result;
    }


    /**
     * 切割主控函数
     */
    public static Result<List<Integer>> cutMaster(List<String> img , List<Integer> score){
        String responsedata = postPythonData(img , score);
        Result<List<Integer>> redisResult;
        if("".equals(responsedata) || null == responsedata){
            //TODO , 服务异常
            redisResult = new Result<>(false , null , "识别服务异常");
        }else{
            //获取到数据
            //向redis服务获取数据
            redisResult = getRedisData(responsedata);
        }
        return redisResult;
    }

    //向redis服务获取数据
    private static Result<List<Integer>> getRedisData(String responseData){
        Result<List<Integer>> result;
        //json解析
        try{
            logger.info("python服务请求: "+responseData);
            JsonObject json = new JsonParser().parse(responseData).getAsJsonObject();
            String status = json.get("status").toString().replaceAll("\"","").trim();
            if("succeed".equals(status)){
                //请求成功
                String serial = json.get("serial").toString().replaceAll("\"" , "").trim();

                String time = json.get("time").toString().replaceAll("\"" , "").trim();

                int ntime = 5000;
                //向redis服务获取数据
                try{
                    ntime = Integer.parseInt(time) * 1000;
                }catch (Exception e){
                    //TODO
                    logger.error("休眠时间格式转换错误: " + e.toString());
                }

                //线程休眠
                Thread.sleep(ntime);

                //向redis服务获取数据
                String redisData = getRedisData(IP , PORT , PREX + serial);

                json = new JsonParser().parse(redisData).getAsJsonObject();

                logger.info("redis服务获取数据: " + json);

                Boolean isSucess = true;
                int redisCount = 0;
                try{
                    isSucess = Boolean.parseBoolean(json.get("isSucess").toString());
                    redisCount = Integer.parseInt(json.get("count").toString());
                }catch (Exception e){
                    isSucess = false;
                    logger.error("redis数据格式转换错误: " + e.toString());
                }

                if(isSucess){
                    //数据解析成功
                    System.out.println("redis数据解析成功");
                    List<Integer> redisResult = new ArrayList<>();
                    for(int i = 1 ; i < redisCount ; i++){
                        redisResult.add(Integer.parseInt(json.get("result"+i).toString()));
                    }
                    result = new Result<>(true , redisResult , null);
                }else{
                    //数据解析失败
                    result = new Result<>(false , null , json.get("information").toString());

                }
            }else{
                //请求失败
                result = new Result<>(false , null , json.get("information").toString());
            }

        }catch (Exception e){
            result = new Result<>(false , null , "json解析错误，服务器异常: " + e.toString());
            logger.error("json解析错误: " + e.toString());
        }
        return result;
    }


    //向redis服务获取数据
    private static String getRedisData(String ip , int port , String key){
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

    //向python服务发送请求
    private static String postPythonData(List<String> img , List<Integer> score){
        int count = 0;
        String responsedata = "";
        //构造的请求数据(json字符串)
        String requestData = constructPythonData(constructPostObj(img , score));
        do{
            count++;
            //获取到的返回数据
            responsedata = PostPythonHttpUtils.postPython(requestData);
            if(count > 3){
                break;
            }
        }while ("".equals(responsedata) || null == responsedata);

        return responsedata;
    }

    //构造向python服务请求的数据
    private static String constructPythonData(PostObj postObj){
        return new Gson().toJson(postObj , PostObj.class);
    }

    //构造PostObj对象
    private static PostObj constructPostObj(List<String> img , List<Integer> score){
        PostObj postObj = new PostObj();
        postObj.setCategory("1");
        postObj.setImg(img);
        postObj.setScore(score);
        postObj.setCount(img.size());

        String[] values = img.get(0).replaceAll("\\\\" , "/").split("/");
        String value = values[values.length - 2];
        postObj.setKey(value);
        postObj.setSerial(value);
        return postObj;
    }

//    public static void main(String[] args) {
//        List<Integer> score = new ArrayList<>();
//        score.add(20);
//        score.add(10);
//        score.add(10);
//        score.add(100);
//        List<String> img = new ArrayList<>();
//        img.add("/home/stitp/javaData/cut/p6/p6-0.jpg");
//        img.add("/home/stitp/javaData/cut/p6/p6-1.jpg");
//        img.add("/home/stitp/javaData/cut/p6/p6-2.jpg");
//        img.add("/home/stitp/javaData/cut/p6/p6-3.jpg");
//        img.add("/home/stitp/javaData/cut/p6/p6-4.jpg");
//        img.add("/home/stitp/javaData/cut/p6/p6-5.jpg");
//        img.add("/home/stitp/javaData/cut/p6/p6-6.jpg");
//        System.out.println(cutMaster(img , score));
//    }

}
