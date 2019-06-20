package com.wl4g.devops.common.bean.umc.model.third;

import java.util.Map;

/**
 * @author vjay
 * @date 2019-06-20 15:52:00
 */
public class Zookeeper {

    private ZookeeperInfo[] zookeeperInfos;

    public ZookeeperInfo[] getZookeeperInfos() {
        return zookeeperInfos;
    }

    public void setZookeeperInfos(ZookeeperInfo[] zookeeperInfos) {
        this.zookeeperInfos = zookeeperInfos;
    }

    public static class ZookeeperInfo{
        private int port;
        private Map<String,Object> info;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public Map<String, Object> getInfo() {
            return info;
        }

        public void setInfo(Map<String, Object> info) {
            this.info = info;
        }
    }


}
