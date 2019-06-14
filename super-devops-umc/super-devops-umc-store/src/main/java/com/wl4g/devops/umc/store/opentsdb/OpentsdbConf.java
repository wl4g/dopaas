package com.wl4g.devops.umc.store.opentsdb;


import com.wl4g.devops.umc.store.opentsdb.client.OpenTSDBClient;
import com.wl4g.devops.umc.store.opentsdb.client.OpenTSDBClientFactory;
import com.wl4g.devops.umc.store.opentsdb.client.OpenTSDBConfig;
import com.wl4g.devops.umc.store.opentsdb.client.bean.request.Point;
import com.wl4g.devops.umc.store.opentsdb.client.bean.response.DetailResult;
import com.wl4g.devops.umc.store.opentsdb.client.http.callback.BatchPutHttpResponseCallback;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author vjay
 * @date 2019-06-13 15:09:00
 */
@Component
@ConfigurationProperties(prefix = "opentsdb")
public class OpentsdbConf {

    //@Value("${opentsdb.url}")
    private static String url;

    //@Value("${opentsdb.port}")
    private static Integer port;

    private static OpenTSDBConfig config = null;
    private static OpenTSDBClient client = null;


    public static void init() {
        try {
            config = OpenTSDBConfig.address(url, port)
                    // http连接池大小，默认100
                    .httpConnectionPool(100)
                    // http请求超时时间，默认100s
                    .httpConnectTimeout(100)
                    // 异步写入数据时，每次http提交的数据条数，默认50
                    .batchPutSize(50)
                    // 异步写入数据中，内部有一个队列，默认队列大小20000
                    .batchPutBufferSize(20000)
                    // 异步写入等待时间，如果距离上一次请求超多300ms，且有数据，则直接提交
                    .batchPutTimeLimit(3000)//TODO
                    // 当确认这个client只用于查询时设置，可不创建内部队列从而提高效率
                    //.readonly()
                    // 每批数据提交完成后回调
                    .batchPutCallBack(new BatchPutHttpResponseCallback.BatchPutCallBack() {
                        @Override
                        public void response(List<Point> points, DetailResult result) {
                            // 在请求完成并且response code成功时回调
                        }
                        @Override
                        public void responseError(List<Point> points, DetailResult result) {
                            // 在response code失败时回调
                        }
                        @Override
                        public void failed(List<Point> points, Exception e) {
                            // 在发生错误是回调
                        }
                    }).config();

            client = OpenTSDBClientFactory.connect(config);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void  close(){
        try {
            // 优雅关闭连接，会等待所有异步操作完成
            client.gracefulClose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static OpenTSDBClient getClient() {
        if(client == null){
            init();
        }
        if(client == null){
            throw new RuntimeException("can not init the opentsdb client");
        }
        return client;
    }

    public static String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        OpentsdbConf.url = url;
    }

    public static Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        OpentsdbConf.port = port;
    }

    /*public static void main(String[] args){
        init();
        long timestamp = System.currentTimeMillis();
        Point point = Point.metric("test1")
                .tag("testTag", "test")
                .value(timestamp, 1.0)
                .build();
        client.put(point);

        close();
    }*/





}
