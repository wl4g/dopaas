package com.wl4g.devops.scm.common.utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author vjay
 * @date 2019-06-05 15:21:00
 */
@Component
public class ZKCustor {
    /**
     * @author employeeeee
     * @Description: zookeeper 客户端
     * @date 2019/1/18 14:01
     * @params * @param null
     */
    private CuratorFramework client = null;

    final static Logger log = LoggerFactory.getLogger(ZKCustor.class);


    public static void main(String[] args) throws Exception{
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("47.107.57.204:2181")
                //.sessionTimeoutMs(10000)
                .retryPolicy(retryPolicy)
                //.namespace("admin")
                .build();
        client.start();
        client.close();

        byte[] b = client.getData().forPath("/apps-config/scm-example/test/meta/heweijiedeMacBook-Pro.local:14044");
        System.out.println(new String(b, StandardCharsets.UTF_8));
    }
}