package com.wl4g.devops.umc.store.opentsdb.client.util;

import com.wl4g.devops.umc.store.opentsdb.client.bean.response.ErrorResponse;
import com.wl4g.devops.umc.store.opentsdb.client.common.Json;
import com.wl4g.devops.umc.store.opentsdb.client.exception.http.HttpException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 响应解析工具类
 *
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 下午7:30
 * @Version: 1.0
 */
public class ResponseUtil {

    /***
     * 解析响应的内容
     * @param response 响应内容
     * @return
     * @throws IOException
     */
    public static String getContent(HttpResponse response) throws IOException {
        if (checkGT400(response)) {
            throw new HttpException(convert(response));
        } else {
            return getContentString(response);
        }
    }

    private static String getContentString(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            return EntityUtils.toString(entity, Charset.defaultCharset());
        }
        return null;
    }

    /***
     * 判断响应码的是否为400以上，如果是，则表示出错了
     * @param response  查询对象
     * @return
     */
    private static boolean checkGT400(HttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode >= 400) {
            return true;
        }
        return false;
    }

    /***
     * 将响应内容转换成errorResponse
     * @param response  查询对象
     * @return
     */
    private static ErrorResponse convert(HttpResponse response) throws IOException {
        return Json.readValue(getContentString(response), ErrorResponse.class);
    }


}
