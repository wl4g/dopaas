package cn.edu.njupt.controller;

import cn.edu.njupt.dto.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 *调式接口
 */

@Controller
public class CutTestController {


    @RequestMapping(value = "/cutest")
    @ResponseBody
    public Result<List<Integer>> cut(HttpServletRequest request , @RequestParam String imgPath){
        List<Integer> list = Arrays.asList(18,8,8,27,14,14,89);

        Result<List<Integer>> result = new Result<>(true , list , null);
        System.out.println(imgPath);

        String score = request.getParameter("score");

        String[] strs = score.replaceAll("\\D" , " ").trim().split(" ");

        for(String s : strs){
            System.out.println(s);
        }

        return result;
    }
}
