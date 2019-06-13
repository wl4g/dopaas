package com.wl4g.devops.umc.store.opentsdb;

import com.wl4g.devops.umc.store.opentsdb.client.bean.request.*;
import com.wl4g.devops.umc.store.opentsdb.client.bean.response.LastPointQueryResult;
import com.wl4g.devops.umc.store.opentsdb.client.bean.response.QueryResult;
import com.wl4g.devops.umc.store.opentsdb.client.exception.http.HttpException;
import com.wl4g.devops.umc.store.opentsdb.client.http.callback.QueryHttpResponseCallback;

import java.util.List;

/**
 * @author vjay
 * @date 2019-06-13 15:30:00
 */
public class OpentsdbUtil {


    public static void put() {
        long timestamp = System.currentTimeMillis();
        Point point = Point.metric("point")
                .tag("testTag", "test")
                .value(timestamp, 1.0)
                .build();
        OpentsdbConf.client.put(point);
    }


    ///=============query example==========================
    public void query() throws Exception {

        Query query = Query.begin("7d-ago")
                .sub(SubQuery.metric("metric.test")
                        .aggregator(SubQuery.Aggregator.NONE)
                        .build())
                .build();
        // 同步查询
        List<QueryResult> resultList = OpentsdbConf.client.query(query);

        // 异步查询
        OpentsdbConf.client.query(query, new QueryHttpResponseCallback.QueryCallback() {
            @Override
            public void response(Query query, List<QueryResult> queryResults) {
                // 在请求完成并且response code成功时回调
            }

            @Override
            public void responseError(Query query, HttpException e) {
                // 在response code失败时回调
            }

            @Override
            public void failed(Query query, Exception e) {
                // 在发生错误是回调
            }
        });
    }

    public void queryLast() throws Exception {
        LastPointQuery query = LastPointQuery.sub(LastPointSubQuery.metric("point")
                .tag("testTag", "test_1")
                .build())
                // baskScan表示查询最多向前推进多少小时
                // 比如在5小时前写入过数据
                // 那么backScan(6)可以查出数据，但backScan(4)则不行
                .backScan(1000)
                .build();
        List<LastPointQueryResult> lastPointQueryResults = OpentsdbConf.client.queryLast(query);
    }


    public void delete() throws Exception {
        Query query = Query.begin("7d-ago")
                .sub(SubQuery.metric("metric.test")
                        .aggregator(SubQuery.Aggregator.NONE)
                        .build())
                .build();
        OpentsdbConf.client.delete(query);
    }


    public void querySuggest() throws Exception{
        SuggestQuery query = SuggestQuery.type(SuggestQuery.Type.METRICS)
                .build();
        List<String> suggests = OpentsdbConf.client.querySuggest(query);
    }


}
