package cn.edu.njupt.utils.httpUtils;

import cn.edu.njupt.configure.SystemVariables;
import cn.edu.njupt.dto.IdentifyRequestHead;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * 向python识别服务发送post请求
 */
public class PostPythonHttpUtils {

    /**
     * 向python服务提交ocr识别
     * @param request
     * @return
     */
    public static String postPython(String request){
        RestTemplate restTemplate = new RestTemplate();

        //获取请求头
        HttpEntity<String> header = header(request);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(SystemVariables.IDENTIFY_URL, header , String.class);

        int code = responseEntity.getStatusCodeValue();

        if(code == 200){
            return responseEntity.getBody();
        }else{
            return null;
        }
//        JsonObject json = new JsonParser().parse(body).getAsJsonObject();

    }

    /**
     * 根据请求参数封装请求头
     * @param request
     * @return
     */
    private static HttpEntity<String> header(String request){
        HttpHeaders headers = new HttpHeaders();
        //设置发送数据类型
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        //设置返回数据类型
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        HttpEntity<String> formEntity = new HttpEntity<String>(request, headers);

        return formEntity;
    }

    public static void main(String[] args) {

        String response = postPython(constructRequestHead(Arrays.asList("/home/stitp/pictures/0.jpg","/home/stitp/pictures/1.jpg","/home/stitp/pictures/2.jpg","/home/stitp/pictures/3.jpg") , Arrays.asList(12,23,66,11)));
        JsonObject json = new JsonParser().parse(response).getAsJsonObject();

        System.out.println(json);
        System.out.println(json.get("status").toString().replaceAll("\"" , "").equals("succeed"));

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
        head.setProportion(1);
        return new Gson().toJson(head, IdentifyRequestHead.class);
    }
}
