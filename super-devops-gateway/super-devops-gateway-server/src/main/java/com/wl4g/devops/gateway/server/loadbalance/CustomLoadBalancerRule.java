package com.wl4g.devops.gateway.server.loadbalance;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.Server;
import com.wl4g.devops.gateway.server.model.HostWeight;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2020-07-22 14:41:00
 */
public class CustomLoadBalancerRule extends AbstractLoadBalancerRule {

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        System.out.println(iClientConfig);
    }

    @Override
    public Server choose(Object key) {
        //List<Server> servers = this.getLoadBalancer().getReachableServers();
        if(key instanceof Map){
            Object hosts = ((Map) key).get("hosts");
            if(hosts instanceof List){
                List<HostWeight> list = (List) hosts;
                Server server = new Server(weightChoose(list).getUri());
                return server;
            }
        }
        //Server server = new Server("localhost",14086);
        return null;
    }


    private HostWeight randomChoose(List<HostWeight> servers) {
        if (servers.isEmpty()) {
            return null;
        }
        if (servers.size() == 1) {
            return servers.get(0);
        }
        int randomIndex = RandomUtils.nextInt(0,servers.size());
        return servers.get(randomIndex);
    }


    private HostWeight weightChoose(List<HostWeight> servers){
        Integer weightSum = 0;
        for (HostWeight hw : servers) {
            weightSum += hw.getWeight();
        }

        if (weightSum <= 0) {
            return randomChoose(servers);
        }
        Integer n = RandomUtils.nextInt(0,weightSum); // n in [0, weightSum)
        Integer m = 0;
        for (HostWeight wc : servers) {
            if (m <= n && n < m + wc.getWeight()) {
                System.out.println("This Random Host is "+wc.getUri());
                break;
            }
            m += wc.getWeight();
        }
        return null;
    }

}
