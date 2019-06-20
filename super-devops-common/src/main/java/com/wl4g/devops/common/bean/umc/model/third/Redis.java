package com.wl4g.devops.common.bean.umc.model.third;

import com.wl4g.devops.common.bean.umc.model.PhysicalInfo;

import java.util.Map;

/**
 * @author vjay
 * @date 2019-06-20 15:52:00
 */
public class Redis extends PhysicalInfo {

    private RedisInfo[] redisInfos;

    public RedisInfo[] getRedisInfos() {
        return redisInfos;
    }

    public void setRedisInfos(RedisInfo[] redisInfos) {
        this.redisInfos = redisInfos;
    }

    public static class RedisInfo{
        private int port;
        private Map<String,String> info;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public Map<String, String> getInfo() {
            return info;
        }

        public void setInfo(Map<String, String> info) {
            this.info = info;
        }
    }


}
